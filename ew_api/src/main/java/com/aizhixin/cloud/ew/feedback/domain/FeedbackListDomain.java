package com.aizhixin.cloud.ew.feedback.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "问题反馈列表及详情结构体")
@Data
public class FeedbackListDomain {

	@ApiModelProperty(value = "id ID", position = 1, required = false)
	private Long id;
	@ApiModelProperty(value = "name 姓名", position = 2, required = true)
	private String name;
	@ApiModelProperty(value = "phone 手机号", position = 3, required = true)
	private String phone;
	@ApiModelProperty(value = "school 学校", position = 4, required = true)
	private String school;
	@ApiModelProperty(value = "classes 班级", position = 5, required = true)
	private String classes;
	@ApiModelProperty(value = "createdDate 创建日期", position = 6, required = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
	@ApiModelProperty(value = "phoneDeviceInfo 手机设备信息", position = 7, required = true)
	private String phoneDeviceInfo;
	@ApiModelProperty(value = "description 问题描述", position = 8, required = true)
	private String description;
	@ApiModelProperty(value = "pictureUrls 上传图片数组", position = 9, required = false)
	private List<String> pictureUrls;

	public FeedbackListDomain() {
	}

	public FeedbackListDomain(Long id, String name, String phone, Date createdDate, String description, String phoneDeviceInfo) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.createdDate = createdDate;
		this.description = description;
		this.phoneDeviceInfo = phoneDeviceInfo;
	}

}
