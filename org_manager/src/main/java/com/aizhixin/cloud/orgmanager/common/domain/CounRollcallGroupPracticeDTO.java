package com.aizhixin.cloud.orgmanager.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel(description = "实践导员点名组")
public class CounRollcallGroupPracticeDTO {
    @ApiModelProperty(value = "实践id")
    private Long practiceId;

    @ApiModelProperty(value = "学校ID(选填)")
    Long orgId;
    @ApiModelProperty(value = "学院ID(选填)")
    Set<Long> collegeIds;
    @ApiModelProperty(value = "专业ID(选填)")
    Set<Long> proIds;
    @ApiModelProperty(value = "班级ID(选填)")
    Set<Long> classIds;
    @ApiModelProperty(value = "教学班ID(选填)")
    Set<Long> teachingClassIds;
    @ApiModelProperty(value = "组包含学生")
    Set<Long> studentList;
}
