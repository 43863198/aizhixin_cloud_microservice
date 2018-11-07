package com.aizhixin.cloud.studentpractice.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "课表")
@Data
@EqualsAndHashCode(callSuper = false)
public class PushMessageDTO {

	private Long id;

	@ApiModelProperty(value = "消息标题")
	private String title;
	@ApiModelProperty(value = "消息内容")
	private String content;
	@ApiModelProperty(value = "业务消息内容(任务打分分数)")
	private String businessContent;
	@ApiModelProperty(value = "消息模块")
	private String module;
	@ApiModelProperty(value = "发送消息的方法")
	private String function;
	@ApiModelProperty(value = "发送时间")
	private String pushTime;
	@ApiModelProperty(value = "是否已读")
	private Boolean haveRead;
	private boolean push = true;
	@ApiModelProperty(value = "实践课程id")
	private String weekTaskId;
	@ApiModelProperty(value = "实践导师任务id")
	private String mentorTaskId;
	@ApiModelProperty(value = "实践学生任务id")
	private String stuTaskId;
	@ApiModelProperty(value = "实践任务名称/周日志标题/实践计划标题")
	private String taskName;
	@ApiModelProperty(value = "实践课程名称")
	private String weekTaskName;
	@ApiModelProperty(value = "学生任务审批状态")
	private String stuTaskStatus;
	@ApiModelProperty(value = "周日志id")
	private String summaryId;
	@ApiModelProperty(value = "实践报告id")
	private String reportId;
	@ApiModelProperty(value = "实践计划id")
	private Long groupId;
	
	@ApiModelProperty(value = "消息接收人id集合")
    List<Long> userIds = new ArrayList<Long>();
	
	@ApiModelProperty(value = "学生任务id和学生id对应关系")
	HashMap<Long,String> stuTaskIdMap = new HashMap<Long,String>();
}
