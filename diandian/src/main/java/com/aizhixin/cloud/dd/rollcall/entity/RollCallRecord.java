package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

@Entity(name = "DD_ROLLCALL_RECORD")
@ToString
public class RollCallRecord extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /*
     * 排课表id
     */
    @NotNull
    @Column(name = "schedule_rollcall_id")
    @Getter
    @Setter
    private Long scheduleRollCallId;

    /*
     * 学生id
     */
    @NotNull
    @Column(name = "student_id")
    @Getter
    @Setter
    private Long studentId;

    /**
     * 经纬度信息
     */
    @Column(name = "GPS_LOCALTION")
    @Getter
    @Setter
    private String gpsLocation;

    /**
     * gps详细位置信息
     */
    @Column(name = "GPS_DETAIL")
    @Getter
    @Setter
    private String gpsDetail;

    /**
     * gps 类型 (wifi,4g,gps)
     */
    @Column(name = "GPS_TYPE")
    @Getter
    @Setter
    private String gpsType;

    @Column(name = "SIGN_TIME")
    @Getter
    @Setter
    private String signTime;

    @Column(name = "DEVICETOKEN")
    @Getter
    @Setter
    private String deviceToken;

}
