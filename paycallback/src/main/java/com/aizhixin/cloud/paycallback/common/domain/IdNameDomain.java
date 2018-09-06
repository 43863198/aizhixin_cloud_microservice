/**
 * 
 */
package com.aizhixin.cloud.paycallback.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="id和name信息")
@NoArgsConstructor
@ToString
public class IdNameDomain implements java.io.Serializable {
	@ApiModelProperty(value = "ID", allowableValues = "range[1,infinity]", position=1)
	@Getter @Setter protected Long id;

	@NotNull(message ="名称不能为空")
	@ApiModelProperty(value = "名称", required = true, position=2)
	@Getter @Setter protected String name;

	public IdNameDomain(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
}
