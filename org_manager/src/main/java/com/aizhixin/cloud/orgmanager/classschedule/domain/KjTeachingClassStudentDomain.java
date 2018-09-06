package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhen.pan on 2017/6/27.
 */
@ApiModel(description="教学班学生添加")
@Data
public class KjTeachingClassStudentDomain {
    @ApiModelProperty(value = "教学班id")
    private Long teachingClassId;
    @ApiModelProperty(value = "学院id集合")
    private Set<Long> collegeIds = new HashSet<>();
    @ApiModelProperty(value = "专业id集合")
    private Set<Long> profIds = new HashSet<>();
    @ApiModelProperty(value = "行政班id集合")
    private Set<Long> classesIds = new HashSet<>();
    @ApiModelProperty(value = "用户id集合")
    private Set<Long> userIds= new HashSet<>();

}
