package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BedAndStuDomain {
	@ApiModelProperty(value="宿舍id")
	private Long roomId;
	@ApiModelProperty(value="床铺id")
	private Long bedId;
}
