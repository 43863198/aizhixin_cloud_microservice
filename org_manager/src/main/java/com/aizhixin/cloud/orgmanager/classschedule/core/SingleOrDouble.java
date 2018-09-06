/**
 * 
 */
package com.aizhixin.cloud.orgmanager.classschedule.core;

/**
 * 单周或双周
 * @author zhen.pan
 *
 */
public enum SingleOrDouble {
	ALL(10),//不区分单双周
	SINGLE(20),//单周
	DOUBLE(30);//双周
	private Integer state;
	private String stateDesc;
	SingleOrDouble(Integer state) {
		this.state = state;
		if(20 == state) {
			this.stateDesc = "单";
		} else if(30 == state) {
			this.stateDesc = "双";
		} else {
			this.state = 10;
			this.stateDesc = "";
		}
	}
	public Integer getState() {
		return state;
	}
	public String getStateDesc() {
		return stateDesc;
	}
}