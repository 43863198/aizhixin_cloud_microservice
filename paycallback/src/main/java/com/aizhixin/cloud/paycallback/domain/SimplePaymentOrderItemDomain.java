package com.aizhixin.cloud.paycallback.domain;


import com.aizhixin.cloud.paycallback.common.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="人员费用支付订单明细")
@ToString
@NoArgsConstructor
public class SimplePaymentOrderItemDomain {
    @ApiModelProperty(value = "订单号")
    @Getter @Setter private String orderNo;
    @ApiModelProperty(value = "付款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter @Setter private String payTime;
    @ApiModelProperty(value = "订单金额")
    @Getter @Setter private String amount;

    public SimplePaymentOrderItemDomain (String orderNo, String payTime, String fee) {
        this.orderNo = orderNo;
        this.payTime = payTime;
        if (null != fee) {
            this.amount = NumberUtil.doubleToString(new Double(fee)/100);
        }
    }
}
