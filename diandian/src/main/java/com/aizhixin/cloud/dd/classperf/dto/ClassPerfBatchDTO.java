package com.aizhixin.cloud.dd.classperf.dto;

import com.aizhixin.cloud.dd.classperf.entity.ClassPerfLogFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
@ToString
public class ClassPerfBatchDTO {
    private Long teacherId;
    private String comment;
    private Integer score;
    @ApiModelProperty(value = "10:加分 20:减分")
    private Integer type;
    private List<ClassPerfLogFile> files;

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
