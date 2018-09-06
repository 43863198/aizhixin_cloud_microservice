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
 * 题目(相对于评测的具体题目)
 * @author Rigel.ma
 *
 */
@Entity(name = "PRA_QUESTION")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class PraQuestion extends AbstractOnlyIdEntity{
	private static final long serialVersionUID = -6388063821770054305L;
	
	/**
	 * 题干，试题题目
	 */
	@Column(name = "QUESTION")
	private String question;
	/**
	 * 
	 *测评套题ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EVALUATION_ID")
	private Evaluation evaluation;
	
	/**
	 * 
	 *题号
	 */
	@Column(name = "NUM")
	private Integer num;
	
	
	/**
	 * 题目维度ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIMENSION_ID")
	private PraDimension dimension;
	

	
	/**
	 * 排序码
	 * 非必须字段
	 */
	@Column(name = "SORT_CODE")
	private Integer sortCode;
	
	/**
	 * 
	 *备注
	 */
	@Column(name = "MEMO")
	private String memo;
	
}
