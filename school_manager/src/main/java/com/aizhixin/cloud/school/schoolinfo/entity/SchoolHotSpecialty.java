
package com.aizhixin.cloud.school.schoolinfo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.school.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 
 * @ClassName: SchoolHotSpecialty 
 * @Description: 
 * @author xiagenss
 * @date 2017年5月12日 下午5:14:06 
 *  
 */
@Entity(name="S_HOTSPECIALTY")
@ToString
public class SchoolHotSpecialty extends AbstractEntity{

	private static final long serialVersionUID = -1088279361625425565L;
	
	/**
	 * 学校id
	 */
	@Column(name="SCHOOL_ID")
	@Getter@Setter
	private Long schoolId;
	
	/**
	 * 学院id
	 */
	@Column(name="COLLEGE_ID")
	@Getter@Setter
	private Long collegeId;
	
	
	/**
	 * 专业id
	 */
	@Column(name="SPECIALTY_ID")
	@Getter@Setter
	private Long specialtyId;
	
	/**
	 * 专业介绍
	 */
	@Column(name="INTRODUCTION")
	@Getter@Setter
	private String introduction;
	
	/**
	 * 专业介绍图片
	 */
	@Column(name="IN_URL")
	@Getter@Setter
	private String inUrl;
	
	/**
	 * 模版展示
	 */
	@Column(name="TEMPLATESHOW")
	@Getter@Setter
	private Integer templateShow;
	
	
}
