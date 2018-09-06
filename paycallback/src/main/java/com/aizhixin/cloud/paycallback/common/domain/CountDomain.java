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
@ApiModel(description="id和count信息")
@NoArgsConstructor
@ToString
public class CountDomain {
	@NotNull
	@ApiModelProperty(value = "ID", required = true)
	@Getter @Setter	protected Long id;

	@NotNull
	@ApiModelProperty(value = "COUNT", required = true)
	@Getter @Setter	protected Long count;

	public CountDomain(Long id, Long count) {
		this.id = id;
		this.count = count;
	}
}
