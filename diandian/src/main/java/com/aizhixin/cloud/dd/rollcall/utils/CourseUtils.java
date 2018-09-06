package com.aizhixin.cloud.dd.rollcall.utils;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/15.
 */
public class CourseUtils {

    private final static Logger log = LoggerFactory.getLogger(CourseUtils.class);

    public static long laterTime = 5 * 60 * 1000;

    public static boolean classEndTime(String endTime) {
        try {
            String classBeginTime = DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT) + " " + endTime;
            Date classBT = DateFormatUtil.parse(classBeginTime, "yyyy-MM-dd HH:mm");
            Date date = new Date();
            if (classBT.after(date)) {
                // 可以签到
                return true;
            } else {
                // 不能签到
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            log.warn("计算学生是否迟到异常，默认学生为未迟到!", e);
        }
        return true;
    }

    public static boolean classBeginTime(String beginTime) {
        try {
            String classBeginTime = DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT) + " " + beginTime;
            Date classBT = DateFormatUtil.parse(classBeginTime, "yyyy-MM-dd HH:mm");

            Date date = new Date();
            Date datebegin = new Date(classBT.getTime() - laterTime);
            if (!date.before(datebegin)) {
                // 可以签到
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            log.warn("计算学生是否迟到异常，默认学生为未迟到!", e);
        }
        return true;
    }

    public static boolean isSchoolTime(String beginTime, String endTime) {
        try {
            String classBeginTime = DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT) + " " + beginTime;
            String classEndTime = DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT) + " " + endTime;

            Date classBT = DateFormatUtil.parse(classBeginTime, "yyyy-MM-dd HH:mm");
            Date classET = DateFormatUtil.parse(classEndTime, "yyyy-MM-dd HH:mm");

            Date dateBT = new Date(classBT.getTime() - laterTime);
            Date nowDate = new Date();
            if (nowDate.before(classET) && nowDate.after(dateBT)) {
                // 课堂内
                return true;
            } else {
                // 课堂外
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getWhichLesson(int begin, int len) {
        StringBuffer sb = new StringBuffer("第");
        sb.append(begin);

        if (1 != len) {
            sb.append("～");
            sb.append(begin + len - 1);
        }
        sb.append("节课");
        return sb.toString();
    }

    /**
     * 是否在正常上课时间
     *
     * @param beginTime
     * @param lateTime
     * @param timestamp
     * @return
     */
    public static boolean isNormal(String beginTime, int lateTime, Date timestamp) {
        if (0 == lateTime) {
            lateTime = 1000;
        }
        try {
            String classBeginTime = DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT) + " " + beginTime;
            Date classBT = DateFormatUtil.parse(classBeginTime, DateFormatUtil.FORMAT_MINUTE);
            Long currentTime = 0l;
            if (timestamp == null) {
                currentTime = System.currentTimeMillis();
            } else {
                currentTime = timestamp.getTime();
            }
            long laterTime = (lateTime + 1) * 60 * 1000;
            Date date = new Date(currentTime - laterTime);
            if (classBT.before(date)) {
                // 迟到
                return false;
            } else {
                // 正常
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("计算学生是否迟到异常，默认学生为未迟到!", e);
        }
        return true;
    }

    /**
     * 是否在正常上课时间
     *
     * @param beginTime
     * @param lateTime
     * @param timestamp
     * @return
     */
    public static String getResultType(String beginTime, Integer lateTime, Integer absenteeismTime, Date timestamp) {
        String result = RollCallConstants.TYPE_NORMA;
        try {
            //lateTime为空时 迟到时间默认15分钟
            if (lateTime == null) {
                lateTime = 15;
            }
            String classBeginTime = DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT) + " " + beginTime;
            Date classBT = DateFormatUtil.parse(classBeginTime, DateFormatUtil.FORMAT_MINUTE);
            Long currentTime = 0l;
            if (timestamp == null) {
                currentTime = System.currentTimeMillis();
            } else {
                currentTime = timestamp.getTime();
            }
            //lateTime为0时 不计算迟到时间
            if (lateTime > 0) {
                //判断是否迟到
                if (lateTime > 0) {
                    long lTime = (lateTime + 1) * 60 * 1000;
                    Date date = new Date(currentTime - lTime);
                    if (classBT.before(date)) {
                        result = RollCallConstants.TYPE_LATE;
                    }
                }
            }
            //判断是否旷课
            if (result.equals(RollCallConstants.TYPE_LATE) && absenteeismTime != null && absenteeismTime > 0) {
                long aTime = (absenteeismTime + 1) * 60 * 1000;
                Date date = new Date(currentTime - aTime);
                if (classBT.before(date)) {
                    result = RollCallConstants.TYPE_TRUANCY;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
