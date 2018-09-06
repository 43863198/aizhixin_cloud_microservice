package com.aizhixin.cloud.rest;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Rest请求的基础类
 * Created by zhen.pan on 2017/4/13.
 */
public abstract class RestBase {
    private RestTemplate rest;
    private HttpHeaders headers;
    private List<MediaType> mt;

    /**
     * 如果请求的URL有权限限制，需要使用这个方法来初始化
     * 包含Authorization请求头
     * @param authorization
     */
    public void init(String authorization) {
        rest = new RestTemplate();
        headers = new HttpHeaders();
        mt = new ArrayList<>();
        mt.add(MediaType.APPLICATION_JSON_UTF8);
//        headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
//        headers.add("Encoding", "UTF-8");
        if (null != authorization) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
        headers.setAccept(mt);
    }

    /**
     * 果请求的URL没有权限限制，使用这个方法来初始化
     * 没有Authorization请求头
     */
    public void init() {
        init(null);
    }

    /**
     * 发起http 请求调用
     * 同时打印请求的各种信息
     * @param url       请求URL
     * @param entity    请求实体
     * @param method    请求方法
     */
    private String httpRequest(String url, HttpEntity<?> entity, HttpMethod method) {
        try {
            System.out.println("start invoke " + method.toString() + " request url:" + url);
            ResponseEntity<String> response = rest.exchange(url, method,  entity,  String.class);
            System.out.println("HTTP Code: " + response.getStatusCode().value());
            System.out.println("Response body:-----------------------------------------------------");
            System.out.println(response.getBody());
            return response.getBody();
        } catch (Exception e) {
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException c = (HttpClientErrorException)e;
                System.out.println("-------------" + c.getResponseBodyAsString() + "\t http code:" + c.getStatusCode());
                c.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
        System.out.println("-----------------------------------------------------END.");
        return null;
    }

    /**
     * auth2 access token的获取
     * @param tokenUrl      OAuth2地址
     * @param username      用户名
     * @param password      密码
     */
    public void login(String tokenUrl, String username, String password) {
        init("Basic " + Base64.getEncoder().encodeToString("dleduApp:mySecretOAuthSecret".getBytes()));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        map.add("grant_type", "password");
        map.add("scope", "read write");
        map.add("client_secret", "mySecretOAuthSecret");
        map.add("client_id", "dleduApp");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        httpRequest(tokenUrl, entity, HttpMethod.POST);
    }

    /**
     * 发送GET的HTTP请求，请求参数作为url的参数
     * @param url       请求地址
     */
    public void get(String url) {
        HttpEntity<String> entity = new HttpEntity<>( headers);
        httpRequest(url, entity, HttpMethod.GET);
    }

    /**
     * 将请求参数通过url参数的方式post出去
     * @param url       请求地址
     */
    public void post(String url) {
        HttpEntity<String> entity = new HttpEntity<>( headers);
        httpRequest(url, entity, HttpMethod.POST);
    }

    /**
     * 将json内容作为http请求的body内容post出去
     * @param url       请求地址
     * @param json      post内容
     */
    public String postBody(String url, String json) {
        try {
            HttpEntity<byte[]> entity = new HttpEntity<>(json.getBytes("UTF-8"), headers);
            return httpRequest(url, entity, HttpMethod.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将请求参数通过url参数的方式put出去
     * @param url       请求地址
     */
    public void put(String url) {
        HttpEntity<String> entity = new HttpEntity<>( headers);
        httpRequest(url, entity, HttpMethod.PUT);
    }

    /**
     * 将json内容作为http请求的body内容put出去
     * @param url       请求地址
     * @param json      post内容
     */
    public String putBody(String url, String json) {
        try {
            HttpEntity<byte[]> entity = new HttpEntity<>(json.getBytes("UTF-8"), headers);
            return httpRequest(url, entity, HttpMethod.PUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将请求参数通过url参数的方式delete出去
     * @param url       请求地址
     */
    public void delete(String url) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        httpRequest(url, entity, HttpMethod.DELETE);
    }
}