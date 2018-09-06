package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StuInfoDomain {
	@ApiModelProperty(value="序号")
	private Integer no;
	@ApiModelProperty(value="学生名称")
	private String stuName;
	@ApiModelProperty(value="学号")
	private String stuNo;
	@ApiModelProperty(value="学生id")
	private Long stuId;
	@ApiModelProperty(value="学生头像")
	private String avatar;
	@ApiModelProperty(value="学生地址")
	private String studentSource;
	@ApiModelProperty(value="学生性别")
	private String sex;
	@ApiModelProperty(value="学院名称")
	private String collegeName;
	@ApiModelProperty(value="专业名称")
	private String profName;
	@ApiModelProperty(value="行政班名称")
	private String classesName;
	@ApiModelProperty(value="手机号")
	private String phone;
	@ApiModelProperty(value="房间Id")
	private Long roomId;
	@ApiModelProperty(value="床位Id")
	private Long bedId;
	@ApiModelProperty(value="床位名称")
	private String bedName;
	@ApiModelProperty(value="床位类型 (10:上铺 20:下铺)")
	private Integer bedType;
}
