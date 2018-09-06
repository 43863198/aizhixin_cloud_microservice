package com.aizhixin.cloud.dd.login.rest;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * Created by LIMH on 2017/10/19.
 */
public class AsynRestUtil {

    private RestTemplate rest = new RestTemplate();

    private AsyncRestTemplate asyncRest = new AsyncRestTemplate();

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

    private HttpHeaders getAuthorizationHeaders(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        List <MediaType> mt = new ArrayList <>();
        mt.add(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
        if (null != authorization) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
        headers.setAccept(mt);
        return headers;
    }

    private HttpHeaders getLoginAuthorizationHeaders(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        List <MediaType> mt = new ArrayList <>();
        mt.add(MediaType.APPLICATION_JSON_UTF8);
        if (null != authorization) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
        headers.setAccept(mt);
        return headers;
    }

    public String oauthLogin(String url, String username, String password) {
        return httpRequest(url, getLoginEntiry(getLoginAuthorizationHeaders("Basic " + Base64.getEncoder().encodeToString("dleduApp:mySecretOAuthSecret".getBytes())), username, password), HttpMethod.POST).getBody();
    }

    public ResponseEntity<String> httpRequest(String url, HttpEntity<?> entity, HttpMethod method) {
        return rest.exchange(url, method, entity, String.class);
    }

    public String postBody(String url, String json, String authorization) {
        HttpEntity<byte[]> entity = new HttpEntity<>(json.getBytes(Charset.forName("UTF-8")), getAuthorizationHeaders(authorization));
        return httpRequest(url, entity, HttpMethod.POST).getBody();
    }

    public String putBody(String url, String json, String authorization) {
        HttpEntity<byte[]> entity = new HttpEntity<>(json.getBytes(Charset.forName("UTF-8")), getAuthorizationHeaders(authorization));
        return httpRequest(url, entity, HttpMethod.PUT).getBody();
    }

    public String get(String url, String authorization) {
        HttpEntity<String> entity = new HttpEntity<>(getAuthorizationHeaders(authorization));
        return httpRequest(url, entity, HttpMethod.GET).getBody();
    }

    public ListenableFuture<ResponseEntity<String>> getAsyn(String url, String authorization) {
        HttpEntity<String> entity = new HttpEntity<>(getAuthorizationHeaders(authorization));
        return httpRequestAsyn(url, entity, HttpMethod.GET);
    }

    public ListenableFuture<ResponseEntity<String>> httpRequestAsyn(String url, HttpEntity<?> entity, HttpMethod method) {
        ListenableFuture<ResponseEntity<String>> exchange = asyncRest.exchange(url, method, entity, String.class);
        return exchange;
    }
}
