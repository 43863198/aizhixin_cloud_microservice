package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@ApiModel(description = "教师端 我的请假审批")
@Data
@EqualsAndHashCode(callSuper = false)
public class LeaveForTeacherDTO {

	private Long leaveId;

	private String createdDate;

	private String lastModifyDate;

	private String className;

	private String majorName;

	private String collegeName;

	private String startDate;

	private String endDate;

	private Boolean leaveSchool;

	private String status;

	private String requestContent;

	private String rejectContent;

	private String requestType;

	private Long studentId;

	private String studentName;

	private Long teacherId;

	private String teacherName;

	private Long startPeriodId;

	private Long endPeriodId;

	private String leavePictureUrls;

	private String startTime;
	private String endTime;
	private Integer leavePublic;
	private Integer leaveType;
	private String duration;
}
