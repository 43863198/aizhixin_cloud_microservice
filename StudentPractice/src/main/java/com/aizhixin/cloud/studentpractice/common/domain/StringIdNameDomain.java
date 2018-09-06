/**
 * 
 */
package com.aizhixin.cloud.studentpractice.common.domain;

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
@ApiModel(description="id和name字符型信息")
@ToString
public class StringIdNameDomain {
	@NotNull(message ="id不能为空")
	@ApiModelProperty(value = "ID", required = true,  position=1)
	@Size(min = 1, max = 36)
	@Getter @Setter protected String id;

	@NotNull(message ="名称不能为空")
	@ApiModelProperty(value = "名称", required = true, position=2)
	@Size(min = 1, max = 80)
	@Getter @Setter protected String name;
	
	public StringIdNameDomain() {}
	public StringIdNameDomain(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
}
