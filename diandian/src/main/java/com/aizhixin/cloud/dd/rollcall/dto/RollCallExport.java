package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "课程节信息")
@Data
public class RollCallExport {
    private String personId;
    private String studentName;
    private String className;
    private String courseName;
    private String semeterName;
    private int normal;
    // 请假
    private int askforlevae;
    private int late;
    private int truancy;
    // 早退
    private int Leave;
    private Long studentId;
}
