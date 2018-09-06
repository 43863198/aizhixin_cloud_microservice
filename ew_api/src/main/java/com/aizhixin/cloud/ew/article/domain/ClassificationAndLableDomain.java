package com.aizhixin.cloud.ew.article.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "ClassificationAndLableDomain", description = "分类标签结构")
@Data
public class ClassificationAndLableDomain {

	// 分类ID
	@ApiModelProperty(value = "分类ID", required = true)
	private Long classificationId;

	// 分类名称
	@ApiModelProperty(value = "分类名称", required = true)
	private String classificationName;

	// 标签域
	@ApiModelProperty(value = "标签", required = true)
	private List<LableDomain> lableDomains;

}
