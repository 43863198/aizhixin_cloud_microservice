package com.aizhixin.cloud.ew.article.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "ClassificationDomain", description = "文章分类结构")
@Data
public class ClassificationDomain {

	// 分类ID
	@ApiModelProperty(value = "分类ID", required = true)
	private Long classificationId;

	// 分类名称
	@ApiModelProperty(value = "分类名称", required = true)
	private String classificationName;

}
