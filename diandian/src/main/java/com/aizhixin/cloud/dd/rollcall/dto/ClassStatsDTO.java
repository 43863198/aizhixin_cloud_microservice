package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "班级信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class ClassStatsDTO {

	private Long collegeId;

	private String collegeName;

	private Long majorId;

	private String majorName;

	private Long classId;

	private String className;

	private String headTeacherName;

	private Long headTeacherId;

	private Long studentCount;

}
