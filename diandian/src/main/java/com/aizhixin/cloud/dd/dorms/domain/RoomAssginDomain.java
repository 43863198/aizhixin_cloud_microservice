package com.aizhixin.cloud.dd.dorms.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoomAssginDomain {
	@ApiModelProperty(value="专业id")
	private Long profId;
	@ApiModelProperty(value="专业名称")
	private String profName;
	@ApiModelProperty(value="学院id")
	private Long collegeId;
	@ApiModelProperty(value="学院名称")
	private String collegeName;
	@ApiModelProperty(value="房间id集合")
	private List<Long> roomIds=new ArrayList<>();
	@ApiModelProperty(value="性别类型 10：男，20：女")
	private Integer sexType;
	@ApiModelProperty(value="辅导员id,多个逗号隔开")
	private String counselorIds;
	@ApiModelProperty(value="辅导员name,多个逗号隔开")
	private String counselorNames;
}
