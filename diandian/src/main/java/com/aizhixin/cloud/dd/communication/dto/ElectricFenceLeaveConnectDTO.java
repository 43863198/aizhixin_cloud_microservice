package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 
 * 离线时长
 */
public class ElectricFenceLeaveConnectDTO extends BaseDTO {

	@ApiModelProperty(value = "id", required = false)
	protected Long            id;
	
	@NotNull
    @ApiModelProperty(value = "通报时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    protected Date            noticeTime;

	@ApiModelProperty(value = "姓名", required = false)
	protected String            name;
	
	@ApiModelProperty(value = "离线时长", required = false)
	protected String            leaveNum;
	
	@ApiModelProperty(value = "离线时长", required = false)
	protected Long            noticeTimeInterval;
	
	@ApiModelProperty(value = "位置", required = false)
	protected String            address;
	
	@ApiModelProperty(value = "头像", required = false)
	protected String 			avatar;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(Date noticeTime) {
		this.noticeTime = noticeTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLeaveNum() {
		return leaveNum;
	}

	public void setLeaveNum(String leaveNum) {
		this.leaveNum = leaveNum;
	}

	
	public Long getNoticeTimeInterval() {
		return noticeTimeInterval;
	}

	public void setNoticeTimeInterval(Long noticeTimeInterval) {
		this.noticeTimeInterval = noticeTimeInterval;
	}
	

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ElectricFenceLeaveConnectDTO(Long id, Date noticeTime, String name,
			String leaveNum, Long noticeTimeInterval) {
		super();
		this.id = id;
		this.noticeTime = noticeTime;
		this.name = name;
		this.leaveNum = leaveNum;
		this.noticeTimeInterval = noticeTimeInterval;
	}

	public ElectricFenceLeaveConnectDTO() {
		super();

	}

	@Override
	public String toString() {
		return "ElectricFenceLeaveConnectDTO [id=" + id + ", noticeTime=" + noticeTime + ", name=" + name
				+ ", leaveNum=" + leaveNum + ", noticeTimeInterval=" + noticeTimeInterval + ", address=" + address
				+ ", avatar=" + avatar + "]";
	}

	
	
	

	
	
}
