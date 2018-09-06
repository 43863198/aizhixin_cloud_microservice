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
public class StuTaskStatisticsDomain {
	
	@ApiModelProperty(value = "学生学号", required = false)
	protected String jobNum;
	
	@ApiModelProperty(value = "学生名称", required = false)
	protected String studentName;
	
	@ApiModelProperty(value = "实践企业名称", required = false)
	protected String enterpriseName;
	
	@ApiModelProperty(value = "机构id", required = false)
	protected Long orgId;
	
	@ApiModelProperty(value = "学生id", required = false)
	protected Long studentId;
	
	@ApiModelProperty(value = "导师名称", required = false)
	protected String mentorName;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	protected String counselorName;
	
	@ApiModelProperty(value = "总任务数", required = false)
	protected int totalNum;
	
	@ApiModelProperty(value = "通过任务数", required = false)
	protected int passNum;
	
	@ApiModelProperty(value = "未通过任务数", required = false)
	protected int notPassNum;
	
	@ApiModelProperty(value = "被打回任务数", required = false)
	protected int backToNum;
	
	@ApiModelProperty(value = "待审核任务数", required = false)
	protected int checkPendingNum;
	
	@ApiModelProperty(value = "未提交任务数", required = false)
	protected int uncommitNum;
	
	@ApiModelProperty(value = "任务状态(uncommit:待提交,checkPending:待审核,backTo:已打回,finish:已完成)", required = false)
	protected String studentTaskStatus;
	
	@ApiModelProperty(value = "班级id", required = false)
	private Long classId;
	
	@ApiModelProperty(value = "院系id", required = false)
	private Long collegeId;
	
	@ApiModelProperty(value = "专业id", required = false)
	private Long professionalId;
	
	@ApiModelProperty(value = "提交日报数量", required = false)
	private Integer dailyNum;
	
	@ApiModelProperty(value = "提交周报数量", required = false)
	private Integer weeklyNum;
	
	@ApiModelProperty(value = "提交月报数量", required = false)
	private Integer monthlyNum;
	
	@ApiModelProperty(value = "年级", required = false)
	private String grade;
	
	@ApiModelProperty(value = "班级名称", required = false)
	private String className;
	
	@ApiModelProperty(value = "院系名称", required = false)
	private String collegeName;
	
	@ApiModelProperty(value = "专业名称", required = false)
	private String professionalName;
	
	@ApiModelProperty(value = "实践计划Id", required = false)
	private Long groupId;
	
	@ApiModelProperty(value = "实践计划名称", required = false)
	private String groupName;
	
	@ApiModelProperty(value = "学生实践是否激活（true:激活,false:未激活）", required = false)
	private boolean isActive;
	
	@ApiModelProperty(value = "学生实践是否参与[提交过日志月志或实践任务]（true:参与,false:未参与）", required = false)
	private boolean isJoin;
}
