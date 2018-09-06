package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel(description="老师总课表")
public class TeacherCourseScheduleAllDomain {

    @ApiModelProperty(value = "星期几（周一1，周日7）")
    @Getter @Setter private Integer dayOfWeek;

    @ApiModelProperty(value = "节数据")
    @Getter @Setter private List<TeacherCourseSchdulePeroidDomain> peroidList;
}
