package com.aizhixin.cloud.dd.rollcallv2.domain;

import java.util.Date;

import com.aizhixin.cloud.dd.constant.RollCallConstants;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description = "学生签到列表信息缓存对象")
@NoArgsConstructor
@ToString
public class RollcallRedisDomain implements java.io.Serializable {
    @ApiModelProperty(value = "课堂规则ID")
    @Getter
    @Setter
    private Long scheduleRollcallId;

    @ApiModelProperty(value = "课程ID")
    @Getter
    @Setter
    private Long courseId;

    @ApiModelProperty(value = "学生ID")
    @Getter
    @Setter
    private Long studentId;

    @ApiModelProperty(value = "学生姓名")
    @Getter
    @Setter
    private String studentName;

    @ApiModelProperty(value = "学生学号")
    @Getter
    @Setter
    private String studentNum = "";

    @ApiModelProperty(value = "班级ID")
    @Getter
    @Setter
    private Long classId;

    @ApiModelProperty(value = "班级ID")
    @Getter
    @Setter
    private String className;

    @ApiModelProperty(value = "专业ID")
    @Getter
    @Setter
    private Long professionalId;

    @ApiModelProperty(value = "专业")
    @Getter
    @Setter
    private String professionalName;

    @ApiModelProperty(value = "学院ID")
    @Getter
    @Setter
    private Long collegeId;

    @ApiModelProperty(value = "学院")
    @Getter
    @Setter
    private String collegeName;

    @ApiModelProperty(value = "年级")
    @Getter
    @Setter
    private String teachingYear;

    @ApiModelProperty(value = "学期ID")
    @Getter
    @Setter
    private Long semesterId;

    @ApiModelProperty(value = "学校")
    @Getter
    @Setter
    private Long orgId;

    @ApiModelProperty(value = "老师ID")
    @Getter
    @Setter
    private Long teacherId;

    @ApiModelProperty(value = "教学班ID")
    @Getter
    @Setter
    private Long teachingClassId;

    @ApiModelProperty(value = "点名结果")
    @Getter
    @Setter
    private String type;

    @ApiModelProperty(value = "是否可以点名")
    @Getter
    @Setter
    private Boolean canRollCall = false;

    @ApiModelProperty(value = "上次点名结果")
    @Getter
    @Setter
    private String lastType;

    @ApiModelProperty(value = "本轮自动点名是否签到过")
    @Getter
    @Setter
    private Boolean haveReport = false;

    @ApiModelProperty(value = "经纬度信息")
    @Getter
    @Setter
    private String gpsLocation;

    @ApiModelProperty(value = "gps详细位置信息")
    @Getter
    @Setter
    private String gpsDetail;

    @ApiModelProperty(value = "gps 类型 (wifi,4g,gps)")
    @Getter
    @Setter
    private String gpsType;

    @ApiModelProperty(value = "距离")
    @Getter
    @Setter
    private String distance;

    @ApiModelProperty(value = "签到时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter
    @Setter
    private Date signTime;

    @ApiModelProperty(value = "设备DeviceToken")
    @Getter
    @Setter
    private String deviceToken;

    @ApiModelProperty(value = "本轮自动点名是否签到过创建日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter
    @Setter
    private Date createdDate;

    public void resetSign() {
        if (!RollCallConstants.TYPE_ASK_FOR_LEAVE.equals(this.type)) {
            this.type = RollCallConstants.TYPE_UNCOMMITTED;
        }
        this.lastType = null;
        this.distance = null;
    }
}