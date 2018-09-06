package com.aizhixin.cloud.org_manager.company;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.aizhixin.cloud.orgmanager.Main;
import com.aizhixin.cloud.orgmanager.common.async.AsyncTaskBase;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.rest.RestBase;

/**
 * 测试样例
 * 班级API测试
 * Created by zhen.pan on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Main.class)
public class TestClassesController extends RestBase {
//    private String url = "http://172.16.23.120:3333/org-manager";//通过API Gateway调用
    private String url = "http://172.16.40.188:8080";//直接调用
    @Before
    public void init() {
        super.init();
    }
    
//    @Test
//   	public void msg() throws UnsupportedEncodingException {
//           RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
//           redisTemplate.setConnectionFactory(redisConnectionFactory);
//           redisTemplate.setValueSerializer(new StringRedisSerializer());
//           redisTokenStore.setRedisTemplate(redisTemplate);
//           redisTokenStore.sendMessage("addClassMessage", "123");
//   	}
    @Test
    public void add() throws IOException {
        postBody(url + "/v1/classes/add", "{\"professionalId\":13,\"name\":\"中文班级23\",\"userId\":136908}");
    }
    @Test
    public void update() throws IOException {
        putBody(url + "/v1/classes/update", "{\"id\":1,\"name\":\"测试班级55\",\"userId\":136908,\"professionalId\":13}");
    }
    @Test
    public void list() {
        get(url + "/v1/classes/list?orgId=214");
    }
    @Test
    public void get() {
        get(url + "/v1/classes/get/1");
    }
    @Test
    public void droplist() {
        get(url + "/v1/classes/droplist?collegeId=4");
    }
    @Test
    public void delete() {
        delete(url + "/v1/classes/delete/1?userId=136908");
    }
}
