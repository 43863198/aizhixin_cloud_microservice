package com.aizhixin.cloud.ew.praEvaluation.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="MBTIReportDomain", description="MBTI报告结构体")
@Data
public class MBTIReportDomain {
	
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
		List<DimensionScoreDomain> dimensionScoreDomains;	
		
		//报告内容
		@ApiModelProperty(value = "报告内容", required = true)
		private String reportContent;
		
		
		//简版报告
		@ApiModelProperty(value = "简版报告", required = true)
		private String briefContent;
				
		//报告编码
		@ApiModelProperty(value = "报告编码", required = true)
		private String reportCode;
}
