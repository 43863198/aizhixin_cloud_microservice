package com.aizhixin.cloud.dd.approve.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

    public static String getApproveNum(){
        String TimeNow = new SimpleDateFormat("yyyyMMddHHmmssSS").format(Calendar.getInstance().getTime());
        return TimeNow;
    }
}
