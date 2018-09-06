package com.aizhixin.cloud.studentpractice.task.core;

public class TaskStatusCode {

	// 任务状态：未提交
	public final static String TASK_STATUS_UNCOMMIT = "uncommit";
	// 任务状态：待审核
	public final static String TASK_STATUS_CHECK_PENDING = "checkPending";
	// 任务状态：已通过
	public final static String TASK_STATUS_PASS = "pass";
	// 任务状态：未通过
	public final static String TASK_STATUS_NOT_PASS = "notPass";
	// 任务状态：被打回
	public final static String TASK_STATUS_BACK_TO = "backTo";
	// 任务状态：已完成
	public final static String TASK_STATUS_FINISH = "finish";
	
	
	// 做任务状态：保存
	public final static String DO_TASK_SAVE = "save";
	// 做任务状态：提交
	public final static String DO_TASK_COMMIT = "commit";
	//参加实践小组
	public final static String JOIN_PRACTICE = "join";
	//未参加实践小组
	public final static String NOT_JOIN_PRACTICE = "not join";
	//提交过实践任务或日志周志
	public final static String PRACTICE_COMMIT = "commit";
	//未提交过实践任务或日志周志
	public final static String PRACTICE_NOT_COMMIT = "not commit";
	
}
