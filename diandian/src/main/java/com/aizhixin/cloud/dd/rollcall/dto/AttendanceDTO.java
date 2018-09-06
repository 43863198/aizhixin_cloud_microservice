package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel(description = "考勤率")
@EqualsAndHashCode(callSuper = false)
public class AttendanceDTO {

    private Long scheduleRollCallId;
    private Integer allCount;
    private Integer normalCount;
    private Integer laterCount;
    private Integer askForLeaveCount;
    private Integer leaveCount;

}
