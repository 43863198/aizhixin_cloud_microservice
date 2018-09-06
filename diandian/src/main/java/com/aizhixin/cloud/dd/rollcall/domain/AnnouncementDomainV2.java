package com.aizhixin.cloud.dd.rollcall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AnnouncementDomainV2{
	@ApiModelProperty(value="主键")
	private Long id;
	@ApiModelProperty(value="发送者id")
	private Long fromUserId;
	@ApiModelProperty(value="发送者名称")
	private String fromUserName;
	@ApiModelProperty(value="发送者头像")
	private String fromUserAvatar;
	@ApiModelProperty(value="发送者类型，60：教师，70：学生")
	private Integer fromUserType;
	@ApiModelProperty(value="发送者性别")
	private String fromUserSex;
	@ApiModelProperty(value="学院id")
	private Long fromCollegeId;
	@ApiModelProperty(value="学院名称")
	private String fromCollegeName;
	@ApiModelProperty(value="班级id")
	private Long fromClassesId;
	@ApiModelProperty(value="班级名称")
	private String fromClassesName;
	@ApiModelProperty(value="专业id")
	private Long fromProfId;
	@ApiModelProperty(value="专业名称")
	private String fromProfName;
	@ApiModelProperty(value="发送者电话")
	private String fromUserPhone;
 	@ApiModelProperty(value="发送内容")
	private String content;
	@ApiModelProperty(value="是否定时发送")
	private boolean timeTask;
	@ApiModelProperty(value="人员发送组id")
	private String groupId;
	@ApiModelProperty(value="是否可以评论")
	private boolean assess;
	@ApiModelProperty(value="评论数量")
	private Integer assessTotal;
	@ApiModelProperty(value="发送时间，不是定时发送不填")
	private Date sendTime;
	@ApiModelProperty(value="发送人数")
	private Integer sendUserTotal;
	@ApiModelProperty(value="是否发送")
	private boolean send;
	@ApiModelProperty(value="文件信息")
	private List<AnnouncementFileDomain> announcementFileDomainList=new ArrayList<>();
}
