/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.studentpractice.common.domain.StringIdNameDomain;
import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="任务详情信息(教务查询)")
@Data
@EqualsAndHashCode(callSuper=false)
public class StuTaskDetailForSchoolDomain {
	
	@ApiModelProperty(value = "学生任务id", required = false)
	protected String stuTaskId;
	
	@ApiModelProperty(value = "任务信息id", required = false)
	protected String taskId;
	
	@ApiModelProperty(value = "评审学生任务id", required = false)
	protected String reviewTaskId;
	
	@ApiModelProperty(value = "实践课程名称", required = true, position=1)
	private String weekTaskName;

	@ApiModelProperty(value = "任务名称", required = true, position=1)
	private String taskName;
	
	@ApiModelProperty(value = "任务描述", required = false)
	private String description;
	
	@ApiModelProperty(value = "学生完成任务描述", required = false)
	private String resultDescription;
	
	@ApiModelProperty(value = "任务截至时间 ", required = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date deadLine;
	
	@ApiModelProperty(value = "任务开始时间 ", required = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date beginDate;
	
	@ApiModelProperty(value = "任务状态", required = false)
	private String stuTaskStatus;
	
	@ApiModelProperty(value = "任务得分", required = false)
	private String taskScore;
	
	@ApiModelProperty(value = "任务评语", required = false)
	private String taskAdvice;

	@ApiModelProperty(value = "任务时长", required = false)
	private Integer classHour;
	
	@ApiModelProperty(value = "任务附件信息", required = false)
	private List<File> taskFileList = new ArrayList<File>();
	
	@ApiModelProperty(value = "学生提交附件信息", required = false)
	private List<File> stuFileList = new ArrayList<File>();
	
	@ApiModelProperty(value = "导师评审附件信息", required = false)
	private List<File> reviewFileList = new ArrayList<File>();

	@ApiModelProperty(value = "导师描述", required = false)
	private String describe;
	
}
