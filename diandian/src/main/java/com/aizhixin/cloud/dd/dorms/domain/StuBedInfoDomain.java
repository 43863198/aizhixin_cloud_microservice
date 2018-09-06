package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StuBedInfoDomain {
	@ApiModelProperty(value="学生名称")
	private String name;
	@ApiModelProperty(value="地址")
	private String address;
	@ApiModelProperty(value="床铺名称")
	private String bedName;
	@ApiModelProperty(value="床铺类型")
	private Integer bedType;
}
