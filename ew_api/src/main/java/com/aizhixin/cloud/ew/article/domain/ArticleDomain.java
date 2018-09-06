package com.aizhixin.cloud.ew.article.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "ArticleDomain", description = "文章结构体")
@Data
public class ArticleDomain {
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

	// 链接URl
	@ApiModelProperty(value = "图片", required = true)
	private String linkUrl;

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

	// 发布
	@ApiModelProperty(value = "发布", required = true)
	private Integer published;

	public ArticleDomain() {
	}

	public ArticleDomain(Long id, String title, String picUrl, String linkUrl, Long classificationId,
			String classificationName, Long hitCount, Long praiseCount, boolean openComment) {
		super();
		this.id = id;
		this.title = title;
		this.picUrl = picUrl;
		this.linkUrl = linkUrl;
		this.classificationId = classificationId;
		this.classificationName = classificationName;
		this.hitCount = hitCount;
		this.praiseCount = praiseCount;
		this.openComment = openComment;
	}

}
