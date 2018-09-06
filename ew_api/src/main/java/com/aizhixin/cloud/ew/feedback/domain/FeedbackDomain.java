package com.aizhixin.cloud.ew.feedback.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "问题反馈保存结构体")
@Data
public class FeedbackDomain {

	@ApiModelProperty(value = "id ID", position = 1, required = false)
	private Long id;
	@ApiModelProperty(value = "name 姓名", position = 2, required = true)
	private String name;
	@ApiModelProperty(value = "phone 手机号", position = 3, required = true)
	private String phone;
	@ApiModelProperty(value = "phoneDeviceInfo 手机设备信息", position = 4, required = true)
	private String phoneDeviceInfo;
	@ApiModelProperty(value = "school 学校", position = 6, required = true)
	private String school;
	@ApiModelProperty(value = "classes 班级", position = 7, required = true)
	private String classes;
	@ApiModelProperty(value = "description 问题描述", position = 8, required = true)
	private String description;
	@ApiModelProperty(value = "pictureUrls 上传图片数组", position = 9, required = false)
	private List<String> pictureUrls;
}
