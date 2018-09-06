package com.aizhixin.cloud.paycallback.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="缴费科目统计对象")
@NoArgsConstructor
@ToString
public class PaymentSubjectCalculateDomain {
    @ApiModelProperty(value = "缴费科目名称")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "总人数")
    @Getter @Setter private Long totalPersons;
    @ApiModelProperty(value = "未付款人数")
    @Getter @Setter private Long noPayPersons;
    @ApiModelProperty(value = "欠费人数")
    @Getter @Setter private Long owedPersons;
    @ApiModelProperty(value = "已结清人数")
    @Getter @Setter private Long completePersions;
    @ApiModelProperty(value = "总应付金额")
    @Getter @Setter private Double totalShouldPay;
    @ApiModelProperty(value = "未付款总应付金额")
    @Getter @Setter private Double noPayShouldPay;
    @ApiModelProperty(value = "欠费总已付金额")
    @Getter @Setter private Double owedHasPay;
    @ApiModelProperty(value = "已结清总已付金额")
    @Getter @Setter private Double completeHasPay;
}
