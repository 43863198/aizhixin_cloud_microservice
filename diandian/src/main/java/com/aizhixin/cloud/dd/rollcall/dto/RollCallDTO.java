package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "课程节信息")
@Data
public class RollCallDTO {

	private Long id = 0L;

	private Long scheduleId = 0L;

	private Long studentScheduleId = 0L;

	private Long userId = 0L;

	private Long teacherId = 0L;

	private Long courseId = 0L;

	private String userName = "";

	private String type = "";

	private String className = "";

	private Long classId = 0L;

	private Long semesterId = 0L;

	private String distance = "";

	private String signTime = "";

	private String avatar = "";

	private Boolean rollCall = false;

	private Boolean classroomrollcall = false;
}
