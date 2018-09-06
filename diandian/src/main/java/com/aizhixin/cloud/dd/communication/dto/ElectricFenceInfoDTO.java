package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * @author HUM
 * 电子围栏设置
 *
 * */
@ApiModel
public class ElectricFenceInfoDTO extends BaseDTO {

	 @NotNull
	 @ApiModelProperty(value = "学校范围", required = false)
	 protected List<List<Lonlat>>              lltudes;


	 @NotNull
	 @ApiModelProperty(value = "监控日期", required = true)
	 protected List<Long>              monitorDate;

	 @NotNull
	 @ApiModelProperty(value = "非监控日期", required = true)
	 protected List<Long>             nomonitorDate;


	 @NotNull
	 @ApiModelProperty(value = "学期", required = true)
	 protected Long              semesterId;


    public List<List<Lonlat>> getLltudes() {
        return lltudes;
    }

    public void setLltudes(List<List<Lonlat>> lltudes) {
        this.lltudes = lltudes;
    }

    public List<Long> getMonitorDate() {
		return monitorDate;
	}

	public void setMonitorDate(List<Long> monitorDate) {
		this.monitorDate = monitorDate;
	}

	public List<Long> getNomonitorDate() {
		return nomonitorDate;
	}

	public void setNomonitorDate(List<Long> nomonitorDate) {
		this.nomonitorDate = nomonitorDate;
	}
	public Long getSemesterId() {
		return semesterId;
	}

	public void setSemesterId(Long semesterId) {
		this.semesterId = semesterId;
	}

	public ElectricFenceInfoDTO() {
		super();

	}

    public ElectricFenceInfoDTO(List<List<Lonlat>> lltudes, List<Long> monitorDate, List<Long> nomonitorDate, Long semesterId) {
        this.lltudes = lltudes;
        this.monitorDate = monitorDate;
        this.nomonitorDate = nomonitorDate;
        this.semesterId = semesterId;
    }
}
