package com.aizhixin.cloud.paycallback.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="学生最近缴费科目的缴费情况")
@NoArgsConstructor
@ToString
public class PayResultDomain {
    @ApiModelProperty(value = "缴费次数")
    @Getter @Setter private Long payCount;
    @ApiModelProperty(value = "最低缴费（如果有分期）")
    @Getter @Setter private Double smallPay;
    @ApiModelProperty(value = "已缴费")
    @Getter @Setter private Double hasPay;
}
