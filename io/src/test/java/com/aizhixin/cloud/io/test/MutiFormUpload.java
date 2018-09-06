package com.aizhixin.cloud.io.test;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;

/**
 * Created by zhen.pan on 2017/6/14.
 */
public class MutiFormUpload {
    private RestTemplate rest;
    private HttpHeaders headers;
    private List<MediaType> mt;
    public void init(String authorization) {
        rest = new RestTemplate();
        headers = new HttpHeaders();//文件名中文乱码未解决
//        mt = new ArrayList<>();
//        mt.add(MediaType.APPLICATION_JSON_UTF8);
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//        headers.add("Content-Type", "multipart/form-data;charset=UTF-8");
//        headers.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
//        headers.add("Encoding", "UTF-8");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Transfer-Encoding", "chunked");
//        headers.setAccept(mt);
    }

    public void uploadFile(String url, String filePath) {
        FileSystemResource resource = new FileSystemResource(new File(filePath));
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        param.add("appId", "appId");
        param.add("token", "token");
//        param.add("fileName", resource.getFilename());
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(param, headers);
        System.out.println(headers.toString());
        String string = rest.postForObject(url, entity, String.class);
    }
    public static void main(String[] args) {
        MutiFormUpload t = new MutiFormUpload();
        t.init(null);
        t.uploadFile("http://localhost:8080/v1/doc/upload", "d:/所有用户及激活用户统计.sql");
    }
}
