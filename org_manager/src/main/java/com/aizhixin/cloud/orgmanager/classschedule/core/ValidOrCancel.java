/**
 * 
 */
package com.aizhixin.cloud.orgmanager.classschedule.core;

/**
 *  有效10
 *  取消20
 * @author zhen.pan
 *
 */
public enum ValidOrCancel {
	VALID(10),//有效
	CANDEL(20);//取消
	private Integer state;
	private String stateDesc;
	ValidOrCancel(Integer state) {
		this.state = state;
		if(10 == state) {
			this.stateDesc = "有效";
		} else {
			this.state = 20;
			this.stateDesc = "取消";
		}
	}
	public Integer getState() {
		return state;
	}
	public String getStateDesc() {
		return stateDesc;
	}
}