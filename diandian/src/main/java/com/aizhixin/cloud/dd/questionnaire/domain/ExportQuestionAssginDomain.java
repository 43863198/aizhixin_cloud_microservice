package com.aizhixin.cloud.dd.questionnaire.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ExportQuestionAssginDomain {
	private String courseName;
	private String teacherName;
	private Long questionAssginId;
	private String teachingClassName;
	private List<QuestionDomain> qdl=new ArrayList<>();
	private double avg;
}
