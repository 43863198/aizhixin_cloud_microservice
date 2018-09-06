package com.aizhixin.cloud.dd.approve.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AdjustCourseScheduleRecordDomainV2 {
    private Long id;
    private String type;
    private String teachingClassName;
    private String courseName;
    private String teacherName;
    private String agoAttendClassTime;
    private String agoAttendClassAddress;
    private String newAttendClassTime;
    private String newAttendClassAddress;
    private Date createdDate;
}
