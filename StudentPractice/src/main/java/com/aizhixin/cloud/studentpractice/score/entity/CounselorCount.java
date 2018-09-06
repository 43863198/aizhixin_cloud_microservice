
package com.aizhixin.cloud.studentpractice.score.entity;

import io.swagger.annotations.ApiModelProperty;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;


/**
 * 辅导员实践参与统计表
 * @author zhengning
 *
 */
@Entity(name = "SP_COUNSELOR_COUNT")
@ToString
public class CounselorCount  {
	
	@ApiModelProperty(value = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@Getter @Setter private Long id;

	@ApiModelProperty(value = "辅导员工号")
	@Column(name = "JOB_NUM")
	@Getter @Setter private String jobNum;
	
	@ApiModelProperty(value = "机构id")
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	
	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称")
	@Column(name = "GROUP_NAME")
	@Getter @Setter private String groupName;
	
	@ApiModelProperty(value = "指导学生总数")
	@Column(name = "GROUP_STU_NUM")
	@Getter @Setter private Integer groupStuNum;
	
	@ApiModelProperty(value = "辅导员id")
	@Column(name = "COUNSELOR_ID")
	@Getter @Setter private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称")
	@Column(name = "COUNSELOR_NAME")
	@Getter @Setter private String counselorName;
	
	@ApiModelProperty(value = "辅导员所属院系id")
	@Column(name = "COUNSELOR_COLLEGE_ID")
	@Getter @Setter private Long counselorCollegeId;
	
	@ApiModelProperty(value = "辅导员所属院系名称")
	@Column(name = "COUNSELOR_COLLEGE_NAME")
	@Getter @Setter private String counselorCollegeName;
	
	@ApiModelProperty(value = "日报总数(若不需要为:noNeed)")
	@Column(name = "DAILY_NUM")
	@Getter @Setter private String dailyNum;
	
	@ApiModelProperty(value = "批阅日报数量")
	@Column(name = "REVIEW_DAILY_NUM")
	@Getter @Setter private Integer reviewDailyNum;
	
	@ApiModelProperty(value = "周报总数(若不需要为:noNeed)")
	@Column(name = "WEEKLY_NUM")
	@Getter @Setter private String weeklyNum;
	
	@ApiModelProperty(value = "批阅周报数量")
	@Column(name = "REVIEW_WEEKLY_NUM")
	@Getter @Setter private Integer reviewWeeklyNum;
	
	@ApiModelProperty(value = "月报总数(若不需要为:noNeed)")
	@Column(name = "MONTHLY_NUM")
	@Getter @Setter private String monthlyNum;
	
	@ApiModelProperty(value = "批阅月报数量")
	@Column(name = "REVIEW_MONTHLY_NUM")
	@Getter @Setter private Integer reviewMonthlyNum;
	
	@ApiModelProperty(value = "实践报告总数(若不需要为:noNeed)")
	@Column(name = "REPORT_NUM")
	@Getter @Setter private String reportNum;
	
	@ApiModelProperty(value = "批阅实践报告数量")
	@Column(name = "REVIEW_REPORT_NUM")
	@Getter @Setter private Integer reviewReportNum;
	
}
