package com.aizhixin.cloud.dd.dorms.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data 
public class RoomAndBedInfoDomain {
	@ApiModelProperty(value="宿舍id")
	private Long roomId;
	@ApiModelProperty(value="宿舍类型，10：普通型，20：套间型")
	private Integer roomType;
	@ApiModelProperty(value="楼栋名称")
	private String floorName;
	@ApiModelProperty(value="宿舍图片")
	private String imageUrl;
	@ApiModelProperty(value="单元号")
	private String unitNo;
	@ApiModelProperty(value="楼层号")
	private String floorNo;
	@ApiModelProperty(value="宿舍号")
	private String no;
	@ApiModelProperty(value="床铺信息")
	private List<BedDomain>  bdl=new ArrayList<>();
}
