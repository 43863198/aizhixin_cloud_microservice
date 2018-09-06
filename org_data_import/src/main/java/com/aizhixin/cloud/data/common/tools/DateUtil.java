/**
 * 
 */
package com.aizhixin.cloud.data.common.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期计算工具
 * 
 * @author zhen.pan
 *
 */
public class DateUtil {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private static long ONE_DAY_SEC = 86400000L;

	/**
	 * 判断是否周一
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isMonday(Calendar d) {
		if (Calendar.MONDAY == d.get(Calendar.DAY_OF_WEEK)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否周日
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isSunday(Calendar d) {
		if (Calendar.SUNDAY == d.get(Calendar.DAY_OF_WEEK)) {
			return true;
		}
		return false;
	}

	/**
	 * 计算任意一天所在周的周一的日期 每周的周一作为第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonday(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		zeroTime(c);
		int delta = 0;
		if (isSunday(c)) {// Calendar的星期是从周日开始的，对周日需要修正
			delta = 6;
		} else {
			delta = c.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
		}
		c.add(Calendar.DATE, 0 - delta);
		return c.getTime();
	}

	/**
	 * 计算任意一天所在周的周日 每周的周一作为第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getSunday(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		zeroTime(c);
		int delta = 0;
		if (isSunday(c)) {
			return c.getTime();
		} else {
			delta = Calendar.SATURDAY + 1 - c.get(Calendar.DAY_OF_WEEK);
		}
		c.add(Calendar.DATE, delta);
		return c.getTime();
	}

	/**
	 * 根据输入的周一计算本周输出的周日
	 * 
	 * @param monday
	 * @return
	 */
	public static Date getSunDayByMonday(Date monday) {
		Calendar c = Calendar.getInstance();
		c.setTime(monday);
		if (!isMonday(c)) {
			throw new RuntimeException("Input date[" + format.format(c.getTime()) + "\tWEEK:"
					+ c.get(Calendar.DAY_OF_WEEK) + "] is not MONDAY");
		}
		c.add(Calendar.DATE, 6);
		return c.getTime();
	}

	/**
	 * 得到传入日期的下一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date nextDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		zeroTime(c);
		return c.getTime();
	}

	/**
	 * 前一天
	 * 
	 * @param c
	 * @return
	 */
	public static Calendar getPreDate(Calendar c) {
		c.add(Calendar.DATE, -1);
		return c;
	}

	/**
	 * 获取两个日期之前的天的数量，start是开区间，end是闭区间
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getDaysBetweenDate(Date start, Date end) {
		if (end.before(start)) {
			throw new RuntimeException(
					"Input end date [" + format.format(end) + "] before start date [" + format.format(start) + "]");
		}
		Calendar cs = Calendar.getInstance();
		cs.setTime(start);
		zeroTime(cs);

		Calendar ce = Calendar.getInstance();
		ce.setTime(end);
		zeroTime(ce);

		long s = cs.getTimeInMillis();
		long e = ce.getTimeInMillis();
		return (int) ((e - s) / ONE_DAY_SEC);
	}

	/**
	 * 日历时间归0时、0分、0秒、0纳秒 直接修改传入对象
	 * 
	 * @param c
	 */
	public static void zeroTime(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * 获取下周一的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextMonday(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.WEEK_OF_YEAR, 1);
		return getMonday(c.getTime());
	}

	/**
	 * 返回n天后的日期
	 */
	public static Date afterNDay(Date date, int n) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, n);
		Date d2 = c.getTime();
		return d2;
	}

	/**
	 * 获取一个日期，时间部分是全是0
	 * 
	 * @param d
	 * @return
	 */
	public static Date getZerotime(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		zeroTime(c);
		return c.getTime();
	}

	/**
	 * 获取两个日期之间包含的周的数量
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getWeekNumbersBetweenDate(Date start, Date end) {
		// int days = getDaysBetweenDate(start, end);
		int weeks = 0;
		Calendar cs = Calendar.getInstance();
		cs.setTime(start);
		zeroTime(cs);

		Calendar ce = Calendar.getInstance();
		ce.setTime(end);
		zeroTime(ce);

		Date sm = getNextMonday(cs.getTime());
		System.out.println("Date:" + format.format(cs.getTime()) + "\t Monday: "
				+ format.format(getMonday(cs.getTime())) + "\tNext Monday:" + format.format(sm));
		if (sm.before(ce.getTime())) {
			weeks += 1;
		}
		cs.setTime(sm);
		//
		// c = getPreDate(c);//
		// if(!isSunday(c)) {
		// weeks += 1;
		// }
		// int days = getDaysBetweenDate(sm, end);
		// weeks += days/7;
		// return weeks;
		// DateTime dateTime1 = new DateTime(start);
		// DateTime dateTime2 = new DateTime(end);

		while (cs.before(ce)) {
			cs.add(Calendar.WEEK_OF_YEAR, 1);
			weeks++;
		}
		return weeks;

		// int weeks = Weeks.weeksBetween(dateTime1, dateTime2).getWeeks();
		// return weeks;
	}

	/**
	 * 周一到周天用1到7来表示
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SUNDAY) {
			return 7;
		} else {
			return dayOfWeek - 1;
		}
	}

	public static Map<Integer, Date> getWeekAllDate(Date firstDate) {
		Map<Integer, Date> wds = new HashMap<>();
		wds.put(1, firstDate);
		Date n = nextDate(firstDate);
		wds.put(2, n);
		n = nextDate(n);
		wds.put(3, n);
		n = nextDate(n);
		wds.put(4, n);
		n = nextDate(n);
		wds.put(5, n);
		n = nextDate(n);
		wds.put(6, n);
		n = nextDate(n);
		wds.put(7, n);
		return wds;
	}

	public static String format(Date date) {
		return format.format(date);
	}

	public static String getCurrentTime(String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		return format.format(new Date());
	}

	public static void main(String[] args) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DATE, 2);
		Date d = c.getTime();
		System.out.println(format.format(d));
		d = DateUtil.getMonday(d);
		System.out.println("\tMonday:" + format.format(d));
		d = DateUtil.getSunDayByMonday(d);
		System.out.println("\tSunday:" + format.format(d));
		System.out.println("\tNext Monday:" + format.format(nextDate(d)));

		c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DATE, 7);
		Date s = c.getTime();
		c.setTime(new Date());
		// c.set(Calendar.MONTH, 1);
		c.set(Calendar.DATE, 24);
		Date e = c.getTime();
		System.out.println(format.format(s) + "\t" + format.format(e) + "\t day numbers:" + getDaysBetweenDate(s, e)
				+ "\t week numbers:" + getWeekNumbersBetweenDate(s, e));

		c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DATE, 2);
		// c.add(Calendar.DATE, -1);
		d = c.getTime();
		System.out.println(format.format(d));
		d = DateUtil.getSunday(d);
		System.out.println("\tSunday:" + format.format(d));

		// d = new Date ();
		System.out.println(getDayOfWeek(d));
	}
}