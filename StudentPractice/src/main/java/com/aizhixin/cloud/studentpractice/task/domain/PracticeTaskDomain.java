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
public class PracticeTaskDomain {
	
	protected String id;
	
	@ApiModelProperty(value = "任务名称", required = false)
	@Size(min = 1, max = 80)
	private String taskName;
	
	@ApiModelProperty(value = "任务内容", required = false)
	@Size(min = 1, max = 500)
	private String description;
	
	private Long orgId;
	
	@ApiModelProperty(value = "实践课程id", required = false)
	private String weekTaskId;
	
	@ApiModelProperty(value = "所属实践课程", required = false)
	private String weekTaskName;
	
	@ApiModelProperty(value = "时长", required = false)
	private Integer classHour;
	
	@ApiModelProperty(value = "实践参与计划id", required = false)
	private Long groupId;
	
	@ApiModelProperty(value = "开始时间", required = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date beginDate;
	
	@ApiModelProperty(value = "结束时间", required = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endDate;
	
	@ApiModelProperty(value = "附件信息", required = false)
	private List<FileDomain> fileList = new ArrayList<FileDomain>();

	@ApiModelProperty(value = "描述信息", required = false)
	private String describe;
	
	@ApiModelProperty(value = "第几页", required = false)
	private Integer pageNumber;
	
	@ApiModelProperty(value = "每页数据的数目", required = false)
	private Integer pageSize;
}
