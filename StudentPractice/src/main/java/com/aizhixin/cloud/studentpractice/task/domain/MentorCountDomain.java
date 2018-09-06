/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;




import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="企业导师周任务统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class MentorCountDomain {
	
	@ApiModelProperty(value = "周任务Id", required = false)
	protected String weekTaskId;

	@ApiModelProperty(value = "周任务完成进度", required = false)
	protected double progress;
	
}
