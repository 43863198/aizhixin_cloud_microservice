package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "教学班")
@Data
public class TeachingClassesDTO {
	Long teachingClassesId;
	String teachingClassName;
	String teachingClassCode;
	Long collegeId;
	String collegeName;
	Long courseId;
	String courseName;
	Long teacherId;
	String teacherName;

}
