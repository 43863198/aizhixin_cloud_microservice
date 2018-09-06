package com.zhizixin.cloud.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 我们项目中采用kafka作为数据总线(databus)
 * kafka依赖zookeeper，暂时采用单节点的方式
 * 进入安装目录：启动ZK，./bin/zookeeper-server-start.sh config/zookeeper.properties
 * 启动kafka，./bin/kafka-server-start.sh config/server.properties
 * 服务发现服务端
 * Created by zhen.pan on 2017/3/27.
 */
@SpringBootApplication
@EnableEurekaServer
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
