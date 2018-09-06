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
 * 特点分析表
 * @author Rigel.ma  2017-02-21
 *
 */

@Entity(name = "APP_CHARACTERISTIC")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class AppCharacteristic extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428476L;
	/**
	 * 特点编码
	 */	
	@Column(name = "CODE")
	private String code;
	
	/**
	 * 维度
	 */
	@Column(name = "DIMENSION")
	private String dimension;
	
	/**
	 * 小于等于某个分值
	 */
	@Column(name = "MIN_SCORE")
	private Double minScore;
	
	/**
	 * 大于等于某个分值
	 */
	@Column(name = "MAX_SCORE")
	private Double maxScore;

	/**
	 * 报告内容 
	 */
	@Column(name = "CONTENT")
	private String content;
	

	/**
	 * 测评题ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EVALUATION_ID")
	private Evaluation evaluation;

}
