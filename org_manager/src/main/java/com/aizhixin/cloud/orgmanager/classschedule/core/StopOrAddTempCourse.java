/**
 * 
 */
package com.aizhixin.cloud.orgmanager.classschedule.core;

/**
 * 停止或者增加临时课程
 * @author zhen.pan
 *
 */
public enum StopOrAddTempCourse {
	ADD(10),//增加
	STOP(20),//停止
	CHANGE(30);//调整
	private Integer state;
	private String stateDesc;
	StopOrAddTempCourse(Integer state) {
		this.state = state;
		if(10 == state) {
			this.stateDesc = "增加";
		} else if (20 == state) {
			this.state = 20;
			this.stateDesc = "停止";
		} else {
			this.state = 30;
			this.stateDesc = "调整";
		}
	}
	public Integer getState() {
		return state;
	}
	public String getStateDesc() {
		return stateDesc;
	}
}