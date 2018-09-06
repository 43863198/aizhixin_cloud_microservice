package com.aizhixin.cloud.orgmanager.electrict.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by zhen.pan on 2017/7/7.
 */
@ApiModel(description = "电子围栏设置信息")
@Data
public class ElectricFenceInfoDomain {
    @ApiModelProperty(value = "围栏设置信息ID")
    private Long id;
    @NotNull
    @ApiModelProperty(value = "学校围栏，每个围栏用一个数组表示")
    private List<List<LonLatDomain>> lltudes;
    @NotNull
    @ApiModelProperty(value = "监控日期，按照天来表示，每天一条数据", required = true)
    private List<Long> monitorDate;
    @NotNull
    @ApiModelProperty(value = "非监控日期，按照天来表示，每天一条数据", required = true)
    private List<Long> nomonitorDate;
    @NotNull
    @ApiModelProperty(value = "学期", required = true)
    private Long semesterId;
    @NotNull
    @ApiModelProperty(value = "学校ID", required = true)
    private Long orgId;
    @NotNull
    @ApiModelProperty(value = "启用停用(10启用，20关闭)", required = true)
    private Integer setupOrClose;
    @NotNull
    @ApiModelProperty(value = "操作人ID", required = true)
    private Long userId;
}
