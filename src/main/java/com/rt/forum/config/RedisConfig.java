package com.rt.forum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {



    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        //创建连接，需要连接工厂，注入连接工厂spring已经注入了RCF
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //设置key的序列化
        template.setKeySerializer(RedisSerializer.string());
        //设置value序列化  json是结构化的恢复回来好使
        template.setValueSerializer(RedisSerializer.json());
        //设置hash key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设值hash value序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        //配置生效
        template.afterPropertiesSet();
        return template;
    }
}
