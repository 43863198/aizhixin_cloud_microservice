package com.aizhixin.cloud.dd.rollcallv2.v1.controller;

import com.aizhixin.cloud.dd.rollcall.dto.SignInDTO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aizhixin.cloud.dd.login.Login;
import com.aizhixin.cloud.dd.login.rest.RestUtil;
import com.aizhixin.cloud.dd.url.DDURL;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * StudentPhoneResourceV5 Tester.
 *
 * @author limh
 * @since
 * @version 1.0
 */
public class StudentPhoneResourceV5Test {

    private RestUtil restUtil = null;

    private String studentToken = "";

    @Before
    public void before() throws Exception {
        restUtil = new RestUtil();
        studentToken = Login.getToken(restUtil, "dmcs03", "123456");
        System.out.println(studentToken);
    }

    @After
    public void after() throws Exception {
        restUtil = null;
    }

    /**
     *
     * Method: getStudentCourseList(@ApiParam(value = "token信息:</b><br/>
     * 需要在http header添加登录过程中获取到的token值,必填<br/>
     * 示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken, @ApiParam(value = "teachTime 时间") @RequestParam(value = "teachTime", required = false) String
     * teachTime, @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset, @ApiParam(value = "limit 每页的限制数目") @RequestParam(value =
     * "limit", required = false) Integer limit)
     *
     */
    @Test
    public void testGetStudentCourseList() throws Exception {
        System.out.println(studentToken);

        String studentCourseList = restUtil.get(DDURL.STUDENTPHONERESOURCEV5_GETSTUDENTCOURSELIST, studentToken);
        System.out.println(studentCourseList);
    }

    /**
     *
     * Method: getStudentSignCourseV2(@RequestHeader("Authorization") String accessToken)
     *
     */
    @Test
    public void testGetStudentSignCourseV2() throws Exception {
        String signInCourse = restUtil.get(DDURL.STUDENTPHONERESOURCEV5_GETSTUDENTSIGNCOURSEV2, studentToken);
        System.out.println(signInCourse);
    }

    /**
     *
     * Method: signIn(@ApiParam(value = "点名信息") @RequestBody SignInDTO signInDTO, @RequestHeader("Authorization") String accessToken)
     *
     */
    @Test
    public void testSignIn() throws Exception {
        String signInCourse = restUtil.get(DDURL.STUDENTPHONERESOURCEV5_GETSTUDENTSIGNCOURSEV2, studentToken);
        JSONArray jsonArray = JSONArray.fromObject(signInCourse);
        if (jsonArray == null || jsonArray.length() == 0) {
            assert false;
        }
        for (int i = 0, len = jsonArray.length(); i < len; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            SignInDTO signInDTO = new SignInDTO();
            signInDTO.setScheduleId(jsonObject.getLong("id"));
            signInDTO.setRollCallType(jsonObject.getString("rollcallType"));
            // signInDTO.setAuthCode(jsonObject.getString(""));
            signInDTO.setDeviceToken("123456");
            signInDTO.setGps("13,465");
            signInDTO.setGpsDetail("123");
            signInDTO.setGpsType("wifi");
            String s = restUtil.postBody(DDURL.STUDENTPHONERESOURCEV5_SIGNIN, JSONObject.fromObject(signInDTO).toString(), studentToken);
            System.out.println(s);
        }
        getTQ();
    }

    public void getTQ() {
        ResponseEntity<String> stringResponseEntity = restUtil.httpRequest("http://www.sojson.com/open/api/weather/xml.shtml?city=北京", null, HttpMethod.GET);
    }
}
