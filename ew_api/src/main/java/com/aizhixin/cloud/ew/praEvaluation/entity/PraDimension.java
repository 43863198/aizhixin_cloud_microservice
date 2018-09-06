package com.aizhixin.cloud.ew.praEvaluation.entity;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 维度表（题目、选项可以划分的维度）
 * @author Rigel.ma  2017-06-07
 *
 */

@Entity(name = "PRA_DIMENSION")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class PraDimension  extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = 7768641233403499805L;
	/**
	 * 名称
	 */	
	@Column(name = "NAME")
	private String name;
	/**
	 * 描述信息 
	 */
	@Column(name = "`DESCRIPTION`")
	private String description;
	/**
	 * 上一层维度ID
	 */
	@Column(name = "PARENT_DIMENTION_ID")
	private Long parentDimensionId;
	
	/**
	 * 测评套题ID
	 */
	@Column(name = "EVALUATION_ID")
	private Long evaluationId;
}
