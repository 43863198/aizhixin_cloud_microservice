package com.aizhixin.cloud.ew.article.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "NewLableDomain", description = "标签结构")
@Data
public class NewLableDomain {
	// 标签ID
	@ApiModelProperty(value = "标签ID	", required = true)
	private Long lableId;

	// 标签名称
	@ApiModelProperty(value = "标签名称", required = true)
	private String lableName;

	// 分类ID
	@ApiModelProperty(value = "分类ID	", required = true)
	private Long classificationId;

}
