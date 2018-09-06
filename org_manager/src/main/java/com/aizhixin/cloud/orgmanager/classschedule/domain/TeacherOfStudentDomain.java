package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * Created by zhen.pan on 2017/6/15.
 */
@ApiModel(description="根据学生和课程列表查询老师列表")
@ToString
public class TeacherOfStudentDomain {
    @ApiModelProperty(value = "学生ID", required = true)
    @Getter @Setter private Long studentId;
    @ApiModelProperty(value = "课程ID列表", required = true)
    @Getter @Setter private Set<Long> courseIds;
    @ApiModelProperty(value = "学期ID,缺省当前学期")
    @Getter @Setter private Long semersterId;
}
