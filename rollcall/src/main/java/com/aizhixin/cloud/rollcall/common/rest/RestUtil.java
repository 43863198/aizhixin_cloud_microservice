package com.aizhixin.cloud.rollcall.common.rest;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

/**
 * Created by zhen.pan on 2017/6/29.
 */
@Component
public class RestUtil {
    private RestTemplate rest = new RestTemplate();

    private HttpHeaders getAuthorizationHeaders(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mt = new ArrayList<>();
        mt.add(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
        headers.add("Encoding", "UTF-8");
        if (null != authorization) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
        headers.setAccept(mt);
        return headers;
    }

    private HttpEntity<MultiValueMap<String, String>> getLoginEntiry(HttpHeaders headers, String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        map.add("grant_type", "password");
        map.add("scope", "read write");
        map.add("client_secret", "mySecretOAuthSecret");
        map.add("client_id", "dleduApp");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        return entity;
    }

    public ResponseEntity<String> httpRequest(String url, HttpEntity<?> entity, HttpMethod method) {
        System.out.println("------------------url:" + url + "\t" + method.name());
        return  rest.exchange(url, method,  entity,  String.class);
    }

    public String oauthLogin(String url, String username, String password) {
        return httpRequest(url, getLoginEntiry(getAuthorizationHeaders("Basic " + Base64.getEncoder().encodeToString("dleduApp:mySecretOAuthSecret".getBytes())),username, password), HttpMethod.POST).getBody();
    }
    public String get(String url, String authorization) {
        HttpEntity<String> entity = new HttpEntity<>(getAuthorizationHeaders(authorization));
        return httpRequest(url, entity, HttpMethod.GET).getBody();
    }
    public String post(String url, String authorization) {
        HttpEntity<String> entity = new HttpEntity<>(getAuthorizationHeaders(authorization));
        return httpRequest(url, entity, HttpMethod.POST).getBody();
    }
    public String postBody(String url, String json, String authorization) {
        HttpEntity<byte[]> entity = new HttpEntity<>(json.getBytes(Charset.forName("UTF-8")), getAuthorizationHeaders(authorization));
        return httpRequest(url, entity, HttpMethod.POST).getBody();
    }
    public String postBody(String url, Set<Long> set, String authorization) {
        HttpEntity<Set<Long>> entity = new HttpEntity<>(set,getAuthorizationHeaders(authorization));
        return httpRequest(url, entity, HttpMethod.POST).getBody();
    }
    public String postStringListBody(String url, Set<String> set, String authorization) {
        HttpEntity<Set<String>> entity = new HttpEntity<>(set,getAuthorizationHeaders(authorization));
        return httpRequest(url, entity, HttpMethod.POST).getBody();
    }
    public String delete(String url, String authorization) {
        HttpEntity<String> entity = new HttpEntity<>(getAuthorizationHeaders(authorization));
        return httpRequest(url, entity, HttpMethod.DELETE).getBody();
    }
}
