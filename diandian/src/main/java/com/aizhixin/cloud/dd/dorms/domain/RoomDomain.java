package com.aizhixin.cloud.dd.dorms.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoomDomain {
	@ApiModelProperty(value="房间主键")
	private Long id;
	@ApiModelProperty(value="宿舍号")
	private String no;
	@ApiModelProperty(value="宿舍楼id")
	private Long floorId;
	@ApiModelProperty(value="所在单元号")
	private String unitNo;
	@ApiModelProperty(value="所在楼层号")
	private String floorNo;
	@ApiModelProperty(value="房间备注")
	private String roomDesc;
	@ApiModelProperty(value="床位数量,可以不传")
	private Integer beds;
	@ApiModelProperty(value="空床位数量,可以不传")
	private Integer emBeds;
	@ApiModelProperty(value="是否开放")
	private boolean open;
	@ApiModelProperty(value="床位信息")
	private List<BedDomain> bedList=new ArrayList<>();
	@ApiModelProperty(value="年级")
	private String grade;
}
