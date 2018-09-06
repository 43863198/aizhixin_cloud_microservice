package com.aizhixin.cloud.orgmanager.company.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="班级excel导入信息")
@NoArgsConstructor
@ToString
public class ClassesExcelDomain extends LineCodeNameBaseDomain {
    @ApiModelProperty(value = "professionalCode 专业编码", position=4)
    @Getter @Setter protected String professionalCode;
    @ApiModelProperty(value = "professionalName 专业名称", position=5)
    @Getter @Setter protected String professionalName;
    @ApiModelProperty(value = "grade 年级", position=8)
    @Getter @Setter protected String grade;

    public ClassesExcelDomain(Integer line, String code, String name, String professionalCode, String professionalName, String grade) {
        super(line, code, name);
        this.professionalCode = professionalCode;
        this.professionalName = professionalName;
        this.grade = grade;
    }
}
