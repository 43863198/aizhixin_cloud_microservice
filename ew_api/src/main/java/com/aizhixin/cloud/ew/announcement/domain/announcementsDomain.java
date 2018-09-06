package com.aizhixin.cloud.ew.announcement.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(description = "公告发布列表信息")
@Data
public class announcementsDomain {
	@ApiModelProperty(value = "ID", position = 1)
	private Long id;
	@ApiModelProperty(value = "title 公告标题", position = 2)
	private String title;
	@ApiModelProperty(value = "title 公告内容", position = 3)
	private String content;
	@ApiModelProperty(value = "publishDate 发布时间", position = 4)
	private String publishDate;
	@ApiModelProperty(value = "createdDate 创建时间", position = 5)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
	@ApiModelProperty(value = "type 公告类型", position = 6)
	private String type;
	@ApiModelProperty(value = "picUrl 公告图片", position = 7)
	private String picUrl;
	@ApiModelProperty(value = "publishStatus 发布状态(0未发布，1已发布)", position = 10)
	private Integer publishStatus;
	
	public announcementsDomain(Long id, String title, String content, String publishDate, Date createdDate,
			String type, Integer publishStatus) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.publishDate = publishDate;
		this.createdDate = createdDate;
		this.type = type;
		this.publishStatus = publishStatus;
	}

	public announcementsDomain(Long id, String title, String publishDate) {
		this.id = id;
		this.title = title;
		this.publishDate = publishDate;
	}

	public announcementsDomain(Long id, String title, String publishDate, Date createdDate, String type, String picUrl,
			Integer publishStatus) {
		this.id = id;
		this.title = title;
		this.publishDate = publishDate;
		this.createdDate = createdDate;
		this.type = type;
		this.picUrl = picUrl;
		this.publishStatus = publishStatus;
	}
}
