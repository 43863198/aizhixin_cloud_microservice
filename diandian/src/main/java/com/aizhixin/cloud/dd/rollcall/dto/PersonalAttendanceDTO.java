package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "web 个人中心")
@Data
@EqualsAndHashCode(callSuper = false)
public class PersonalAttendanceDTO {
    private int normal = 0;
    // 请假
    private int askforlevae = 0;
    private int late = 0;
    private int truancy = 0;
    // 早退
    private int Leave = 0;
    private Long studentId = 0L;
    private Long teacherId = 0L;
    private double assessAvage;

    private int assessTotalCount = 0;
    private double assessTotalScore = 0;

    private Long waitAssess = 0L;
}
