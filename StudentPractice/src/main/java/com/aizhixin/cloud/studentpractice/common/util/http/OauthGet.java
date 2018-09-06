package com.aizhixin.cloud.studentpractice.common.util.http;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class OauthGet implements ResponseUtil {

	@SuppressWarnings("finally")
	public HttpResponse get(String host, String url, String authorization)
			throws IOException {
		HttpResponse response = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			// 用get方法发送http请求
			HttpGet get = new HttpGet(host + url);
			System.out.println("执行get请求:...." + get.getURI());
			get.setHeader("Content-Type", HttpContants.APPLICATION_JSON);
			get.setHeader("Encoding", "UTF-8");
			get.setHeader("Authorization", authorization);
			get.setHeader("clientType", "diandian_client");
			// 发送get请求
			try (CloseableHttpResponse chr = httpClient.execute(get)) {
				response = getHttpResponse(chr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return response;
		}
	}

	public HttpResponse get1(String url, String param, String authorization)
			throws IOException {
		HttpResponse response = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			// 用get方法发送http请求
			HttpGet get = new HttpGet(url + param);
			System.out.println("执行get请求:...." + get.getURI());
			get.setHeader("Content-Type", HttpContants.APPLICATION_JSON);
			get.setHeader("Encoding", "UTF-8");
			get.setHeader("Authorization", authorization);
			get.setHeader("clientType", "diandian_client");

			// 发送get请求
			try (CloseableHttpResponse chr = httpClient.execute(get)) {
				response = getHttpResponse(chr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return response;
		}
	}

	// public static void main(String[] args) {
	// OauthGet get = new OauthGet();
	// String host = "http://172.16.40.188:8080";
	// String url = "/api/phone/v1/teacher/personCenter/get";
	// String authorization ="Bearer 2a2764c8-b4ce-488e-9391-68be10f50fb3";
	// try {
	// System.out.print(get.get(host, url, authorization).getResponseBody());
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
