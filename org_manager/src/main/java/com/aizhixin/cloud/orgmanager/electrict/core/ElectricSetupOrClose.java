/**
 * 
 */
package com.aizhixin.cloud.orgmanager.electrict.core;

import lombok.Getter;

/**
 * 用户类型(10启用，20停用)
 * @author zhen.pan
 *
 */
public enum ElectricSetupOrClose {
	SETUP(10),//启用
	CLOSE(20);//停用
	@Getter private Integer state;
	@Getter private String stateDesc;

	ElectricSetupOrClose(Integer state) {
		this.state = state;
		switch (state) {
		case 10:
			this.stateDesc = "启用";
			break;
		default:
			this.state = 20;
			this.stateDesc = "停用";
		}
	}
}