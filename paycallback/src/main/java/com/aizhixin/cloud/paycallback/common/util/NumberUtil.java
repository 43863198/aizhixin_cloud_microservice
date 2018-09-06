package com.aizhixin.cloud.paycallback.common.util;

public class NumberUtil {
    public static String doubleToString(Double number) {
        if (null == number) return "0.0";
        return String.format("%.2f", number);
    }
    public static String doubleToIntString(Double number) {
        if (null == number) return "0";
        return String.format("%.0f", number);
    }

    public static String subDouble(Double one, Double two) {
        double d1 = 0.0;
        double d2 = 0.0;
        if (null != one) {
            d1 = one.doubleValue();
        }
        if (null != two) {
            d2 = two.doubleValue();
        }
        d1 = d1 - d2;
        return String.format("%.2f", d1);
    }
}
