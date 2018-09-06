package com.aizhixin.cloud.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * Created by zhen.pan on 2017/5/15.
 */
@SpringBootApplication
@EnableHystrixDashboard
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
