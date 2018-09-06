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

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="logicId和id信息")
@NoArgsConstructor
@ToString
public class LogicIdAndIdDomain {
	@ApiModelProperty(value = "LOGIC_ID")
	@Getter @Setter protected Long logicId;

	@ApiModelProperty(value = "ID")
	@Getter @Setter protected Long id;

	public LogicIdAndIdDomain(Long logicId, Long id) {
		this.logicId = logicId;
		this.id = id;
	}
}
