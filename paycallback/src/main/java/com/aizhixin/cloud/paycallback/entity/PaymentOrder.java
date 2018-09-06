package com.aizhixin.cloud.paycallback.entity;

import com.aizhixin.cloud.paycallback.common.entity.AbstractUUIDEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;


@ApiModel(description="支付宝学校缴费大厅缴费订单")
@Entity(name = "T_PAYMENT_ORDER")
@NoArgsConstructor
@ToString
public class PaymentOrder extends AbstractUUIDEntity implements java.io.Serializable {
    @ApiModelProperty(value = "缴费者id（学号/工号等）/身份证号码")
    @Column(name = "USER_NO")
    @Getter @Setter private String userNo;

    @ApiModelProperty(value = "支付方式,默认为支付宝支付(AL))")
    @Column(name = "PAY_TYPE")
    @Getter @Setter private String payType;

    @ApiModelProperty(value = "业务方订单号,不能超过40个字符")
    @Column(name = "ORDER_ID")
    @Getter @Setter private String orderId;

    @ApiModelProperty(value = "订单开始时间，格式为2017-01-09 10:10:10")
    @Column(name = "ORDER_START_DATE")
    @Getter @Setter private String orderStartDate;

    @ApiModelProperty(value = "订单完成时间，格式为2017-01-09 10:10:10")
    @Column(name = "ORDER_END_DATE")
    @Getter @Setter private String orderEndDate;

    @ApiModelProperty(value = "订单金额")
    @Column(name = "TOTAL_FEE")
    @Getter @Setter private String totalFee;

    @ApiModelProperty(value = "结果说明")
    @Column(name = "INFO")
    @Getter @Setter private String info;
}
