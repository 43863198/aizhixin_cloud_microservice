package com.aizhixin.cloud.paycallback.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="人员费用缴费大厅订单明细")
@ToString
@NoArgsConstructor
public class PaymentOrderItemListDomain {
    @ApiModelProperty(value = "订单号")
    @Getter @Setter private String orderNo;
    @ApiModelProperty(value = "姓名")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "身份证号码")
    @Getter @Setter private String idNumber;
    @ApiModelProperty(value = "专业名称")
    @Getter @Setter private String professionalName;
    @ApiModelProperty(value = "付款时间")
    @Getter @Setter private String payTime;
    @ApiModelProperty(value = "订单金额")
    @Getter @Setter private String amount;
    @ApiModelProperty(value = "人员状态(10正常，20自愿放弃)")
    @Getter @Setter private Integer personalState;

//    public PaymentOrderItemListDomain(String personCostId, String name, String idNumber, String professionalName, Integer personalState) {
//        this.personCostId = personCostId;
//        this.name = name;
//        this.idNumber = idNumber;
//        this.professionalName = professionalName;
//        this.personalState = personalState;
//    }
}
