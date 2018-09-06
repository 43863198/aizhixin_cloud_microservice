package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2017/6/10.
 */
@ApiModel(description = "学生根据状态获取请假列表")
@Data
public class StudentForLeaveDTO {
    private Long leaveId;

    private String createdDate;

    private String lastModifyDate;

    private String endDate;

    private Boolean leaveSchool;

    private String rejectContent;

    private String requestContent;

    private String requestType;

    private String startDate;

    private String status;

    private Long teacherId;

    private String teacherName;

    private Long endPeriodId;

    private Long startPeriodId;

    private String leavePictureUrls;

    private String startTime;
    private String endTime;
    private Integer leavePublic;
    private Integer leaveType;
    private String duration;

//    private String className;
//
//    private String majorName;
//
//    private String collegeName;
//
//    private Long studentId;
//
//    private String studentName;
}
