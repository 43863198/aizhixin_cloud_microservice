package com.aizhixin.cloud.paycallback.common.util;

public class FormatUtil {
    public static String addZeroForNum(String str,int strLength) {
        int strLen =str.length();
        if (strLen <strLength) {
            while (strLen< strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);
                str= sb.toString();
                strLen= str.length();
            }
        }

        return str;
    }
}
