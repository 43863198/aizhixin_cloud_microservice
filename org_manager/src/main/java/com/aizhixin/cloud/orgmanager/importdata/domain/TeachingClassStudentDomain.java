package com.aizhixin.cloud.orgmanager.importdata.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description = "教学班学生")
@NoArgsConstructor
@ToString
public class TeachingClassStudentDomain implements Serializable {

    @ApiModelProperty(value = "教学班编码", position = 1)
    @Getter
    @Setter
    private String teachingClassCode;

    @ApiModelProperty(value = "教学班名称", position = 2)
    @Getter
    @Setter
    private String teachingClassName;

    @ApiModelProperty(value = "学号", position = 3)
    @Getter
    @Setter
    private String jobNum;

    @ApiModelProperty(value = "学生姓名", position = 4)
    @Getter
    @Setter
    private String name;

    @ApiModelProperty(value = "错误信息", position = 5)
    @Getter
    @Setter
    private String msg;

    public TeachingClassStudentDomain(String teachingClassCode, String teachingClassName, String jobNum, String name) {
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.jobNum = jobNum;
        this.name = name;
    }

    public TeachingClassStudentDomain(String teachingClassCode, String teachingClassName, String jobNum, String name, String msg) {
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.jobNum = jobNum;
        this.name = name;
        this.msg = msg;
    }
}
