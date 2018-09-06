package com.aizhixin.test.task;

import com.aizhixin.test.json.JsonUtil;
import com.aizhixin.test.rest.RestUtil;

public class SignName {
    private RestUtil restUtil;
    private String signUrl;
    private String authorization;
    private SignDomain signDomain;

    public SignName (RestUtil restUtil, String signUrl, String authorization, SignDomain signDomain) {
        this.restUtil = restUtil;
        this.signUrl = signUrl;
        this.authorization = authorization;
        this.signDomain = signDomain;
    }

    public void sign () {
        String json = JsonUtil.encode(signDomain);
        restUtil.postBody(signUrl, json, authorization);
    }

    public static void main(String[] args) {
        RestUtil restUtil = new RestUtil();
        String signUrl = "http://gatewaytest.aizhixin.com/diandian_api/api/phone/v1/student/signInNull";
        String authorization = "Bearer 7a7bac0d-1c48-4b02-b7a6-5ee6a1766a46";
        SignDomain signDomain = new SignDomain(2783, "automatic", "34.22842-108.872819");
        restUtil.postBody(signUrl, JsonUtil.encode(signDomain), authorization);
    }
}
