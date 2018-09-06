package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 
 * 查询超出范围学生的信息
 */        
public class ElectricFenceQueryOutOfRangeDTO extends BaseDTO {
	@ApiModelProperty(value = "id", required = false)
	protected Long            id;
	

	@ApiModelProperty(value = "姓名", required = false)
	protected String            name;
	
	@ApiModelProperty(value = "离线时长", required = false)
	protected String            address;
	
	@NotNull
    @ApiModelProperty(value = "通报时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    protected Date            noticeTime;

	@ApiModelProperty(value = "离线时长", required = false)
	protected Long            noticeTimeInterval;
	
	@ApiModelProperty(value = "经纬度", required = false)
	protected String            lltude;
	  
	@ApiModelProperty(value = "联系方式", required = false)
	protected String            lltudes;
	
	@ApiModelProperty(value = "联系方式", required = false)
	protected Long            outOfRange;
	
	@ApiModelProperty(value = "头像", required = false)
	protected String 			avatar;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(Date noticeTime) {
		this.noticeTime = noticeTime;
	}

	public Long getNoticeTimeInterval() {
		return noticeTimeInterval;
	}

	public void setNoticeTimeInterval(Long noticeTimeInterval) {
		this.noticeTimeInterval = noticeTimeInterval;
	}

	public String getLltude() {
		return lltude;
	}

	public void setLltude(String lltude) {
		this.lltude = lltude;
	}

	public String getLltudes() {
		return lltudes;
	}

	public void setLltudes(String lltudes) {
		this.lltudes = lltudes;
	}

	public Long getOutOfRange() {
		return outOfRange;
	}

	public void setOutOfRange(Long outOfRange) {
		this.outOfRange = outOfRange;
	}

	
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return "ElectricFenceQueryOutOfRangeDTO [id=" + id + ", name=" + name + ", address=" + address + ", noticeTime="
				+ noticeTime + ", noticeTimeInterval=" + noticeTimeInterval + ", lltude=" + lltude + ", lltudes="
				+ lltudes + ", outOfRange=" + outOfRange + ", avatar=" + avatar + "]";
	}

	
	
	
}
