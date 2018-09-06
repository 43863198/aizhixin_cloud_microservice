package com.aizhixin.cloud.rollcall.monitor.utils;

import com.aizhixin.cloud.rollcall.common.core.ApiReturnConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LIMH on 2017/11/29.
 */
public class ApiReturn {
    public static Map<String, Object> message(Boolean result, String message, Object object) {
        Map resObj = new HashMap();
        resObj.put(ApiReturnConstants.SUCCESS, result);
        resObj.put(ApiReturnConstants.MESSAGE, message);
        resObj.put(ApiReturnConstants.DATA, object);
        return resObj;
    }
}
