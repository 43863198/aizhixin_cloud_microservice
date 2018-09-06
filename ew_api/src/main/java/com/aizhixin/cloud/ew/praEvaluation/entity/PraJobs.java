/**
 * 
 */
package com.aizhixin.cloud.ew.praEvaluation.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;


import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 职位表
 * @author Rigel.ma  2017-06-19
 *
 */
@Entity(name = "PRA_JOBS")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class PraJobs extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428476L;
	
	/**
	 * 选项内容
	 */
	
	@Column(name = "NAME")
	private String name;
	
}
