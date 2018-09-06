package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel(description = "考勤记录比例")
@EqualsAndHashCode(callSuper = false)

public class AttendanceDetailDTO {
    Long scheduleId;
    Long teacherId;
    Long courseId;
    Long studentId;
    String studentName;
    String personId;
    String type;
    String className;
}
