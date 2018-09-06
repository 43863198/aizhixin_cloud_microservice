package com.aizhixin.cloud.dd;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

public class TestGetAccessToken {

    // 本机
    // public static String AIZHIXINURL = "http://dledudev.aizhixin.com/zhixin_api";
    // public static String DIANDIANURL = "http://localhost:8080";

    // 开发
    //   public static String AIZHIXINURL = "http://dledudev.aizhixin.com/zhixin_api";
//    public static String DIANDIANURL = "http://dddev.aizhixin.com/diandian_api";

    // 测试
    public static String AIZHIXINURL = "http://dledutest.aizhixin.com/zhixin_api";
    // public static String DIANDIANURL = "http://ddtest.aizhixin.com/diandian_api";

    // 生产
    // public static String AIZHIXINURL = "http://dledu.aizhixin.com/zhixin_api";
    // public static String DIANDIANURL = "http://dd.aizhixin.com/diandian_api";

    public static void main(String[] args) {
        TestGetAccessToken TestGetAccessToken = new TestGetAccessToken();
        TestGetAccessToken.getLoginToken("sjlb2016001", "1234567");
    }

    // 获取token
    public static String getLoginToken(String userName, String password) {
        String accessToken = "";
        try {
            JSONObject authResponseMap = sendLoginRequest(userName, password);
            if (authResponseMap.has("access_token")) {
                accessToken = "Bearer " + (String) authResponseMap.get("access_token");
                System.out.println("登录环境为:" + AIZHIXINURL + "\n用户名：" + userName + ",密码:" + password + ",\n获取token:"
                        + accessToken);
            } else {
                System.out.println("is null");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;

    }

    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    private static JSONObject sendLoginRequest(String login, String password) throws HttpException, IOException, JSONException {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(AIZHIXINURL + "/oauth/token");
        post.setRequestHeader("Content-Type", APPLICATION_FORMURL);
        post.setRequestHeader("Encoding", "UTF-8");
        post.setRequestHeader("Accept", APPLICATION_JSON);
        post.setRequestHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(SECRET.getBytes()));
        String requestBody = "username=" + login + "&password=" + password
                + "&grant_type=password&scope=read%20write&client_secret=mySecretOAuthSecret&client_id=dleduApp";
        // System.out.println(requestBody);
        post.setRequestBody(requestBody);
        client.executeMethod(post);
        // System.out.println(post.getStatusCode());
        String s = new String(post.getResponseBody());
        JSONObject jsonObject = new JSONObject(s);
        return jsonObject;
    }

    @SuppressWarnings("rawtypes")
    private static Map toMap(String jsonString) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        Map<String, String> result = new HashMap<String, String>();
        Iterator iterator = jsonObject.keys();
        String key = null;
        String value = null;

        while (iterator.hasNext()) {
            key = (String) iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);
        }
        return result;

    }

    public static String APPLICATION_JSON = "application/json;charset=UTF-8";
    public static String ACCEPT = "application/json, text/plain, */*";
    public static String APPLICATION_FORMURL = "application/x-www-form-urlencoded";
    public static String SECRET = "dleduApp" + ':' + "mySecretOAuthSecret";


}
