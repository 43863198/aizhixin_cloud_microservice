package com.aizhixin.cloud.ew.praEvaluation.entity;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 维度描述表
 * @author Rigel.ma  2017-06-12
 *
 */

@Entity(name = "PRA_DIMENSION_DESCRIPTION")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class PraDimensionDescription  extends AbstractOnlyIdEntity {
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
	 * 维度ID
	 */
	@Column(name = "DIMENSION_ID")
	private Long dimensionId;
}
