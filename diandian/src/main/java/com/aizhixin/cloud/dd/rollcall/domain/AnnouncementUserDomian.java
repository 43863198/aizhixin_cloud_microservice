package com.aizhixin.cloud.dd.rollcall.domain;

import java.util.HashSet;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AnnouncementUserDomian {
	@ApiModelProperty(value="用户id集合")
	private Set<Long> userIds=new HashSet<>();
	@ApiModelProperty(value="行政班id集合")
	private Set<Long> classesIds=new HashSet<>();
	@ApiModelProperty(value="教学班id集合")
	private Set<Long> teachingClassIds=new HashSet<>();
	@ApiModelProperty(value="专业id集合")
	private Set<Long> profIds=new HashSet<>();
	@ApiModelProperty(value="学院id集合")
	private Set<Long> collegeIds=new HashSet<>();
}
