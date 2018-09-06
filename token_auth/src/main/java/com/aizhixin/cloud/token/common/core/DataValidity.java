/**
 * 
 */
package com.aizhixin.cloud.token.common.core;

/**
 * 数据是否有效,有效0 无效1
 * @author zhen.pan
 *
 */
public enum DataValidity {
	VALID(0),//有效
	INVALID(1);//无效
	private Integer state;
	private String stateDesc;
	DataValidity(Integer state) {
		this.state = state;
		if(0 == state) {
			this.stateDesc = "有效";
		} else {
			this.state = 1;
			this.stateDesc = "无效";
		}
	}
	public Integer getState() {
		return state;
	}
	public String getStateDesc() {
		return stateDesc;
	}
}