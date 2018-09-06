package com.aizhixin.cloud.io;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * Created by zhen.pan on 2017/6/10.
 */
@EnableFeignClients
@SpringCloudApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
