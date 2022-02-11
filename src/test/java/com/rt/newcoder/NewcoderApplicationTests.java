package com.rt.newcoder;

import com.rt.newcoder.dao.DiscussPostMapper;
import com.rt.newcoder.dao.UserMapper;
import com.rt.newcoder.entity.DiscussPost;
import com.rt.newcoder.entity.User;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.logging.SimpleFormatter;

@SpringBootTest

class NewcoderApplicationTests implements ApplicationContextAware {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;
    private org.springframework.context.ApplicationContext applicationContext;
    @Test
    void contextLoads() {
    }

    @Test
    public void test01(){
        SimpleFormatter simpleFormatter = (SimpleFormatter) applicationContext.getBean(SimpleFormatter.class);
        System.out.println(simpleFormatter);
    }


    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost discussPost:discussPosts){
            System.out.println(discussPost);
        };
    }

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(1);
        System.out.println(user);
    }
}
