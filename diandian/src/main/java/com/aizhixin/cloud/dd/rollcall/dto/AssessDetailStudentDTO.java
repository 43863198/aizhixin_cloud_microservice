package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "班级信息")
@Data
public class AssessDetailStudentDTO {
    Long studentId;
    String className;
    String teachTime;
    String periodName;
    int score;
    String assessDate;
    String content;
    String classRoomName;
}
