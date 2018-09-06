package com.aizhixin.cloud.orgmanager.classschedule.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description="教学班教师excel信息")
@NoArgsConstructor
@ToString
public class TeacherExcelDomain extends LineIdMsgDomain implements Serializable {
    @ApiModelProperty(value = "teachingClassCode 教学班编码", position=4)
    @Getter @Setter private String teachingClassCode;
    @ApiModelProperty(value = "teachingClassName 教学班名称", position=5)
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "teacherCode 教师工号", position=6)
    @Getter @Setter private String teacherCode;
    @ApiModelProperty(value = "teacherName 教师姓名", position=7)
    @Getter @Setter private String teacherName;

    public TeacherExcelDomain (Integer line, String teachingClassCode, String teachingClassName, String teacherCode, String teacherName) {
        this.line = line;
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.teacherCode = teacherCode;
        this.teacherName = teacherName;
    }
}
