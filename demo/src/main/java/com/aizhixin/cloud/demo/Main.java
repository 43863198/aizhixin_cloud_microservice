package com.aizhixin.cloud.demo;

import com.aizhixin.cloud.demo.redis.RedisTokenStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@RestController
//@SpringCloudApplication
@SpringBootApplication
public class Main {
    private java.util.Random random = new Random();
    private AtomicLong automicLong = new AtomicLong(0);
    @Autowired
    private RedisTokenStoreService redisTokenStoreService;
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    @RequestMapping(value = "/counter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public long getCounter() {
        return automicLong.incrementAndGet();
    }

    @RequestMapping(value = "/writerinredis", method = RequestMethod.GET)
    public String write() {
        String key = "pzkey_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
        String value = "pzvalue_" + random.nextLong();
        redisTokenStoreService.storeString(key, value);
        return key;
    }

    @RequestMapping(value = "/readinredis", method = RequestMethod.GET)
    public String read(@RequestParam(value = "key") String key) {
        return redisTokenStoreService.readString(key);
    }
}
