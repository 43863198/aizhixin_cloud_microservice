package com.aizhixin.cloud.ew.announcement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.ew.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 公告发布列表
 * 
 * @author bly
 * @data 2017年8月15日
 */
@Entity(name = "ANNOUNCEMENT_LIST")
@ToString
public class announcementList extends AbstractEntity {

	private static final long serialVersionUID = -7908570043062889574L;

	/**
	 * 公告标题
	 */
	@Column(name = "TITLE")
	@Getter @Setter private String title;
	/**
	 * 公告内容
	 */
	@Column(name = "CONTENT")
	@Getter @Setter private String content;
	/**
	 * 公告图片
	 */
	@Column(name = "PICURL")
	@Getter @Setter private String picUrl;
	/**
	 * 公告类型
	 */
	@Column(name = "TYPE")
	@Getter @Setter private String type;
	/**
	 * 组织机构ID
	 */
	@Column(name = "ORGAN_ID")
	@Getter @Setter private Long organId;
	/**
	 * 组织机构名称
	 */
	@Column(name = "ORGAN_NAME")
	@Getter @Setter private String organName;
	/**
	 * 发布标记
	 */
	@Column(name = "PUBLISH_STATUS")
	@Getter @Setter private Integer publishStatus;
	/**
	 * 发布时间
	 */
	@Column(name = "PUBLISH_DATE")
	@Getter @Setter private String publishDate;
}
