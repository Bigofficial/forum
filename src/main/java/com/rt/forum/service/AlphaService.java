package com.rt.forum.service;

import com.rt.forum.dao.DiscussPostMapper;
import com.rt.forum.dao.UserMapper;
import com.rt.forum.entity.DiscussPost;
import com.rt.forum.entity.User;
import com.rt.forum.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * @author zhengguohuang
 * @date 2021/03/17
 */
@Service
public class AlphaService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(AlphaService.class);

    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init() {
        System.out.println("AlphaService init().");
    }

    @PreDestroy
    public void destory() {
        System.out.println("销毁AlphaService");
    }

    /**
     * 没有事务支持，这两个任然会插入表格
     * 传播机制：A调用另外的业务方法B，这两个方法可能都有事务，以B还是A的为准。
     * propagation  * REQUIRED(0),  支持当前事务 A调B A就是当前事务（外部事务），如果不存在就创建新事务
     *     SUPPORTS(1),
     *     MANDATORY(2),
     *  *   REQUIRES_NEW(3),        创建一个新的事务，并且暂停当前事务（外部事务），A调用B，暂停A的
     *     NOT_SUPPORTED(4),
     *     NEVER(5),
     *   *  NESTED(6);      当前存在事务（外部事务），则嵌套在该事务中执行，A调B，B在执行时有独立提交和回滚（虽然我是嵌在你里面），
     *                      如果不存在就和required一样。
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED,
                    propagation = Propagation.REQUIRED)
    public Object save1(){
        //增加用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //增加帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        //搞个错，看看能不能回滚
        Integer.valueOf("abc");
        return "OK";
    }


    //编程式事务
    //TransactionTemplate
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        //传一个回调接口
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //增加用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
                user.setEmail("beata@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);
                //增加帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好吗骂骂");
                post.setContent("新人报道！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);
                //搞个错，看看能不能回滚
                Integer.valueOf("abc");
                return "ok";
            }
        });
    }
    /** 让该方法在多线性环境下，被异步的调用 */
    @Async
    public void execute1() {
        LOGGER.debug("execute1");
    }

    //    @Scheduled(initialDelay = 10_000, fixedRate = 1_000)
    public void execute2() {
        LOGGER.debug("execute2");
        //testcommentasdsda
    }
    //asdasd
}
