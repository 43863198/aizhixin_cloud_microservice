package com.aizhixin.test.task;

import com.aizhixin.test.rest.RestUtil;

public class ZookeeperLockTask {
    private RestUtil restUtil;
    private String url;

    public ZookeeperLockTask(RestUtil restUtil, String url) {
        this.restUtil = restUtil;
        this.url = url;
    }

    public String execute () {
        return restUtil.get(url, null);
    }

    public static void main(String[] args) {
        ZookeeperLockTask test = new ZookeeperLockTask(new RestUtil(), "http://gateway.aizhixin.com/rollcall/v1/manual/get/lock");
        for (int i = 0; i < 10000; i++) {
            String json = test.execute();
            System.out.println(json);
        }
    }
}
