package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 排课信息
 * 
 * @author meihua.li
 *
 */
@ApiModel(description = "排课信息")
@Data
public class ScheduleDTO {
	private static final long serialVersionUID = -4138514236088577990L;

	private Long id;
	@ApiModelProperty(value = "组织结构ID", required = true)
	private Long organId;
	private Long courseId;
	private String courseName;

	private Long teacherId;

	private String teacherNname;

	private Long semesterId;
	private String semesterName;
	private Long weekId;
	private String weekName;
	private Integer dayOfWeek;
	private Long periodId;
	private Integer periodNo;
	private Integer periodNum;
	private String scheduleStartTime;
	private String scheduleEndTime;
	private String classRoomName;
	private String teachDate;
	private Long teachingclassId;
	private String teachingclassName;
	private Boolean isInitRollcall;

	// 该排课的迟到时间
	private Integer courseLaterTime = 0;

	// 该课程是否可以进行点名操作
	private Boolean isRollCall = false;

	private String attendance = "0";

	// 是否在课程內
	private Boolean classrommRollCall = false;
}