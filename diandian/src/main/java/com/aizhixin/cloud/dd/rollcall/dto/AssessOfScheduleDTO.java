package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;

import java.util.List;

import lombok.Data;

@ApiModel(description = "评分")
@Data
public class AssessOfScheduleDTO {

	private Long scheduleId;

	private Double avgScore = 0.0;

	private String dayOfWeek;

	private String periodName;

	private String courseName;

	private Integer count = 0;

	private List<AssessOfClassDTO> classInfo;

}
