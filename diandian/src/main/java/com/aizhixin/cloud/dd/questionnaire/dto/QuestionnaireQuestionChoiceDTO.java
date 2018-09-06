package com.aizhixin.cloud.dd.questionnaire.dto;

import lombok.Data;

@Data
public class QuestionnaireQuestionChoiceDTO {
	//选项
	private String choice;
	//选项分数
	private String score;
	//选项占比
	private String choiceZb;
}
