package com.aizhixin.cloud.paycallback.domain.third;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ApiModel(description="支付宝学校缴费大厅缴费订单视图对象")
@NoArgsConstructor
@ToString
public class PaymentOrderDomain {
    @ApiModelProperty(value = "缴费者id（学号/工号等）/身份证号码")
    @Getter @Setter private String user_no;

    @ApiModelProperty(value = "支付方式,默认为支付宝支付(AL))")
    @Getter @Setter private String pay_type;

    @ApiModelProperty(value = "业务方订单号,不能超过40个字符")
    @Getter @Setter private String order_id;

    @ApiModelProperty(value = "订单开始时间，格式为2017-01-09 10:10:10")
    @Getter @Setter private String order_start_date;

    @ApiModelProperty(value = "订单完成时间，格式为2017-01-09 10:10:10")
    @Getter @Setter private String order_end_date;

    @ApiModelProperty(value = "订单金额")
    @Getter @Setter private String total_fee;

    @ApiModelProperty(value = "结果说明")
    @Getter @Setter private String info;

    @ApiModelProperty(value = "缴费项列表")
    @Getter @Setter private List<PaymentOrderItemDomain> data;
}
