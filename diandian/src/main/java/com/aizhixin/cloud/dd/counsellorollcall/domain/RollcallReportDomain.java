package com.aizhixin.cloud.dd.counsellorollcall.domain;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by LIMH on 2017/12/6.
 */
@ApiModel(description = "学生签到列表")
@ToString
@Data
public class RollcallReportDomain {
    private Long id;
    @ApiModelProperty(value = "辅导员id", dataType = "Long", notes = "辅导员id")
    private Long teacherId;
    @ApiModelProperty(value = "辅导员名称", dataType = "Boolean", notes = "辅导员名称")
    private String teacherName;
    @ApiModelProperty(value = "学生姓名", dataType = "String", notes = "学生姓名")
    private String studentName;
    @ApiModelProperty(value = "学号", dataType = "String", notes = "学号")
    private String psersonId;
    @ApiModelProperty(value = "id", dataType = "Long", notes = "id")
    private Long counsellorId;
    @ApiModelProperty(value = "位置坐标", dataType = "String", notes = "位置坐标")
    private String gpsLocation;
    @ApiModelProperty(value = "位置详细信息", dataType = "String", notes = "位置详细信息")
    private String gpsDetail;
    @ApiModelProperty(value = "签到时间", dataType = "String", notes = "签到时间")
    private String signTime;
    @ApiModelProperty(value = "开启时间", dataType = "String", notes = "开启时间")
    private String openTime;
    @ApiModelProperty(value = "是否提交", dataType = "Integer", notes = "是否提交")
    private Integer haveReport = 0;
    @ApiModelProperty(value = "是否阅读", dataType = "Integer", notes = "是否阅读")
    private Boolean haveRead = false;
    @ApiModelProperty(value = "状态", dataType = "String", notes = "状态")
    private String status;
    @ApiModelProperty(value = "开启状态", dataType = "Boolean", notes = "开启状态")
    private Boolean isOpen = false;

    public RollcallReportDomain() {}

    public RollcallReportDomain(Long id, Long teacherId, String teacherName, String studentName, String psersonId, Long counsellorId, String gpsLocation, String gpsDetail,
        Date signTime, Date openTime, Integer haveReport, String status, Boolean haveRead, Boolean isOpen) {
        this.id = id;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.studentName = studentName;
        this.psersonId = psersonId;
        this.counsellorId = counsellorId;
        this.gpsLocation = gpsLocation;
        this.gpsDetail = gpsDetail;
        this.signTime = DateFormatUtil.format(signTime, DateFormatUtil.FORMAT_MINUTE);
        this.openTime = DateFormatUtil.format(openTime, DateFormatUtil.FORMAT_MINUTE);
        this.haveReport = haveReport;
        this.status = status;
        this.haveRead = haveRead;
        this.isOpen = isOpen;
    }
}
