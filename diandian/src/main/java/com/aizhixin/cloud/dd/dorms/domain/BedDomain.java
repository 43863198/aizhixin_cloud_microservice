package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BedDomain {
	@ApiModelProperty(value="床铺id")
    private Long bedId;
	@ApiModelProperty(value="床位名称")
	private String name;
	@ApiModelProperty(value="床位类型，10：上铺，20：下铺")
	private Integer bedType;
	@ApiModelProperty(value="是否住人")
	private boolean live;
	@ApiModelProperty(value="学生姓名")
	private String stuName;
}
