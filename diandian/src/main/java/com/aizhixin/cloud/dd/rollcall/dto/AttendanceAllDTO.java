package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2017/6/14.
 */
@ApiModel(description = "学生端 我的考勤")
@Data
public class AttendanceAllDTO {
    private Long teacherId;
    private String name;
    private Long typeId;
    private Long dayWeek;
    private Date teach_time;
    private String periodName;
    private String courseName;
    private String courseId;
    private String scheduleId;
    private String scheduleRollCallId;
}
