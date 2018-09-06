package com.aizhixin.cloud.dd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.aizhixin.cloud.dd.common.utils.SpringContextUtil;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by zhen.pan on 2017/4/19.
 */
@EnableAutoConfiguration
@SpringCloudApplication
@EnableFeignClients
@EnableCaching
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
public class Main {
    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
