package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoomAssginDomainV2 {
	@ApiModelProperty(value="专业id")
	private Long profId;
	@ApiModelProperty(value="专业名称")
	private String profName;
	@ApiModelProperty(value="学院id")
	private Long collegeId;
	@ApiModelProperty(value="学院名称")
	private String collegeName;
}
