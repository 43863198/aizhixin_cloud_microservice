package com.aizhixin.cloud.dd.orgStructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "UserInfo")
@CompoundIndexes({
        @CompoundIndex(name = "c_u", def = "{'collegeId':1,'userType':1}"),
        @CompoundIndex(name = "cs_u", def = "{'classesId':1,'userType':1}")
})
public class UserInfo {
    @Id
    private String id;

    @Indexed
    private Long userId;

    private String name;

    private String jobNum;

    private String sex;

    private String phone;

    private String avatar;

    @Indexed
    private Integer userType;

    @Indexed
    private Long classesId;

    private String classesName;

    @Indexed
    private Long collegeId;

    private String collegeName;

    @Indexed
    private Long profId;

    private String profName;

    @Indexed
    private Long orgId;

    private String orgName;

    private String idNumber;

    @Indexed
    private Integer teacherType;

    @Indexed
    private Boolean isHTeacher;

    private String teachingYear;

    private String studentSource;

    @Indexed
    private Boolean isMonitor;//是否班长
}
