/**
 * 
 */
package com.aizhixin.cloud.studentpractice.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="id和count信息")
@Data
public class CountDomain {

	@NotNull
	@ApiModelProperty(value = "ID", required = true)
	protected Long id;

	@NotNull
	@ApiModelProperty(value = "COUNT", required = true)
	protected Long count;
	
	public CountDomain() {}
	public CountDomain(Long id, Long count) {
		this.id = id;
		this.count = count;
	}
}
