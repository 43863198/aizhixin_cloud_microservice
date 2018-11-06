package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoomInfoDomain {
	@ApiModelProperty(value="宿舍id")
	private Long roomId;
	@ApiModelProperty(value="楼栋名称")
	private String floorName;
	@ApiModelProperty(value="单元号")
	private String unitNo;
	@ApiModelProperty(value="楼层号")
	private String floorNo;
	@ApiModelProperty(value="宿舍号")
	private String no;
	@ApiModelProperty(value="床位数")
	private Integer beds;
	@ApiModelProperty(value="空床位数")
	private Integer emBeds;
	@ApiModelProperty(value="开发状态")
	private boolean open;
	@ApiModelProperty(value="是否分配")
	private boolean assgin;
	@ApiModelProperty(value="性别")
	private String gender;
	@ApiModelProperty(value="专业名称")
	private String profNames;

	@ApiModelProperty(value="辅导员名称")
	private String counselorNames;
	@ApiModelProperty(value="年级")
	private String grade;
}
