package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Administrator on 2017/6/15.
 */
@Data
@ApiModel(description = "考勤记录比例")
@EqualsAndHashCode(callSuper = false)
public class AttendancesInfoDTO {
    private String code;
    private String courseName;
    private String teacherName;
    private Long participationCount;
    private Long practical;
    private String proportion;
    private Long classId;
    private Long semesterId;
    private String semesterName;
}
