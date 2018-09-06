package com.aizhixin.test.task;

import com.aizhixin.test.example.UserInfo;
import com.aizhixin.test.json.JsonUtil;
import com.aizhixin.test.rest.RestUtil;
import lombok.Getter;
import org.springframework.util.StringUtils;

public class UserInfoTask {
    private RestUtil restUtil;
    private String url;
    @Getter private String authorization;

    public UserInfoTask(RestUtil restUtil, String url, String authorization) {
        this.restUtil = restUtil;
        this.url = url;
        this.authorization = authorization;
    }

    public String execute () {
        return restUtil.get(url, authorization);
    }

    public static void main(String[] args) {
        UserInfoTask test = new UserInfoTask(new RestUtil(), "http://dledudev.aizhixin.com/zhixin_api/api/account/infoplus", "Bearer 57634d03-7cd0-4532-b624-829e4558b27f");
        String json = test.execute();
        if (!StringUtils.isEmpty(json)) {
            UserInfo userInfo = JsonUtil.decode(json, UserInfo.class);
            System.out.println(userInfo);
        }
    }
}
