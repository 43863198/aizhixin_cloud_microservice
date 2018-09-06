package com.aizhixin.cloud.ew.article.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "ArticleCommentDomain", description = "文章评论结构")
@Data
public class ArticleCommentDomain {

	// ID
	@ApiModelProperty(value = "ID", required = true)
	private Long id;

	// 评论内容
	@ApiModelProperty(value = "评论内容", required = true)
	private String comment;

	// 文章ID
	@ApiModelProperty(value = "文章ID	", required = true)
	private Long articleId;

}
