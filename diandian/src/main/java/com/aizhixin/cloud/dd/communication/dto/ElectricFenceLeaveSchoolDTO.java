package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 *
 * @author www.0001.GA
 *
 */
public class ElectricFenceLeaveSchoolDTO extends BaseDTO {


	@ApiModelProperty(value = "id", required = false)
	protected Long            id;

	@NotNull
    @ApiModelProperty(value = "通报时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    protected Date            noticeTime;


	@ApiModelProperty(value = "经纬度", required = false)
	protected String            lltude;

	@ApiModelProperty(value = "联系方式", required = false)
	protected String            lltudes;

	@ApiModelProperty(value = "超出范围人数", required = false)
	protected Long            outOfRange;

	@ApiModelProperty(value = "离线人数", required = false)
	protected Long            leavenum;

	@ApiModelProperty(value = "在线人数", required = false)
	protected Long            OnLinenum;

    @ApiModelProperty(value = "未激活人数", required = false)
    protected Long            NotActive;

	@ApiModelProperty(value = "申报时隔", required = false)
	protected Long            noticeTimeInterval;

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

	public Long getLeavenum() {
		return leavenum;
	}

	public void setLeavenum(Long leavenum) {
		this.leavenum = leavenum;
	}

	public Long getNoticeTimeInterval() {
		return noticeTimeInterval;
	}

	public void setNoticeTimeInterval(Long noticeTimeInterval) {
		this.noticeTimeInterval = noticeTimeInterval;
	}


    public Long getNotActive() {
        return NotActive;
    }

    public void setNotActive(Long notActive) {
        NotActive = notActive;
    }

    public Long getOnLinenum() {
		return OnLinenum;
	}

	public void setOnLinenum(Long onLinenum) {
		OnLinenum = onLinenum;
	}

    public ElectricFenceLeaveSchoolDTO(Long id, Date noticeTime, String lltude, String lltudes, Long outOfRange, Long leavenum, Long onLinenum, Long notActive, Long noticeTimeInterval) {
        this.id = id;
        this.noticeTime = noticeTime;
        this.lltude = lltude;
        this.lltudes = lltudes;
        this.outOfRange = outOfRange;
        this.leavenum = leavenum;
        this.OnLinenum = onLinenum;
        this.NotActive = notActive;
        this.noticeTimeInterval = noticeTimeInterval;
    }

    public ElectricFenceLeaveSchoolDTO() {
		super();

	}


    @Override
    public String toString() {
        return "ElectricFenceLeaveSchoolDTO{" +
            "id=" + id +
            ", noticeTime=" + noticeTime +
            ", lltude='" + lltude + '\'' +
            ", lltudes='" + lltudes + '\'' +
            ", outOfRange=" + outOfRange +
            ", leavenum=" + leavenum +
            ", OnLinenum=" + OnLinenum +
            ", NotActive=" + NotActive +
            ", noticeTimeInterval=" + noticeTimeInterval +
            '}';
    }
}
