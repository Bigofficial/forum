package com.rt.nowcoder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.SimpleFormatter;

@Configuration
public class AlphaConfig {


    @Bean
    public SimpleFormatter simpleFormatter(){
        return new SimpleFormatter();
    }
}
