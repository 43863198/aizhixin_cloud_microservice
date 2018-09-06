package com.aizhixin.cloud.ew.article.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "LableDomain", description = "标签结构")
@Data
public class LableDomain {
	// 标签ID
	@ApiModelProperty(value = "标签ID	", required = true)
	private Long lableId;

	// 标签名称
	@ApiModelProperty(value = "标签名称", required = true)
	private String lableName;

}
