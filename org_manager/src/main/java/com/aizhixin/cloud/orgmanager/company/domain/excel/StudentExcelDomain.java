package com.aizhixin.cloud.orgmanager.company.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description="B端学生excel导入结果信息")
@NoArgsConstructor
@ToString
public class StudentExcelDomain extends LineCodeNameBaseDomain implements Serializable {
    @ApiModelProperty(value = "sex 性别", position=4)
    @Getter @Setter protected String sex;
    @ApiModelProperty(value = "classesCode 班级编码", position=5)
    @Getter @Setter protected String classesCode;
    @ApiModelProperty(value = "classesName 班级名称", position=6)
    @Getter @Setter protected String classesName;
    @ApiModelProperty(value = "phone 手机号码", position=7)
    @Getter @Setter protected String phone;
    @ApiModelProperty(value = "mail 电子邮箱", position=8)
    @Getter @Setter protected String mail;
    @ApiModelProperty(value = "idNumber 身份证号码", position=9)
    @Getter @Setter protected String idNumber;

    public StudentExcelDomain(Integer line, String code, String name, String sex, String classesCode, String classesName, String phone, String mail, String idNumber) {
        super(line, code, name);
        this.sex = sex;
        this.classesCode = classesCode;
        this.classesName = classesName;
        this.phone = phone;
        this.mail = mail;
        this.idNumber = idNumber;
    }
}
