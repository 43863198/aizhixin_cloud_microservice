/**
 *
 */
package com.aizhixin.cloud.dd.questionnaire.dto;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author meihua.li
 */
@ApiModel
public class QuestionnaireCensusDTO extends QuestionnaireDTO {
    @Getter
    @Setter
    private Long questionnaireAssignId;
    @Getter
    @Setter
    private Long questionnaireAssignStudentId;
    @Getter
    @Setter
    private Long collegeId;
    @Getter
    @Setter
    private String collegeName;
    @Getter
    @Setter
    private Long teacherId;
    @Getter
    @Setter
    private String teacherName;
    @Getter
    @Setter
    private Long courseId;
    @Getter
    @Setter
    private String courseName;
    @Getter
    @Setter
    private String courseCode;
    @Getter
    @Setter
    private Date enddate;
    @Getter
    @Setter
    private Date systemDate;
    @Getter
    @Setter
    private String adminName;
    @Getter
    @Setter
    private String questionnaireStatus;
    @Getter
    @Setter
    private Long classId;
    @Getter
    @Setter
    private String className;
    @Getter
    @Setter
    private Long weight;

    public QuestionnaireCensusDTO() {
    }
}
