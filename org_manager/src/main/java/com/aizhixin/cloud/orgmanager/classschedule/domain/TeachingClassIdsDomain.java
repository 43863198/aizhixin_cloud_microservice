package com.aizhixin.cloud.orgmanager.classschedule.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdsDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by zhen.pan on 2017/4/25.
 */
@ApiModel(description="id和name信息")
public class TeachingClassIdsDomain extends IdsDomain {
    @NotNull
    @ApiModelProperty(value = "教学班ID", allowableValues = "range[1,infinity]", required = true)
    @Getter @Setter private Long teachingClassId;
}
