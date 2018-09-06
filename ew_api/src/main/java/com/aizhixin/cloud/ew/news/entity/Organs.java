package com.aizhixin.cloud.ew.news.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.springframework.data.annotation.CreatedBy;

import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 校园动态表
 * 
 * @author Rigel.ma 2017-04-17
 *
 */
@Entity(name = "N_OrganS")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class Organs extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428476L;

	/**
	 * 组织机构ID
	 */
	@Column(name = "ORGAN_ID")
	private Long organId;

	/**
	 * 新闻ID
	 */
	@Column(name = "NEWS_ID")
	private Long newsId;

	@CreatedBy
	@Column(name = "CREATED_BY")
	protected Long createdBy;

	@Column(name = "CREATED_DATE")
	private String createdDate;

	/**
	 * 删除标记
	 */
	@Column(name = "DELETE_FLAG")
	protected Integer deleteFlag = DataValidity.VALID.getState();

}
