package com.aizhixin.cloud;

import com.aizhixin.cloud.orgmanager.Main;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 集成测试入口 Created by zhen.pan on 2017/4/13.
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest()
//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
//@SpringApplicationConfiguration(classes = Main.class)
//public class MainTest {
//    @Autowired
//    private RedisConnectionFactory redisConnectionFactory;
//
//    @Test
//    public void testAddTeachingclass() {
//        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
//        redisTokenStore.setPrefix("org_api:add");
//        redisTokenStore.storeTeachingclassId("123456789");
//
//        redisTokenStore.pushAddTeachingclassEvent();
//    }
//}
