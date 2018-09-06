/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.core;

import lombok.Getter;

/**
 * 用户类型(10暂停考勤，20恢复考勤)
 * @author zhen.pan
 *
 */
public enum StudentRollcallStatus {
	CANSEL(10),//暂停考勤
	RECOVER(20);//恢复考勤
	@Getter private Integer state;
	@Getter private String stateDesc;

	StudentRollcallStatus(Integer state) {
		this.state = state;
		switch (state) {
		case 10:
			this.stateDesc = "暂停考勤";
			break;
		default:
			this.state = 20;
			this.stateDesc = "恢复考勤";
		}
	}
}