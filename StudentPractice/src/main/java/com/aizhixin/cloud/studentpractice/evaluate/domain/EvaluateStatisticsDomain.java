/**
 * 
 */
package com.aizhixin.cloud.studentpractice.evaluate.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description="实践评价统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class EvaluateStatisticsDomain {
	
	
	@ApiModelProperty(value = "ID")
	private String id;

	@ApiModelProperty(value = "第一项评分(0-10分,两分为一颗星)", required = false)
	private Integer firstEvaluate;
	
	@ApiModelProperty(value = "第二项评分(0-10分,两分为一颗星)", required = false)
	private Integer secondEvaluate;
	
	@ApiModelProperty(value = "评价建议", required = false)
	private String advice;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	private String counselorName;
	
	@ApiModelProperty(value = "辅导员学号")
	private String couJobNum;
	
	@ApiModelProperty(value = "学生名称", required = false)
	private String studentName;
	
	@ApiModelProperty(value = "学生学号")
	private String stuJobNum;
	
	@ApiModelProperty(value = "导师名称")
	private String mentorName;
	
	@ApiModelProperty(value = "实践参与计划名称")
	private String groupName;
	
	@ApiModelProperty(value = "学生所属班级名称")
	private String className;
	
	@ApiModelProperty(value = "学生所属专业名称")
	private String professionalName;
	
	@ApiModelProperty(value = "学生所属学院名称")
	private String collegeName;
	
	
	
}
