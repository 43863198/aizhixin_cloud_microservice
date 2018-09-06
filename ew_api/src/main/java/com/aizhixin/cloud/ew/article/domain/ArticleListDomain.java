package com.aizhixin.cloud.ew.article.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "ArticleListDomain", description = "文章结构体")
@Data
public class ArticleListDomain {
	// ID
	@ApiModelProperty(value = "ID", required = true)
	private Long id;

	// 标题
	@ApiModelProperty(value = "标题", required = true)
	private String title;

	// 图片
	@ApiModelProperty(value = "图片", required = true)
	private String picUrl;

	// 链接URl
	@ApiModelProperty(value = "图片", required = true)
	private String linkUrl;

	// 点击数
	@ApiModelProperty(value = "点击数", required = true)
	private Long hitCount;

	// 点赞数
	@ApiModelProperty(value = "点赞数", required = true)
	private Long praiseCount;

}
