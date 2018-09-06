package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "课表")
@Data
@EqualsAndHashCode(callSuper = false)
public class CourseListDTO {

	private String dayOfWeek;

	private List<CourseDTO> courseList;

}
