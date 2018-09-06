package com.aizhixin.test.task;

import com.aizhixin.test.example.AuthToken;
import com.aizhixin.test.json.JsonUtil;
import com.aizhixin.test.rest.RestUtil;

public class Login {
    private String loginUrl = "http://dledutest.aizhixin.com/zhixin_api/oauth/token";
    private String username;
    private String password;
    private RestUtil restUtil;

    public Login (RestUtil restUtil, String loginUrl, String username, String password) {
        this.restUtil = restUtil;
        this.loginUrl = loginUrl;
        this.username = username;
        this.password = password;
    }

    public String login () {
        return restUtil.oauthLogin(loginUrl, username, password);
    }

    public static void main(String[] args) {
        Login test = new Login(new RestUtil(), "http://dledutest.aizhixin.com/zhixin_api/oauth/token", "lmcs1001", "1234567");
        String json = test.login();
        AuthToken token = JsonUtil.decode(json, AuthToken.class);
        System.out.println(token.toString());
    }
}
