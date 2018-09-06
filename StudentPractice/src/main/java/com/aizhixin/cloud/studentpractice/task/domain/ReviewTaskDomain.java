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




@ApiModel(description="任务评审信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class ReviewTaskDomain {
	
	@ApiModelProperty(value = "学生任务id", required = false)
	protected String studentTaskId;

	@ApiModelProperty(value = "评审建议", required = false)
	private String reviewAdvice;
	
	@ApiModelProperty(value = "评审结果(finish:完成,backTo:被打回)", required = false)
	private String reviewResult;
	
	@ApiModelProperty(value = "评审分数", required = false)
	private String reviewScore;

	@ApiModelProperty(value = "第几次评审", required = false)
	private int reviewNumber;

//	@ApiModelProperty(value = "任务评审者名称", required = false)
//	private String creatorName;
	
	@ApiModelProperty(value = "附件信息", required = false)
	private List<FileDomain> fileList = new ArrayList<FileDomain>();
	
	
}
