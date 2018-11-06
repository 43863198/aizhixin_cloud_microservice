package com.aizhixin.cloud.dd.classperf.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ClassPerfTeacher")
@Data
public class ClassPerfTeacher {
    @Id
    protected String id;
    @Indexed
    private Long teacherId;
    @Indexed
    private Long orgId;
    private Integer ratingLimit;
    private Integer residualScore;
}
