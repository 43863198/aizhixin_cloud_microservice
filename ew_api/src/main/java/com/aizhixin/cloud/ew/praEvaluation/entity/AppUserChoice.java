package com.aizhixin.cloud.ew.praEvaluation.entity;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户报告表
 * @author Rigel.ma  2017-02-23
 *
 */
@Entity(name = "APP_USER_CHOICE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class AppUserChoice extends AbstractOnlyIdEntity {
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
	 * 岛屿 代码
	 */	
	@Column(name = "CODE")
	private String code;
	
	/**
	 * 选择时间
	 */	
	@Column(name = "CHOICE_DATE")
	private String choiceDate;
	
	/**
	 * 评测ID
	 */
	@Column(name = "EVALUATION_ID")
	private Long evaluationId;
	
	/**
	 * 结果代码
	 */	
	@Column(name = "RESULT_CODE")
	private String resultCode;
	
	/**
	 * 判定结果
	 */	
	@Column(name = "RESULT")
	private String result;
	
	/**
	 * 结果时间
	 */	
	@Column(name = "RESULT_DATE")
	private String resultDate;
	
	/**
	 * 答题次数
	 */
	@Column(name = "TIMES")
	private Integer times;
}
