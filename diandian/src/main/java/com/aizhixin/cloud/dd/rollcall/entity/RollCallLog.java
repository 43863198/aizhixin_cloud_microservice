package com.aizhixin.cloud.dd.rollcall.entity;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import lombok.Data;

import java.util.Date;

@Data
public class RollCallLog {
    private Long id;
    private Long createdBy;
    private Date createdDate = new Date();
    private Long lastModifiedBy;
    private Date lastModifiedDate = new Date();
    private Integer deleteFlag = DataValidity.VALID.getState();
    private Long scheduleRollcallId;
    private Long courseId;
    private Long semesterId;
    private Long classId;
    private String className;
    private Long studentId;
    private String studentNum = "";
    private Long teacherId;
    private Long teachingClassId;
    private String type;
    private Boolean canRollCall = false;
    private String lastType;
    private Boolean haveReport = false;
    private String gpsLocation;
    private String gpsDetail;
    private String gpsType;
    private String distance;
    private Date signTime;
    private String deviceToken;
    private String studentName;
    private Long professionalId;
    private String professionalName;
    private Long collegeId;
    private String collegeName;
    private String teachingYear;
    private Long orgId;
    private Boolean isPublicLeave = false;
}
