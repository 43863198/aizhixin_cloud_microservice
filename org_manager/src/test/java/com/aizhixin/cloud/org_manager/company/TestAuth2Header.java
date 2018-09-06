package com.aizhixin.cloud.org_manager.company;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhen.pan on 2017/4/21.
 */
public class TestAuth2Header {

    public void testHeader() {
        RestTemplate rest;
        HttpHeaders headers;
        List<MediaType> mt;
        rest = new RestTemplate();
        headers = new HttpHeaders();
        mt = new ArrayList<>();
        mt.add(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
        headers.add("Encoding", "UTF-8");
        headers.set(HttpHeaders.AUTHORIZATION, "test-1243uwAXBSH78wrwre870708");
        headers.setAccept(mt);
        HttpEntity<String> entity = new HttpEntity<>( headers);
        ResponseEntity<String> response = rest.exchange("http://172.16.23.107:3333/api/org-api/v1/test/add?x=2&y=10", HttpMethod.POST,  entity,  String.class);
        System.out.println(response.getBody());
    }

    public static void main(String[] args) {
        TestAuth2Header t = new TestAuth2Header();
        t.testHeader();
    }
}

