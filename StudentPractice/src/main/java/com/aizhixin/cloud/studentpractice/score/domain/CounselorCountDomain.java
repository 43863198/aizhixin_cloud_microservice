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




@ApiModel(description="辅导员实践参与过程统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class CounselorCountDomain {
	
	@ApiModelProperty(value = "辅导员工号")
	@Column(name = "JOB_NUM")
	private String jobNum;
	
	@ApiModelProperty(value = "机构id")
	private Long orgId;
	
	@ApiModelProperty(value = "实践参与计划id")
	private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称")
	private String groupName;
	
	@ApiModelProperty(value = "指导学生总数")
	private Integer groupStuNum;
	
	@ApiModelProperty(value = "辅导员id")
	private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称")
	private String counselorName;
	
	@ApiModelProperty(value = "辅导员所属院系id")
	private Long counselorCollegeId;
	
	@ApiModelProperty(value = "辅导员所属院系名称")
	private String counselorCollegeName;
	
	@ApiModelProperty(value = "日报总数(若不需要为:noNeed)")
	private String dailyNum;
	
	@ApiModelProperty(value = "批阅日报数量")
	private Integer reviewDailyNum;
	
	@ApiModelProperty(value = "周报总数(若不需要为:noNeed)")
	private String weeklyNum;
	
	@ApiModelProperty(value = "批阅周报数量")
	private Integer reviewWeeklyNum;
	
	@ApiModelProperty(value = "月报总数(若不需要为:noNeed)")
	private String monthlyNum;
	
	@ApiModelProperty(value = "批阅月报数量")
	private Integer reviewMonthlyNum;
	
	@ApiModelProperty(value = "实践报告总数(若不需要为:noNeed)")
	private String reportNum;
	
	@ApiModelProperty(value = "批阅实践报告数量")
	private Integer reviewReportNum;
	
}
