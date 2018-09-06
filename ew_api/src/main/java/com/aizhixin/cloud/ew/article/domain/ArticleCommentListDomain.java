package com.aizhixin.cloud.ew.article.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "ArticleCommentListDomain", description = "文章评论列表结构")
@Data
public class ArticleCommentListDomain {

	// 评论ID
	@ApiModelProperty(value = "评论ID", required = true)
	private Long commentId;

	// 用户ID
	@ApiModelProperty(value = "用户ID", required = true)
	private Long userId;

	// 用户名称
	@ApiModelProperty(value = "用户名称", required = true)
	private String userName;

	// 学校名称
	@ApiModelProperty(value = "学校名称", required = true)
	private String organ;

	// 用户图像
	@ApiModelProperty(value = "用户图像", required = true)
	private String avtar;

	// 评论内容
	@ApiModelProperty(value = "评论内容", required = true)
	private String comment;

	// 发布时间
	@ApiModelProperty(value = "发布时间", required = true)
	private String publishTime;

	// 点赞次数
	@ApiModelProperty(value = "点赞次数", required = true)
	private Long praiseCount;

	// 点赞状态
	@ApiModelProperty(value = "点赞状态", required = true)
	private Integer praiseStatus;

	// 删除状态
	@ApiModelProperty(value = "删除状态", required = true)
	private Integer deleteStatus;

}
