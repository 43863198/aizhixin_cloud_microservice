package com.aizhixin.cloud.sqzd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SuqianZedaMain {
    public static void main(String[] args) {
        SpringApplication.run(SuqianZedaMain.class, args);
    }
}

