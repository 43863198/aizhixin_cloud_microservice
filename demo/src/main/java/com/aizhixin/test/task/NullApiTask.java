package com.aizhixin.test.task;

import com.aizhixin.test.json.JsonUtil;
import com.aizhixin.test.rest.RestUtil;

public class NullApiTask {
    private RestUtil restUtil;
    private String nullUrl;

    public NullApiTask(RestUtil restUtil, String nullUrl) {
        this.restUtil = restUtil;
        this.nullUrl = nullUrl;
    }

    public void restInvoke () {
//        restUtil.postBody(nullUrl, "{}", "Bearer b582b11f-b1ac-426a-9fc7-03cfec22cb27");
        restUtil.get(nullUrl, null);
    }

    public static void main(String[] args) {
        RestUtil restUtil = new RestUtil();
        String signUrl = "http://gatewaytest.aizhixin.com/diandian_api/api/phone/v1/student/signInNull";
        String authorization = "Bearer 7a7bac0d-1c48-4b02-b7a6-5ee6a1766a46";
        SignDomain signDomain = new SignDomain(2783, "automatic", "34.22842-108.872819");
        restUtil.postBody(signUrl, JsonUtil.encode(signDomain), authorization);
    }
}
