/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.core;

import lombok.Getter;

/**
 * 用户类型(10学校管理员，20学院管理员，40班级管理员，60老师，70学生)
 * @author zhen.pan
 *
 */
public enum UserType {
	SCHOOL_ADMIN(10),//学校管理员
	COLLEGE_ADMIN(20),//学院管理员
	CLASSES_ADMIN(40),//班级管理员
	B_TEACHER(60),//老师
	B_STUDENT(70);//学生
	@Getter private Integer state;
	@Getter private String stateDesc;

	UserType(Integer state) {
		this.state = state;
		switch (state) {
		case 10:
			this.stateDesc = "学校管理员";
			break;
		case 20:
			this.stateDesc = "学院管理员";
			break;
		case 40:
			this.stateDesc = "班级管理员";
			break;
		case 60:
			this.stateDesc = "老师";
			break;
		default:
			this.state = 70;
			this.stateDesc = "学生";
		}
	}
}