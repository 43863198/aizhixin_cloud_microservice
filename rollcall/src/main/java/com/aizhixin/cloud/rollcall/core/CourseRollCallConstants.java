package com.aizhixin.cloud.rollcall.core;

public class CourseRollCallConstants {

	/**
	 * 自动点名
	 */
	public final static  String TYPE_ROLL_CALL_AUTOMATIC = "automatic";
	/**
	 * 手动点名
	 */
	public final static  String TYPE_ROLL_CALL_MANUALLY = "manually";
	/**
	 * 数字点名
	 */
	public final static  String TYPE_ROLL_CALL_DIGITAL = "digital";

	/**
	 * 开启点名
	 */
	public final static String OPEN_ROLLCALL = "enable";

	/**
	 * 关闭点名
	 */
	public final static String CLOSE_ROLLCALL = "disable";
	/**
	 * 打卡机
	 */
	public final static Integer NOT_OPEN_CLASSROOMROLLCALL = 10;
	/**
	 * 随堂点
	 */
	public final static Integer OPEN_CLASSROOMROLLCALL = 20;
	/**
	 * 关闭随堂点
	 */
	public final static Integer CLOSED_CLASSROOMROLLCALL = 30;


	/**
	 * 课前
	 */
	public final static Integer COURSE_BEFORE = 10;
	/**
	 * 课中
	 */
	public final static Integer COURSE_IN = 20;
	/**
	 * 课后
	 */
	public final static Integer COURSE_OUT = 30;
}
