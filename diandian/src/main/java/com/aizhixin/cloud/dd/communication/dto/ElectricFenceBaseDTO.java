package com.aizhixin.cloud.dd.communication.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import javax.validation.constraints.NotNull;

import java.util.Date;

import javax.validation.constraints.NotNull;
/**
 *
 * @author HUM
 * 手机端GPS申报信息
 *
 * */
@ApiModel
@Data
@NoArgsConstructor
public class ElectricFenceBaseDTO{
	@ApiModelProperty(value = "GPS位置", required = true)
	private String              address;
	@ApiModelProperty(value = "设备码", required = true)
	private String              equipmentCode;
	@ApiModelProperty(value = "经纬度", required = true)
	private String              lltude;
	@ApiModelProperty(value = "连线方式", required = false)
	private String              connectWay;
	@ApiModelProperty(value = "操作用户", required = false)
	private Long              userId;
	@ApiModelProperty(value = "学校ID", required = false)
	private Long              organId;

	public ElectricFenceBaseDTO(String address, String equipmentCode,
								String lltude, String connectWay) {
		this.address = address;
		this.equipmentCode = equipmentCode;
		this.lltude = lltude;
		this.connectWay = connectWay;
	}
}
