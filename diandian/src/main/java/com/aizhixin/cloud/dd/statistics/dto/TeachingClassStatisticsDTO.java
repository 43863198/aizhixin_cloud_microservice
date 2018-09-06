package com.aizhixin.cloud.dd.statistics.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by LIMH on 2017/8/21.
 */
@ApiModel
@Data
public class TeachingClassStatisticsDTO {
    @ApiModelProperty(value = "行政班名称集合")
    List classes;
    @ApiModelProperty(value = "课程名称")
    String courseName;
    @ApiModelProperty(value = "老师名称")
    String teacherName;

    @ApiModelProperty(value = "老师头像")
    String teacherAvatar;

    @ApiModelProperty(value = "老师Id")
    Long teacherId;
    @ApiModelProperty(value = "签到人数")
    String signCount;
    @ApiModelProperty(value = "学生数量")
    String allStudent;
    @ApiModelProperty(value = "班级到课率")
    String classRate;

    @ApiModelProperty(value = "教学班ID")
    Long teachingClassesId;

    @ApiModelProperty(value = "排课id")
    Long scheduleRollCallId;

    @ApiModelProperty(value = "开始时间")
    String beginTime;
    @ApiModelProperty(value = "结束时间")
    String endTime;
}
