package com.aizhixin.cloud.dd.constant;

/*
 * 点名的errcode
 */
public class RollCallConstants {

    // 未点名 并不会出现在数据库的数据中 只是在未点名往前台传输的是
    public static final String TYPE_UNROLLCALL = "0";
    // 已到
    public static final String TYPE_NORMA = "1";

    // 旷课
    public static final String TYPE_TRUANCY = "2";

    // 迟到
    public static final String TYPE_LATE = "3";

    // 请假
    public static final String TYPE_ASK_FOR_LEAVE = "4";

    // 早退
    public static final String TYPE_LEAVE = "5";

    // 已提交
    public static final String TYPE_COMMITTED = "6";

    // 未提交
    public static final String TYPE_UNCOMMITTED = "7";

    // 超出设定的距离
    public static final String TYPE_EXCEPTION = "8";

    // 取消本次考勤
    public static final String TYPE_CANCEL_ROLLCALL = "9";

    public static final int EMPTY_ID = 40025001;

    public static final int NOT_EXIST_ROLLCALL = 40025002;

    public static final int EMPTY_TYPE = 40025003;

    public static final String ROLL_CALL_CHANGE = "40025004";
    public static final String ROLL_CALL_ASKFORLEAVE = "40025005";//请假
    public static final String ROLL_CALL_ANTICHEATING = "40025006";//防作弊
    public static final String ROLL_CALL_WARNING_TEACHERNOTOPEN_CODE = "40025007";//等待老师开启点名
    public static final String ROLL_CALL_HAVING_CHANGE = "40025008";//点名方式切换
    public static final String ROLL_CALL_CLOSE = "40025009";//点名关闭
    public static final String ROLL_CALL_PAUSE = "40025010";//暂停考勤
    public static final String ROLL_CALL_SCHEDULE = "40025011";//无排课

    public static final String ROLL_CALL_WARNING_MESSAGE = "该手机已为其他账号签到！";
    public static final String ROLL_CALL_WARNING_TEACHERNOTOPEN_MESSAGE = "老师未开启点名！";
    public static final String ROLL_CALL_WARNING_HAVINTCHANGE_MESSAGE = "老师更换点名方式！";
    public static final String ROLL_CALL_CLOSE_MESSAGE = "老师已关闭该点名！";

}
