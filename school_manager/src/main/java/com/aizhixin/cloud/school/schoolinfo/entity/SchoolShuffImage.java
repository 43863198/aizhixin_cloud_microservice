
package com.aizhixin.cloud.school.schoolinfo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.school.common.entity.AbstractOnlyIdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 
 * @ClassName: SchoolShuffImage 
 * @Description: 学校轮播图
 * @author xiagen
 * @date 2017年5月12日 上午11:14:47 
 *  
 */
@Entity(name="S_SCHOOLSHUFFIMAGE")
@ToString
public class SchoolShuffImage extends AbstractOnlyIdEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 93236139872370792L;
	
	/***
	 * 学校id
	 */
	@Column(name="SCHOOL_ID")
	@Getter@Setter
	private Long schoolId;
	
	/**
	 * 轮播图地址
	 */
	@Column(name="IMAGE_URL")
	@Getter@Setter
	private String imageUrl;
	
	/***
	 * 轮播图顺序
	 */
	@Column(name="IMAGE_SORT")
	@Getter@Setter
	private Integer imageSort;
	/**
	 * 轮播图连接
	 */
	@Column(name="HREF")
	@Getter@Setter
	private String href;
}
