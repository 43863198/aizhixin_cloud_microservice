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
 * 选项表（对应套题题目选项）
 * @author Rigel.ma  2017-06-07
 *
 */
@Entity(name = "PRA_CHOICE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class PraChoice extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428476L;
	
	/**
	 * 选项内容
	 */
	
	@Column(name = "CHOICE_CONTENT")
	private String choiceContent;
	/**
	 * 选项编码A、B、C、D 
	 */
	@Column(name = "CHOICE_CODE")
	private String choiceCode;
	/**
	 * 
	 *测评套题ID
	 */
	@Column(name = "EVALUATION_ID")
	private Long evaluationId;
	/**
	 * 题目ID
	 */
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "QUESTION_ID")
	@Column(name = "QUESTION_ID")
	private Long questionId;
	
	/**
	 * 维度ID
	 */
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "DIMENSION_ID")
	@Column(name = "DIMENSION_ID")
	private Long dimensionId;
	
	/**
	 * 分值
	 */
	@Column(name = "SCORE")
	private Integer score;

	/**
	 * 排序码
	 * 非必须字段
	 */
	@Column(name = "SORT_CODE")
	private Integer sortCode;
}
