package com.aizhixin.cloud.rollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

/**
 * Created by LIMH on 2017/11/28.
 */
@ApiModel(description = "签到")
@ToString
@NoArgsConstructor
public class SignInDomain {
    @Size(max = 128)
    @ApiModelProperty(value = "排课id ", required = true)
    @Getter
    @Setter
    private Long scheduleId;
    @Size(max = 10)
    @ApiModelProperty(value = "点名方式 ", required = true)
    @Getter
    @Setter
    private String rollCallType;
    @Size(max = 6)
    @ApiModelProperty(value = "验证码", required = false)
    @Getter
    @Setter
    private String authCode;
    @Size(max = 50)
    @ApiModelProperty(value = "经纬度", required = false)
    @Getter
    @Setter
    private String gps;
    @Size(max = 200)
    @ApiModelProperty(value = "位置详细信息", required = false)
    @Getter
    @Setter
    private String gpsDetail;
    @Size(max = 10)
    @ApiModelProperty(value = "获取gps方式(wifi/gps/4G)", required = false)
    @Getter
    @Setter
    private String gpsType;
    @Getter
    @Setter
    private String deviceToken;
}
