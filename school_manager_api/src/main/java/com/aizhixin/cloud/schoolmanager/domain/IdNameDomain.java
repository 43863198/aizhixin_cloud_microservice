/**
 * 
 */
package com.aizhixin.cloud.schoolmanager.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="id和name信息")
@ToString
public class IdNameDomain {
	@ApiModelProperty(value = "ID", required = false, allowableValues = "range[1,infinity]", position=1)
	@Getter @Setter protected Long id;

	@NotNull(message ="名称不能为空")
	@ApiModelProperty(value = "名称", required = true, position=2)
	@Size(min = 1, max = 140)
	@Getter @Setter protected String name;
	
	public IdNameDomain() {}
	public IdNameDomain(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
}
