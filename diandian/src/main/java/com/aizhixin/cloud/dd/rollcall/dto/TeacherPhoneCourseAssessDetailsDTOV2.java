package com.aizhixin.cloud.dd.rollcall.dto;

import lombok.Data;

@Data
public class TeacherPhoneCourseAssessDetailsDTOV2 {
	private Long teachingClassId;//教学班id
	private String teachingClassName; //教学班名称
	private String teachingClassCode; //教学班编号
	private String courseName;  //课程名称
	private Double averageScore;  //平均评分
	private Integer assessNum;  //评分次数
	private Integer stuTotal;  //学生人数
}
