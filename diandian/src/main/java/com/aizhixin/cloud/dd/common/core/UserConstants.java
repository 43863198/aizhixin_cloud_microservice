package com.aizhixin.cloud.dd.common.core;

public interface UserConstants extends ErrorCode {

	/**
	 * 无效token
	 */
	public final static Integer UNVALID_TOKEN = 40025001;

	/**
	 * 用户信息不存在
	 */
	public final static Integer USER_NOT_EXIST = 40025002;
	/**
	 * 权限错误
	 */
	public final static int ROLE_ERROR = 40025003;

	// 该域名已存在
	public final static int DOMAIN_NAME_ALREADY_EXIST = 40024003;

	// id不能为空
	public final static int ID_IS_EMPTY = 40024005;

	/**
	 * 传入学号不能为空
	 */
	public final static Integer ACCOUNT_IS_NOTNULL = 40024008;

	public final static int DATA_ERROR = 40024009;
}
