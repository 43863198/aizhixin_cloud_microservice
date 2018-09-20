package com.aizhixin.cloud.dd.rollcall.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "教师到课率")
@Data
public class TeacherAttendanceDomain {
    private Long teacherId;
    private String jobNum;
    private String name;
    private String collegeName;
    private Integer totalCount = 0;//应到
    private Integer normaCount = 0;//实到
    private Integer lateCount = 0;//迟到
    private Integer askForLeaveCount = 0;//请假
    private Integer truancyCount = 0;//旷课
    private Integer leaveCount = 0;//早退
    private String attendance = "0.00";
}
