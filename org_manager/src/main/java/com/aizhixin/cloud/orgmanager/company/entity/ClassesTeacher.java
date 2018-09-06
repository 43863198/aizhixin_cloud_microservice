/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractOnlyIdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 班级班主任实体
 * @author zhen.pan
 *
 */
@Entity(name = "T_CLASSES_TEACHER")
@ToString
public class ClassesTeacher extends AbstractOnlyIdEntity {
	/**
	 * 班级
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLASSES_ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@Getter @Setter private Classes classes;

	/**
	 * 教师
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEACHER_ID")
	@Getter @Setter private User teacher;
	
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
}