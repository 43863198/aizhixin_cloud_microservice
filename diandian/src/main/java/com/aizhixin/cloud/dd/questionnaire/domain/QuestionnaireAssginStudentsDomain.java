package com.aizhixin.cloud.dd.questionnaire.domain;

import lombok.Data;

@Data
public class QuestionnaireAssginStudentsDomain{
	private Long questionnaireAssginId;
	private Long stuId;
	private String stuName;
	private Long classesId;
	private String classesName;
	private  Integer status;
}
