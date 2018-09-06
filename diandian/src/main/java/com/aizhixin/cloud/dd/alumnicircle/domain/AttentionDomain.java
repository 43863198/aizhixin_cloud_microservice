package com.aizhixin.cloud.dd.alumnicircle.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttentionDomain {
	@ApiModelProperty(value="关注者id")
	private Long attentionUserId;
	@ApiModelProperty(value="关注者名称")
	private String attentionUserName;
	@ApiModelProperty(value="被关注人id")
	private Long followedUserId;
	@ApiModelProperty(value="被关注者名称")
	private String followedUserName;
}
