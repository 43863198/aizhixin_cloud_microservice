package com.aizhixin.cloud.paycallback.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel(description="订单费用明细")
@NoArgsConstructor
@ToString
public class PersonCostOrderListDomain {
    @ApiModelProperty(value = "订单号")
    @Getter @Setter private String orderNo;
    @ApiModelProperty(value = "付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter @Setter private Date payTime;
    @ApiModelProperty(value = "订单金额")
    @Getter @Setter private Double amount;
    @ApiModelProperty(value = "支付宝订单号")
    @Getter @Setter private String aliOrderNo;

    public PersonCostOrderListDomain (String orderNo, Date payTime, Double amount, String aliOrderNo) {
        this.orderNo = orderNo;
        this.payTime = payTime;
        this.amount = amount;
        this.aliOrderNo = aliOrderNo;
    }
}
