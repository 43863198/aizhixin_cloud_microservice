package com.aizhixin.cloud.dd.questionnaire.dto;

import lombok.Getter;
import lombok.Setter;

public class QuestionnaireDetailDTO extends QuestionnaireDTO {
    @Getter@Setter
    private Long   questionnaireAssginId;
    @Getter@Setter
    private String className;
    @Getter@Setter
    private String studentName;
    @Getter@Setter
    private String courseName;
    @Getter@Setter
    private String courseCode;
    @Getter@Setter
    private String teacherName;
    @Getter@Setter
    private double totalActualScore;
    @Getter@Setter
    private Long   questionnaireAssginStudentId;
    @Getter @Setter
    private String comment;
    @Getter@Setter
    private Integer commit;
    @Getter@Setter
    private Integer classType;

   
}
