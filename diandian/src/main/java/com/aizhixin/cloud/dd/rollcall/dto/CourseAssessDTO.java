package com.aizhixin.cloud.dd.rollcall.dto;

import lombok.Data;

@Data
public class CourseAssessDTO {
	private Long teachingClassId;// 教学班id
	private Long semesterId;// 学期id
	private String semesterName;// 学期名称
	private String teachingClassCode;// 教学班编号
	private String courseName;// 课程名称
	private String teacherName;// 教师名称
	private double averageScore;// 综合评分
}
