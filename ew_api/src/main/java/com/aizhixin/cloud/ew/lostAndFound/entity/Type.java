package com.aizhixin.cloud.ew.lostAndFound.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物品类型表
 * @author Rigel.ma  2017-05-02
 *
 */
@Entity(name = "LF_TYPE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class Type extends AbstractOnlyIdEntity{
private static final long serialVersionUID = -5836009047318428476L;
	
	/**
	 * 物品类型
	 */	
	@Column(name = "TYPE")
	private String Type;
	
	
	
	
}
