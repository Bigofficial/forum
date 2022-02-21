package com.rt.nowcoder.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer(){
        //配置文件 kaptcha太长了那个key,我们在这里设置
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
        //几个字符拼在一起
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        //采用干扰，防止机器人破解
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");


        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}