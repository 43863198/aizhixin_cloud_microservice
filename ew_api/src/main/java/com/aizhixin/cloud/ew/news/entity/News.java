package com.aizhixin.cloud.ew.news.entity;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.common.entity.AbstractOnlyIdEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新闻动态表
 * 
 * @author Rigel.ma 2017-04-17
 *
 */
@Entity(name = "N_NEWS")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class News extends AbstractOnlyIdEntity {
	private static final long serialVersionUID = -5836009047318428476L;

	/**
	 * 标题
	 */
	@Column(name = "TITLE")
	private String title;
	/**
	 * 文章内容
	 */
	@Column(name = "CONTENT")
	private String content;

	/**
	 * 图片URL
	 */
	@Column(name = "PICURL1")
	private String picUrl1;

	/**
	 * 图片URL
	 */
	@Column(name = "PICURL2")
	private String picUrl2;

	/**
	 * 图片URL
	 */
	@Column(name = "PICURL3")
	private String picUrl3;

	/**
	 * 点击数
	 */

	@Column(name = "HITCOUNT")
	private Long hitCount;

	/**
	 * 组织机构数组
	 */
	@Column(name = "ORGANIDS")
	private String organIds;

	/**
	 * 组织机构
	 */
	@Column(name = "ORGANID")
	private Long organId;

	/**
	 * 创建人组织机构
	 */
	@Column(name = "ORGAN")
	private String organ;

	/**
	 * 全部
	 */
	@Column(name = "PUBLISHED")
	private Integer published;

	/**
	 * 全部
	 */
	@Column(name = "ALL_FLAG")
	private Integer allFlag;

	@CreatedBy
	@Column(name = "CREATED_BY")
	protected Long createdBy;

	@Column(name = "CREATED_DATE")
	private String createdDate;

	@LastModifiedBy
	@Column(name = "LAST_MODIFIED_BY")
	protected Long lastModifiedBy;

	@CreatedDate
	@Column(name = "LAST_MODIFIED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	protected Date lastModifiedDate = new Date();

	@Column(name = "DELETE_FLAG")
	protected Integer deleteFlag = DataValidity.VALID.getState();

}
