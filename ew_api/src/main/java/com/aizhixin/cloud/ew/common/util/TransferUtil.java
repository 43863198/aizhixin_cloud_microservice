package com.aizhixin.cloud.ew.common.util;

import java.util.Arrays;
import java.util.List;

public class TransferUtil {
	
	/**
     * 将List集合转化字符串 以,分割,该方法用于拼接
     */
    public static String arrayToString(List<String> PicUrls) {
        if (null == PicUrls) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (Object obj : PicUrls) {
            sb.append(obj);
            sb.append(",");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }
    
    /**
     * 将字符串转化List集合 以,分割
     */
    public static List<String> stringToList(String PicUrls){
    	String str[] = PicUrls.split(",");
    	return Arrays.asList(str);
    }
}
