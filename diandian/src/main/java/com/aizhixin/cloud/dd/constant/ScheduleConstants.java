package com.aizhixin.cloud.dd.constant;

/*
 * */
public interface ScheduleConstants {

	/**
	 * 自动点名
	 */
	String TYPE_ROLL_CALL_AUTOMATIC = "automatic";
	/**
	 * 手动点名
	 */
	String TYPE_ROLL_CALL_MANUALLY = "manually";
	/**
	 * 数字点名
	 */
	String TYPE_ROLL_CALL_DIGITAL = "digital";

	// 10:未开启，20:已开启, 30:已关闭
	int TYPE_NOT_OPEN_CLASSROOMROLLCALL = 10;

	int TYPE_OPEN_CLASSROOMROLLCALL = 20;

	int TYPE_CLOSE_CLASSROOMROLLCALL = 30;

}
