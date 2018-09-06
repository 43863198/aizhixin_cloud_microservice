package com.aizhixin.cloud.orgmanager.electrict.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zhen.pan on 2017/7/7.
 */
@ApiModel(description = "经纬度")
@Data
public class LonLatDomain {
    @ApiModelProperty(value = "经度", required = true)
    private String longitude;
    @ApiModelProperty(value = "纬度", required = true)
    private String latitude;
}