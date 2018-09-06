package com.aizhixin.cloud.dd.questionnaire.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel(description = "问卷分配到人")
@Data
public class QuestionnaireAssignUserListDTO {
    private Long questionId;
    private List<QuestionnaireAssignUserDTO> users;
    private boolean isAll;
    private Integer teacherType;//10:非授课教师 20:授课教师
    private String teacherName;//姓名或者工号
    private Long collegeId;
}
