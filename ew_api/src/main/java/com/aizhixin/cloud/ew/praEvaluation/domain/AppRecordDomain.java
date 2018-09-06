package com.aizhixin.cloud.ew.praEvaluation.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="AppRecordDomain", description="App答题记录的结构体")
@Data
public class AppRecordDomain {

	// 题目ID
	@ApiModelProperty(value = "题目ID", required = false)
	private Long questionId;

	// 测评套题ID
	@ApiModelProperty(value = "测评套题ID", required = true)
	private Long evaluationId;

	// 题目维度ID
	@ApiModelProperty(value = "题目维度ID", required = false)
	private Long dimensionId;

	// 选项ID
	@ApiModelProperty(value = "选项ID", required = false)
	private Long choiceId;

	// 选项分值
	@ApiModelProperty(value = "选项分值", required = false)
	private Double score;

}
