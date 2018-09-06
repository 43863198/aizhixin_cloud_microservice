package com.aizhixin.cloud.orgmanager.company.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="专业excel导入信息")
@NoArgsConstructor
@ToString
public class ProfessionalExcelDomain extends LineCodeNameBaseDomain {
    @ApiModelProperty(value = "collegeCode 学院编码", position=4)
    @Getter @Setter protected String collegeCode;
    @ApiModelProperty(value = "collegeName 学院名称", position=5)
    @Getter @Setter protected String collegeName;

    public ProfessionalExcelDomain (Integer line, String code, String name, String collegeCode, String collegeName) {
        super(line, code, name);
        this.collegeCode = collegeCode;
        this.collegeName = collegeName;
    }
}
