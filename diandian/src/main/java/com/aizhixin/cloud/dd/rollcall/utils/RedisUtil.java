package com.aizhixin.cloud.dd.rollcall.utils;

import java.util.Date;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;

public class RedisUtil {

    public final static String DIANDIAN_REIDS = "dledu:";

    public final static String DIANDIAN_REIDS_MIDVALUE = "_MidValue";
    public final static String DIANDIAN_SIGNIN_RECORD = "_SignInRecord";
    public final static String DIANDIAN_SINGIN_ANTICHEATING = "devicetoken";
    public final static String DIANDIAN_COUN_SINGIN_ANTICHEATING = "coundevicetoken";

    public final static String DIANDIAN_SCHEDULE_UPDATE = "schedule_update_";

    public final static String DIANDIAN_ORGID = "orgIds:";

    public final static String DIANDIAN_TASK_RUNNING = "running";
    public final static String DIANDIAN_TASK_UNRUNNING = "umrunning";

    public final static String DIANDIAN_ROLLCALL = DIANDIAN_REIDS + "ROLLCALL:" + DateFormatUtil.formatShort(new Date());

    public static String getScheduleTask() {
        return DIANDIAN_REIDS + "tasking";
    }

    public static String getScheduleOrganId() {
        return DIANDIAN_REIDS + DIANDIAN_ORGID;
    }

    public static String getScheduleRollCallIngKey(Long id) {
        return DIANDIAN_REIDS + "_ing_" + id;
    }

    public static String getScheduleRollCallDateKey(Long id) {
        return DIANDIAN_REIDS + "data:" + id;
    }

    public static String getScheduleRollCallKey(Long id) {
        return DIANDIAN_REIDS + "_" + id;
    }

    public static String getAntiCheatingKey(Long scheduleId) {
        return DIANDIAN_REIDS + scheduleId + "_" + DIANDIAN_SINGIN_ANTICHEATING;
    }

    public static String getCounslorAntiCheatingKey(Long counsellorId) {
        return DIANDIAN_REIDS + counsellorId + "_" + DIANDIAN_COUN_SINGIN_ANTICHEATING;
    }

    public static String getScheduleUpdateKey(Long courseId) {
        return DIANDIAN_REIDS + DIANDIAN_SCHEDULE_UPDATE + courseId;
    }

    /**
     * 存储 排课id 与 排课对象 对应关系的 key
     */
    public static String getSchduleDominKey(Long scheduleId) {
        StringBuffer sb = new StringBuffer();
        sb.append(DIANDIAN_REIDS);
        sb.append("pid_");
        sb.append(scheduleId);
        return sb.toString();
    }

    /**
     * 存储 排课id 与该排课点名规则的对应关系的 key
     */
    public static String getSchduleRollCallDominKey(Long scheduleId) {
        StringBuffer sb = new StringBuffer();
        sb.append(DIANDIAN_REIDS);
        sb.append("sid_");
        sb.append(scheduleId);
        return sb.toString();
    }

    public static long getRollCallId() {
        // id策略为 年月日时分秒(20170517121111)14+4位随机数
        StringBuffer sb = new StringBuffer();
        // sb.append(DateFormatUtil.format(new Date(), "yyyyMMddHHmmss"));
        sb.append(DateFormatUtil.format(new Date(), "mmss"));
        sb.append(java.util.concurrent.ThreadLocalRandom.current().nextInt(10000));

        return Long.parseLong(sb.toString());
    }

    public static String getQuesAssignResultKey(Long quesId) {
        return DIANDIAN_REIDS + quesId + "_quesAssignResult";
    }
}
