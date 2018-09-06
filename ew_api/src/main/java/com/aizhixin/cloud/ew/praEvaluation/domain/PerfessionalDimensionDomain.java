package com.aizhixin.cloud.ew.praEvaluation.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="PerfessionalDimensionDomain", description="职业能力4个优势结构体")
@Data
public class PerfessionalDimensionDomain {
		    		
		
		//维度ID	
		@ApiModelProperty(value = "维度ID", required = true)
		private Long dimensionId;
		
		//维度名称
		@ApiModelProperty(value = "维度名称", required = true)
		private String dimensionName;
				
		//内容
		@ApiModelProperty(value = "报告内容", required = true)
		private List<String> content;
		
		//维度描述
		@ApiModelProperty(value = "维度描述", required = true)
		private String dimensionDescription;
		
		
}
