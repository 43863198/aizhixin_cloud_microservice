/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;




import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class TaskCountDomain {
	
	@ApiModelProperty(value = "实践小组id", required = false)
	private long groupId;
	
	@ApiModelProperty(value = "实践课程id", required = false)
	private String weekTaskId;
	
	@ApiModelProperty(value = "实践任务id", required = false)
	private String taskId;

	@ApiModelProperty(value = "发布任务数量", required = false)
	private long taskCountNum;
	
	@ApiModelProperty(value = "导师任务id", required = false)
	private String mentorTaskId;
	
	@ApiModelProperty(value = "学生任务状态", required = false)
	private String taskStatus;
	
	
	
}
