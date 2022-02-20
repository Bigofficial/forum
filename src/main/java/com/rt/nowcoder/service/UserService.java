package com.rt.nowcoder.service;

import com.rt.nowcoder.dao.LoginTicketMapper;
import com.rt.nowcoder.dao.UserMapper;
import com.rt.nowcoder.entity.LoginTicket;
import com.rt.nowcoder.entity.User;
import com.rt.nowcoder.util.CommunityConstant;
import com.rt.nowcoder.util.CommunityUtil;
import com.rt.nowcoder.util.MailClient;
import com.rt.nowcoder.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int userId){
//        return userMapper.selectById(userId);

        //redis改版
        //先从cache
        User user = getCache(userId);
        if(user == null){
            //没在cache放到cache
            user = initCache(userId);
        }
        return user;
    }


    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //判断空值user
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //判断账号重复
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }
            u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已存在");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        //mybatis回填userid
        userMapper.insertUser(user);


        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // http://localhost:8080/community/activation/101(用户id)/code(激活码)
        String url = domain + contextPath + "/activation" + "/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(), "激活账号",content);
        return map;
    }


    public int activation(int userId,String code){
        //查用户，判断激活码对不对
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            //已经激活
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            //redis改版  清理缓存
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }
    //比较加密后的密码
    public Map<String,Object> login(String username , String password, int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        //空值判断
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //合法性验证
        //有没有账号，密码是否一致
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","账号不存在");
            return map;
        }
        //判断是否激活
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        //判断密码
        //同样规则加密
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }
        //都没问题，登入成功，生成登入凭证ticket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        //使用之前的生成随机字符串
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);

        //redis改版
        //保存凭证到redis
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        //对象安装字符串存 每个人单独存一分
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        //发给客户端ticket
        //下次来我只要去库里找
        //login成功我们放入ticket
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
//       loginTicketMapper.updateStatus(ticket, 1);

        //redis改版
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }


    public LoginTicket findLoginTicket(String ticket){
//        return loginTicketMapper.selectByTicket(ticket);

        //redis改版
        return (LoginTicket) redisTemplate.opsForValue().get(RedisKeyUtil.getTicketKey(ticket));
    }

    //修改头像路径
    public int updateHeader(int userId,String headerUrl){

//       return userMapper.updateHeader(userId,headerUrl);
        //redis改版用户被修改
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    //名字查
    public User findUserByName(String name){
        return userMapper.selectByName(name);
    }

    //做缓存findUserById
    //改了用户数据更新缓存，或者删除缓存，我们使用删除缓存
    //1优先从缓存取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        //直接存user对象他会化成字符串
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2取不到初始化缓存数据
    private User initCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        User user = userMapper.selectById(userId);
        //放到redis 设置过期时间
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3数据变更清除缓存数据
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);

    }
}
