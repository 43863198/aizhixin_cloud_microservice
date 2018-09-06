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
public class TeacherDomain implements Serializable {

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

    @ApiModelProperty(value = "学院名称", position = 5)
    @Getter
    @Setter
    private String collegeName;

    @ApiModelProperty(value = "错误信息", position = 6)
    @Getter
    @Setter
    private String msg;

    public TeacherDomain(String name, String jobNum, String gender, String collegeName) {
        this.name = name;
        this.jobNum = jobNum;
        this.gender = gender;
        this.collegeName = collegeName;
    }

    public TeacherDomain(String name, String jobNum, String gender, String collegeName, String msg) {
        this.name = name;
        this.jobNum = jobNum;
        this.gender = gender;
        this.collegeName = collegeName;
        this.msg = msg;
    }
}
