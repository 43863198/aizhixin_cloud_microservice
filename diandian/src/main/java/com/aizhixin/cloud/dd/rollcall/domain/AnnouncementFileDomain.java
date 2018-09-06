package com.aizhixin.cloud.dd.rollcall.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AnnouncementFileDomain {
	@ApiModelProperty(value="文件名称")
	private String fileName;
	@ApiModelProperty(value="文件地址")
	private String fileSrc;
	@ApiModelProperty(value="文件类型")
	private String type;
	@ApiModelProperty(value="文件名称")
	private Long fileSize;
}
