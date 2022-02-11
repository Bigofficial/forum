package com.rt.newcoder.config;

import org.springframework.beans.factory.annotation.Configurable;
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