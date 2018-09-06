package com.aizhixin.cloud.dd.rollcall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author zhen.pan
 */
@ApiModel(description = "学生用户信息")
@Data
public class StudentDomain {
    @ApiModelProperty(value = "ID", allowableValues = "range[1,infinity]", position = 1)
    private Long id;
    @ApiModelProperty(value = "账号ID", allowableValues = "range[1,infinity]", position = 2)
    private Long accountId;
    @ApiModelProperty(value = "用户类型(10学校管理员，20学院管理员，40班级管理员，60老师，70学生)", position = 3, allowableValues = "10,20,40,60,70")
    private Integer userType;
    @NotNull
    @ApiModelProperty(value = "姓名", required = true, position = 4)
    @Size(min = 0, max = 50)
    private String name;
    @ApiModelProperty(value = "姓名电话", position = 5)
    @Size(min = 0, max = 20)
    private String phone;
    @Email
    @ApiModelProperty(value = "邮箱", position = 6)
    @Size(min = 0, max = 65)
    private String email;
    @ApiModelProperty(value = "工号", position = 7)
    @Size(min = 0, max = 50)
    private String jobNumber;
    @ApiModelProperty(value = "性别(男性male|女性female)", allowableValues = "male,female", position = 8)
    @Size(min = 0, max = 10)
    private String sex;
    @ApiModelProperty(value = "班级ID", allowableValues = "range[1,infinity)", position = 9)
    @Digits(fraction = 0, integer = 18)
    private Long classesId;
    @ApiModelProperty(value = "班级名称", position = 10)
    private String classesName;
    @ApiModelProperty(value = "班级编码")
    private String classesCode;
    @ApiModelProperty(value = "专业ID", allowableValues = "range[1,infinity)", position = 11)
    @Digits(fraction = 0, integer = 18)
    private Long professionalId;
    @ApiModelProperty(value = "专业名称", position = 12)
    private String professionalName;
    @ApiModelProperty(value = "学院ID", allowableValues = "range[1,infinity)", position = 13)
    @Digits(fraction = 0, integer = 18)
    private Long collegeId;
    @ApiModelProperty(value = "学院名称", position = 14)
    private String collegeName;
    @ApiModelProperty(value = "学校ID", allowableValues = "range[1,infinity)", position = 15)
    @Digits(fraction = 0, integer = 18)
    private Long orgId;
    @ApiModelProperty(value = "创建日期", position = 16)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;
    @ApiModelProperty(value = "操作用户ID", allowableValues = "range[1,infinity)", position = 13)
    @Digits(fraction = 0, integer = 30)
    private Long userId;
    @ApiModelProperty(value = "入学日期(yyyy-MM-dd)")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date inSchoolDate;
    @ApiModelProperty(value = "在校10、毕业20")
    private Integer schoolStatus;
    @ApiModelProperty(value = "学生学年")
    private String teachingYear;

    public StudentDomain() {
    }

    public StudentDomain(Long id, String name, String phone, String email, String jobNumber, String sex) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.jobNumber = jobNumber;
        this.sex = sex;
    }

    public StudentDomain(Long id, String name, String phone, String email, String jobNumber, String sex, Long classesId, String classesName) {
        this(id, name, phone, email, jobNumber, sex);
        this.classesId = classesId;
        this.classesName = classesName;
    }

    public StudentDomain(Long id, String name, String phone, String email, String jobNumber, String sex, Long classesId, String classesName, Long professionalId, String professionalName, Long collegeId, String collegeName) {
        this(id, name, phone, email, jobNumber, sex, classesId, classesName);
        this.professionalId = professionalId;
        this.professionalName = professionalName;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
    }

    public StudentDomain(Long id, String name, String phone, String email, String jobNumber, String sex, Long classesId, String classesName, Long professionalId, String professionalName, Long collegeId, String collegeName, String teachingYear) {
        this(id, name, phone, email, jobNumber, sex, classesId, classesName);
        this.professionalId = professionalId;
        this.professionalName = professionalName;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.teachingYear = teachingYear;
    }
}
