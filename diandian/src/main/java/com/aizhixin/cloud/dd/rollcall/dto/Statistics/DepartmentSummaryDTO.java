package com.aizhixin.cloud.dd.rollcall.dto.Statistics;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-22
 */
@Data
public class DepartmentSummaryDTO {
    @ApiModelProperty(value = "学院名称")
    private String collegeName;
    @ApiModelProperty(value = "总数")
    private int total;
    @ApiModelProperty(value = "正常数")
    private int normal;
    @ApiModelProperty(value = "迟到数")
    private int late;
    @ApiModelProperty(value = "请假数")
    private int leave;
    @ApiModelProperty(value = "旷课数")
    private int absenteeism;
    @ApiModelProperty(value = "早退")
    private int askForLeave;
    @ApiModelProperty(value = "到课率")
    private Double classRate;

}
