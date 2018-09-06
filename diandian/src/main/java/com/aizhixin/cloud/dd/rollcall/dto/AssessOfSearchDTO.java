package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;

import java.util.List;

import lombok.Data;

@ApiModel(description = "评分")
@Data
public class AssessOfSearchDTO {

	private Long scheduleId;

	private Long classId;

	private Double score;

	private String dayOfWeek;

	private String periodName;

	private String className;

	private String courseName;

	private Integer count;

	private Long courseId;

}
