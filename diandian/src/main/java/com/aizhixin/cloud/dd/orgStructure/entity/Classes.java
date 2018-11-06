package com.aizhixin.cloud.dd.orgStructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "classes")
@Data
public class Classes {
    @Id
    protected String id;
    //行政班id
    @Indexed
    private Long classesId;
    //行政班名称
    private String classesName;
    private Long pepleNumber;
    @Indexed
    private Long profId;
    @Indexed
    private Long collegeId;
    @Indexed
    private Long orgId;

    private String teachingYear;
}
