
package com.aizhixin.cloud.school.schoolinfo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.school.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 
 * @ClassName: SchoolLogo 
 * @Description: 
 * @author xiagen
 * @date 2017年5月11日 下午6:16:16 
 *  
 */
@Entity(name="S_SCHOOLLOGOINFO")
@ToString
public class SchoolLogoInfo extends AbstractEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4985597172206033304L;

	/**
	 * 学校id
	 */
	@Column(name="SCHOOL_ID")
	@Getter@Setter
    private Long schoolId;
	
	/***
	 * logo地址
	 */
	@Column(name="LOGO_URL")
	@Getter@Setter
	private String logoUrl;
	
	/**
	 * logo描述
	 */
	
	@Column(name="DESCRIPTION")
	@Getter@Setter
	private String description;
	/**
	 * logo尺寸
	 */
	
	@Column(name="LOGO_SIZE")
	@Getter@Setter
	private Integer logoSize;
	
	/**
	 * logo顺序
	 */
	
	@Column(name="LOGO_SORT")
	@Getter@Setter
	private Integer logoSort;
	
}
