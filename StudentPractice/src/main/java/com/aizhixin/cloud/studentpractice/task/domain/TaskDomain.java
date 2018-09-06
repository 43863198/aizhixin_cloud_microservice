/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.StringIdNameDomain;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="任务信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class TaskDomain {
	
	@ApiModelProperty(value = "任务id", required = false)
	protected String id;

	@ApiModelProperty(value = "任务名称", required = true, position=1)
	@Size(min = 1, max = 80)
	private String taskName;
	
	@ApiModelProperty(value = "任务内容", required = false)
	private String description;
	
	@ApiModelProperty(value = "任务开始时间 ", required = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date beginDate;
	
	@ApiModelProperty(value = "任务截至时间 ", required = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date deadLine;
	
	@ApiModelProperty(value = "任务状态", required = false)
	private String mentorTaskStatus;

	@ApiModelProperty(value = "任务进度", required = false)
	private String progress;

	@ApiModelProperty(value = "导师id", required = false)
	private Long mentorId;
	
	@ApiModelProperty(value = "导师名称", required = false)
	private String mentorName;
	
	@ApiModelProperty(value = "任务创建者id", required = false)
	private Long createdBy;
	
	@ApiModelProperty(value = "实践企业名称", required = false)
	private String enterpriseName;
	
	@ApiModelProperty(value = "任务创建者名称", required = false)
	private String creatorName;
	
	@ApiModelProperty(value = "学时", required = false)
	private Integer classHour;
	
	@ApiModelProperty(value = "任务创建时间", required = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	protected Date createDate;
	
	@ApiModelProperty(value = "分配学生信息", required = false)
	private List<StuInforDomain> userList = new ArrayList<StuInforDomain>();
	
	@ApiModelProperty(value = "附件信息", required = false)
	private List<FileDomain> fileList = new ArrayList<FileDomain>();
	
	@ApiModelProperty(value = "实践课程/周任务id", required = false)
	private String weekTaskId;
	
	@ApiModelProperty(value = "实践任务id", required = false)
	private String taskId;
	
	@ApiModelProperty(value = "已打分人数", required = false)
	private int scoreNum;
	
	@ApiModelProperty(value = "实践组id")
	private Long groupId;
	
	@ApiModelProperty(value = "实践小组名称")
	private String groupName;
	
	@ApiModelProperty(value = "完成任务数", required = false)
	private Long finishNum;
	
	@ApiModelProperty(value = "待审核任务数", required = false)
	private Long checkPendingNum;

	@ApiModelProperty(value = "教师任务详情描述")
	private String describe;

	@ApiModelProperty(value = "教师任务详情描述图片集合")
	private List<String> imageList = new ArrayList<String>();
}
