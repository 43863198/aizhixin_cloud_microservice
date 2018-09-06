package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by zhen.pan on 2017/5/11.
 */
@Data
@ApiModel(description="点点学生天课表")
public class DianDianDaySchoolTimeTableDomain {
    @ApiModelProperty(value = "教学班ID")
    private Long teachingClassId;
    @ApiModelProperty(value = "学期ID")
    private Long semesterId;
    @ApiModelProperty(value = "学期名称")
    private String semesterName;
    @ApiModelProperty(value = "课程Id")
    private Long courseId;
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    @ApiModelProperty(value = "教师列表: 格式[id,name;*]")
    private String teachers;
    @ApiModelProperty(value = "第几周ID")
    private Long weekId;
    @ApiModelProperty(value = "第几周")
    private Integer weekNo;
    @ApiModelProperty(value = "周几")
    private Integer dayOfWeek;
    @ApiModelProperty(value = "从第几节开始ID")
    private Long periodId;
    @ApiModelProperty(value = "从第几节开始，序号")
    private Integer periodNo;
    @ApiModelProperty(value = "总共持续几个小节")
    private Integer periodNum;
    @ApiModelProperty(value = "开始时间")
    private String periodStarttime;
    @ApiModelProperty(value = "结束时间")
    private String periodEndtime;
    @ApiModelProperty(value = "上课日期")
    private Date teachDate;
    @ApiModelProperty(value = "教室")
    private String classroom;

    public DianDianDaySchoolTimeTableDomain () {}

    public DianDianDaySchoolTimeTableDomain(Long teachingClassId, Long weekId, Long periodId, Integer periodNum, Integer dayOfWeek, String classroom) {
        this.teachingClassId = teachingClassId;
        this.weekId = weekId;
        this.periodId = periodId;
        this.dayOfWeek = dayOfWeek;
        this.periodNum = periodNum;
        this.classroom = classroom;
    }

    public DianDianDaySchoolTimeTableDomain(Long teachingClassId, Integer dayOfWeek, Integer periodNo, Integer periodNum, String classroom) {
        this(teachingClassId, null, null, periodNum, dayOfWeek, classroom);
        this.periodNo = periodNo;
    }
}
