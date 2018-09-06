package com.aizhixin.cloud.dd.questionnaire.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QuestionsChoiceDTO {
	@ApiModelProperty(value="选项id",required=false)
	protected Long id;
	@ApiModelProperty(value="试题id",required=false)
	private Long questionId;
	@ApiModelProperty(value="选项",required=true)
	private String choice;
	@ApiModelProperty(value="选项内容",required=true)
	private String content;
	@ApiModelProperty(value="选项分值",required=false)
	private String score;
	public QuestionsChoiceDTO(){}
	public QuestionsChoiceDTO(Long id, String choice, String content, String score,Long questionId) {
		super();
		this.id = id;
		this.choice = choice;
		this.content = content;
		this.score = score;
		this.questionId=questionId;
	}
	
	
}
