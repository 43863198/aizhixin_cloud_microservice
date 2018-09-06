package com.aizhixin.cloud.ew.praEvaluation.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="PerfessionalReportDomain", description="职业能力报告结构体")
@Data
public class PerfessionalReportDomain {
	
	    //用户ID	
		@ApiModelProperty(value = "用户ID", required = true)
		private Long userId;

		//用户名
		@ApiModelProperty(value = "用户名", required = true)
		private String userName;				
		
		//报告内容
		List<PerfessionalDimensionDomain> perfessionalDimensionDomains;	
											
}
