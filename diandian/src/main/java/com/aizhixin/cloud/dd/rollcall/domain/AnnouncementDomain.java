package com.aizhixin.cloud.dd.rollcall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AnnouncementDomain{
	@ApiModelProperty(value="主键")
	private Long id;
	@ApiModelProperty(value="发送内容")
	private String content;
	@ApiModelProperty(value="是否可以评论")
	private boolean assess;
	@ApiModelProperty(value="发送时间，不是定时发送不填")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
	private Date sendTime;
	@ApiModelProperty(value="发送人数")
	private Integer sendUserTotal;
	@ApiModelProperty(value="是否定时发送")
	private boolean timeTask;
	@ApiModelProperty(value="文件信息")
	private List<AnnouncementFileDomain> announcementFileDomainList=new ArrayList<>();
	@ApiModelProperty(value="接收人员")
	private AnnouncementUserDomian announcementUserDomian;
}
