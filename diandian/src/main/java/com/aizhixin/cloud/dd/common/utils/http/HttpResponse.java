package com.aizhixin.cloud.dd.common.utils.http;

public class HttpResponse {

	private int statusCode;

	private String responseBody;

	/***/
	public int getStatusCode() {
		return statusCode;
	}

	/***/
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/***/
	public String getResponseBody() {
		return responseBody;
	}

	/***/
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
}
