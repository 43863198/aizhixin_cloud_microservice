/**
 * 
 */
package com.aizhixin.cloud.rollcall.common.core;

/**
 * Excel导入状态机
 * @author zhen.pan
 *
 */
public enum ExcelImportStatus {
	DOING(10),//进行中
	SUCCESS(20),//导入成功结束
	FAIL(30);//导入失败结束
	private Integer state;
	private String stateDesc;
	ExcelImportStatus(Integer state) {
		this.state = state;
		if(10 == state) {
			this.stateDesc = "进行中";
		} else if(20 == state) {
			this.stateDesc = "成功结束";
		} else {
			this.state = 30;
			this.stateDesc = "失败结束";
		}
	}
	public Integer getState() {
		return state;
	}
	public String getStateDesc() {
		return stateDesc;
	}
}