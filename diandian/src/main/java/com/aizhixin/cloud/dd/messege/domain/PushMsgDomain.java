package com.aizhixin.cloud.dd.messege.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PushMsgDomain {
	@ApiModelProperty(value="模块")
	private String module;
	@ApiModelProperty(value="模块名称")
	private String moduleName;
	@ApiModelProperty(value="函数")
	private String function;
	@ApiModelProperty(value="最后一次推送时间")
	private String lastPushTime;
	@ApiModelProperty(value="未读数")
	private Long notRead;
	@ApiModelProperty(value="跳转类型")
	private String jumpType;
	@ApiModelProperty(value="跳转地址")
	private String jumpUrl;
	@ApiModelProperty(value="图标地址")
	private String icon;
	@ApiModelProperty(value="每个模块下最新记录")
	private String newInfo;
}
