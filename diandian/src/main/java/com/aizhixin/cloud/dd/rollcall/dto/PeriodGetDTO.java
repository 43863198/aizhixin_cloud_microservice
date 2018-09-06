package com.aizhixin.cloud.dd.rollcall.dto;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "课程节信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class PeriodGetDTO {

    @ApiModelProperty(value = "第几节课", required = true)
    private String name;

    @ApiModelProperty(value = "id", required = true)
    private Long id;

    @ApiModelProperty(value = "开始时间", required = true)
    private String beginTime;

    @ApiModelProperty(value = "开始时间", required = true)
    private String endTime;

    @ApiModelProperty(value = "单双节  单节课为1，双节课为2", required = true)
    private String periodType;

    @ApiModelProperty(value = "开始节数", required = true)
    private Integer startNum;

    private Long organId;


}
