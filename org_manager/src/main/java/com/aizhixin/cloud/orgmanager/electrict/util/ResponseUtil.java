package com.aizhixin.cloud.orgmanager.electrict.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author ShuHao Wong
 * @date 2015/12/4
 */
public interface ResponseUtil {
    default HttpResponse getHttpResponse(CloseableHttpResponse httpResponse) throws IOException {
        HttpResponse res = new HttpResponse();

        if (httpResponse != null) {
            res.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            res.setResponseBody(EntityUtils.toString(httpResponse.getEntity()));
        }
        return res;
    }
}
