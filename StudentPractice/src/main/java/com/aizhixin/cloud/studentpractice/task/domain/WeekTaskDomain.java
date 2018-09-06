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

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="实践课程/周任务")
@Data
@EqualsAndHashCode(callSuper=false)
public class WeekTaskDomain {
	
	protected String id;
	
	@ApiModelProperty(value = "机构id", required = false)
	protected Long orgId;
	
	@ApiModelProperty(value = "实践第几周", required = true)
	private String weekNo;
	
	@ApiModelProperty(value = "任务标题", required = true, position=1)
	@Size(min = 1, max = 100)
	protected String taskTitle;
	
	@ApiModelProperty(value = "开始时间", required = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date beginDate;
	
	@ApiModelProperty(value = "结束时间", required = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endDate;
	
	@ApiModelProperty(value = "备注", required = false)
	protected String remark;
	
	@ApiModelProperty(value = "任务进度", required = false)
	private String progress;
	
	@ApiModelProperty(value = "完成任务数", required = false)
	private long totalNum; 
	
	@ApiModelProperty(value = "未完成任务数", required = false)
	private long unfinishNum; 
	
	@ApiModelProperty(value = "任务数", required = false)
	private long taskNum; 
	
	@ApiModelProperty(value = "学时", required = false)
	private Integer classHour;
	
	
}
