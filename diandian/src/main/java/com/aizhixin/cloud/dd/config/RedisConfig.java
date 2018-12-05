package com.aizhixin.cloud.dd.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
@Configuration
public class RedisConfig {

    @Autowired
    private Environment env;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setHostName(env.getProperty("spring.redis.host",
                "172.16.23.31"));
        jedisConnectionFactory.setPort(Integer.parseInt(env.getProperty(
                "spring.redis.port", "6379")));
        jedisConnectionFactory.setDatabase(Integer.parseInt(env.getProperty("spring.redis.database", "9")));
//        jedisConnectionFactory.setPassword(env.getProperty("spring.redis.password", "123456"));
        jedisConnectionFactory.setUsePool(Boolean.TRUE);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate <String, Map <Long, RollCall>> redisTemplate(
            RedisConnectionFactory factory) {
        RedisTemplate <String, Map <Long, RollCall>> template = new RedisTemplate <String, Map <Long, RollCall>>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
    }
}
