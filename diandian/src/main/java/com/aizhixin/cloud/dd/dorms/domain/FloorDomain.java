package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FloorDomain {
	@ApiModelProperty(value="主键，添加不传，修改传")
	private Long id;
	@ApiModelProperty(value="序号，添加修改都不传")
	private Integer no;
	@ApiModelProperty(value="楼栋名称")
	private String name;
	@ApiModelProperty(value="楼栋类型,10:普通型，20：套间型")
	private Integer floorType;
	@ApiModelProperty(value="楼层总数")
	private Integer floorNum;
	@ApiModelProperty(value="单元总数")
	private Integer unitNum;
	@ApiModelProperty(value="备注")
	private String floorDesc;
	@ApiModelProperty(value="图片地址")
	private String floorImage;
	@ApiModelProperty(value="是否能编辑")
	private boolean  editor;
}
