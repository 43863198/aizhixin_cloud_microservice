package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * 
 * *历史记录中离线时长的查询
 * 
 * */
public class ElectricFenceLeaveNum extends BaseDTO {
	@NotNull
    @ApiModelProperty(value = "姓名", required = false)
    protected Long            id;
	
	@NotNull
    @ApiModelProperty(value = "姓名", required = false)
    protected Long            leaveNum;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLeaveNum() {
		return leaveNum;
	}

	public void setLeaveNum(Long leaveNum) {
		this.leaveNum = leaveNum;
	}
	
	
}
