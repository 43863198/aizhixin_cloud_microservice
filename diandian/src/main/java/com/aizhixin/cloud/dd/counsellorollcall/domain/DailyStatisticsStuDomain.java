package com.aizhixin.cloud.dd.counsellorollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description = "日统计")
@ToString
@Data
public class DailyStatisticsStuDomain {
    @ApiModelProperty(value = "签到记录id", dataType = "Long", notes = "签到记录id")
    private Long id;
    @ApiModelProperty(value = "学生id", dataType = "Long", notes = "学生id")
    private Long studentId;
    @ApiModelProperty(value = "学生姓名", dataType = "String", notes = "学生姓名")
    private String studentName;
    @ApiModelProperty(value = "班级名称", dataType = "String", notes = "班级名称")
    private String className;
    @ApiModelProperty(value = "头像", dataType = "String", notes = "头像")
    private String avatar;

    @ApiModelProperty(value = "状态", dataType = "String", notes = "状态")
    private String status;
    @ApiModelProperty(value = "位置坐标", dataType = "String", notes = "位置坐标")
    private String gpsLocation;
    @ApiModelProperty(value = "位置详细信息", dataType = "String", notes = "位置详细信息")
    private String gpsDetail;
    @ApiModelProperty(value = "签到时间", dataType = "String", notes = "签到时间")
    private String signTime;
    @ApiModelProperty(value = "是否提交", dataType = "Integer", notes = "是否提交")
    private Integer haveReport = 0;

    @ApiModelProperty(value = "状态2", dataType = "String", notes = "状态2")
    private String status2;
    @ApiModelProperty(value = "位置坐标2", dataType = "String", notes = "位置坐标2")
    private String gpsLocation2;
    @ApiModelProperty(value = "位置详细信息2", dataType = "String", notes = "位置详细信息2")
    private String gpsDetail2;
    @ApiModelProperty(value = "签到时间2", dataType = "String", notes = "签到时间2")
    private String signTime2;
    @ApiModelProperty(value = "是否提交2", dataType = "Integer", notes = "是否提交2")
    private Integer haveReport2 = 0;

    @ApiModelProperty(value = "第一次打卡时间")
    private String firstTime;
    @ApiModelProperty(value = "第一次打卡迟到时间")
    private String lateTime;
    @ApiModelProperty(value = "第二次打卡时间")
    private String secondTime;
    @ApiModelProperty(value = "第二次结束时间")
    private String endTime;
}
