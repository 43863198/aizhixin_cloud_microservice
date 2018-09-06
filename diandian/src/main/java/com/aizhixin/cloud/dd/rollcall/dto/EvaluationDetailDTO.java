package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "web个人中心评教汇总")
@Data
@EqualsAndHashCode(callSuper = false)
public class EvaluationDetailDTO {
    private String courseId;
    private String courseName;
    private String ceTeaName;
    private String CourseTime;
    private String evaluationTime;
    private String classRom;
    private String evaluationScore;
    private String evaluationContent;
}
