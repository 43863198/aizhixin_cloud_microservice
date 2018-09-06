package com.aizhixin.cloud.dd.alumnicircle.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DotZanDomain {
	@ApiModelProperty(value="用户id")
	private Long userId;
	@ApiModelProperty(value="用户名称")
	private String userName;
	@ApiModelProperty(value="校友圈id")
	private Long alumniCircleId;
}
