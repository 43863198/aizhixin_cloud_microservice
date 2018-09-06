package com.aizhixin.cloud.org_manager.classschedule;

import com.aizhixin.cloud.rest.RestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * 排课API测试
 * Created by zhen.pan on 2017/5/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SchoolTimeTableControlerTest extends RestBase {
        private String url = "http://172.16.23.120:3333/org-manager";//通过API Gateway调用
//    private String url = "http://172.16.40.188:8080";//直接调用
//    private String url = "http://localhost:8080";//本地

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void add() throws IOException {
        postBody(url + "/v1/schooltimetable/add", "{\"teachingClassId\": 5,\"userId\": 1236,\"weekSchoolTimeTableDomain\": [{\"courseTimePeriod\": [{\"classroom\": \"4#楼5区201\",\"color\": \"#231245\",\"endWeekId\": 1,\"periodId\": 3,\"periodNum\": 2,\"remark\": \"备注\",\"singleOrDouble\": 10,\"startWeekId\": 3}],\"dayOfWeek\": 0}]}");
    }

    @Test
    public void update() throws IOException {
        putBody(url + "/v1/schooltimetable/update", "{\"teachingClassId\": 5,\"userId\": 1236,\"weekSchoolTimeTableDomain\": [{\"courseTimePeriod\": [{\"classroom\": \"4#楼5区201\",\"color\": \"#231245\",\"endWeekId\": 1,\"periodId\": 3,\"periodNum\": 2,\"remark\": \"备注\",\"singleOrDouble\": 10,\"startWeekId\": 3}],\"dayOfWeek\": 0}]}");
    }

    @Test
    public void get() throws IOException {
        get(url + "/v1/schooltimetable/get/5");
    }

    @Test
    public void delete() throws IOException {
        delete(url + "/v1/schooltimetable/delete/5?userId=123");
    }
}
