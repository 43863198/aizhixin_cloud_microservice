/**
 * 
 */
package com.aizhixin.cloud.studentpractice.evaluate.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description="实践评价")
@Data
@EqualsAndHashCode(callSuper=false)
public class EvaluateDomain {
	
	
	@ApiModelProperty(value = "ID")
	private String id;

	@ApiModelProperty(value = "机构id", required = false)
	private Long orgId;
	
	@ApiModelProperty(value = "第一项评分(0-10分,两分为一颗星)", required = false)
	private Integer firstEvaluate;
	
	@ApiModelProperty(value = "第二项评分(0-10分,两分为一颗星)", required = false)
	private Integer secondEvaluate;
	
	@ApiModelProperty(value = "评价类型(s:学生自评,stc:学生对辅导员,stm:学生对导师,cts:辅导员对学生,mts:导师对学生)", required = false)
	private String evaluateType;
	
	@ApiModelProperty(value = "评价建议", required = false)
	private String advice;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	private String counselorName;
	
	@ApiModelProperty(value = "辅导员学号")
	private String couJobNum;
	
	@ApiModelProperty(value = "学生id", required = false)
	private Long studentId;
	
	@ApiModelProperty(value = "学生名称", required = false)
	private String studentName;
	
	@ApiModelProperty(value = "学生学号")
	private String stuJobNum;
	
	@ApiModelProperty(value = "导师id")
	private Long mentorId;
	
	@ApiModelProperty(value = "导师名称")
	private String mentorName;
	
	@ApiModelProperty(value = "实践参与计划id")
	private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称")
	private String groupName;
	
}
