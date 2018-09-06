package com.aizhixin.cloud.dd.rollcall.dto;

import java.util.Comparator;

/**
 * Created by black on 2017/7/18.
 */
public class ComparatorWeekTendency implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        WeekTendencyDto week1 = (WeekTendencyDto) o1;
        WeekTendencyDto week2 = (WeekTendencyDto) o2;
        int flag = Integer.valueOf(week1.getWeek()).compareTo(Integer.valueOf(week2.getWeek()));
        if (flag > 0) {
            return 1;
        } else if (flag == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}
