package com.aizhixin.cloud.dd.classperf.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClassPerfTeacherDomain {
    private Long userId;
    private String name;
    private String jobNum;
    private String sex;
    private String phone;
    private String avatar;
    private Long collegeId;
    private String collegeName;
    private Integer ratingLimit;
    private Integer residualScore;
}
