package com.zhizixin.cloud.config;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 配置服务服务端
 * Created by zhen.pan on 2017/3/28.
 */
@EnableConfigServer
@SpringCloudApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
