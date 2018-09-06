/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 课程基本信息
 * @author zhen.pan
 *
 */
@Entity(name = "T_COURSE")
@ToString
public class Course extends AbstractEntity {
	private static final long serialVersionUID = 5713930482249544987L;
	/**
	 * 课程名称
	 */
	@NotNull
	@Column(name = "NAME")
	@Getter @Setter private String name;
	/**
	 * 课程描述
	 */
	@Column(name = "COURSE_DESC")
	@Getter @Setter private String courseDesc;
	/**
	 * 课程附加属性
	 */
	@Column(name = "COURSE_PROP")
	@Getter @Setter private String courseProp;
	/**
	 * 所属组织机构
	 */
	@NotNull
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	//	@NotNull
	@Column(name = "CODE")
	@Getter @Setter private String code;
	/**
	 * 学分
	 */
	@Column(name = "CREDIT")
	@Getter @Setter private Float credit;
	/**
	 * 创建标志
	 */
	@Column(name = "source")
	@Getter@Setter
	private Integer source;

}
