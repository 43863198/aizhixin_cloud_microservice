package com.aizhixin.cloud.dd.alumnicircle.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="校友圈文件")
public class AlumniCircleFileDomain {
	@ApiModelProperty(value="校友圈文件展示地址")
	private String srcUrl;
	@ApiModelProperty(value="校友圈文件大小")
	private Long fileSize;
}
