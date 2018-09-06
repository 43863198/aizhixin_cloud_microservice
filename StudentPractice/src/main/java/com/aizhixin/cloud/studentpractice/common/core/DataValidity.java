/**
 * 
 */
package com.aizhixin.cloud.studentpractice.common.core;

/**
 * 数据是否有效
 * @author zhen.pan
 *
 */
public enum DataValidity {
VALID(0),//有效
INVALID(1);//无效
	private Integer intValue;
	private String strValue;
	DataValidity(Integer state) {
		this.intValue = state;
		if(0 == state) {
			this.strValue = "有效";
		} else {
			this.intValue = 1;
			this.strValue = "无效";
		}
	}
	public Integer getIntValue() {
		return intValue;
	}
	public String getStrValue() {
		return strValue;
	}
}