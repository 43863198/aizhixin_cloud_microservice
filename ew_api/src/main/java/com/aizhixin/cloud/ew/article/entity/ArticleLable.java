package com.aizhixin.cloud.ew.article.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签关系表
 * 
 * @author Rigel.ma 2016-12-8
 *
 */
@Entity(name = "AM_ARTICLE_LABLE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleLable extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428422L;

	/**
	 * 标签
	 */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LABLE_ID")
	private Lables lable;
	/**
	 * 分类ID
	 */
	@Column(name = "CLASSIFICATION_ID")
	private Long classificationId;
	/**
	 * 
	 * 文章
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ARTICLE_ID")
	private Article article;

	/**
	 * 删除标记
	 */
	@Column(name = "DELETE_FLAG")
	private Integer deleteFlag;

}
