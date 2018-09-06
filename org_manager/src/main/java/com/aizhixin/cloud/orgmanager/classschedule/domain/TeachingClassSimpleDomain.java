package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zhen.pan on 2017/7/6.
 */
@ApiModel(description="教学班排课信息")
@Data
public class TeachingClassSimpleDomain {
    @ApiModelProperty(value = "教学班ID", position=1)
    private Long id;
    @ApiModelProperty(value = "教学班名称", position=2)
    private String name;
    @ApiModelProperty(value = "教学班编码（选课课号）", position=3)
    private String code;

    public TeachingClassSimpleDomain () {}

    public TeachingClassSimpleDomain (Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
