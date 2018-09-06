package com.aizhixin.cloud.studentpractice.common.util.http;

public interface HttpContants {
	public static String APPLICATION_JSON = "application/json;charset=utf-8";

	public static String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

	public static String APPLICATION_FORMURL = "application/x-www-form-urlencoded";

	// 服务器地址
	public static String SERVICE_URL = "http://127.0.0.1:8080";// "http://172.16.13.37:8080";//
																// "http://172.16.23.58:8083/dledu";//
	// oauth地址
	public static String AUTH_URL = SERVICE_URL + "/oauth/token";
	// REST API地址
	public static String REST_API_URL = SERVICE_URL + "/api/web/v1";

	public static String SECRET = "dleduApp" + ':' + "mySecretOAuthSecret";

	public static String DEFAULT_USER_EMAIL = "admin@localhost";

	public static String DEFAULT_USER_PASSWORD = "admin";
}
