package com.aizhixin.cloud.ew.praEvaluation.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="HollandReportDomain", description="Holland报告结构体")
@Data
public class HollandReportDomain {
	
	    //用户ID	
		@ApiModelProperty(value = "用户ID", required = true)
		private Long userId;

		//用户名
		@ApiModelProperty(value = "用户名", required = true)
		private String userName;
		
		//测评套题ID	
		@ApiModelProperty(value = "测评套题ID", required = true)
		private Long evaluationId;
		
		//维度及得分
		List<AppDimensionScoreDomain> dimensionScoreDomains;	
		
		
		//职业优势报告
		@ApiModelProperty(value = "职业优势", required = true)
		private String advantageContent;
		
		
		//推荐职业
		@ApiModelProperty(value = "推荐职业", required = true)
		private List<String> jobs;
		
				
		//特点分析
		@ApiModelProperty(value = "特点分析", required = true)
		private List<CharacteristicDomain> characteristicDomains;
}
