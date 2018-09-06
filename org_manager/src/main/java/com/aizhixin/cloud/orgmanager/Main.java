package com.aizhixin.cloud.orgmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by zhen.pan on 2017/3/30.
 */
@SpringCloudApplication
@EnableAsync
@EnableScheduling
@EnableFeignClients
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
