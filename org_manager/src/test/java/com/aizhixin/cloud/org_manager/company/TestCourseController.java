package com.aizhixin.cloud.org_manager.company;

import com.aizhixin.cloud.rest.RestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * 学院API测试
 * Created by zhen.pan on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCourseController extends RestBase {

    private String url = "http://172.16.23.120:3333/org-manager";//通过API Gateway调用
//    private String url = "http://localhost:8080";//直接调用
    @Before
    public void init() {
        super.init();
    }
    @Test
    public void add() throws IOException {
        postBody(url + "/v1/course/add", "{\"orgId\":214,\"name\":\"大学英语一\",\"userId\":136908}");
    }
    @Test
    public void update() throws IOException {
        putBody(url + "/v1/course/update", "{\"id\":1,\"name\":\"大学英语二\",\"userId\":136908,\"orgId\":214}");
    }
    @Test
    public void list() {
        get(url + "/v1/course/list?orgId=214");
    }
    @Test
    public void get() {
        get(url + "/v1/course/get/1");
    }
    @Test
    public void droplist() {
        get(url + "/v1/course/droplist?orgId=214");
    }
    @Test
    public void delete() {
        delete(url + "/v1/course/delete/1?userId=136908");
    }
}
