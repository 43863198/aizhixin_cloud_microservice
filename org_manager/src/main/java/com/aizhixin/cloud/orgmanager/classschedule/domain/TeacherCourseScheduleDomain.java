package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description="老师学期总课表")
public class TeacherCourseScheduleDomain {

    @ApiModelProperty(value = "星期几（周一1，周日7）")
    @Getter @Setter private Integer dayOfWeek;
    @ApiModelProperty(value = "从第几节开始，序号")
    @Getter @Setter  private Integer periodNo;
    @ApiModelProperty(value = "教学班ID")
    @Getter  @Setter private Long teachingClassId;
    @ApiModelProperty(value = "教学班名称")
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "教学班ID")
    @Getter @Setter private Long courseId;
    @ApiModelProperty(value = "课程名称")
    @Getter @Setter private String courseName;
    @ApiModelProperty(value = "起始周，第几周序号")
    @Getter @Setter private Integer startWeekNo;
    @ApiModelProperty(value = "结束周，第几周序号")
    @Getter @Setter private Integer endWeekNo;
    @ApiModelProperty(value = "单周或双周,10不区分单双周,20单周,30双周")
    @Getter @Setter private Integer singleOrDouble;
    @ApiModelProperty(value = "总共持续几个小节")
    @Getter @Setter private Integer periodNum;
    @ApiModelProperty(value = "教室")
    @Getter @Setter private String classroom;


    public TeacherCourseScheduleDomain (Long teachingClassId, String teachingClassName, Long courseId, String courseName, Integer startWeekNo, Integer endWeekNo, Integer singleOrDouble, Integer dayOfWeek, Integer periodNo, Integer periodNum, String classroom) {
        this.teachingClassId = teachingClassId;
        this.teachingClassName = teachingClassName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.startWeekNo = startWeekNo;
        this.endWeekNo = endWeekNo;
        this.singleOrDouble = singleOrDouble;
        this.dayOfWeek = dayOfWeek;
        this.periodNo = periodNo;
        this.periodNum = periodNum;
        this.classroom = classroom;
    }
}
