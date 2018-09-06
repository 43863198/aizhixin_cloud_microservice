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
 * 测评分类
 * @author zhen.pan
 *
 */
@Entity(name = "EW_CLASSIFICATION")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class Classification extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -8349792738390451470L;

	/**
	 * 分类编码
	 */
	@Column(name = "CODE")
	private String code;
	/**
	 * 分类名称
	 */
	@Column(name = "NAME")
	private String name;
	/**
	 * 排序码
	 * 非必须字段
	 */
	@Column(name = "SORT_CODE")
	private Integer sortCode;
}
