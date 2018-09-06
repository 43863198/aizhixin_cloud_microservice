package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by zhen.pan on 2017/5/2.
 */
@ApiModel(description="教学班排课信息")
@ToString
public class TeachingClassSchoolTimeTableDomain {
    @NotNull
    @ApiModelProperty(value = "教学班ID", allowableValues = "range[1,infinity]", required = true)
    @Getter @Setter private Long teachingClassId;

    @ApiModelProperty(value = "教学班名称")
    @Getter @Setter private String teachingClassName;

    @ApiModelProperty(value = "排课")
    @Getter @Setter private List<CourseTimePeriodDomain> timePeriod;

    @NotNull
    @ApiModelProperty(value = "操作人ID", allowableValues = "range[1,infinity]", required = true)
    @Getter @Setter private Long userId;
}
