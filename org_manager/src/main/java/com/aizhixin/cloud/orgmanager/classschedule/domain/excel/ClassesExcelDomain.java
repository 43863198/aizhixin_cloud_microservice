package com.aizhixin.cloud.orgmanager.classschedule.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description="教学班行政班excel信息")
@NoArgsConstructor
@ToString
public class ClassesExcelDomain extends LineIdMsgDomain implements Serializable {
    @ApiModelProperty(value = "teachingClassCode 教学班编码", position=4)
    @Getter @Setter private String teachingClassCode;
    @ApiModelProperty(value = "teachingClassName 教学班名称", position=5)
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "classesCode 行政班编码", position=6)
    @Getter @Setter private String classesCode;
    @ApiModelProperty(value = "classesName 行政班名称", position=7)
    @Getter @Setter private String classesName;

    public ClassesExcelDomain (Integer line, String teachingClassCode, String teachingClassName, String classesCode, String classesName) {
        this.line = line;
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.classesCode = classesCode;
        this.classesName = classesName;
    }
}
