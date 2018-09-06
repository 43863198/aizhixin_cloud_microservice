package com.aizhixin.cloud.orgmanager.company.domain.excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@ApiModel(description="新生Excel导入信息")
@ToString
@NoArgsConstructor
public class NewStudentExcelDomain implements Serializable {
    @ApiModelProperty(value = "id ID", position=1)
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "name 姓名", position=1)
    @Getter @Setter private String name;
    @ApiModelProperty(value = "sex 性别", position=3)
    @Getter @Setter private String sex;
    @ApiModelProperty(value = "身份证号", position=4)
    @Getter @Setter private String idNumber;
    @ApiModelProperty(value = "录取通知书号", position=5)
    @Getter @Setter private String admissionNoticeNumber;
    @ApiModelProperty(value = "生源地", position=6)
    @Getter @Setter private String studentSource;
    @ApiModelProperty(value = "学生类别(单招、统招)", position=7)
    @Getter @Setter private String studentType;
    @ApiModelProperty(value = "层次(专科、本科)", position=8)
    @Getter @Setter private String eduLevel;
    @ApiModelProperty(value = "年级", position=9)
    @Getter @Setter private String grade;
    @ApiModelProperty(value = "校区", position=10)
    @Getter @Setter private String schoolLocal;
    @ApiModelProperty(value = "专业名称", position = 11)
    @Getter  @Setter private String professionalName;
    @ApiModelProperty(value = "系别", position = 12)
    @Getter  @Setter private String collegeName;
    @ApiModelProperty(value = "错误信息", position = 13)
    @Getter  @Setter private String msg;
    public NewStudentExcelDomain (Long id, String name, String sex, String idNumber, String admissionNoticeNumber, String studentSource, String studentType, String eduLevel, String professionalName, String collegeName) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.idNumber = idNumber;
        this.admissionNoticeNumber = admissionNoticeNumber;
        this.studentSource = studentSource;
        this.studentType = studentType;
        this.eduLevel = eduLevel;
        this.professionalName = professionalName;
        this.collegeName = collegeName;
    }

    public NewStudentExcelDomain (String name, String sex, String idNumber, String admissionNoticeNumber, String studentSource, String studentType, String eduLevel, String grade, String schoolLocal, String professionalName, String collegeName) {
        this.name = name;
        this.sex = sex;
        this.idNumber = idNumber;
        this.admissionNoticeNumber = admissionNoticeNumber;
        this.studentSource = studentSource;
        this.studentType = studentType;
        this.eduLevel = eduLevel;
        this.grade = grade;
        this.schoolLocal = schoolLocal;
        this.professionalName = professionalName;
        this.collegeName = collegeName;
    }
}
