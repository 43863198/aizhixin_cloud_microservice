/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;



import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;

import java.util.List;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="学年信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class YearDomain {
	
	@ApiModelProperty(value = "学年ID", required = true)
	protected String id;

	@NotNull(message ="学年名称不能为空")
	@ApiModelProperty(value = "学年名称", required = true)
	private String name;
	
	@ApiModelProperty(value = "学校ID", required = true)
	@NotNull(message ="机构id不能为空")
	@Digits(fraction = 0, integer = 20)
	private Long orgId;
	
	@NotNull(message ="操作用户id不能为空")
	@ApiModelProperty(value = "操作用户id ", required = true)
	private Long userId;
	
	@ApiModelProperty(value = "学期集合", required = false)
	private List<SemesterDomain> semesterList;
	
	@ApiModelProperty(value = "学期id和name集合", required = false)
	private List<IdNameDomain> semesterIdNameList;
	
	public YearDomain() {}
	public YearDomain(String id, String name) {
		this.id = id;
		this.name = name;
	}
}
