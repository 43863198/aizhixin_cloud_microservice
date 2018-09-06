package com.aizhixin.cloud.dd.rollcall.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by LIMH on 2017/8/10.
 */
@Data
public class ScheduleRollCallIngDTO implements Serializable {
    String beginTime;
    int lateTime;
    int absenteeismTime;

    public ScheduleRollCallIngDTO() {
    }

    public ScheduleRollCallIngDTO(String beginTime, int lateTime, int absenteeismTime) {
        this.beginTime = beginTime;
        this.lateTime = lateTime;
        this.absenteeismTime = absenteeismTime;
    }
}
