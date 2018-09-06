package com.aizhixin.cloud.ew.common.util;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;


public  class  SendHttp {

    private static String APPLICATION_JSON = "application/json;charset=utf-8";
    private static String APPLICATION_FORMURL = "application/x-www-form-urlencoded";
    private static String SECRET = "dleduApp" + ':' + "mySecretOAuthSecret";

    @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	public static Map sendAuthRequest(String login, String password, String url) throws HttpException, IOException, JSONException {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(url);
        post.setRequestHeader("Content-Type", APPLICATION_FORMURL);
        post.setRequestHeader("Encoding", "UTF-8");
        post.setRequestHeader("Accept", APPLICATION_JSON);
        post.setRequestHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(SECRET.getBytes()));
        String requestBody = "username=" + login + "&password=" + password + "&grant_type=password&scope=read%20write&client_secret=mySecretOAuthSecret&client_id=dleduApp";
        System.out.println(requestBody);
        post.setRequestBody(requestBody);

        client.executeMethod(post);
        System.out.println(post.getStatusCode());
        String s = new String(post.getResponseBody());
        JSONObject jsonObject = new JSONObject(s);
        Map<String, String> authResponseMap = toMap(jsonObject.toString());

        Date date = new Date();
        date.setSeconds(date.getSeconds() + Integer.parseInt((String) authResponseMap.get("expires_in")));
        return authResponseMap;
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
}