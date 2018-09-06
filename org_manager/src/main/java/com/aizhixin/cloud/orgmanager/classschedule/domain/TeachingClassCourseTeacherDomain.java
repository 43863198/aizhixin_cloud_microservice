package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by zhen.pan on 2017/6/29.
 */
@ApiModel(description="教学班课程老师查询输出对象")
@ToString
public class TeachingClassCourseTeacherDomain {
    @ApiModelProperty(value = "教学班ID")
    @Getter @Setter private Long teachingClassId;
    @ApiModelProperty(value = "教学班名称")
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "选课课号(教学班编码)")
    @Getter @Setter private String teachingClassCode;
    @ApiModelProperty(value = "学院名称")
    @Getter @Setter private String collegeName;
    @ApiModelProperty(value = "课程名称")
    @Getter @Setter private String courseName;
    @ApiModelProperty(value = "老师ID")
    @Getter @Setter private Long teacherId;
    @ApiModelProperty(value = "老师名称")
    @Getter @Setter private String teacherName;

    public TeachingClassCourseTeacherDomain () {}

    public TeachingClassCourseTeacherDomain(Long teachingClassId, String teachingClassName, String teachingClassCode, String courseName) {
        this.teachingClassId = teachingClassId;
        this.teachingClassName = teachingClassName;
        this.teachingClassCode = teachingClassCode;
        this.courseName = courseName;
    }

    public TeachingClassCourseTeacherDomain(Long teachingClassId, String teachingClassName, String teachingClassCode, String courseName, String teacherName) {
        this(teachingClassId, teachingClassName, teachingClassCode, courseName);
        this.teacherName = teacherName;
    }

    public TeachingClassCourseTeacherDomain(Long teachingClassId, String teachingClassName, String teachingClassCode, String courseName, Long teacherId, String teacherName) {
        this(teachingClassId, teachingClassName, teachingClassCode, courseName, teacherName);
        this.teacherId = teacherId;
    }

    public TeachingClassCourseTeacherDomain(Long teachingClassId, String teachingClassName, String teachingClassCode, String collegeName, String courseName, Long teacherId, String teacherName) {
        this.teachingClassId = teachingClassId;
        this.teachingClassName = teachingClassName;
        this.teachingClassCode = teachingClassCode;
        this.collegeName = collegeName;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }
}
