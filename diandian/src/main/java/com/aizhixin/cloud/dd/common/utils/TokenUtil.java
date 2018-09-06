package com.aizhixin.cloud.dd.common.utils;

import java.util.HashMap;
import java.util.Map;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PublicErrorCode;

public class TokenUtil {

    public static String accessToken = "";

    public static Map <String, Object> tokenValid() {
        Map <String, Object> resBody = new HashMap <>();
        resBody.put(ApiReturnConstants.MESSAGE,
                PublicErrorCode.TOKEN_UNVALID_EXCEPTION.getStrValue());
        resBody.put(ApiReturnConstants.ERROR,
                PublicErrorCode.TOKEN_UNVALID_EXCEPTION.getIntValue());
        return resBody;
    }
}
