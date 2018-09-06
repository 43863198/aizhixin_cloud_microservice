package com.aizhixin.cloud.dd.login;

import com.aizhixin.cloud.dd.login.rest.AuthToken;
import com.aizhixin.cloud.dd.login.rest.JsonUtil;
import com.aizhixin.cloud.dd.login.rest.RestUtil;
import com.aizhixin.cloud.dd.url.URLUtils;

/**
 * Created by LIMH on 2017/10/19.
 */
public class Login {
    private String loginUrl = URLUtils.getAzxUrl() + "/oauth/token";
    private String username;
    private String password;
    private RestUtil restUtil;

    public Login(RestUtil restUtil, String username, String password) {
        this.username = username;
        this.password = password;
        this.restUtil = restUtil;
    }

    public Login(RestUtil restUtil, String loginUrl, String username, String password) {
        this.restUtil = restUtil;
        this.loginUrl = loginUrl;
        this.username = username;
        this.password = password;
    }

    public String login() {
        return restUtil.oauthLogin(loginUrl, username, password);
    }

    public static String getToken(RestUtil restUtil, String username, String password) {
        Login test = new Login(restUtil, username, password);
        String json = test.login();
        AuthToken token = JsonUtil.decode(json, AuthToken.class);
        return token.getToken();
    }

    public static void main(String[] args) {
        Login test = new Login(new RestUtil(), URLUtils.getAzxUrl() + "/oauth/token", "kjkf201", "1234567");
        String json = test.login();
        AuthToken token = JsonUtil.decode(json, AuthToken.class);
        System.out.println(token.toString());
        System.out.println(token.getToken());
    }
}
