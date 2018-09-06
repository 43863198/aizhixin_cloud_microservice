package com.aizhixin.cloud.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * Created by zhen.pan on 2017/5/15.
 */
@SpringBootApplication
@EnableTurbine
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
