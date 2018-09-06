package com.aizhixin.cloud.dd.rollcall.dto;

import lombok.Data;

@Data
public class CourseAssessDetailsDTO {
	private Long scheduleId;// 排课Id
	private String semesterName;// 学期名称
	private String teachingClassCode;// 教学班编号
	private String courseName;// 课程名称
	private String teacherName;// 教师名称
	private String teachDate;// 上课日期
	private Integer periodNo;// 节数
	private String classroomName;// 教室
	private Integer assessNum;// 评教人数
	private Integer star5;// 评五星人数
	private Integer star4;// 评四星人数
	private Integer star3;// 评三星人数
	private Integer star2;// 评二星人数
	private Integer star1;// 评一星人数
}
