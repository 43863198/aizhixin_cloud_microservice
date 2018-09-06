/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author zhen.pan
 *
 */
@ToString
public class IdCodeNameBase extends IdNameDomain implements java.io.Serializable {
	@NotNull
	@ApiModelProperty(value = "编号", position=30)
	@Getter @Setter protected String code;
	public IdCodeNameBase() {}

	public IdCodeNameBase(Long id, String name, String code) {
		super(id, name);
		this.code = code;
	}
}
