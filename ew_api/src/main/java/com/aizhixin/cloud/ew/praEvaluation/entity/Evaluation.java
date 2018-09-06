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

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 测评类型
 * @author zhen.pan 2016-10-17
 *
 */
@Entity(name = "EW_EVALUATION")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class Evaluation extends AbstractEntity {
	private static final long serialVersionUID = -5836009047318428476L;
	/**
	 * 测评分类
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLASSIFICATION_ID")
	private Classification classification;
	/**
	 * 测评编码
	 */
	@Column(name = "CODE")
	private String code;
	/**
	 * 测评名称
	 */
	@Column(name = "NAME")
	private String name;
	/**
	 * 简介
	 */
	@Column(name = "INTRODUCE")
	private String introduce;
	/**
	 * 详细描述
	 */
	@Column(name = "DESCRIPTION")
	private String description;
	/**
	 * 简介图片
	 */
	@Column(name = "INTRO_PIC")
	private String intro_pic;
	/**
	 * 详述图片
	 */
	@Column(name = "DES_PIC")
	private String des_pic;
	/**
	 * 评测题的数量
	 */
	@Column(name = "TOTAL")
	private Integer total;
	/**
	 * 时长
	 */
	@Column(name = "DURATION")
	private String duration;
	/**
	 * 总分
	 */
	@Column(name = "SCORE")
	private Integer score;
	/**
	 * 排序码
	 * 
	 */
	@Column(name = "SORT_CODE")
	private Integer sortCode;
	/**
	 * 备注信息
	 */
	@Column(name = "MEMO")
	private String memo;
	/**
	 * 测评人数
	 */
	@Column(name = "NUM")
	private Integer num;
}
