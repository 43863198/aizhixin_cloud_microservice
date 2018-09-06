package com.aizhixin.cloud.dd.rollcall.utils;

import java.math.BigDecimal;

public class MathUtil {
	
	public static String doubleToBFBString(Double d){
		BigDecimal   b   =   new   BigDecimal(d);  
		double   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
		Integer value=(int) (f1*100);
		return value+"%";
	}
	
//	public static void main(String[] args) {
//		System.out.println(doubleToBFBString(0.111));
//	}
}
