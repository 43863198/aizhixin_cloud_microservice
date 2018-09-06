package com.aizhixin.cloud.ew.praEvaluation.entity;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户报告表
 * @author Rigel.ma  2017-06-07
 *
 */

@Entity(name = "PRA_USER_REPORT")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class PraUserReport extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = 8797449974208735503L;


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
	 * 学校ID
	 */		
	@Column(name = "ORGAN_ID")
	private Long organId;
	
	
	/**
	 * 优势报告ID
	 */
	@Column(name = "ADVANTAGE_ID")
	private Long advantageId;
	
	/**
	 * 特点分析ID
	 */            
	@Column(name = "CHARACTERISTIC_ID")
	private Long characteristicId;
	
	
	@Column(name = "CHARACTERISTIC_ID1")
	private Long characteristicId1;
	

	@Column(name = "CHARACTERISTIC_ID2")
	private Long characteristicId2;
	
	@Column(name = "CHARACTERISTIC_ID3")
	private Long characteristicId3;
	
	
	@Column(name = "CHARACTERISTIC_ID4")
	private Long characteristicId4;
	

	@Column(name = "CHARACTERISTIC_ID5")
	private Long characteristicId5;
	
	/**
	 * 工作推荐ID
	 */
	@Column(name = "JOBS_ID")
	private Long jobsId;
	
	/**
	 * 评测套题ID
	 */
	//@ManyToOne(fetch = FetchType.LAZY)
	@Column(name = "EVALUATION_ID")
	private Long evaluationId;
	
	/**
	 * 评测得分
	 */
	
	@Column(name = "RESULT_SCORE")
	private Double resultScore;
	
	/**
	 * 报告时间
	 */
	@Column(name = "REPORT_DATE")
	private String reportDate;
	
	/**
	 * 答题次数
	 */
	@Column(name = "TIMES")
	private Integer times;
	
	/**
	 * 备注
	 */		
	@Column(name = "MEMO")
	private String memo;
	
}
