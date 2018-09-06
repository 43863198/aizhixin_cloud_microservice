/**
 * 
 */
package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhen.pan
 *
 */
@ToString
public class IdUserNameBase extends IdNameDomain {
	@ApiModelProperty(value = "用户ID", required = true, position=30)
	@Getter @Setter protected Long userId;
	public IdUserNameBase() {}

	public IdUserNameBase(Long id, String name) {
		super(id, name);
	}
}
