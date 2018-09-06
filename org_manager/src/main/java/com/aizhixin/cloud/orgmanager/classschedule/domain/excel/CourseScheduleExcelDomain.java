package com.aizhixin.cloud.orgmanager.classschedule.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description="教学班课程表excel信息")
@NoArgsConstructor
@ToString
public class CourseScheduleExcelDomain extends LineIdMsgDomain implements Serializable {
    @ApiModelProperty(value = "teachingClassCode 教学班编码", position=4)
    @Getter @Setter private String teachingClassCode;
    @ApiModelProperty(value = "teachingClassName 教学班名称", position=5)
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "startWeek 起始周", position=6)
    @Getter @Setter private Integer startWeek;
    @ApiModelProperty(value = "endWeek 结束周", position=7)
    @Getter @Setter private Integer endWeek;
    @ApiModelProperty(value = "oneOrDouble 单双周", position=8)
    @Getter @Setter private String oneOrDouble;
    @ApiModelProperty(value = "week 星期几", position=9)
    @Getter @Setter private Integer week;
    @ApiModelProperty(value = "startPeriod 起始节", position=10)
    @Getter @Setter private Integer startPoriod;
    @ApiModelProperty(value = "periodNum 持续节数量", position=11)
    @Getter @Setter private Integer periodNum;
    @ApiModelProperty(value = "classesRoom 上课地点", position=12)
    @Getter @Setter private String classesRoom;

    public CourseScheduleExcelDomain(Integer line, String teachingClassCode, String teachingClassName, Integer startWeek, Integer endWeek, String oneOrDouble, Integer week, Integer startPoriod, Integer periodNum, String classesRoom) {
        this.line = line;
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.oneOrDouble = oneOrDouble;
        this.week = week;
        this.startPoriod = startPoriod;
        this.periodNum = periodNum;
        this.classesRoom = classesRoom;
    }
}
