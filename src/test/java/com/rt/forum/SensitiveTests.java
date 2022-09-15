package com.rt.forum;

import com.rt.forum.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "这里可以，可以嫖娼，可以吸毒，可以开票，哈哈哈赌博fabc";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
