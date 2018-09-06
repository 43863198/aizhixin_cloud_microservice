/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;




import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="实践教学汇总统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class PeopleCountDomain {
	
	@ApiModelProperty(value = "班级名称")
	private String className;
	
	@ApiModelProperty(value = "班级id")
	private Long classId;
	
	@ApiModelProperty(value = "院系名称")
	private String collegeName;
	
	@ApiModelProperty(value = "院系id")
	private Long collegeId;
	
	@ApiModelProperty(value = "专业名称")
	private String professionalName;
	
	@ApiModelProperty(value = "专业id")
	private Long professionalId;
	
	@ApiModelProperty(value = "总任务数")
	private int stuNum;
	
	@ApiModelProperty(value = "实践人数")
	private int praticeNum;
	
	@ApiModelProperty(value = "未实践人数")
	private int notPraticeNum;
	
	@ApiModelProperty(value = "提交过日志或任务的学生人数")
	private int joinNum;
	
	@ApiModelProperty(value = "未提交过日志或任务的学生人数")
	private int notJoinNum;
	
	@ApiModelProperty(value = "实践日志周志提交总数")
	private int summaryNum;
	
	@ApiModelProperty(value = "实践报告提交总数")
	private int reportNum; 
	
}
