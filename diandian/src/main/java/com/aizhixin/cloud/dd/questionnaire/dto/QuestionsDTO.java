package com.aizhixin.cloud.dd.questionnaire.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "学生问卷信息保存")
@Data
public class QuestionsDTO {
    private Long id;
    private Long questionnaireId;
    private Long questionnaireAssginId;
    private Long questionnaireAssginStudentId;
    private Float totalActualScore;
    private String levelName;
    private List<QuestionDTO> questions;
    private String comment;


}
