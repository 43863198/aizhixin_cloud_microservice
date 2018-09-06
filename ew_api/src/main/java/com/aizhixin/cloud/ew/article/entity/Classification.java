package com.aizhixin.cloud.ew.article.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分类表
 * 
 * @author Rigel.ma 2016-12-8
 *
 */
@Entity(name = "AM_CLASSIFICATION")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class Classification extends AbstractEntity {

	private static final long serialVersionUID = -5271869029584055437L;

	/**
	 * 名称
	 */
	@Column(name = "NAME")
	private String name;

}
