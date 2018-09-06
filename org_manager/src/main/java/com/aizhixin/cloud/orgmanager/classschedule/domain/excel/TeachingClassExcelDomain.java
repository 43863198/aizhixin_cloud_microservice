package com.aizhixin.cloud.orgmanager.classschedule.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description="教学班excel信息")
@NoArgsConstructor
@ToString
public class TeachingClassExcelDomain extends LineIdMsgDomain implements Serializable {
    @ApiModelProperty(value = "teachingClassCode 教学班编码", position=4)
    @Getter @Setter private String teachingClassCode;
    @ApiModelProperty(value = "teachingClassName 教学班名称", position=5)
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "courseCode 课程编码", position=6)
    @Getter @Setter private String courseCode;
    @ApiModelProperty(value = "courseName 课程名称", position=7)
    @Getter @Setter private String courseName;
    @ApiModelProperty(value = "schoolYear 学年", position=8)
    @Getter @Setter private String schoolYear;
    @ApiModelProperty(value = "semester 学期", position=9)
    @Getter @Setter private String semester;

    public TeachingClassExcelDomain(Integer line, String teachingClassCode, String teachingClassName, String courseCode, String courseName, String schoolYear, String semester) {
        this.line = line;
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.schoolYear = schoolYear;
        this.semester = semester;
    }
}
