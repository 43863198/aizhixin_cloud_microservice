package com.aizhixin.cloud.org_manager.company;

import com.aizhixin.cloud.rest.RestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Created by 郑宁 on 2017/4/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSemesterController extends RestBase {

//    private String url = "http://172.16.23.120:3333/org-manager";//通过API Gateway调用
    private String url = "http://localhost:8080";//直接调用
    @Before
    public void init() {
        super.init();
    }
    @Test
    public void add() throws IOException {
        postBody(url + "/v1/semester/add", "{\"endDate\":\"2017-07-01\",\"orgId\":1,\"startDate\":\"2017-04-13\",\"name\":\"第三学期\",\"userId\":1}");
    }
    @Test
    public void update() throws IOException {
        putBody(url + "/v1/semester/update", "{\"endDate\": \"2017-07-13\",\"orgId\": 1,\"startDate\": \"2017-04-13\",\"id\": 1,\"name\": \"第3个学期\",\"userId\":1}");
    }
    @Test
    public void list() {
        get(url + "/v1/semester/list?orgId=1");
    }
    @Test
    public void droplist() {
        get(url + "/v1/semester/droplist?orgId=1");
    }
    @Test
    public void delete() {
        delete(url + "/v1/semester/delete/2?userId=1");
    }
}
