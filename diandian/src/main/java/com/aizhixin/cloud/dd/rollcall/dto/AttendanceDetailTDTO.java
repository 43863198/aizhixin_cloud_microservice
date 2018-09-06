package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@ApiModel(description = "考勤记录比例")
@EqualsAndHashCode(callSuper = false)
public class AttendanceDetailTDTO {
    String personId;
    String studentName;
    String className;
    List<Long> scheduleIds;
    String[] types;
}
