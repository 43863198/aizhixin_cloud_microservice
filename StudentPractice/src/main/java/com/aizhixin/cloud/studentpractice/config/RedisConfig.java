package com.aizhixin.cloud.studentpractice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@Configuration
public class RedisConfig {
    @Bean
    @Autowired
    public RedisTemplate <String, Map <String, Object>> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Map <String, Object>> template = new RedisTemplate <>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        return template;
    }
}
