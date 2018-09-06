package com.aizhixin.cloud.paycallback.domain.third;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="支付宝学校缴费大厅缴费订单项明细视图对象")
@NoArgsConstructor
@ToString
public class PaymentOrderItemDomain {
    @ApiModelProperty(value = "缴费项id(人员费用ID)")
    @Getter @Setter private String item_id;

    @ApiModelProperty(value = "缴费项名称")
    @Getter @Setter private String name;

    @ApiModelProperty(value = "单项缴纳金额，单位为分，例如1.99元的金额该参数为199")
    @Getter @Setter private String fee;
}
