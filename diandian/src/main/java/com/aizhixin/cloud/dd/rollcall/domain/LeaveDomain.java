package com.aizhixin.cloud.dd.rollcall.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class LeaveDomain {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long headTeacherId;
    private String teacherName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date endTime;
    private String className;
    private String requestContent;
    private String status;
    private Integer leavePublic;
    private Integer leaveType;

    public LeaveDomain() {

    }

    public LeaveDomain(Long id, Long studentId, String studentName, Long headTeacherId, String teacherName, Date startTime, Date endTime, String className, String requestContent, String status, Integer leavePublic, Integer leaveType) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.headTeacherId = headTeacherId;
        this.teacherName = teacherName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.className = className;
        this.requestContent = requestContent;
        this.status = status;
        this.leavePublic = leavePublic;
        this.leaveType = leaveType;
    }
}
