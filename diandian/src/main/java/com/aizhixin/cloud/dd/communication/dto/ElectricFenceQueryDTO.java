package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 电子围栏按学生姓名及学号查询结果
 *
 */
public class ElectricFenceQueryDTO extends BaseDTO {
	
	@NotNull
    @ApiModelProperty(value = "状态", required = false)
    protected String            status;
	
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
    protected String            adress;
	
	@NotNull
    @ApiModelProperty(value = "通报时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    protected Date            noticeTime;
	
	@NotNull
    @ApiModelProperty(value = "离校时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    protected Date            leaveSchoolStartTime;
	
	@NotNull
    @ApiModelProperty(value = "离线时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm" , timezone = "GMT+8")
    protected Date            leaveConnectStartTime;
	
	@NotNull
    @ApiModelProperty(value = "离线次数", required = false)
    protected long            leaveConnectTime;
	
	@NotNull
    @ApiModelProperty(value = "联系方式", required = false)
    protected String            phone;

	@ApiModelProperty(value = "经纬度", required = false)
	protected String            lltude;
	  
	@ApiModelProperty(value = "联系方式", required = false)
	protected String            lltudes;
	
	@ApiModelProperty(value = "申报时间间隔", required = false)
	protected long            noticeTimeInterval;

	@ApiModelProperty(value = "头像", required = false)
	protected String 			avatar;
	
	
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public Date getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(Date noticeTime) {
		this.noticeTime = noticeTime;
	}

	public Date getLeaveSchoolStartTime() {
		return leaveSchoolStartTime;
	}

	public void setLeaveSchoolStartTime(Date leaveSchoolStartTime) {
		this.leaveSchoolStartTime = leaveSchoolStartTime;
	}

	public Date getLeaveConnectStartTime() {
		return leaveConnectStartTime;
	}

	public void setLeaveConnectStartTime(Date leaveConnectStartTime) {
		this.leaveConnectStartTime = leaveConnectStartTime;
	}

	public long getLeaveConnectTime() {
		return leaveConnectTime;
	}

	public void setLeaveConnectTime(long leaveConnectTime) {
		this.leaveConnectTime = leaveConnectTime;
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

	@Override
	public String toString() {
		return "ElectricFenceQueryDTO [status=" + status + ", name=" + name
				+ ", college=" + college + ", major=" + major + ", className="
				+ className + ", adress=" + adress + ", noticeTime="
				+ noticeTime + ", leaveSchoolStartTime=" + leaveSchoolStartTime
				+ ", leaveConnectStartTime=" + leaveConnectStartTime
				+ ", leaveConnectTime=" + leaveConnectTime + ", phone=" + phone
				+ ", lltude=" + lltude + ", lltudes=" + lltudes
				+ ", noticeTimeInterval=" + noticeTimeInterval + "]";
	}
	
	
	
}
	
