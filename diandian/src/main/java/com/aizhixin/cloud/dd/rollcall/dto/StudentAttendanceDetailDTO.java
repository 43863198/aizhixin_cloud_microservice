package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "个人中心学生")
@Data
public class StudentAttendanceDetailDTO {
    Long studentId;
    String className;
    String courseName;
    String teachName;
    String teachTime;
    String classRoomName;
    String type;
    String signTime;
    String gpsDetail;
}
