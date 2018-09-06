package com.aizhixin.cloud.paycallback.entity;

import com.aizhixin.cloud.paycallback.common.entity.AbstractUUIDEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;


@ApiModel(description="支付宝学校缴费大厅缴费订单项明细")
@Entity(name = "T_PAYMENT_ORDER_ITEM")
@NoArgsConstructor
@ToString
public class PaymentOrderItem extends AbstractUUIDEntity implements java.io.Serializable {
    @ApiModelProperty(value = "缴费订单信息")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ORDER_ID")
    @Getter @Setter private PaymentOrder paymentOrder;

    @ApiModelProperty(value = "缴费项id(人员费用ID)")
    @Column(name = "ITEM_ID")
    @Getter @Setter private String itemId;

    @ApiModelProperty(value = "缴费项名称")
    @Column(name = "NAME")
    @Getter @Setter private String name;

    @ApiModelProperty(value = "未缴费金额，单位为分，例如1.99元的金额该参数为199")
    @Column(name = "FEE")
    @Getter @Setter private String fee;
}
