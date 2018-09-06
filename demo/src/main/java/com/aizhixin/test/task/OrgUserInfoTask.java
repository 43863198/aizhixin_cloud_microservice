package com.aizhixin.test.task;

import com.aizhixin.test.rest.RestUtil;

public class OrgUserInfoTask {
    private RestUtil restUtil;
    private String url;
    private Long id;
//    @Getter private String authorization;

    public OrgUserInfoTask(RestUtil restUtil, String url, Long id) {
        this.restUtil = restUtil;
        this.url = url;
        this.id = id;
//        this.authorization = authorization;
    }

    public String execute () {
        return restUtil.get(url + "/" + id, null);
    }

//    public static void main(String[] args) {
//        OrgUserInfoTask test = new OrgUserInfoTask(new RestUtil(), "http://localhost:8080/v1/user/get/163837", null);
//        String json = test.execute();
//        if (!StringUtils.isEmpty(json)) {
//            System.out.println(json);
//        }
//    }
}
