package com.aizhixin.cloud.ew.common.service;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class OAuth2Get {

	public HttpContent get(String url ,String authorization) throws IOException {
		HttpContent response = null;
	 HttpGet get = new HttpGet(url);
   try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
       //用get方法发送http请求
      
       System.out.println("执行get请求:...."+get.getURI());
       get.setHeader("Content-Type", "application/json;charset=utf-8");
       get.setHeader("Encoding", "UTF-8");
       get.setHeader("Authorization", authorization);
       //发送get请求
       try(CloseableHttpResponse chr = httpClient.execute(get)) {
           response = getHttpResponse(chr);
       }
   } catch (Exception e) {
       e.printStackTrace();
   } finally{
  	 get.releaseConnection();
   }
   return response;
}
	
	public HttpContent get(String url ) throws IOException {
		HttpContent response = null;
	 HttpGet get = new HttpGet(url);
   try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
       //用get方法发送http请求      
       System.out.println("执行get请求:...."+get.getURI());
       get.setHeader("Content-Type", "application/json;charset=utf-8");
       get.setHeader("Encoding", "UTF-8");    
       //发送get请求
       try(CloseableHttpResponse chr = httpClient.execute(get)) {
           response = getHttpResponse(chr);
       }
   } catch (Exception e) {
       e.printStackTrace();
   } finally{
  	 get.releaseConnection();
   }
   return response;
}

public HttpContent getHttpResponse(CloseableHttpResponse httpResponse) throws IOException {
	HttpContent res = new HttpContent();

   if (httpResponse != null) {
       res.setStatusCode(httpResponse.getStatusLine().getStatusCode());
       res.setResponseBody(EntityUtils.toString(httpResponse.getEntity()));
   }
   return res;
}
}
