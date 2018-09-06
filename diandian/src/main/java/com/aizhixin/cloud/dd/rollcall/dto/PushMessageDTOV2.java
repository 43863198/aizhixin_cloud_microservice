package com.aizhixin.cloud.dd.rollcall.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "消息信息添加")
@Data
@EqualsAndHashCode(callSuper = false)
public class PushMessageDTOV2 {
	@ApiModelProperty(value="true:推送，false:不推送")
	private boolean push;
    @ApiModelProperty(value="消息主题")
	private String title;
    @ApiModelProperty(value="消息内容")
	private String content;
    @ApiModelProperty(value="消息备注")
	private String businessContent;
    @ApiModelProperty(value="所属模块")
	private String module;
    @ApiModelProperty(value="模块消息函数，随意自定义")
	private String function;
    @ApiModelProperty(value="接收者集合")
    private List<Long> userIds=new ArrayList<>();
}
