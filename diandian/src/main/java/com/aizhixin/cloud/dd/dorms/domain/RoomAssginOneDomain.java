package com.aizhixin.cloud.dd.dorms.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoomAssginOneDomain {
	@ApiModelProperty(value="专业信息集合")
	private List<RoomAssginDomainV2> radl=new ArrayList<>();
	@ApiModelProperty(value="性别类型 10：男，20：女")
	private Integer sexType;
	@ApiModelProperty(value="宿舍id")
	private Long roomId;

	@ApiModelProperty(value="辅导员id,多个逗号隔开")
	private String counselorIds;
	@ApiModelProperty(value="辅导员name,多个逗号隔开")
	private String counselorNames;
}
