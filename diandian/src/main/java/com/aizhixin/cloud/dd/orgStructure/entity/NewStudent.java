package com.aizhixin.cloud.dd.orgStructure.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "NewStudent")
public class NewStudent {
    @Id
    private String id;

    @Indexed
    private Long orgId;

    @Indexed
    private Long stuId;

    private String name;

    private String avatar;

    private String phone;

    private String sex;

    private String idNumber;

    private String admissionNoticeNumber;

    private String studentSource;

    private String studentType;

    private String eduLevel;

    private String grade;

    private String schoolLocal;

    private String professionalName;

    private String collegeName;

    private String msg;


}
