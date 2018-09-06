package com.aizhixin.cloud.ew.article.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "ArticleDetailDomain", description = "文章详情结构")
@Data
public class ArticleDetailDomain {
	// ID
	@ApiModelProperty(value = "ID", required = true)
	private Long id;

	// 标题
	@ApiModelProperty(value = "标题", required = true)
	private String title;
	// 文章内容
	@ApiModelProperty(value = "文章内容", required = true)
	private String content;

	// 分类ID
	@ApiModelProperty(value = "分类ID", required = true)
	private Long classificationId;

	// 分类名称
	@ApiModelProperty(value = "分类名称", required = true)
	private String classificationName;

	// 标签域
	@ApiModelProperty(value = "标签", required = true)
	private List<LableDomain> lableDomains;

	// 发布时间
	@ApiModelProperty(value = "发布时间", required = true)
	private String publishDate;

	// 点击数
	@ApiModelProperty(value = "点击数", required = true)
	private Long hitCount;

	// 评论数
	@ApiModelProperty(value = "评论数", required = true)
	private Long commentCount;

	// 点赞数
	@ApiModelProperty(value = "点赞数", required = true)
	private Long praiseCount;

	// 开启评论
	@ApiModelProperty(value = "开启评论", required = true)
	private boolean openComment;

	// 作者
	@ApiModelProperty(value = "作者", required = true)
	private String createdBy;

	// 当前用户
	@ApiModelProperty(value = "当前用户", required = true)
	private String userName;

	// 当前用户成长值
	@ApiModelProperty(value = "当前用户成长值", required = true)
	private Long userScore;

	// 文章是否已收藏
	@ApiModelProperty(value = "文章收藏状态 0为未收藏，1为已收藏", required = true)
	private Integer collectionStatus;

}
