package com.aizhixin.cloud.ew.praEvaluation.entity;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 维度记录表
 * @author Rigel.ma  2017-06-07
 *
 */

@Entity(name = "PRA_DIMENSION_REPORT")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class PraDimensionReport extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = 5217115318279016012L;
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
	 * 维度ID
	 */	
	@Column(name = "DIMENSION_ID")
	private Long dimensionId;
		
	/**
	 * 维度名称
	 */	
	@Column(name = "DIMENSION_NAME")
	private String name;


	/**
	 * 测评ID
	 */
	@Column(name = "EVALUATION_ID")
	private Long evaluationId;
	
	/**
	 * 答题用时
	 */
	@Column(name = "COST")
	private String cost;
	
	
	/**
	 * 得分
	 */
	@Column(name = "SCORE")
	private Double score;
	
	
	/**
	 * 测评时间
	 */
	@Column(name = "REPORT_DATE")
	private String reportDate;
	
	/**
	 * 答题次数
	 */
	@Column(name = "TIMES")
	private Integer times;
	
	
}
