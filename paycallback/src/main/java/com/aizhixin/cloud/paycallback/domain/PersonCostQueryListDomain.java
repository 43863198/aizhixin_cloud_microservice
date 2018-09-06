package com.aizhixin.cloud.paycallback.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="学生缴费数据视图对象")
@NoArgsConstructor
@ToString
public class PersonCostQueryListDomain {
    @ApiModelProperty(value = "ID")
    @Getter @Setter private String id;

    @ApiModelProperty(value = "姓名")
    @Getter @Setter private String name;

    @ApiModelProperty(value = "身份证号码")
    @Getter @Setter private String idNumber;

    @ApiModelProperty(value = "联系电话")
    @Getter @Setter private String phone;

    @ApiModelProperty(value = "录取编号")
    @Getter @Setter private String admissionNoticeNumber;

    @ApiModelProperty(value = "专业名称")
    @Getter @Setter private String professionalName;

    @ApiModelProperty(value = "应缴费")
    @Getter @Setter private Double shouldPay;

    @ApiModelProperty(value = "已缴费")
    @Getter @Setter private Double hasPay;

    @ApiModelProperty(value = "缴费状态(10未缴费，20已欠费, 30已结清)")
    @Getter @Setter private Integer paymentState;

    @ApiModelProperty(value = "费用明细说明")
    @Getter @Setter private String payDesc;

    @ApiModelProperty(value = "人员状态(10正常，20自愿放弃)")
    @Getter @Setter private Integer personalState;

    @ApiModelProperty(value = "缴费次数统计")
    @Getter @Setter private Long payNumber;

    public PersonCostQueryListDomain (String id, String name, String idNumber, String admissionNoticeNumber, String professionalName, Double shouldPay, Double hasPay, Integer paymentState, String payDesc, Integer personalState, Long payNumber) {
        this.id = id;
        this.name = name;
        this.idNumber = idNumber;
        this.admissionNoticeNumber = admissionNoticeNumber;
        this.personalState = personalState;
        this.professionalName = professionalName;
        this.shouldPay = shouldPay;
        if(null != hasPay) {
            this.hasPay = hasPay;
        } else {
            this.hasPay = 0.00;
        }
        this.paymentState = paymentState;
        this.payDesc = payDesc;
        this.personalState = personalState;
        this.payNumber = payNumber;
    }
}