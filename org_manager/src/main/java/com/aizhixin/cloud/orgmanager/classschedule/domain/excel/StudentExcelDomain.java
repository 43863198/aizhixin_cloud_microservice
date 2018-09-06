package com.aizhixin.cloud.orgmanager.classschedule.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description="教学班学生excel信息")
@NoArgsConstructor
@ToString
public class StudentExcelDomain extends LineIdMsgDomain implements Serializable {
    @ApiModelProperty(value = "teachingClassCode 教学班编码", position=4)
    @Getter @Setter private String teachingClassCode;
    @ApiModelProperty(value = "teachingClassName 教学班名称", position=5)
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "studentCode 学生学号", position=6)
    @Getter @Setter private String studentCode;
    @ApiModelProperty(value = "studentName 学生姓名", position=7)
    @Getter @Setter private String studentName;

    public StudentExcelDomain (Integer line, String teachingClassCode, String teachingClassName, String studentCode, String studentName) {
        this.line = line;
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.studentCode = studentCode;
        this.studentName = studentName;
    }
}
