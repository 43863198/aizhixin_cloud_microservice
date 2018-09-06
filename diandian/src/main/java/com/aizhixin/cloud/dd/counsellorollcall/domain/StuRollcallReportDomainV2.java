package com.aizhixin.cloud.dd.counsellorollcall.domain;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by LIMH on 2017/12/6.
 */
@ApiModel(description = "学生签到列表")
@ToString
@Data
public class StuRollcallReportDomainV2 {
    private Long id;
    @ApiModelProperty(value = "签到任务id", dataType = "Long", notes = "签到任务id")
    private Long counsellorId;
    @ApiModelProperty(value = "开启状态", dataType = "Boolean", notes = "开启状态")
    private Boolean isOpen = false;
    @ApiModelProperty(value = "开启时间", dataType = "String", notes = "开启时间")
    private String openTime;
    @ApiModelProperty(value = "是否阅读", dataType = "Integer", notes = "是否阅读")
    private Boolean haveRead = false;

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

    public StuRollcallReportDomainV2() {
    }

    public StuRollcallReportDomainV2(Long id, Long counsellorId, String gpsLocation, String gpsDetail,
                                     Date signTime, Date openTime, Integer haveReport, String status,
                                     Boolean haveRead, String gpsLocation2, String gpsDetail2,
                                     Date signTime2, Integer haveReport2, String status2, Boolean isOpen) {
        this.id = id;
        this.counsellorId = counsellorId;
        this.isOpen = isOpen;
        this.status = status;
        this.gpsLocation = gpsLocation;
        this.gpsDetail = gpsDetail;
        this.signTime = DateFormatUtil.format(signTime, DateFormatUtil.FORMAT_SHORT_MINUTE);
        this.openTime = DateFormatUtil.format(openTime, DateFormatUtil.FORMAT_SHORT_MINUTE);
        this.haveReport = haveReport;
        this.haveRead = haveRead;
        this.status2 = status2;
        this.gpsLocation2 = gpsLocation2;
        this.gpsDetail2 = gpsDetail2;
        this.signTime2 = DateFormatUtil.format(signTime2, DateFormatUtil.FORMAT_SHORT_MINUTE);
        this.haveReport2 = haveReport2;
    }
}
