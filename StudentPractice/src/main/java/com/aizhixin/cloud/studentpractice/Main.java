package com.aizhixin.cloud.studentpractice;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;


@SpringCloudApplication
@EnableFeignClients
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
