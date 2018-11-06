package com.aizhixin.cloud.dd.classperf.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ClassPerfStudent")
@Data
public class ClassPerfStudent {
    @Id
    protected String id;
    @Indexed
    private Long studentId;
    @Indexed
    private Long semesterId;
    private String name;
    private String jobNum;
    private String avatar;
    private Long classesId;
    private String classesName;
    private Long collegeId;
    private String collegeName;
    private Long profId;
    private String profName;
    @Indexed
    private Long orgId;
    private Integer totalScore;
    private String updateDate;
}
