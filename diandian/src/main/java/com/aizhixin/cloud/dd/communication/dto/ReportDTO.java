package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;

public class ReportDTO extends BaseDTO {

    @Size(max = 128)
    @ApiModelProperty(value = "id ", required = true)
    private Long   id;

    @Size(max = 50)
    @ApiModelProperty(value = "经纬度", required = false)
    private String gpsLocaltion;
    @Size(max = 200)
    @ApiModelProperty(value = "位置详细信息", required = false)
    private String gpsDetail;
    @Size(max = 10)
    @ApiModelProperty(value = "获取gps方式(wifi/gps/4G)", required = false)
    private String gpsType;

    private String deviceToken;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getGpsLocaltion() {

        return gpsLocaltion;
    }

    public void setGpsLocaltion(String gpsLocaltion) {

        this.gpsLocaltion = gpsLocaltion;
    }

    public String getGpsDetail() {

        return gpsDetail;
    }

    public void setGpsDetail(String gpsDetail) {

        this.gpsDetail = gpsDetail;
    }

    public String getGpsType() {

        return gpsType;
    }

    public void setGpsType(String gpsType) {

        this.gpsType = gpsType;
    }

    public String getDeviceToken() {

        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {

        this.deviceToken = deviceToken;
    }

}
