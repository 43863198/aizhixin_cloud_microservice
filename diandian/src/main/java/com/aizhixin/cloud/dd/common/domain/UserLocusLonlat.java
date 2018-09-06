package com.aizhixin.cloud.dd.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Created by jianwei.wu on ${date} ${time}
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel(description="使用电子围栏的用户轨迹信息")
@Data
public class UserLocusLonlat {
    private Long userId;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "学院名称")
    private String collegeName;
    @ApiModelProperty(value = "专业名称")
    private String professionalName;
    @ApiModelProperty(value = "班级名称")
    private String classesName;
    @ApiModelProperty(value = "联系方式")
    private String userPhone;
    List<UseElectricFenceUserDaomin> useElectricFenceUserDaominList;
}
