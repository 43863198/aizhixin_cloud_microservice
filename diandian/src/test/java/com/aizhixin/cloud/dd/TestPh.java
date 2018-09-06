package com.aizhixin.cloud.dd;

import com.aizhixin.cloud.dd.common.utils.http.HttpResponse;
import com.aizhixin.cloud.dd.common.utils.http.OauthGet;
import net.sf.json.JSONObject;

import java.io.IOException;

public class TestPh {

    public static void main(String[] args) {
        TestGetAccessToken TestGetAccessToken = new TestGetAccessToken();
        getZXUserByAuthorizationNew(TestGetAccessToken.getLoginToken("sjlb01", "1234567"));
    }

    private static JSONObject getZXUserByAuthorizationNew(String authorization) {
        OauthGet get = new OauthGet();
        HttpResponse response = null;
        try {
            response = get
                    .get("http://dledutest.aizhixin.com/zhixin_api/api/web/v1/users/userinfonew",
                            "", authorization);
            System.out.println("http://dledutest.aizhixin.com/zhixin_api/api/web/v1/users/userinfonew");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = response.getResponseBody();
        JSONObject obj = JSONObject.fromString(s);
        System.out.println(obj.getString("avatar"));
        return obj;
    }
}
