package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@ApiModel(description="老师总课表节数据")
public class TeacherCourseSchdulePeroidDomain {
    @ApiModelProperty(value = "从第几节开始，序号")
    @Getter @Setter  private Integer periodNo;

    @ApiModelProperty(value = "明细数据")
    @Getter @Setter  private List<TeacherCourseSchduleDetailDomain> details;
}
