package com.aizhixin.cloud.ew.praEvaluation.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="PraJobsDomain", description="推荐职位结构体")
@Data
public class PraJobsDomain {

	// ID
	@ApiModelProperty(value = "职位ID", required = false)
	private Long jobsId;

	// 职位名称
	@ApiModelProperty(value = "职位名称", required = false)
	private String name;
	

}
