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
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="学生端任务详情信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class MentorTaskDetailDomain {
	
	@ApiModelProperty(value = "任务id", required = false)
	protected String id;

	@ApiModelProperty(value = "任务名称", required = true, position=1)
	@Size(min = 1, max = 80)
	private String taskName;
	
	@ApiModelProperty(value = "任务描述", required = false)
	private String description;
	
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
	
	@ApiModelProperty(value = "任务创建者名称", required = false)
	private String creatorName;
	
	@ApiModelProperty(value = "附件信息", required = false)
	private List<FileDomain> fileList = new ArrayList<FileDomain>();
	
	@ApiModelProperty(value = "未提交任务集合", required = false)
	private List<StuTaskDomain> uncommitTaskList = new ArrayList<StuTaskDomain>();
	
	@ApiModelProperty(value = "已提交任务集合", required = false)
	private List<StuTaskDomain> commitTaskList = new ArrayList<StuTaskDomain>();
	
	@ApiModelProperty(value = "导师学生信息", required = false)
	private List<StuInforDomain> studentList = new ArrayList<StuInforDomain>();

	@ApiModelProperty(value = "教师任务详情描述")
	private String describe;

	@ApiModelProperty(value = "教师任务详情描述图片集合")
	private List<String> imgList = new ArrayList<String>();
}
