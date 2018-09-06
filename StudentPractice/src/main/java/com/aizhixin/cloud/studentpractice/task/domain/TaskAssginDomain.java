/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;




import java.util.ArrayList;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="实践任务分配")
@Data
@EqualsAndHashCode(callSuper=false)
public class TaskAssginDomain {
	
	@ApiModelProperty(value = "实践课程id集合", required = false)
	private ArrayList<String> weekTaskIdList = new ArrayList<String>();
	
	@ApiModelProperty(value = "实践任务id集合", required = false)
	private ArrayList<String> practiceTaskIdList = new ArrayList<String>();
	
	@ApiModelProperty(value = "实践小组id集合", required = true)
	private ArrayList<Long> practiceGroupIdList = new ArrayList<Long>();
	
	@ApiModelProperty(value = "开始时间", required = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date beginDate;
	
	@ApiModelProperty(value = "结束时间", required = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endDate;
	
	
}
