/**
 * 
 */
package com.aizhixin.cloud.orgmanager.classschedule.core;

/**
 *  审批状态：10通过 20其它（待扩展）
 * @author zhen.pan
 *
 */
public enum ApprovalStatus {
	PASS(10),//不区分单双周
	UNPASS(20);//双周
	private Integer state;
	private String stateDesc;
	ApprovalStatus(Integer state) {
		this.state = state;
		if(10 == state) {
			this.stateDesc = "通过";
		} else {
			this.state = 20;
			this.stateDesc = "未通过";
		}
	}
	public Integer getState() {
		return state;
	}
	public String getStateDesc() {
		return stateDesc;
	}
}