package com.aizhixin.cloud.dd.rollcall.dto;

import lombok.Data;

@Data
public class EvaluationCountDTO {
    private String courseId;
    private String courseName;
    private String courseTeacher;
    private Long oneStar;
    private Long twoStar;
    private Long threeStar;
    private Long fourStar;
    private Long fiveStar;
}
