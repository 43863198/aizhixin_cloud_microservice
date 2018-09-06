package com.aizhixin.cloud.ew.praEvaluation.entity;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 测评记录表
 * @author Rigel.ma  2017-06-07
 *
 */

@Entity(name = "PRA_ANSWER_RECORD")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class PraAnswerRecord extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -7244815395631694706L;
	/**
	 * 用户ID
	 */	
	@Column(name = "USER_ID")
	private Long userId;
	/**
	 * 用户名称
	 */		
	@Column(name = "USER_NAME")
	private String userName;
	
	
	/**
	 * 题目ID 
	 */
	@Column(name = "QUESTION_ID")
	private Long questionId;
	/**
	 * 选项ID
	 */
	@Column(name = "CHOICE_ID")
	private Long choiceId;
	
	/**
	 * 维度ID
	 */
	@Column(name = "DIMENSION_ID")
	private Long dimensionId;
	

	
	/**
	 * 得分
	 */
	@Column(name = "SCORE")
	private Double score;
	
	/**
	 * 评测ID
	 */
	@Column(name = "EVALUATION_ID")
	private Long evaluationId;
	
	/**
	 * 测评时间
	 */
    @Column(name = "RECORD_DATE")	
	private String recordDate;
    
    /**
	 * 答题次数
	 */
	@Column(name = "TIMES")
	private Integer times;
	
	/**
	 * 备注信息
	 */
	@Column(name = "memo")
	private String memo;
}
