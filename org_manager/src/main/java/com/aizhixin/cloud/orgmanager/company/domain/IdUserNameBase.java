/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhen.pan
 *
 */
@ToString
public class IdUserNameBase extends IdNameDomain implements java.io.Serializable {
	@ApiModelProperty(value = "用户ID", required = true, position=30)
	@Getter @Setter protected Long userId;
	public IdUserNameBase () {}

	public IdUserNameBase(Long id, String name) {
		super(id, name);
	}
}
