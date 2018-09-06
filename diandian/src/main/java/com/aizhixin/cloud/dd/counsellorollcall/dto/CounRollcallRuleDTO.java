package com.aizhixin.cloud.dd.counsellorollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "点名规则")
public class CounRollcallRuleDTO {

    @ApiModelProperty(value = "规则ID")
    private Long id;
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "开始弹性时间")
    private Integer startFlexTime;
    @ApiModelProperty(value = "迟到时间")
    private Integer lateTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    @ApiModelProperty(value = "结束弹性时间")
    private Integer endFlexTime;
    @ApiModelProperty(value = "结束签到时间")
    private Integer stopTime;
    @ApiModelProperty(value = "签到日期（周几，逗号隔开）")
    private String days;
}
