package com.aizhixin.cloud.ew.news.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "NewsDetailDomain", description = "新闻详情结构体")
@Data
public class NewsDetailDomain {
	// ID
	@ApiModelProperty(value = "ID", required = true)
	private Long id;

	// 标题
	@ApiModelProperty(value = "标题", required = true)
	private String title;
	// 文章内容
	@ApiModelProperty(value = "文章内容", required = true)
	private String content;

	// 图片
	@ApiModelProperty(value = "图片", required = true)
	private String picUrl;

	// 图片2
	@ApiModelProperty(value = "图片", required = true)
	private String picUr2;

	// 图片3
	@ApiModelProperty(value = "图片", required = true)
	private String picUr3;

	// 发布时间
	@ApiModelProperty(value = "发布时间", required = true)
	private String publishDate;

	// 点击数
	@ApiModelProperty(value = "点击数", required = true)
	private Long hitCount;

}
