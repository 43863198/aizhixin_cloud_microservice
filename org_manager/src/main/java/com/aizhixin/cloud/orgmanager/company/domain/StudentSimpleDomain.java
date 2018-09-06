package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

/**
 * 简单的学生信息
 * Created by zhen.pan on 2017/4/18.
 */
@ApiModel(description="简单的学生信息")
public class StudentSimpleDomain extends IdNameDomain {
    @ApiModelProperty(value = "学号", position=7)
    @Size(min = 0, max = 50)
    @Getter @Setter protected String jobNumber;

    @ApiModelProperty(value = "性别", position=8)
    @Size(min = 0, max = 10)
    @Getter @Setter protected String sex;

    @ApiModelProperty(value = "班级ID", position=9)
    @Getter @Setter private Long classesId;

    @ApiModelProperty(value = "班级名称", position=10)
    @Size(min = 0, max = 150)
    @Getter @Setter protected String classesName;
    
    @ApiModelProperty(value = "专业名称")
    @Getter @Setter private String professionalName;
    @ApiModelProperty(value = "专业id")
    @Getter @Setter private Long professionalId;
    @ApiModelProperty(value = "学院名称")
    @Getter @Setter private String collegeName;
    @ApiModelProperty(value = "学院")
    @Getter @Setter private Long collegeId;

    @ApiModelProperty(value = "电话", position=5)
    @Getter @Setter private String phone;

    @ApiModelProperty(value = "邮箱", position=6)
    @Getter @Setter
    private String email;

    public StudentSimpleDomain() {}

    public StudentSimpleDomain(Long id, String name, String jobNumber, String classesName, String sex) {
        super(id, name);
        this.jobNumber = jobNumber;
        this.sex = sex;
        this.classesName = classesName;
    }
    
    public StudentSimpleDomain(Long id, String name, String jobNumber, String classesName, String sex,String professionalName,String collegeName,String phone,String email) {
        super(id, name);
        this.jobNumber = jobNumber;
        this.sex = sex;
        this.classesName = classesName;
        this.professionalName = professionalName;
        this.collegeName = collegeName;
        this.phone = phone;
        this.email = email;
    }

    public StudentSimpleDomain(Long id, String name, String jobNumber, String phone, String email, String sex, Long  classesId, String classesName) {
        this(id, name, jobNumber, classesName, sex);
        this.phone = phone;
        this.email = email;
        this.classesId = classesId;
    }
}
