package com.aizhixin.cloud.orgmanager.importdata.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description = "教学任务")
@NoArgsConstructor
@ToString
public class TeachingClassDomain implements Serializable {
    @ApiModelProperty(value = "教学班编码", position = 1)
    @Getter
    @Setter
    private String teachingClassCode;

    @ApiModelProperty(value = "教学班名称", position = 2)
    @Getter
    @Setter
    private String teachingClassName;

    @ApiModelProperty(value = "课程编码", position = 3)
    @Getter
    @Setter
    private String courseCode;

    @ApiModelProperty(value = "课程名称", position = 4)
    @Getter
    @Setter
    private String courseName;

    @ApiModelProperty(value = "课程类型", position = 5)
    @Getter
    @Setter
    private String courseType;

    @ApiModelProperty(value = "学期", position = 6)
    @Getter
    @Setter
    private String semester;

    @ApiModelProperty(value = "教师工号", position = 7)
    @Getter
    @Setter
    private String teacherJobNums;

    @ApiModelProperty(value = "教师名称", position = 8)
    @Getter
    @Setter
    private String teacherNames;

    @ApiModelProperty(value = "班级名称", position = 9)
    @Getter
    @Setter
    private String className;

    @ApiModelProperty(value = "错误信息", position = 10)
    @Getter
    @Setter
    private String msg;

    public TeachingClassDomain(String teachingClassCode, String teachingClassName, String courseCode, String courseName, String courseType, String semester, String teacherJobNums, String teacherNames, String className) {
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseType = courseType;
        this.semester = semester;
        this.teacherJobNums = teacherJobNums;
        this.teacherNames = teacherNames;
        this.className = className;
    }

    public TeachingClassDomain(String teachingClassCode, String teachingClassName, String courseCode, String courseName, String courseType, String semester, String teacherJobNums, String teacherNames, String className, String msg) {
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseType = courseType;
        this.semester = semester;
        this.teacherJobNums = teacherJobNums;
        this.teacherNames = teacherNames;
        this.className = className;
        this.msg = msg;
    }
}
