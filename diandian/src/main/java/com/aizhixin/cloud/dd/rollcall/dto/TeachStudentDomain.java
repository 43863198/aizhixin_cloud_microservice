package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zhen.pan on 2017/5/11.
 */
@ApiModel(description="教学班学生信息")
@Data
public class TeachStudentDomain {
    @ApiModelProperty(value = "ID", position=1)
    private Long id;
    @ApiModelProperty(value = "姓名", position=3)
    private String name;
    @ApiModelProperty(value = "工号", position=4)
    private String jobNumber;
    @ApiModelProperty(value = "教学班ID", position=5)
    private Long teachingClassId;
//    @ApiModelProperty(value = "电话", position=5)
//    private String phone;
//    @ApiModelProperty(value = "邮箱", position=6)
//    private String email;
//    @ApiModelProperty(value = "性别(男|女)", position=8)
//    private String sex;

    public TeachStudentDomain() {}
    public TeachStudentDomain(Long id, String name, String jobNumber) {
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
    }
    public TeachStudentDomain(Long id, String name, String jobNumber, Long teachingClassId) {
        this(id, name, jobNumber);
        this.teachingClassId = teachingClassId;
    }
}
