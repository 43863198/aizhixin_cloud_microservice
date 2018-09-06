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
public class AttendanceCountDTO {
    private String courseName;
    private String teacherName;
    private Long arrvied; //已到
    private Long crunk;//旷到
    private Long late;//迟到
    private Long beg; //请假
    private Long early;//早退
    private Long CountSum;
    private Long normal;//正常
    private Long abnormal;//异常
}
