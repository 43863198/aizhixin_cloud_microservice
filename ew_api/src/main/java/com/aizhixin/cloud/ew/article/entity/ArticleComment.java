package com.aizhixin.cloud.ew.article.entity;

import java.util.Date;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论表
 * 
 * @author Rigel.ma 2017-05-19
 *
 */
@Entity(name = "AM_ARTICLE_COMMENT")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleComment extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318228476L;
	/**
	 * 评论
	 */
	@Column(name = "COMMENT")
	private String comment;

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
	@Column(name = "ARTICLE_ID")
	private Long articleId;

	/**
	 * 学校ID
	 */
	@Column(name = "ORGAN_ID")
	private Long organId;

	/**
	 * 学校名称
	 */
	@Column(name = "ORGAN_NAME")
	private String organName;

	/**
	 * 楼层
	 */
	@Column(name = "FLOOR")
	private Integer floor;

	/**
	 * 创建日期
	 */
	@Column(name = "DATE")
	private Date date;

	/**
	 * 删除标记
	 */
	@Column(name = "DELETE_FLAG")
	private Integer deleteFlag;

	/**
	 * 点赞数
	 */
	@Column(name = "PRAISECOUNT")
	private Long praiseCount;

}
