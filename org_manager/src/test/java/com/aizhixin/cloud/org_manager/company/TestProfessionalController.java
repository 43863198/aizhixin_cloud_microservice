package com.aizhixin.cloud.org_manager.company;

import com.aizhixin.cloud.rest.RestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * 专业API测试
 * Created by zhen.pan on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TestProfessionalController extends RestBase {

//    private String url = "http://172.16.23.120:3333/org-manager";//通过API Gateway调用
    private String url = "http://localhost:8080";//直接调用
    @Before
    public void init() {
        super.init();
    }
    @Test
    public void add() throws IOException {
        postBody(url + "/v1/professionnal/add", "{\"collegeId\":214,\"name\":\"计算机应用\",\"userId\":136908}");
    }
    @Test
    public void update() throws IOException {
        putBody(url + "/v1/professionnal/update", "{\"id\":29,\"name\":\"计算机应用基础\",\"userId\":136908,\"orgId\":214}");
    }
    @Test
    public void list() {
        get(url + "/v1/professionnal/list?collegeId=214");
    }
    @Test
    public void get() {
        get(url + "/v1/professionnal/get/1");
    }
    @Test
    public void droplist() {
        get(url + "/v1/professionnal/droplist?collegeId=214");
    }
    @Test
    public void delete() {
        delete(url + "/v1/professionnal/delete/1?userId=136908");
    }
}
