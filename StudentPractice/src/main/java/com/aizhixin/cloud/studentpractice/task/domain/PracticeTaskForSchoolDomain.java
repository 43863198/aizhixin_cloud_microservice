/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;




import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;




import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="实践任务")
@Data
@EqualsAndHashCode(callSuper=false)
public class PracticeTaskForSchoolDomain {
	
	@ApiModelProperty(value = "任务id", required = false)
	protected String id;
	
	@ApiModelProperty(value = "任务名称", required = false)
	private String taskName;
	
	@ApiModelProperty(value = "实践课程名称", required = false)
	private String weekTaskName;
	
	@ApiModelProperty(value = "时长", required = false)
	private Integer classHour;
	
	@ApiModelProperty(value = "实践小组名称", required = false)
	private String groupName;
	
	@ApiModelProperty(value = "创建者名称", required = false)
	private String creatorName;
	
	@ApiModelProperty(value = "开始时间", required = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date beginDate;
	
	@ApiModelProperty(value = "结束时间", required = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date deadLine;
	
}
