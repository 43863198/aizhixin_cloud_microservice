package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zhen.pan on 2017/5/11.
 */
@ApiModel(description = "教学班学生信息")
@Data
public class TeachStudentDomain {
    @ApiModelProperty(value = "ID", position = 1)
    private Long id;
    @ApiModelProperty(value = "姓名", position = 3)
    private String name;
    @ApiModelProperty(value = "工号", position = 4)
    private String jobNumber;
    @ApiModelProperty(value = "教学班ID", position = 5)
    private Long teachingClassId;
    @ApiModelProperty(value = "行政班ID", position = 6)
    private Long classesId;
    @ApiModelProperty(value = "行政班名称", position = 7)
    private String classesName;
    @ApiModelProperty(value = "专业ID", position = 6)
    private Long professionalId;
    @ApiModelProperty(value = "专业名称", position = 7)
    private String professionalName;
    @ApiModelProperty(value = "学院ID", position = 6)
    private Long collegeId;
    @ApiModelProperty(value = "学院名称", position = 7)
    private String collegeName;
//    @ApiModelProperty(value = "电话", position=5)
//    private String phone;
//    @ApiModelProperty(value = "邮箱", position=6)
//    private String email;
//    @ApiModelProperty(value = "性别(男|女)", position=8)
//    private String sex;

    public TeachStudentDomain() {
    }

    public TeachStudentDomain(Long id, String name, String jobNumber) {
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
    }
    
    public TeachStudentDomain(Long id, String name, String jobNumber,Long collegeId) {
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
        this.collegeId = collegeId;
    }

    public TeachStudentDomain(Long id, String name, String jobNumber, Long classesId, String classesName) {
        this(id, name, jobNumber);
        this.classesId = classesId;
        this.classesName = classesName;
    }

    public TeachStudentDomain(Long id, String name, String jobNumber, Long classesId, String classesName, Long teachingClassId) {
        this(id, name, jobNumber, classesId, classesName);
        this.teachingClassId = teachingClassId;
    }

    public TeachStudentDomain(Long id, String name, String jobNumber, Long classesId, String classesName, Long professionalId, String professionalName, Long collegeId, String collegeName) {
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
        this.classesId = classesId;
        this.classesName = classesName;
        this.professionalId = professionalId;
        this.professionalName = professionalName;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
    }
}
