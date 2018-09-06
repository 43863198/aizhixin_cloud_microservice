package com.aizhixin.cloud.dd.counsellorollcall.domain;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by LIMH on 2017/11/30.
 */
@ApiModel(description = "学生签到详情")
@ToString
@Data
public class StudentSignInDomain implements Serializable {
    private Long id;
    @ApiModelProperty(value = "学生id", dataType = "Long", notes = "学生id")
    private Long studentId;
    @ApiModelProperty(value = "学生姓名", dataType = "String", notes = "学生姓名")
    private String studentName;
    @ApiModelProperty(value = "班级Id", dataType = "Long", notes = "班级Id")
    private Long classId;
    @ApiModelProperty(value = "班级名称", dataType = "String", notes = "班级名称")
    private String className;
    @ApiModelProperty(value = "点名id", dataType = "Long", notes = "点名id")
    private Long rollCallEverId;
    @ApiModelProperty(value = "是否已阅读", dataType = "Integer", notes = "是否已阅读")
    private Boolean lookStatus;
    @ApiModelProperty(value = "经纬度信息", dataType = "String", notes = "经纬度信息")
    private String gpsLocation;
    @ApiModelProperty(value = "gps详细位置信息", dataType = "String", notes = "gps详细位置信息")
    private String gpsDetail;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "签到时间", dataType = "String", notes = "签到时间")
    private String signTime;
    @ApiModelProperty(value = "是否提交", dataType = "Integer", notes = "是否提交")
    private Integer haveReport = 0;
    @ApiModelProperty(value = "状态", dataType = "String", notes = "状态")
    private String status;

    public StudentSignInDomain() {}

    public StudentSignInDomain(Long id, Long rollCallEverId, Long studentId, String studentName, Long classId, String className, Boolean lookStatus, String gpsLocation,
        String gpsDetail, Date signTime, Integer haveReport, String status) {
        this.id = id;
        this.rollCallEverId = rollCallEverId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.classId = classId;
        this.className = className;
        this.lookStatus = lookStatus;
        this.gpsLocation = gpsLocation;
        this.gpsDetail = gpsDetail;
        this.signTime = DateFormatUtil.format(signTime);
        this.haveReport = haveReport;
        this.status = status;
    }
}
