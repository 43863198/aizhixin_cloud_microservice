package com.aizhixin.cloud.dd.classperf.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Document(collection = "ClassPerfLog")
@Data
public class ClassPerfLog {
    @Id
    protected String id;
    private String classPerfId;
    private Long studentId;
    private Long teacherId;
    private String teacherName;
    private String teacherJobnum;
    private String teacherGender;
    private String avatar;
    private Integer score;
    private Integer type;
    private String comment;
    private List<ClassPerfLogFile> files;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate = new Date();
}
