package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;


/**
 * 临时调课记录表
 */
@ToString
@Data
public class TempAdjustCourseScheduleDTO {

    /**
     * 排课id
     */
    private Long scheduleId;

    /**
     * 教学班
     */
    @ApiModelProperty(value = "教学班ID")
    private Long teachingClassId;
    /**
     * 原第几周
     */
    private Integer srcWeekNo;
    /**
     * 原星期几（周日1，周六7）
     */
    private Integer srcDayOfWeek;
    /**
     * 原日期
     */
    @ApiModelProperty(value = "原日期")
    private String srcDate;
    /**
     * 原第几节
     */
    @ApiModelProperty(value = "原第几节")
    private Integer srcPeriodNo;

    /**
     * 原持续节
     */
    @ApiModelProperty(value = "原持续节")
    private Integer srcPeriodNum;

    /**
     * 目标第几周
     */
    private Integer desWeekNo;
    /**
     * 目标星期几（周日1，周六7）
     */
    private Integer desDayOfWeek;
    /**
     * 目标日期
     */
    @ApiModelProperty(value = "目标日期")
    private String desDate;

    /**
     * 目标第几节
     */
    @ApiModelProperty(value = "目标第几节ID")
    private Long desPeriodId;

    /**
     * 目标第几节
     */
    @ApiModelProperty(value = "目标第几节")
    private Integer desPeriodNo;

    /**
     * 目标持续节
     */
    private Integer desPeriodNum;

    /**
     * 目标课程开始时间
     */
    @ApiModelProperty(value = "目标持续节")
    private String desPeriodStartTime;

    /**
     * 目标课程结束时间
     */
    @ApiModelProperty(value = "目标持续节")
    private Integer desPeriodEndTime;

    /**
     * 目标教室
     */
    @ApiModelProperty(value = "目标教室", required = true)
    private String classroom;


}
