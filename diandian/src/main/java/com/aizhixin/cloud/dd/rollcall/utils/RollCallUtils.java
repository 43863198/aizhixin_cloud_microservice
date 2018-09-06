package com.aizhixin.cloud.dd.rollcall.utils;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;

public class RollCallUtils {

    public static RollCallSer copy(RollCall rollCall) {
        RollCallSer ser = new RollCallSer();
        BeanUtils.copyProperties(rollCall, ser);
        ser.setSignTime(DateFormatUtil.format(rollCall.getSignTime(),
                DateFormatUtil.FORMAT_LONG));
        ser.setCreateDate(rollCall.getCreatedDate() == null ? null
                : DateFormatUtil.format(rollCall.getCreatedDate(),
                DateFormatUtil.FORMAT_LONG));
        ser.setLastModifiedDate(rollCall.getLastModifiedDate() == null ? null
                : DateFormatUtil.format(rollCall.getLastModifiedDate(),
                DateFormatUtil.FORMAT_LONG));
        return ser;
    }

    public static RollCall reduction(Object obj) {
        RollCallSer rcs = (RollCallSer) JSONObject.toBean(
                JSONObject.fromObject(obj), RollCallSer.class);
        RollCall rollcall = new RollCall();
        BeanUtils.copyProperties(rcs, rollcall);
        if (StringUtils.isNotBlank(rcs.getSignTime())) {
            rollcall.setSignTime(Timestamp.valueOf(rcs.getSignTime()));
        }
        return rollcall;
    }

    public static RollCallSer reductionRcs(Object obj) {
        RollCallSer rcs = (RollCallSer) JSONObject.toBean(
                JSONObject.fromObject(obj), RollCallSer.class);
        return rcs;
    }

    public static String AttendanceAccount(int total, int normal, int later, int askForleave, int leave, int type) {
        if (type == 0) {
            type = 10;
        }
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);
        int element = 0;
        switch (type) {
            case 10:
                element = normal;
                break;
            case 20:
                element = normal + askForleave;
                break;
            case 30:
                element = normal + later;
                break;
            case 40:
                element = normal + leave;
                break;
            case 50:
                element = normal + later + leave;
                break;
            case 60:
                element = normal + later + leave + askForleave;
                break;
        }
        float temp = (float) element / (total == 0 ? 1 : total);
        return nt.format(temp);
    }

    public static Integer AttendanceAccountCount(int total, int normal, int later, int askForleave, int leave, int type) {
        if (type == 0) {
            type = 10;
        }
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);
        int element = 0;
        switch (type) {
            case 10:
                element = normal;
                break;
            case 20:
                element = normal + askForleave;
                break;
            case 30:
                element = normal + later;
                break;
            case 40:
                element = normal + leave;
                break;
            case 50:
                element = normal + later + leave;
                break;
            case 60:
                element = normal + later + leave + askForleave;
                break;
        }
        return element;
    }
}
