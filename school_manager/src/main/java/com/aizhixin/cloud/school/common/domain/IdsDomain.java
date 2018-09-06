package com.aizhixin.cloud.school.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@ApiModel(description="id列表信息")
@Data
public class IdsDomain {

	@NotNull
	@Size(min = 1)
	@ApiModelProperty(value = "ID列表", required = true)
	protected Set<Long> ids;
}
