package com.aizhixin.test.task;

import com.aizhixin.test.rest.RestUtil;
import org.springframework.util.StringUtils;

public class StudentCourseTeacherApi {
    private RestUtil restUtil;
    private String url;

    public StudentCourseTeacherApi(RestUtil restUtil, String url) {
        this.restUtil = restUtil;
        this.url = url;
    }

    public String execute () {
        return restUtil.putBody(url, "{\"courseIds\": [5024],\"semersterId\": 53,\"studentId\": 46633}", null);
    }

    public static void main(String[] args) {
        StudentCourseTeacherApi test = new StudentCourseTeacherApi(new RestUtil(), "http://gateway.aizhixin.com/org-manager/v1/teachingclassstudent/findteacherbystudentandcourses");
        String json = test.execute();
        if (!StringUtils.isEmpty(json)) {
//            UserInfo userInfo = JsonUtil.decode(json, UserInfo.class);
            System.out.println(json);
        }
    }
}
