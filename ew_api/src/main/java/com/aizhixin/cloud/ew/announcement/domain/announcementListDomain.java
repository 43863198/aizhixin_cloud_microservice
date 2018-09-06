package com.aizhixin.cloud.ew.announcement.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "公告发布信息")
@Data
public class announcementListDomain {
	@ApiModelProperty(value = "ID", position = 1)
	private Long id;
	@ApiModelProperty(value = "title 公告标题", position = 2)
	private String title;
	@ApiModelProperty(value = "type 公告类型(10顶岗实习公告,20学校最新活动)", position = 3)
	private String type;
	@ApiModelProperty(value = "content 公告内容", position = 4)
	private String content;
	@ApiModelProperty(value = "publishDate 发布时间", position = 5)
	private String publishDate;
	@ApiModelProperty(value = "picUrl 公告图片", position = 6)
	private String picUrl;
	@ApiModelProperty(value = "publishStatus 发布状态(0未发布，1已发布)", position = 10)
	private Integer publishStatus;
}
