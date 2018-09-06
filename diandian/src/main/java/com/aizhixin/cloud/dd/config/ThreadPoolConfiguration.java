package com.aizhixin.cloud.dd.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Configurable
@ConfigurationProperties(prefix = "myThreadPool")
@Data
public class ThreadPoolConfiguration {
    @Bean
    public Executor threadPool1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("my-thread-pool-01");
        // 当线程池已满,且等待队列也满了的时候,转为主线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
