package com.aizhixin.cloud.dd.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Slf4j
public class DateFormatUtil {

    private DateTime searchDate;

    /**
     * 把特定的时间格式转为毫秒
     *
     * @param s 'HH:MM:SS'格式的时间
     * @return
     */
    public static Long getVideoLength(String s) {
        Long hour = 0l;
        Long minute = 0l;
        Long second = 0l;
        if (StringUtils.isNotEmpty(s)) {
            String time[] = s.split(":");
            try {
                hour = Long.parseLong(time[0]);
                minute = Long.parseLong(time[1]);
                second = Long.parseLong(time[2]);
            } catch (NumberFormatException e) {
                hour = 0l;
                minute = 0l;
                second = 0l;
            }
        }
        return (hour * 3600 + minute * 60 + second) * 1000;
    }

    /*
     * long转为string时间
     */
    public static String transferLongToDate(Long millSec) {
        // 设置时区，用来消除java获取时间上的误差
        TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
        TimeZone.setDefault(tz);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(millSec);
        String longToDate = sdf.format(date);
        return longToDate;
    }

    /**
     * 将string拆分成hour和min
     *
     * @param strDate 接受 'HH:MM:SS'格式
     * @return
     */
    public static String getHourAndMin(String strDate) {
        int hour = 0;
        int mint = 0;
        // 判断传入的时间是否为空
        if (StringUtils.isNotEmpty(strDate)) {
            try {
                String[] strChapteTime = strDate.split(":");
                hour = Integer.parseInt(strChapteTime[0]);
                mint = Integer.parseInt(strChapteTime[1]);
            } catch (NumberFormatException e) {
                hour = 0;
                mint = 0;
            }
        }
        String resStr = null;
        if (hour > 0 && mint > 0) {
            resStr = hour + "小时" + mint + "分钟";
        } else if (hour > 0 && mint == 0) {
            resStr = hour + "小时";
        } else if (hour == 0 && mint > 0) {
            resStr = mint + "分钟";
        } else if (hour == 0 && mint == 0) {
            resStr = "不足一分钟";
        }
        return resStr;
    }

    /*
     * 将毫秒数换算为分钟数
     */
    public static Long getMin(Long totalTime) {
        Long reslut = 0L;
        reslut = (totalTime / 1000L) / 60L;
        return reslut;
    }

    /*
     * 获取前dayNum天的时间
     */
    public static DateTime getBeforDay(int dayNum) {

        Date dayNow = new Date(); // 当前时间
        Date dayBefore = new Date();

        Calendar calendar = Calendar.getInstance(); // 得到日历
        calendar.setTime(dayNow);// 把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -dayNum);
        dayBefore = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置时间格式
        String defaultStartDate = sdf.format(dayBefore); // 格式化前n天
        DateTimeFormatter format = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss");
        // 时间解析
        DateTime searchDateTime = DateTime.parse(defaultStartDate, format);

        return searchDateTime;
    }

    /*
     * 获取前dayNum天的时间字符串
     */
    public static String getBeforDayStr(int dayNum) {

        Date dayNow = new Date(); // 当前时间
        Date dayBefore = new Date();

        Calendar calendar = Calendar.getInstance(); // 得到日历
        calendar.setTime(dayNow);// 把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -dayNum);
        dayBefore = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置时间格式
        return sdf.format(dayBefore); // 格式化前n天
    }

    /**
     * 英文简写（默认）如：2010-12-01
     */
    public static String FORMAT_SHORT = "yyyy-MM-dd";

    /**
     * 英文简写（默认）如：2010-12-01
     */
    public static String FORMAT_MINUTE = "yyyy-MM-dd HH:mm";

    public static String FORMAT_SHORT_MINUTE = "HH:mm";

    /**
     * 英文全称 如：2010-12-01 23:15:06
     */
    public static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    /**
     * 中文简写 如：2010年12月01日
     */
    public static String FORMAT_SHORT_CN = "yyyy年MM月dd";
    /**
     * 中文全称 如：2010年12月01日 23时15分06秒
     */
    public static String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";

    /**
     * 获得默认的 date pattern
     */
    public static String getDatePattern() {
        return FORMAT_LONG;
    }

    /**
     * 获得默认的 date pattern
     */
    public static String getShortDatePattern() {
        return FORMAT_SHORT;
    }

    /**
     * 根据预设格式返回当前日期
     *
     * @return
     */
    public static String getNow() {
        return format(new Date());
    }

    /**
     * 根据用户格式返回当前日期
     *
     * @param format
     * @return
     */
    public static String getNow(String format) {
        return format(new Date(), format);
    }

    /**
     * 使用预设格式格式化日期
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, getDatePattern());
    }

    public static String formatShort(Date date) {
        return format(date, getShortDatePattern());
    }

    public static String formatDateWithWeek(Date date) {
        if (date == null) {
            date = new Date();
        }
        return String.format(Locale.CHINA, "%tY-%tm-%td %ta %tH:%tM",
                date, date, date, date, date, date);
    }

    /**
     * 使用用户格式格式化日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return
     */
    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    public static Date parse(String dateStr, String pattern)
            throws ParseException {
        Date date = null;
        if (!StringUtils.isBlank(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            date = sdf.parse(dateStr);
        }
        return date;
    }

    public static Date parse2(String dateStr, String pattern){
        Date date = null;
        if (!StringUtils.isBlank(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                log.warn("Exception", e);
            }
        }
        return date;
    }

    public static Date parseDay(String dateStr){
        Date date = null;
        if (!StringUtils.isBlank(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                log.warn("Exception", e);
            }
        }
        return date;
    }

    /**
     * 获取两个日期之间的所有日期，包含起止日期
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<Date> getMonthBetweenDate(Date beginDate, Date endDate) {
        List lDate = new ArrayList();
        if (beginDate.getTime() == endDate.getTime()) {
            lDate.add(beginDate);
            return lDate;
        }

        lDate.add(beginDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);// 把结束时间加入集合
        return lDate;
    }

    /**
     * 比较两个日期之间的大小
     *
     * @param d1
     * @param d2
     * @return 前者大于后者返回true 反之false
     */
    public static boolean compareDate(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);

        int result = c1.compareTo(c2);
        if (result <= 0)
            return true;
        else
            return false;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @return 当前日期是星期几
     */
    public static Integer getWeekOfDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Integer[] weekDays = {7, 1, 2, 3, 4, 5, 6};
        int w = 0;
        Date dt = null;
        try {
            dt = sdf.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
        } catch (Exception e) {
        }
        return weekDays[w];
    }

    /**
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime 时间区间,半闭合,如[10:00-20:00)
     * @param curTime    需要判断的时间 如10:00
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
//            if (args[1].equals("00:00")) {
//                args[1] = "24:00";
//            }
            if (end < start) {
                if (now >= end && now < start) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (now >= start && now < end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
    }
}
