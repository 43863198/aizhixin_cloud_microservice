/**
 * 
 */
package com.aizhixin.cloud.orgmanager.common.core;

/**
 * 班级学生是否在校
 * @author zhen.pan
 *
 */
public enum SchoolStatus {
	IN_SCHOOL(10),//在校
	OUT_SCHOOL(20);//毕业、休学、开除，表示不在校，不能正常进行学习、上课等活动
	private Integer state;
	private String stateDesc;
	SchoolStatus(Integer state) {
		this.state = state;
		if(10 == state) {
			this.stateDesc = "在校";
		} else {
			this.state = 20;
			this.stateDesc = "毕业";
		}
	}
	public Integer getState() {
		return state;
	}
	public String getStateDesc() {
		return stateDesc;
	}
}