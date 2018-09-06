/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.core;

import lombok.Getter;

/**
 * 是否定制化首页(启用10，不启用20)
 * @author zhen.pan
 *
 */
public enum OrgCustomeOrNot {
	SETUP(10),//启用
	CLOSE(20);//不启用
	@Getter private Integer state;
	@Getter private String stateDesc;

	OrgCustomeOrNot(Integer state) {
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