package com.aizhixin.cloud.dd.questionnaire.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ExportTeacherQuestionnaireDomain {
	private String collegeName;
	private String teacherName;
	private String jobNum;
	private Long pepleTotal;
	private List<QuestionDomain> qdl=new ArrayList<>();
	private double avg;
	
}
