package com.aizhixin.cloud.mobile.common.exception;

/**
 * 公共的错误码
 * @author zhen.pan
 *
 */
public enum PublicErrorCode {
	QUERY_EXCEPTION(48001000),//查询异常
	SAVE_EXCEPTION(48002000),//保存异常
	DELETE_EXCEPTION(48003000),//删除异常
	PARAM_EXCEPTION(48004000),//参数异常
	AUTH_EXCEPTION(48005000),//权限异常
	PAGE_PARAM_EXCEPTION(48006000);//分页参数异常
	private Integer intValue;
	private String strValue;
	PublicErrorCode(Integer stateCode) {
		this.intValue = stateCode;
		switch(stateCode) {
		case 48001000:
			this.strValue = "查询异常";
			break;
		case 48002000:
			this.strValue = "保存异常";
			break;
		case 48003000:
			this.strValue = "删除异常";
			break;
		case 48004000:
			this.strValue = "参数异常";
			break;
		case 48005000:
			this.strValue = "权限异常";
			break;
		default:
			this.strValue = "未知异常";
		}
	}
	public Integer getIntValue() {
		return intValue;
	}
	public String getStrValue() {
		return strValue;
	}
}
