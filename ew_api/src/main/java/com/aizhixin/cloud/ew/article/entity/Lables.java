package com.aizhixin.cloud.ew.article.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签表
 * 
 * @author Rigel.ma 2016-12-8
 *
 */
@Entity(name = "AM_LABLES")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class Lables extends AbstractEntity {

	private static final long serialVersionUID = 53280235420579037L;
	/**
	 * 名称
	 */

	@Column(name = "NAME")
	private String name;
	/**
	 * 分类ID
	 */
	@Column(name = "CLASSIFICATION_ID")
	private Long classificationId;

}
