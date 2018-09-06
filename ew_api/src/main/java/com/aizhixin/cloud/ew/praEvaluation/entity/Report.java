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
 * 测评记录表
 * @author Rigel.ma  2016-10-17
 *
 */

@Entity(name = "EW_REPORT")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class Report extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428476L;
	/**
	 * 报告编码
	 */	
	@Column(name = "CODE")
	private String code;
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
	 * 简版报告
	 */
	@Column(name = "BRIEF_CONTENT")
	private String briefContent;
	
	/**
	 * 题目维度ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIMENSION_ID")
	private Dimension dimension;
	/**
	 * 测评题ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EVALUATION_ID")
	private Evaluation evaluation;
	
	/**
	 * 选项ID
	 */
	@Column(name = "CHOICE_ID")
	private Long choiceId;
}
