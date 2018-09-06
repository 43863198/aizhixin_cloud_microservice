package com.aizhixin.cloud.dd.questionnaire.domain;

import lombok.Data;

import java.util.Date;

@Data
public class QuestionnaireAssginUserDomain {
    private Long quesId;
    private Long userId;
    //user type 学生|教师|辅导员|授课教师
    private Long userType;
    private String userName;
    private String jobNum;
    private Long collegeId;
    private String collegeName;
    private Date createdDate;
    private Long weight;
}
