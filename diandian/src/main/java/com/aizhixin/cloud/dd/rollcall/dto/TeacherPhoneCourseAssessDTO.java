package com.aizhixin.cloud.dd.rollcall.dto;

import lombok.Data;

@Data
public class TeacherPhoneCourseAssessDTO {
	private Long teachingClassId;// 教学班id
	private String teachingClassName; //教学班名称
	private Integer stuTotal;//教学班学生数量
	private String courseName;// 课程名称
	private String teachingClassCode;// 教学班编号
	private double averageScore;// 综合评分
	private Integer assessNum;// 评教总人数
	private Integer score5;// 评4-5分人数
	private Integer score4;// 评3-4分人数
	private Integer score3;// 评2-3分人数
	private Integer score2;// 评1-2分人数
	private Integer score1;// 评0-1分人数
}
