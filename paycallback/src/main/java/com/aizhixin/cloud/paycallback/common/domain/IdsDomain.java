package com.aizhixin.cloud.paycallback.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@ApiModel(description="id列表信息")
@NoArgsConstructor
@ToString
public class IdsDomain {

	@NotNull
	@Size(min = 1)
	@ApiModelProperty(value = "ID列表", required = true)
	@Getter @Setter protected Set<Long> ids;
}
