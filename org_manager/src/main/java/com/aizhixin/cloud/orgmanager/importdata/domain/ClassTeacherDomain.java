package com.aizhixin.cloud.orgmanager.importdata.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description = "班主任")
@ToString
@NoArgsConstructor
public class ClassTeacherDomain implements Serializable {
    @ApiModelProperty(value = "班级名称", position = 2)
    @Getter
    @Setter
    private String className;

    @ApiModelProperty(value = "班主任姓名", position = 2)
    @Getter
    @Setter
    private String name;

    @ApiModelProperty(value = "工号", position = 2)
    @Getter
    @Setter
    private String jobNum;

    @ApiModelProperty(value = "错误信息", position = 13)
    @Getter
    @Setter
    private String msg;

    public ClassTeacherDomain(String className, String name, String jobNum) {
        this.className = className;
        this.name = name;
        this.jobNum = jobNum;
    }

    public ClassTeacherDomain(String className, String name, String jobNum, String msg) {
        this.className = className;
        this.name = name;
        this.jobNum = jobNum;
        this.msg = msg;
    }
}
