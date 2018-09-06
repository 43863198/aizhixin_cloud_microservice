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
 * 职业推荐表
 * @author Rigel.ma  2017-02-21
 *
 */

@Entity(name = "APP_JOBS")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class AppJobs extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428476L;
	/**
	 *职业编码
	 */	
	@Column(name = "CODE")
	private String code;
	
	/**
	 * 工作一
	 */
	@Column(name = "JOB1")
	private String job1;
	
	/**
	 * 工作二
	 */
	@Column(name = "JOB2")
	private String job2;
	/**
	 * 工作三
	 */
	@Column(name = "JOB3")
	private String job3;
	/**
	 * 测评题ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EVALUATION_ID")
	private Evaluation evaluation;

}
