package com.aizhixin.cloud.dd.dorms.util;

import java.util.Calendar;

public class DateUtil {
	public static String getSysYear() {
		Calendar date = Calendar.getInstance();
		String year = String.valueOf(date.get(Calendar.YEAR));
		return year;
	}
	
	public static void main(String[] args) {
		Long a=1L,b=2L;
		System.out.println(Double.parseDouble(a+"")/Double.parseDouble(b+""));
	}
}
