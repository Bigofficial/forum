package com.rt.forum.controller;

import com.rt.forum.annotation.LoginRequired;
import com.rt.forum.entity.User;
import com.rt.forum.service.FollowService;
import com.rt.forum.service.LikeService;
import com.rt.forum.service.UserService;
import com.rt.forum.util.CommunityConstant;
import com.rt.forum.util.CommunityUtil;
import com.rt.forum.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followSerice;
    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPath(){
        return "/site/setting";
    }

    /**
     * 接收文件 MultipartFile
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","你还没有选择图片");
            return "/site/setting";
        }
        //上传文件，不要按照原始文件名字存，可能很多人都叫1.png,生成随机名字
        //先读取后缀暂存
        String fileName = headerImage.getOriginalFilename();
        //获得后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        //生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath+"/"+fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常",e);
        }
        //更新用户头像路径
        // web访问路径http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    //流向浏览器输出
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void  getHeader(@PathVariable("fileName")String fileName,HttpServletResponse response){
        //找到服务器存放文件路径就是uploadpath/fileName
        fileName = uploadPath + "/" + fileName;
        //输出图片，声明格式
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try (
                //这里声明变量会自己关闭
                //先得到一个输入流，需要手动关闭
                FileInputStream fis = new FileInputStream(fileName);
                //springmvc自己会关闭
                OutputStream outputStream = response.getOutputStream();
                ){
            //声明缓冲区一批一批输出
            byte[] buffer = new byte[1024];
            //游标
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }


    }

    //个人主页
    //任意用户主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET )
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }

        //用户
        model.addAttribute("user", user);
        //点赞数量
        int count = likeService.findUserLikeCount(userId);
        //查询关注数量
        long followeeCount = followSerice.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //查询粉丝
        long followerCount = followSerice.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //查询是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followSerice.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        model.addAttribute("likeCount", count);

        return "/site/profile";
    }
}
