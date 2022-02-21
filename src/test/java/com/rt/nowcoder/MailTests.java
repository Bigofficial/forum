package com.rt.nowcoder;

import com.rt.nowcoder.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTests {


    @Autowired
    private MailClient mailClient;

    //主动调用模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMail(){
        mailClient.sendMail("1439635048@qq.com","test","你好邮件啊");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","hahaha");

        String content = templateEngine.process("/mail/demo",context);

        System.out.println(content);

        mailClient.sendMail("1439635048@qq.com","HTMLtest",content);
    }
}
