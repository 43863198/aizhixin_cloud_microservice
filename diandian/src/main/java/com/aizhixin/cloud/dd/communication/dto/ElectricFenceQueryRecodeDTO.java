package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 电子围栏按学生姓名及学号查询历史数据结果
 *
 */
public class ElectricFenceQueryRecodeDTO extends BaseDTO {
	
	@NotNull
	@ApiModelProperty(value = "id", required = false)
    protected Long            id;
	
	@NotNull
    @ApiModelProperty(value = "姓名", required = false)
    protected String            name;
	
	@NotNull
    @ApiModelProperty(value = "学院", required = false)
    protected String            college;
	
	@NotNull
    @ApiModelProperty(value = "专业", required = false)
    protected String            major;
	
	@NotNull
    @ApiModelProperty(value = "班级", required = false)
    protected String            className;
	
	@NotNull
    @ApiModelProperty(value = "位置", required = false)
    protected String            address;
	
	@NotNull
    @ApiModelProperty(value = "超出范围次数", required = false)
    protected  long            OutOfRange;
	
	@NotNull
    @ApiModelProperty(value = "离线时长", required = false)
    protected String            offlineTime;
	
	@NotNull
    @ApiModelProperty(value = "通报时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    protected Date            noticeTime;
	
	@NotNull
    @ApiModelProperty(value = "联系方式", required = false)
    protected String            phone;

	@ApiModelProperty(value = "经纬度", required = false)
	protected String            lltude;
	  
	@ApiModelProperty(value = "联系方式", required = false)
	protected String            lltudes; 

	@ApiModelProperty(value = "联系方式", required = false)
	protected long            noticeTimeInterval; 
	
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

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	

	public long getOutOfRange() {
		return OutOfRange;
	}

	public void setOutOfRange(long outOfRange) {
		OutOfRange = outOfRange;
	}

	public String getOfflineTime() {
		return offlineTime;
	}

	public void setOfflineTime(String offlineTime) {
		this.offlineTime = offlineTime;
	}

	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Date getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(Date noticeTime) {
		this.noticeTime = noticeTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getNoticeTimeInterval() {
		return noticeTimeInterval;
	}

	public void setNoticeTimeInterval(long noticeTimeInterval) {
		this.noticeTimeInterval = noticeTimeInterval;
	}
	

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public ElectricFenceQueryRecodeDTO(Long id, String name, String college,
			String major, String className, String address, long outOfRange,
			String offlineTime, Date noticeTime, String phone, String lltude,
			String lltudes, long noticeTimeInterval) {
		super();
		this.id = id;
		this.name = name;
		this.college = college;
		this.major = major;
		this.className = className;
		this.address = address;
		this.OutOfRange = outOfRange;
		this.offlineTime = offlineTime;
		this.noticeTime = noticeTime;
		this.phone = phone;
		this.lltude = lltude;
		this.lltudes = lltudes;
		this.noticeTimeInterval = noticeTimeInterval;
	}

	public ElectricFenceQueryRecodeDTO() {
		super();

	}

	@Override
	public String toString() {
		return "ElectricFenceQueryRecodeDTO [id=" + id + ", name=" + name + ", college=" + college + ", major=" + major
				+ ", className=" + className + ", address=" + address + ", OutOfRange=" + OutOfRange + ", offlineTime="
				+ offlineTime + ", noticeTime=" + noticeTime + ", phone=" + phone + ", lltude=" + lltude + ", lltudes="
				+ lltudes + ", noticeTimeInterval=" + noticeTimeInterval + ", avatar=" + avatar + "]";
	}

	
	
	
	
}
