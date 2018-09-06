package com.aizhixin.cloud.dd.alumnicircle.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="校友圈")
public class AlumniCircleSendDomin {
	@ApiModelProperty(value="校友圈id")
	private Long id;
	@ApiModelProperty(value="发送者id")
	private Long fromUserId;
	@ApiModelProperty(value="发送者名称")
	private String fromUserName;
	@ApiModelProperty(value="学校id")
	private Long orgId;
	@ApiModelProperty(value="学校名称")
	private String orgName;
	@ApiModelProperty(value="学院id")
	private Long collegeId;
	@ApiModelProperty(value="学院名称")
	private String collegeName;
	@ApiModelProperty(value="发送内容")
	private String content;
	@ApiModelProperty(value="点赞数")
	private Integer dzTotal;
	@ApiModelProperty(value="评论数")
	private Integer assessTotal;
	@ApiModelProperty(value="是否昵称发送")
	private boolean nickName;
	@ApiModelProperty(value="校友圈图片集合")
	private List<AlumniCircleFileDomain> alumniCircleFileDomains=new ArrayList<>();
}
