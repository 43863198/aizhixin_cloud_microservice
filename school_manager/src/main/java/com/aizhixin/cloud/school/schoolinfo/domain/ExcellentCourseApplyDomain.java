package com.aizhixin.cloud.school.schoolinfo.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExcellentCourseApplyDomain {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "教师id")
    private Long teacherId;
    @ApiModelProperty(value = "教师名")
    private String teacherName;
    @ApiModelProperty(value = "课程id")
    private Long courseId;
    @ApiModelProperty(value = "课程名")
    private String courseName;
    @ApiModelProperty(value = "申请状态，保存时不填")
    private Integer state;
    @ApiModelProperty(value = "学校id")
    private Long orgId;
}
