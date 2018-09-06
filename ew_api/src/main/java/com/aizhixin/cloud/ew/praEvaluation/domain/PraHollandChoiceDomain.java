package com.aizhixin.cloud.ew.praEvaluation.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel(value="PraHollandChoiceDomain", description="选项结构")
@Data
public class PraHollandChoiceDomain {
	
	//选项ID
	@ApiModelProperty(value = "选项ID", required = true)
	private Long choiceId;
	
	//选项内容
	@ApiModelProperty(value = "选项内容", required = true)
	private String choice;	
			
	//选项编码
	@ApiModelProperty(value = "选项编码", required = true)
	private String choiceCode;
	
	//选项维度ID
	@ApiModelProperty(value = "选项维度ID", required = true)
	private Long dimensionId;
		
	//选项分值
	@ApiModelProperty(value = "选项分值", required = true)
	private Integer score;

		
}
