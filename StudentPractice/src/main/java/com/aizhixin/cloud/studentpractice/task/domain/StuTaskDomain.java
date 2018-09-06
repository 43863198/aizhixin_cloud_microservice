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




@ApiModel(description="学生任务信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class StuTaskDomain {
	
	@ApiModelProperty(value = "学生任务id", required = false)
	protected String stuTaskid;
	
	@ApiModelProperty(value = "导师任务id", required = false)
	protected String mentorTaskid;

	@ApiModelProperty(value = "任务结果描述", required = false)
	private String resultDescription;
	
	@ApiModelProperty(value = "学生id", required = false)
	private Long studentId;
	
	@ApiModelProperty(value = "学生名称", required = false)
	private String studentName;
	@ApiModelProperty(value = "学号", required = false)
	private String jobNum;
	@ApiModelProperty(value = "任务状态(uncommit:待提交,checkPending:待审核,backTo:已打回,finish:已完成)", required = false)
	private String studentTaskStatus;
	
	@ApiModelProperty(value = "评审分数", required = false)
	private String reviewScore;
	
	@ApiModelProperty(value = "评审打回次数", required = false)
	private int backToNum;
	
	@ApiModelProperty(value = "任务创建者id", required = false)
	private Long createdBy;
	
	@ApiModelProperty(value = "任务创建时间", required = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	protected Date createDate;

	@ApiModelProperty(value = "任务标题", required = false)
	private String taskName;
	
	@ApiModelProperty(value = "任务内容", required = false)
	private String description;
	
	@ApiModelProperty(value = "学时", required = false)
	private Integer classHour;
	
	@ApiModelProperty(value = "任务截至时间 ", required = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date deadLine;

	@ApiModelProperty(value = "学生任务附件信息", required = false)
	private List<FileDomain> StufileList = new ArrayList<FileDomain>();
	
	
}
