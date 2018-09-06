package com.aizhixin.cloud.mobile;


import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringCloudApplication
@EnableFeignClients
@EnableCaching
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}