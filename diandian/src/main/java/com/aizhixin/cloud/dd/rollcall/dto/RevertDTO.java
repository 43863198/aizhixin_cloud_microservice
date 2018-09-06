package com.aizhixin.cloud.dd.rollcall.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RevertDTO {
	@ApiModelProperty(value="回复表主键id",required=false)
    private Long id;
	@ApiModelProperty(value="评论表主键id",required=false)
    private Long assessId;
	@ApiModelProperty(value="回复id，回复评论不用传，回复评论下的某条消息传消息id",required=false)
    private Long revertId;
	@ApiModelProperty(value="回复模块",required=false)
    private String module;
	@ApiModelProperty(value="回复者id",required=false)
    private Long fromUserId;
	@ApiModelProperty(value="回复者名称",required=false)
    private String fromUserName;
	@ApiModelProperty(value="回复者头像",required=false)
	private String fromUserAvatar;
	@ApiModelProperty(value="接收者id",required=false)
    private Long toUserId;
	@ApiModelProperty(value="接收者名称",required=false)
    private String toUserName;
	@ApiModelProperty(value="回复内容",required=false)
    private String content;
	@ApiModelProperty(value="是否匿名展示回复者头像",required=false)
	private boolean anonymity;
	@ApiModelProperty(value="创建时间",required=false)
	private Date createdDate;
	@ApiModelProperty(value="课节",required=false)
	private Long scheduleId;
	@ApiModelProperty(value="课程名",required=false)
	private String courseName;
	@ApiModelProperty(value="上课时间",required=true)
	private String  courseDate;
	@ApiModelProperty(value="来源id",required=false)
	private Long sourceId;
}
