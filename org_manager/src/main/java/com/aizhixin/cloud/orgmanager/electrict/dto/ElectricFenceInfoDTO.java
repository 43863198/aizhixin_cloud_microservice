package com.aizhixin.cloud.orgmanager.electrict.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * @author HUM
 * 电子围栏设置
 *
 * */
@ApiModel
public class ElectricFenceInfoDTO extends BaseDTO{

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

	@NotNull
	@Getter
	@Setter
	@ApiModelProperty(value = "学校id", required = true)
	protected Long              organId;

	@NotNull
	@Getter
	@Setter
	@ApiModelProperty(value = "围栏状态（10:开始；20:关闭）", required = true)
	private Integer setupOrClose;


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
		// TODO Auto-generated constructor stub
	}

    public ElectricFenceInfoDTO(List<List<Lonlat>> lltudes, List<Long> monitorDate, List<Long> nomonitorDate, Long semesterId,Long organId) {
        this.lltudes = lltudes;
        this.monitorDate = monitorDate;
        this.nomonitorDate = nomonitorDate;
        this.semesterId = semesterId;
		this.organId = organId;
    }
}
