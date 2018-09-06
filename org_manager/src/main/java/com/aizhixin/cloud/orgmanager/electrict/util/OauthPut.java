package com.aizhixin.cloud.orgmanager.electrict.util;

import com.aizhixin.cloud.orgmanager.electrict.constant.HttpContants;
import com.aizhixin.cloud.orgmanager.electrict.util.HttpResponse;
import com.aizhixin.cloud.orgmanager.electrict.util.ResponseUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author ShuHao Wong
 * @date 2015/12/4
 */
public class OauthPut implements ResponseUtil {

    @SuppressWarnings("finally")
	public HttpResponse put(String url, List<NameValuePair> params, String authorization) {
        HttpResponse httpResponse = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut put = new HttpPut(url);

            put.setHeader("Content-Type", HttpContants.APPLICATION_JSON);
            put.setHeader("Encoding", "UTF-8");
            put.setHeader("Authorization", authorization);
            put.setHeader("clientType", "diandian_client");
            //url格式编码
            if(params!=null) {
            	UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
            	put.setEntity(uefEntity);
            }
            System.out.println("PUT 请求...." + put.getURI());
            //执行请求
            try (CloseableHttpResponse chr = httpClient.execute(put)) {
                httpResponse = getHttpResponse(chr);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return httpResponse;
        }
    }

}
