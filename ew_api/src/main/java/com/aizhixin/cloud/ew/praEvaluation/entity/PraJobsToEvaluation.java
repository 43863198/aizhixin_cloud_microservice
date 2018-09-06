/**
 * 
 */
package com.aizhixin.cloud.ew.praEvaluation.entity;

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
 * 职位表
 * @author Rigel.ma  2017-06-19
 *
 */
@Entity(name = "PRA_JOBS_EVALUATION")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class PraJobsToEvaluation extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428476L;
	
	/**
	 * 工作编码
	 */	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "JOBID")
	private PraJobs praJobs;
	
	/**
	 * 报告编码
	 */	
	@Column(name = "CODE")
	private String code;
	
	/**
	 * 测评ID
	 */	
	@Column(name = "EVALUATIONID")
	private Long evaluationId;
	
}
