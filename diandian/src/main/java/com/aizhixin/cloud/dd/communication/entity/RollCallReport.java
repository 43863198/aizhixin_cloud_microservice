package com.aizhixin.cloud.dd.communication.entity;


import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;


@ApiModel(description = "随时点签到详情表")
@Entity(name = "dd_rollcallreport")
@ToString
public class RollCallReport extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "student_id")
    @Getter
    @Setter
    private Long studentId;

    @Column(name = "rollcallever_id")
    @Getter
    @Setter
    private Long rollCallEverId;

    @Column(name = "gps_location")
    @Getter
    @Setter
    private String gpsLocation;

    @Column(name = "gps_detail")
    @Getter
    @Setter
    private String gpsDetail;

    @Column(name = "gps_type")
    @Getter
    @Setter
    private String gpsType;

    @Column(name = "sign_time")
    @Getter
    @Setter
    private Timestamp signTime;

    @Column(name = "device_token")
    @Getter
    @Setter
    private String deviceToken;

    @Column(name = "have_report")
    @Getter
    @Setter
    private boolean haveReport = false;

    @Column(name = "leave_status")
    @Getter
    @Setter
    private boolean leaveStatus;
    
    @Column(name = "look_status")
    @Getter
    @Setter
    private Integer lookStatus;

    @Column(name = "CLASS_ID")
    @Getter
    @Setter
    private Long classId;


}
