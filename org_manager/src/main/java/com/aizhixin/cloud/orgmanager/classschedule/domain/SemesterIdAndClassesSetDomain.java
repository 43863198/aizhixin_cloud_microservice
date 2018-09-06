package com.aizhixin.cloud.orgmanager.classschedule.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@ApiModel(description = "学期和班级ID信息")
@NoArgsConstructor
public class SemesterIdAndClassesSetDomain {
    @ApiModelProperty(value = "学期ID")
    private  Long semesterId;
    @ApiModelProperty(value = "行政班ID")
    private Set<Long> classesIdSet;
}
