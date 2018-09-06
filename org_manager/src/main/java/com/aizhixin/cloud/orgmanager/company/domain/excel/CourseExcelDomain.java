package com.aizhixin.cloud.orgmanager.company.domain.excel;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="课程excel导入信息")
@NoArgsConstructor
@ToString
public class CourseExcelDomain extends LineCodeNameBaseDomain {
    @ApiModelProperty(value = "courseProp 课程附加属性", position=4)
    @Getter @Setter protected String courseProp;
    @ApiModelProperty(value = "credit 学分", position=5)
    @Getter @Setter protected String credit;

    public CourseExcelDomain(Integer line, String code, String name, String courseProp, String credit) {
        super(line, code, name);
        this.courseProp = courseProp;
        this.credit = credit;
    }
}
