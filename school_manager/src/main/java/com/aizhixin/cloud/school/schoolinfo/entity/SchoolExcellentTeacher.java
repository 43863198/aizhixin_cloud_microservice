
package com.aizhixin.cloud.school.schoolinfo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.school.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 
 * @ClassName: SchoolExcellentTeacher 
 * @Description: 优秀教师
 * @author xiagen
 * @date 2017年5月15日 上午11:32:34 
 *  
 */

@Entity(name="S_EXCELLENTTEACHER")
@ToString
public class SchoolExcellentTeacher extends AbstractEntity{

	private static final long serialVersionUID = -1940938383341591349L;
	/***
	 * 学校id
	 */
	@Column(name="SCHOOL_ID")
	@Getter@Setter
	private Long schoolId;
	
	/***
	 * 教师id
	 */
	@Column(name="TEACHER_ID")
	@Getter@Setter
	private Long teacherId;

	/**
	 * 教师介绍
	 */
	@Column(name="INTRODUCTION")
	@Getter@Setter
	private String introduction;
	
	/**
	 * 教师介绍图片
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
