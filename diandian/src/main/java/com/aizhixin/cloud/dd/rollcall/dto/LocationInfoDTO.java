package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-20
 */
@Data
@ApiModel(description = "学生位置信息")
public class LocationInfoDTO {
    @ApiModelProperty(value = "学生使用量")
    private int activeNumber;
    @ApiModelProperty(value = "学生位置")
    protected List<List<Lonlat>>  lltudes;
}
