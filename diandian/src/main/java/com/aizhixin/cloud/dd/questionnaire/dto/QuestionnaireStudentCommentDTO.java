package com.aizhixin.cloud.dd.questionnaire.dto;

import lombok.Data;

@Data
public class QuestionnaireStudentCommentDTO {
	//学生id
	private Long stuId;
	//学生姓名
	private String stuName;
	//行政班名称
	private String classesName;
	private String teachingClassName;
	private Integer classType;
	//专业
	private String profName;
	//院系
	private String collegeName;
	//评语
	private String comment;
}
