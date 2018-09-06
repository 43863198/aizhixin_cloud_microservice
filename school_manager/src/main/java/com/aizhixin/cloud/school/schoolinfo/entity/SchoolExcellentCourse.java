
package com.aizhixin.cloud.school.schoolinfo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.aizhixin.cloud.school.common.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 
 * @ClassName: SchoolExcellentCourse 
 * @Description: 精品课程实体
 * @author xiagen
 * @date 2017年5月16日 下午5:08:38 
 *  
 */
@Entity(name="S_EXCELLENTCOURSE")
@ToString
public class SchoolExcellentCourse extends AbstractEntity{

	private static final long serialVersionUID = -5437316486777682243L;
    @Column(name="SCHOOL_ID")
	@Getter@Setter
	private Long schoolId;
    
    @Column(name="COURSE_ID")
	@Getter@Setter
	private Long courseId;
    
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
    
	/**
	 * 开卷精品课程id
	 */
    @Column(name="KF_COURSE_ID")
	@Getter@Setter
	private Long kfCourseId;
    
	/**
	 * 开卷精品课程名称
	 */
    @Column(name="KF_COURSE_NAME")
	@Getter@Setter
	private String kfCourseName;
}
