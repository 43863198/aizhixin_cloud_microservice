package com.aizhixin.cloud.dd.questionnaire.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class QuestionnaireQuestionDataDTO {
	//序号
	private Integer no;
	//试题类型
	private Integer questionType;
	//试题内容
	private String content;
	//试题分数
	private String score;
	//试题平均分
	private String avgScore;
	private boolean isQA;
	//试题选项
	private List<QuestionnaireQuestionChoiceDTO> choices=new ArrayList<>();
}
