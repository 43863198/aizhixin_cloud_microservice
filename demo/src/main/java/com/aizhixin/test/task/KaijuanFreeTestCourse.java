package com.aizhixin.test.task;

import com.aizhixin.test.rest.RestUtil;

public class KaijuanFreeTestCourse {
    private RestUtil restUtil;
    private String url;
    private String authorization;

    public KaijuanFreeTestCourse (RestUtil restUtil, String url, String authorization) {
        this.restUtil = restUtil;
        this.url = url;
        this.authorization = authorization;
    }

    public void execute() {
        restUtil.get(url + "?pageSize=10&pageNumber=1", authorization);
    }
}
