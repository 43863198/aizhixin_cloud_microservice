/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;





import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="学生实践统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class StatisticalDomain {
	
	@ApiModelProperty(value = "学生学号", required = false)
	protected String jobNum;
	
	@ApiModelProperty(value = "学生名称", required = false)
	protected String studentName;
	
	@ApiModelProperty(value = "学生头像")
	private String studentAvatar;
	
	@ApiModelProperty(value = "学生id", required = false)
	protected Long studentId;
	
	@ApiModelProperty(value = "未提交任务数", required = false)
	private Integer uncommitNum;
	
	@ApiModelProperty(value = "待审核任务数", required = false)
	private Integer checkPendingNum;
	
	@ApiModelProperty(value = "被打回任务数", required = false)
	private Integer backToNum;
	
	@ApiModelProperty(value = "完成任务数", required = false)
	private Integer passNum;
	
	@ApiModelProperty(value = "提交的日报数量", required = false)
	private Integer dailyNum;
	
	@ApiModelProperty(value = "提交的周报总数量", required = false)
	private Integer weeklyNum;
	
	@ApiModelProperty(value = "提交的月报总数量", required = false)
	private Integer monthlyNum;
	
	@ApiModelProperty(value = "提交的日报/周报/月报总数量", required = false)
	private Integer summaryTotalNum;
	
	@ApiModelProperty(value = "应打卡天数", required = false)
	private Integer signinTotalNum;
	
	@ApiModelProperty(value = "请假天数", required = false)
	private Integer leaveNum;
	
	@ApiModelProperty(value = "正常打卡天数", required = false)
	private Integer signinNormalNum;
	
	@ApiModelProperty(value = "实践计划Id", required = false)
	private Long groupId;
	
	@ApiModelProperty(value = "实践计划名称")
	private String groupName;
}
