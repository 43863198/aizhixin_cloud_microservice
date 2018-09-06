package com.aizhixin.cloud.orgmanager.company.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel(description="B端老师excel导入信息")
@NoArgsConstructor
@ToString
public class TeacherExcelDomain extends LineCodeNameBaseDomain implements Serializable {
    @ApiModelProperty(value = "sex 性别", position=4)
    @Getter @Setter protected String sex;
    @ApiModelProperty(value = "collegeCode 学院编码", position=5)
    @Getter @Setter protected String collegeCode;
    @ApiModelProperty(value = "collegeName 学院名称", position=6)
    @Getter @Setter protected String collegeName;
    @ApiModelProperty(value = "phone 手机号码", position=7)
    @Getter @Setter protected String phone;
    @ApiModelProperty(value = "mail 电子邮箱", position=8)
    @Getter @Setter protected String mail;

    public TeacherExcelDomain(Integer line, String code, String name, String sex, String collegeCode, String collegeName, String phone, String mail) {
        super(line, code, name);
        this.sex = sex;
        this.collegeCode = collegeCode;
        this.collegeName = collegeName;
        this.phone = phone;
        this.mail = mail;
    }
}
