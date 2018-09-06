package com.aizhixin.cloud.ew.praEvaluation.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel(value="CharacteristicDomain", description="特点分析结构")
@Data
public class CharacteristicDomain {
	
	//特点ID
	@ApiModelProperty(value = "特点ID", required = true)
	private Long CharacteristicId;
	
	//特点内容
	@ApiModelProperty(value = "特点内容", required = true)
	private String content;	
		
		
	//特点维度ID
	@ApiModelProperty(value = "特点维度", required = true)
	private String dimensionName;
	


}
