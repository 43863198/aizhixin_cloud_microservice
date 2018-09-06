package com.aizhixin.cloud.ew.praEvaluation.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import lombok.Data;

@ApiModel(value="PraHollandDomain", description="霍兰德测试的结构体")
@Data
public class PraHollandDomain {
	
	 //题干，试题题目
	@ApiModelProperty(value = "题干", required = true)
	private String question;
	
	 //题号
	@ApiModelProperty(value = "题号", required = true)
	private Integer num;
	
	//题目ID
	@ApiModelProperty(value = "题目ID", required = true)
	private Long questionId;
			
	//选项
	@ApiModelProperty(value = "选项", required = true)
	private List<PraHollandChoiceDomain> choices;
	
	//备注内容
   @ApiModelProperty(value = "备注内容", required = true)
	private String memo;
}
