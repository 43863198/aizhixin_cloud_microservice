package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "学生信息")
@Data
public class StudentDTO {
    Long studentId;
    String studentName;
    String sutdentNum;
    Long classesId;
    String classesName;
    Long professionalId;
    String professionalName;
    Long collegeId;
    String collegeName;
    String teachingYear;
    Long orgId;
}
