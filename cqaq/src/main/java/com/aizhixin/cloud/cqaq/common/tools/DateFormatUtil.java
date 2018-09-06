package com.aizhixin.cloud.cqaq.common.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

public class DateFormatUtil {

	// @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	// private DateTime searchDate;

	/**
	 * 把特定的时间格式转为毫秒
	 *
	 * @param s
	 *            'HH:MM:SS'格式的时间
	 * @return
	 */
	public static Long getVideoLength(String s) {
		Long hour = 0l;
		Long minute = 0l;
		Long second = 0l;
		if (!StringUtils.isEmpty(s)) {
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
	 * @param strDate
	 *            接受 'HH:MM:SS'格式
	 * @return
	 */
	public static String getHourAndMin(String strDate) {
		int hour = 0;
		int mint = 0;
		// 判断传入的时间是否为空
		if (StringUtils.isEmpty(strDate)) {
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
	// public static DateTime getBeforDay(int dayNum) {
	//
	// Date dayNow = new Date(); // 当前时间
	// Date dayBefore = new Date();
	//
	// Calendar calendar = Calendar.getInstance(); // 得到日历
	// calendar.setTime(dayNow);// 把当前时间赋给日历
	// calendar.add(Calendar.DAY_OF_MONTH, -dayNum);
	// dayBefore = calendar.getTime();
	//
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //
	// 设置时间格式
	// String defaultStartDate = sdf.format(dayBefore); // 格式化前n天
	// DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd
	// HH:mm:ss");
	// // 时间解析
	// DateTime searchDateTime = DateTime.parse(defaultStartDate, format);
	//
	// return searchDateTime;
	// }

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

	/**
	 * 使用用户格式格式化日期
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式
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

	public static Date parse(String dateStr, String pattern) throws ParseException {
		Date date = null;
		if (!StringUtils.isEmpty(dateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			date = sdf.parse(dateStr);
		}
		return date;
	}

	/**
	 * 获取时间差
	 */
	public static String getTimesToNow(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String now = format.format(new Date());
		String returnText = null;
		try {
			long from = format.parse(date).getTime();
			long to = format.parse(now).getTime();
			int days = (int) ((to - from) / (1000 * 60 * 60 * 24));
			if (days == 0) {// 一天以内，以分钟或者小时显示
				int hours = (int) ((to - from) / (1000 * 60 * 60));
				if (hours == 0) {
					int minutes = (int) ((to - from) / (1000 * 60));
					if (minutes == 0) {
						returnText = "刚刚";
					} else {
						returnText = minutes + "分钟前";
					}
				} else {
					returnText = hours + "小时前";
				}
			} else if (days == 1) {
				returnText = "昨天";
			} else {
				returnText = days + "天前";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnText;
	}
}
