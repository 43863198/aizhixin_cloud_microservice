package com.aizhixin.cloud.dd.url;

/**
 * @author LIMH
 * @date 2018/1/18
 */
public class URLUtils {

    private static String env = "test";

    public static String azxURL = null;
    public static String ddURL = null;

    public static void getLoginUrl() {
        switch (env) {
            case "dev":
                azxURL = "http://dledu.aizhixindev.com/zhixin_api";
                ddURL = "http://dddev.aizhixin.com/diandian_api";
                break;
            case "test":
                azxURL = "http://dledu.aizhixintest.com/zhixin_api";
                ddURL = "http://dd.aizhixintest.com/diandian_api";
                break;
            case "uat":
                break;
            case "pro":
                azxURL = "http://dledu.aizhixin.com/zhixin_api";
                ddURL = "http://dd.aizhixin.com/diandian_api";
                break;
            default:
                azxURL = "http://dledu.aizhixintest.com/zhixin_api";
                ddURL = "http://localhost:8080";
        }
    }

    public static String getAzxUrl() {
        getLoginUrl();
        return azxURL;
    }

    public static String getDdURL() {
        getLoginUrl();
        return ddURL;
    }
}
