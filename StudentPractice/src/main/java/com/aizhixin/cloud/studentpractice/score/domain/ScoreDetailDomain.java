/**
 * 
 */
package com.aizhixin.cloud.studentpractice.score.domain;

import javax.persistence.Column;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="实践成绩详情")
@Data
@EqualsAndHashCode(callSuper=false)
public class ScoreDetailDomain {
	
	@ApiModelProperty(value = "提交日报数量")
	private Integer dailyNum;
	
	@ApiModelProperty(value = "提交周报数量")
	private Integer weeklyNum;
	
	@ApiModelProperty(value = "提交月报数量")
	private Integer monthlyNum;
	
	@ApiModelProperty(value = "实践报告数据状态:未提交[uncommit],待审核[checkPending],已通过[pass],未通过[notPass],被打回[backTo]", required = false)
	private String reportStatus;
	
	@ApiModelProperty(value = "正常签到数")
	private Integer signInNormalNum;
	
	@ApiModelProperty(value = "请假数")
	private Integer leaveNum;
	
	@ApiModelProperty(value = "通过任务数")
	private int passNum;
	
	@ApiModelProperty(value = "被打回任务数")
	private int backToNum;
	
	@ApiModelProperty(value = "待审核任务数")
	private int checkPendingNum;
	
	@ApiModelProperty(value = "未提交任务数")
	private int uncommitNum;
	
	@ApiModelProperty(value = "任务平均分数")
	private Double avgScore;
	
	@ApiModelProperty(value = "实践计划名称")
	private String groupName;
	
	@ApiModelProperty(value = "打卡总数")
	private Integer signinTotalNum;
}
