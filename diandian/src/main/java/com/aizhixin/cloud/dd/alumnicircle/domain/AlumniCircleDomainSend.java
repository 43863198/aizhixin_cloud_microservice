package com.aizhixin.cloud.dd.alumnicircle.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@ApiModel(description="校友圈")
public class AlumniCircleDomainSend {
	@ApiModelProperty(value="校友圈id")
	private Long id;
	@ApiModelProperty(value="发送者id")
	private Long fromUserId;
	@ApiModelProperty(value="发送者名称")
	private String fromUserName;
	@ApiModelProperty(value="发送者头像")
	private String fromUserAvatar;
	@ApiModelProperty(value="学校id")
	private Long orgId;
	@ApiModelProperty(value="学校名称")
	private String orgName;
	@ApiModelProperty(value="学院id")
	private Long collegeId;
	@ApiModelProperty(value="学院名称")
	private String collegeName;
	@ApiModelProperty(value="发送模块，0：全国，1：本校",required=true)
	private 	Integer  sendToModule;
	@ApiModelProperty(value="发送内容",required=true)
	private String content;
	@ApiModelProperty(value="是否昵称发送",required=true)
	private boolean nickName;
	@ApiModelProperty(value="校友圈图片集合")
	private List<AlumniCircleFileDomain> alumniCircleFileDomains=new ArrayList<>();
	@ApiModelProperty(value="是否关注")
	private boolean attention;
	@ApiModelProperty(value="是否点赞")
	private boolean dotZan;
	@ApiModelProperty(value="评论者头像集合")
	private List<String> avatars=new ArrayList<>();
	@ApiModelProperty(value="点赞数")
	private Integer dzTotal;
	@ApiModelProperty(value="评论数")
	private Integer assessTotal;
	@ApiModelProperty(value="发布时间")
	private Date createdDate;
	
}
