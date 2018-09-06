package com.aizhixin.cloud.rollcall.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zhen.pan on 2017/7/6.
 */
@ApiModel(description="系统时间信息")
@Data
public class SystemTimeDomain {
    @ApiModelProperty(value = "系统时间")
    private String time;
}
