package com.aizhixin.cloud.ew.news.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "NewsDomain", description = "新闻结构体")
@Data
public class NewsDomain {
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

	// 发布
	@ApiModelProperty(value = "发布", required = true)
	private Integer published;

	// 全部
	@ApiModelProperty(value = "全部", required = true)
	private Integer allFlag;

	// 学校ID数组
	@ApiModelProperty(value = "学校ID数组", required = true)
	private String organIDs;

}
