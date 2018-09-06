package com.aizhixin.cloud.dd.messege.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MsgModuleDomain{
	@ApiModelProperty(value="主键")
	private Long id;
	@ApiModelProperty(value="模块")
	private String module;
	@ApiModelProperty(value="模块区分函数")
	private String function;
	@ApiModelProperty(value="模块名称")
	private String moduleName;
	@ApiModelProperty(value="跳转类型")
	private String jumpType;
	@ApiModelProperty(value="跳转地址")
	private String jumpUrl;
	@ApiModelProperty(value="图标地址")
	private String icon;

}
