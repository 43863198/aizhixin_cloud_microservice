package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "学生端 未评教")
@Data
@EqualsAndHashCode(callSuper = false)
public class NotAssessDTO {

	private Long scheduleId;
	private String periodName;
	private String beginTime;
	private String endTime;
	private String classroom;
	private String courseName;
	private String teachTime;
}
