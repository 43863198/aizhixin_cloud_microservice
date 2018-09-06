package com.aizhixin.cloud.ew.common.core;

public enum PublishStatus {
	UNPUBLISHED(0), // 未发布
	PUBLISHED(1);// 已发布
	private Integer state;
	private String stateDesc;

	PublishStatus(Integer state) {
		this.state = state;
		if (0 == state) {
			this.stateDesc = "未发布";
		} else {
			this.state = 1;
			this.stateDesc = "已发布";
		}
	}

	public Integer getState() {
		return state;
	}

	public String getStateDesc() {
		return stateDesc;
	}
}
