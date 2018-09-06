package com.aizhixin.cloud.ew.common.util;

/**
 * @author ShuHao Wong
 * @date 2015/12/4
 */
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
