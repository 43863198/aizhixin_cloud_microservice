package com.aizhixin.cloud.ew.article.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章表
 * 
 * @author Rigel.ma 2016-12-6
 *
 */
@Entity(name = "AM_ARTICLE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class Article extends AbstractEntity {
	private static final long serialVersionUID = -5836009047318428476L;

	/**
	 * 标题
	 */

	@Column(name = "TITLE")
	private String title;
	/**
	 * 文章内容
	 */
	@Column(name = "CONTENT")
	private String content;

	/**
	 * 图片URL
	 */
	@Column(name = "PICURL")
	private String picUrl;

	/**
	 * 链接URL
	 */
	@Column(name = "LINKURL")
	private String linkUrl;
	/**
	 * 
	 * 分类ID
	 */

	@Column(name = "CLASSIFICATION_ID")
	private Long classificationId;
	/**
	 * 点击数
	 */

	@Column(name = "HITCOUNT")
	private Long hitCount;

	/**
	 * 评论数
	 */

	@Column(name = "COMMENTCOUNT")
	private Long commentCount;

	/**
	 * 点赞数
	 */

	@Column(name = "PRAISECOUNT")
	private Long praiseCount;

	/**
	 * 总分
	 */

	@Column(name = "TOTAL")
	private Long total;

	/**
	 * 开启评论
	 */
	@Column(name = "OPEN_COMMENT")
	private boolean openComment;

	/**
	 * 发布
	 */
	@Column(name = "PUBLISHED")
	private Integer published;

	/**
	 * 创建人
	 */
	@Column(name = "CREATED")
	private String created;

}
