package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoomChooseInfo {
	@ApiModelProperty(value="宿舍id")
	private Long roomId;
	@ApiModelProperty(value="宿舍类型，10：普通，20：套间")
	private Integer roomType;
	@ApiModelProperty(value="宿舍号")
	private String no;
	@ApiModelProperty(value="楼栋名称")
	private String floorName;
	@ApiModelProperty(value="单元")
	private String unitNo;
	@ApiModelProperty(value="楼层")
	private String floorNo;
	@ApiModelProperty(value="空床铺数")
	private Integer emBeds;
}
