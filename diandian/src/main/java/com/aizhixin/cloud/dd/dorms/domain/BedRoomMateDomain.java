package com.aizhixin.cloud.dd.dorms.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BedRoomMateDomain {
	@ApiModelProperty(value="楼栋名称")
	private String floorName;
	@ApiModelProperty(value="宿舍类型")
	private Integer roomType;
	@ApiModelProperty(value="单元")
	private String unitNo;
	@ApiModelProperty(value="楼层")
	private String floorNo;
	@ApiModelProperty(value="宿舍号")
	private String no;
	@ApiModelProperty(value="床铺名称")
	private String bedName;
	@ApiModelProperty(value="床铺类型")
	private Integer bedType;
	@ApiModelProperty(value="图片地址")
	private String imageUrl;
	@ApiModelProperty(value="室友信息")
	private List<StuBedInfoDomain> sl=new ArrayList<>();
}
