/**
 * 
 */
package com.aizhixin.cloud.orgmanager.classschedule.core;

/**
 * 教学班是和行政班关联还是和学生关联
 * @author zhen.pan
 *
 */
public enum ClassesOrStudents {
	CLASSES(10),//行政班
	STUDENTS(20);//学生
	private Integer state;
	private String stateDesc;
	ClassesOrStudents(Integer state) {
		this.state = state;
		if(10 == state) {
			this.stateDesc = "行政班";
		} else {
			this.state = 20;
			this.stateDesc = "学生";
		}
	}
	public Integer getState() {
		return state;
	}
	public String getStateDesc() {
		return stateDesc;
	}
}