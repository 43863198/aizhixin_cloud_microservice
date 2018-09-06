/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;




import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="学生实践任务统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class CountDomain {
	
	@ApiModelProperty(value = "未提交任务数", required = false)
	private long uncommitNum;

	@ApiModelProperty(value = "待审核任务数", required = false)
	private long checkPendingNum;
	
	@ApiModelProperty(value = "被打回任务数", required = false)
	private long backToNum;
	
	@ApiModelProperty(value = "已通过任务数", required = false)
	private long passNum;
	
	@ApiModelProperty(value = "未通过任务数", required = false)
	private long notPassNum;
	
	@ApiModelProperty(value = "参加实践人数", required = false)
	private long practiceNum;
	
	@ApiModelProperty(value = "未参加实践人数", required = false)
	private long notPracticeNum;
	
	@ApiModelProperty(value = "已完成实践任务数", required = false)
	private long finishNum;
	
	
}
