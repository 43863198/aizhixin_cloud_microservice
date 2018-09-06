package com.aizhixin.cloud.paycallback.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="学生缴费数据Excel对象模板")
@NoArgsConstructor
@ToString
public class PersonCostExcelDomain {
    @ApiModelProperty(value = "姓名")
    @Getter @Setter private String name;

    @ApiModelProperty(value = "性别")
    @Getter @Setter private String sex;

    @ApiModelProperty(value = "身份证号码")
    @Getter @Setter private String idNumber;

    @ApiModelProperty(value = "录取编号")
    @Getter @Setter private String admissionNoticeNumber;

    @ApiModelProperty(value = "生源地")
    @Getter @Setter private String studentSource;

    @ApiModelProperty(value = "学生类别(单招、统招)")
    @Getter @Setter private String studentType;

    @ApiModelProperty(value = "层次(专科、本科)")
    @Getter @Setter private String eduLevel;

    @ApiModelProperty(value = "专业名称")
    @Getter @Setter private String professionalName;

    @ApiModelProperty(value = "学院名称")
    @Getter @Setter private String collegeName;

    @ApiModelProperty(value = "年级")
    @Getter @Setter private String grade;

    @ApiModelProperty(value = "校区")
    @Getter @Setter private String schoolLocal;

    @ApiModelProperty(value = "应缴费")
    @Getter @Setter private Double shouldPay;

    @ApiModelProperty(value = "费用明细说明")
    @Getter @Setter private String payDesc;

    @ApiModelProperty(value = "说明")
    @Getter @Setter private String msg;
}
