/**
 * 
 */
package com.aizhixin.cloud.studentpractice.score.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "实践参与计划与设置内容")
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupSetDomain {
	
	@ApiModelProperty(value = "机构id")
	private Long orgId;

	@ApiModelProperty(value = "参与计划id")
	private Long groupId;

	@ApiModelProperty(value = "参与计划名称")
	private String groupName;
	
	@ApiModelProperty(value = "辅导员id")
	private Long counselorId;

	@ApiModelProperty(value = "辅导员名称")
	private String counselorName;
	
	@ApiModelProperty(value = "辅导员所属院系id")
	private Long counselorCollegeId;
	
	@ApiModelProperty(value = "辅导员所属院系名称")
	private String counselorCollegeName;
	
	@ApiModelProperty(value = "辅导员工号")
	private String jobNum;
	
	@ApiModelProperty(value = "是否需要日志周志")
	private Boolean isNeedSummary;

	@ApiModelProperty(value = "是否需要实践报告")
	private Boolean isNeedReport;
}
