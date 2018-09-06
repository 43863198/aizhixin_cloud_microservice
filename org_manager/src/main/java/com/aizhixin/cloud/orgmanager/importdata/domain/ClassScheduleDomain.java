package com.aizhixin.cloud.orgmanager.importdata.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description = "课程表")
@NoArgsConstructor
@ToString
public class ClassScheduleDomain implements Serializable {

    @ApiModelProperty(value = "教学班编码", position = 1)
    @Getter
    @Setter
    private String teachingClassCode;

    @ApiModelProperty(value = "教学班名称", position = 2)
    @Getter
    @Setter
    private String teachingClassName;

    @ApiModelProperty(value = "起始周", position = 3)
    @Getter
    @Setter
    private Integer startWeek;

    @ApiModelProperty(value = "结束周", position = 4)
    @Getter
    @Setter
    private Integer endWeek;

    @ApiModelProperty(value = "单双周", position = 5)
    @Getter
    @Setter
    private String weekType;

    @ApiModelProperty(value = "星期（1-7）", position = 6)
    @Getter
    @Setter
    private Integer dayOfWeek;

    @ApiModelProperty(value = "起始节", position = 7)
    @Getter
    @Setter
    private Integer startPeriod;

    @ApiModelProperty(value = "持续节", position = 8)
    @Getter
    @Setter
    private Integer periodNum;

    @ApiModelProperty(value = "上课地点", position = 9)
    @Getter
    @Setter
    private String classRoom;


    @ApiModelProperty(value = "错误信息", position = 10)
    @Getter
    @Setter
    private String msg;

    public ClassScheduleDomain(String teachingClassCode, String teachingClassName, Integer startWeek, Integer endWeek, String weekType, Integer dayOfWeek, Integer startPeriod, Integer periodNum, String classRoom) {
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.weekType = weekType;
        this.dayOfWeek = dayOfWeek;
        this.startPeriod = startPeriod;
        this.periodNum = periodNum;
        this.classRoom = classRoom;
    }

    public ClassScheduleDomain(String teachingClassCode, String teachingClassName, Integer startWeek, Integer endWeek, String weekType, Integer dayOfWeek, Integer startPeriod, Integer periodNum, String classRoom, String msg) {
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.weekType = weekType;
        this.dayOfWeek = dayOfWeek;
        this.startPeriod = startPeriod;
        this.periodNum = periodNum;
        this.classRoom = classRoom;
        this.msg = msg;
    }
}
