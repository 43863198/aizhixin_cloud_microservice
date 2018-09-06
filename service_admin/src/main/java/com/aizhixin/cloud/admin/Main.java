package com.aizhixin.cloud.admin;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhen.pan on 2017/4/27.
 */
//@SpringCloudApplication
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableAdminServer
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}