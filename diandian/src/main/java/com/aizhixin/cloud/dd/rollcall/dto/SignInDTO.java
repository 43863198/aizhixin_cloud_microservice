package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 
 * 签到
 * 
 * @author meihua.li
 *
 */
@ApiModel(description = "签到")
@Data
public class SignInDTO {

    @Size(max = 128)
    @ApiModelProperty(value = "排课id ", required = true)
    private Long scheduleId;
    @Size(max = 10)
    @ApiModelProperty(value = "点名方式 ", required = true)
    private String rollCallType;
    @Size(max = 6)
    @ApiModelProperty(value = "验证码", required = false)
    private String authCode;
    @Size(max = 50)
    @ApiModelProperty(value = "经纬度", required = false)
    private String gps;
    @Size(max = 200)
    @ApiModelProperty(value = "位置详细信息", required = false)
    private String gpsDetail;
    @Size(max = 10)
    @ApiModelProperty(value = "获取gps方式(wifi/gps/4G)", required = false)
    private String gpsType;
    private String deviceToken;

}
