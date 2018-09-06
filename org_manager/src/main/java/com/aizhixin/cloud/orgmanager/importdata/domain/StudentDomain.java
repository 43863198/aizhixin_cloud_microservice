package com.aizhixin.cloud.orgmanager.importdata.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description = "学生")
@ToString
@NoArgsConstructor
public class StudentDomain implements Serializable {

    @ApiModelProperty(value = "ID", position = 1)
    @Getter
    @Setter
    private Long id;

    @ApiModelProperty(value = "姓名", position = 2)
    @Getter
    @Setter
    private String name;

    @ApiModelProperty(value = "工号", position = 3)
    @Getter
    @Setter
    private String jobNum;

    @ApiModelProperty(value = "性别", position = 4)
    @Getter
    @Setter
    private String gender;

    @ApiModelProperty(value = "身份证号", position = 5)
    @Getter
    @Setter
    private String idNumber;

    @ApiModelProperty(value = "班级名称", position = 6)
    @Getter
    @Setter
    private String className;

    @ApiModelProperty(value = "专业", position = 7)
    @Getter
    @Setter
    private String profession;

    @ApiModelProperty(value = "院系", position = 8)
    @Getter
    @Setter
    private String department;

    @ApiModelProperty(value = "入学年份", position = 9)
    @Getter
    @Setter
    private String startYear;

    @ApiModelProperty(value = "错误信息", position = 10)
    @Getter
    @Setter
    private String msg;

    public StudentDomain(String name, String jobNum, String gender, String className, String profession, String department, String startYear) {
        this.name = name;
        this.jobNum = jobNum;
        this.gender = gender;
        this.className = className;
        this.profession = profession;
        this.department = department;
        this.startYear = startYear;
    }

    public StudentDomain(String name, String jobNum, String gender, String className, String profession, String department, String startYear, String msg) {
        this.name = name;
        this.jobNum = jobNum;
        this.gender = gender;
        this.className = className;
        this.profession = profession;
        this.department = department;
        this.startYear = startYear;
        this.msg = msg;
    }

    public StudentDomain(String name, String jobNum, String gender, String className, String profession, String department, String startYear, String idNumber, String msg) {
        this.name = name;
        this.jobNum = jobNum;
        this.gender = gender;
        this.className = className;
        this.profession = profession;
        this.department = department;
        this.startYear = startYear;
        this.idNumber = idNumber;
        this.msg = msg;
    }
}
