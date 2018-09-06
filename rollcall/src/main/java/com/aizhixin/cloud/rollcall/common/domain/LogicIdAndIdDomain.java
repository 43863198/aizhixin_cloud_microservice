/**
 * 
 */
package com.aizhixin.cloud.rollcall.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="logicId和id信息")
@Data
public class LogicIdAndIdDomain {
	@ApiModelProperty(value = "LOGIC_ID")
	protected Long logicId;

	@ApiModelProperty(value = "ID")
	protected Long id;

	public LogicIdAndIdDomain() {}
	public LogicIdAndIdDomain(Long logicId, Long id) {
		this.logicId = logicId;
		this.id = id;
	}
}
