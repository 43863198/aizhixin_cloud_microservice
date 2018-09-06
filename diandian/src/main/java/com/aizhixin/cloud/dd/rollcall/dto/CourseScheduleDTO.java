package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "课前初始化信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class CourseScheduleDTO {

	private Long scheduleId;
	private Long scheduleRollCallId;
	private Long teacherId;
	private Long courseId;
	private String teachTime;
	private Boolean isRollCall = false;;
	private String rollCallType;
	private String localtion;
	private String courseStartTime;
	private String isOpen;
	private String rollCallTypeOrigin;
	private Integer lateTime;
	private String beginTime;
	private String endTime;
	private Integer deviation;
	private Long dcoId;
	private Integer reUse;
	private Long semesterId;
	private String teachEndTime;
	private String teachBeginTime;
	private String teachBeginBeforeTime;
	private Long organId;

}
