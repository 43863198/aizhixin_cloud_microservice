package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "课表")
@Data
@EqualsAndHashCode(callSuper = false)
public class PushMessageDTO {

	private Long id;

	private String title;

	private String content;

	private String businessContent;

	private String module;

	private String function;

	private String pushTime;

	private Boolean haveRead;

}
