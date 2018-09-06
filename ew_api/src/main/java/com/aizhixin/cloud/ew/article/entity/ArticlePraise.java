package com.aizhixin.cloud.ew.article.entity;

import java.util.Date;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞表
 * 
 * @author Rigel.ma 2016-12-8
 *
 */
@Entity(name = "AM_ARTICLE_PRAISE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class ArticlePraise extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428434L;

	/**
	 * 用户ID
	 */
	@Column(name = "USER_ID")
	private Long userId;

	/**
	 * 用户名
	 */
	@Column(name = "USER_NAME")
	private String userName;

	/**
	 * 文章ID
	 */
	// @ManyToOne(fetch = FetchType.LAZY)
	@Column(name = "ARTICLE_ID")
	private Long articleId;

	/**
	 * 评论ID
	 */

	@Column(name = "COMMENT_ID")
	private Long commentId;

	/**
	 * 创建日期
	 */
	@Column(name = "CREATED_DATE")
	private Date date;

	/**
	 * 删除标记
	 */
	@Column(name = "DELETE_FLAG")
	private Integer deleteFlag;

}
