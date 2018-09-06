package com.aizhixin.cloud.ew.common.util;

import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class OauthPostJson implements ResponseUtil {
    /**
     * 使用String作为POST参数,
     *
     * @param url
     * @param params          JSON字符串
     * @param authResponseMap
     * @throws HttpException
     * @throws IOException
     */
    @SuppressWarnings("finally")
	public HttpResponse post(String url, String params, String authResponseMap) throws HttpException, IOException {
        HttpResponse httpResponse = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            post.addHeader("Content-Type", HttpContants.APPLICATION_JSON);
            post.addHeader("Encoding", "UTF-8");
            post.addHeader("Authorization", authResponseMap);

            //url格式编码
            StringEntity myEntity = new StringEntity(params,"utf-8");
            post.setEntity(myEntity);
            System.out.println("POST 请求...." + post.getURI());
            //执行请求
            try(CloseableHttpResponse chr = httpClient.execute(post)) {
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



	@SuppressWarnings("finally")
	public HttpResponse post1(String url, String params ,String authResponseMap) {
		HttpResponse httpResponse = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            post.addHeader("Content-Type", HttpContants.APPLICATION_JSON);
            post.addHeader("Encoding", "UTF-8");
            post.addHeader("Authorization", authResponseMap);

            //url格式编码
            StringEntity myEntity = new StringEntity(params,"utf-8");
            post.setEntity(myEntity);
            System.out.println("POST 请求...." + post.getURI());
            //执行请求
            try(CloseableHttpResponse chr = httpClient.execute(post)) {
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

	@SuppressWarnings("finally")
	public HttpResponse post2(String url,  long userId ,String params ,String authResponseMap) {
		HttpResponse httpResponse = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            post.addHeader("Content-Type", HttpContants.APPLICATION_JSON);
            post.addHeader("Encoding", "UTF-8");
            post.addHeader("Authorization", authResponseMap);

            //url格式编码
           
            StringEntity myEntity = new StringEntity(params,"utf-8");
            post.setEntity(myEntity);
            System.out.println("POST 请求...." + post.getURI());
            //执行请求
            try(CloseableHttpResponse chr = httpClient.execute(post)) {
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
