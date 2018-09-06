package com.aizhixin.cloud.dd.questionnaire.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "评教问卷已分配列表")
@Data
public class QuestionnareAssignQueryDTO {
    private Long id;
    private Long teachingClassId;
    private String teachingClassCode;
    private String teachingClassName;
    private Long totalScore;
    private String courseName;
    private String teacherName;
    private String avageScore;
    private Integer assessCount;
    private Integer totalCount;
    private String assginDate;
    private String status;
    private String assAndCanStuatus;
    private boolean quantification;
}
