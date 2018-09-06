package com.aizhixin.cloud.studentpractice.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "SP_PUSH_MESSAGE")
public class PushMessage extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1651645736366646053L;

	// 被推送用户ID
	@Column(name = "user_id")
	@Getter
	@Setter
	private Long userId;

	// 推送内容
	@Column(name = "content")
	@Getter
	@Setter
	private String content;

	// 推送标题
	@Column(name = "title")
	@Getter
	@Setter
	private String title;

	// 模块
	@Column(name = "module")
	@Getter
	@Setter
	private String module;

	// 方法
	@Column(name = "function")
	@Getter
	@Setter
	private String function;

	// 是否已读
	@Column(name = "have_read")
	@Getter
	@Setter
	private Boolean haveRead;

	// 推送时间
	@NotNull
	@Column(name = "push_time")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Getter
	@Setter
	private Date pushTime;

	// 业务内容
	@Column(name = "business_content")
	@Getter
	@Setter
	private String businessContent;
	
	
	@Column(name = "week_task_id")
	@Getter
	@Setter
	private String weekTaskId;
	
	@Column(name = "mentorTaskId")
	@Getter
	@Setter
	private String mentorTaskId;
	
	@Column(name = "stuTaskId")
	@Getter
	@Setter
	private String stuTaskId;
	
	@Column(name = "TASK_NAME")
	@Getter
	@Setter
	private String taskName;
	
	
	@Column(name = "WEEK_TASK_NAME")
	@Getter
	@Setter
	private String weekTaskName;
	
	@Column(name = "STUDENT_TASK_STATUS")
	@Getter
	@Setter
	private String stuTaskStatus;
	
	@Column(name = "SUMMARY_ID")
	@Getter
	@Setter
	private String summaryId;
	
	@Column(name = "REPORT_ID")
	@Getter
	@Setter
	private String reportId;
	
	@Column(name = "COMMIT_USER_NAME")
	@Getter
	@Setter
	private String commitUserName;
}
