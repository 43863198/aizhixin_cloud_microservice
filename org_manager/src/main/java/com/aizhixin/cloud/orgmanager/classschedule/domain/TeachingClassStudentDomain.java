package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhen.pan on 2017/6/27.
 */
@ApiModel(description="教学班学生信息")
public class TeachingClassStudentDomain {
    @ApiModelProperty(value = "ID", position=1)
    @Getter @Setter  private Long id;
    @ApiModelProperty(value = "学生ID", position=2)
    @Getter @Setter  private Long studentId;

    @ApiModelProperty(value = "学号", position=3)
    @Getter @Setter  private String jobNumber;

    @ApiModelProperty(value = "学生姓名", position=4)
    @Getter @Setter  private String name;

    @ApiModelProperty(value = "教学班ID", position=5)
    @Getter @Setter  private Long teachingClassId;

    @ApiModelProperty(value = "教学班编码（选课课号）", position=6)
    @Getter @Setter private String teachingClassCode;

    @ApiModelProperty(value = "教学班名称", position=7)
    @Getter @Setter  private String teachingClassName;

    @ApiModelProperty(value = "学校ID", position=8)
    @Getter @Setter  private Long orgId;
}
