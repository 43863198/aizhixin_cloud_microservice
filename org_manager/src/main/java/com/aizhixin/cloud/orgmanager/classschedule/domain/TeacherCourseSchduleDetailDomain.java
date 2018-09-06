package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="老师总课表节数据")
@NoArgsConstructor
@ToString
public class TeacherCourseSchduleDetailDomain {

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

    public TeacherCourseSchduleDetailDomain (TeacherCourseScheduleDomain d) {
        if (null != d) {
            this.teachingClassId = d.getTeachingClassId();
            this.teachingClassName = d.getTeachingClassName();
            this.courseId = d.getCourseId();
            this.courseName = d.getCourseName();
            this.startWeekNo = d.getStartWeekNo();
            this.endWeekNo = d.getEndWeekNo();
            this.singleOrDouble = d.getSingleOrDouble();
            this.periodNum = d.getPeriodNum();
            this.classroom = classroom;
        }
    }
}
