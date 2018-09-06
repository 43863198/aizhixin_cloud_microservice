/**
 * 
 */
package com.aizhixin.cloud.studentpractice.summary.domain;






import javax.persistence.Column;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="日报，周报，月报数量和实践签到统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class SummaryNumCountDomain {
	
	
	@ApiModelProperty(value = "类型:日报[daily],周报[weekly],月报[monthly]", required = false)
	private String summaryType;
	
	@ApiModelProperty(value = "合计数量", required = false)
	private Integer countNum;
	
	@ApiModelProperty(value = "创建者Id", required = false)
	private Long creatorBy;
	
	@ApiModelProperty(value = "提交日报数量")
	private Integer dailyNum;
	
	@ApiModelProperty(value = "提交周报数量")
	private Integer weeklyNum;
	
	@ApiModelProperty(value = "提交月报数量")
	private Integer monthlyNum;
	
	@ApiModelProperty(value = "实践签到总数")
	private Integer signInTotalNum;
	
	@ApiModelProperty(value = "正常签到数")
	private Integer signInNormalNum;
	
	@ApiModelProperty(value = "请假数")
	private Integer leaveNum;
	
	@ApiModelProperty(value = "实践参与计划id")
	private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称")
	private String groupName;
	
}
