package com.aizhixin.cloud.studentpractice.common.core;

public interface ErrorCode {
	/*
	 * Spring异常
	 */
	Integer SPRING_EXCEPTION_CODE = 48000000;
	/*
	 * System异常
	 */
	Integer SYSTEM_EXCEPTION_CODE = 48200000;

	/*
	 * ID是必须的
	 */
	Integer ID_IS_REQUIRED = 49000000;
	/*
	 * 根据ID没有找到相应的对象
	 */
	Integer ID_NOT_FOUND_OBJECT = 49000001;
	/*
	 * 字段对应的数据不能重复
	 */
	Integer FIELD_IS_REPLICATION = 49000002;
	/*
	 * 参数值之间互相冲突
	 */
	Integer PARAMS_CONFLICT = 49000003;
	/*
	 * 删除时有关联数据存在
	 */
	Integer DELETE_CONFLICT = 49000004;
}
