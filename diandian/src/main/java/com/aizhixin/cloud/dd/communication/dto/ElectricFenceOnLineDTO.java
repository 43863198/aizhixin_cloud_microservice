package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 电子围栏查找在线学生信息
 *
 */
public class ElectricFenceOnLineDTO extends BaseDTO {


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
    @ApiModelProperty(value = "通报时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    protected Date            noticeTime;

	@ApiModelProperty(value = "头像", required = false)
	protected String 			avatar;

	@NotNull
    @ApiModelProperty(value = "联系方式", required = false)
    protected String            phone;

	@ApiModelProperty(value = "经纬度", required = false)
	protected String            lltude;

	@ApiModelProperty(value = "联系方式", required = false)
	protected String            lltudes;

	@ApiModelProperty(value = "申报时间间隔", required = false)
	protected long            noticeTimeInterval;

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



	public Date getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(Date noticeTime) {
		this.noticeTime = noticeTime;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public long getNoticeTimeInterval() {
		return noticeTimeInterval;
	}

	public void setNoticeTimeInterval(long noticeTimeInterval) {
		this.noticeTimeInterval = noticeTimeInterval;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "ElectricFenceOnLineDTO [id=" + id + ", name=" + name + ", college=" + college + ", major=" + major
				+ ", className=" + className + ", address=" + address + ", noticeTime=" + noticeTime + ", avatar="
				+ avatar + ", phone=" + phone + ", lltude=" + lltude + ", lltudes=" + lltudes + ", noticeTimeInterval="
				+ noticeTimeInterval + "]";
	}








}
