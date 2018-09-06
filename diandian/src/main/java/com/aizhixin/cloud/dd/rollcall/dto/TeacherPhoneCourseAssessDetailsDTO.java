package com.aizhixin.cloud.dd.rollcall.dto;

import java.util.Date;

import javax.persistence.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TeacherPhoneCourseAssessDetailsDTO {
	private String courseName;// 课程名称
	private String teachingClassCode;// 教学班编号
	private String weekName;// 上课时间（周名称）
	private String periodNo;// 节数
	private String content;// 评教内容
	private double score;// 评教分数
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date createdDate;// 评教时间
}
