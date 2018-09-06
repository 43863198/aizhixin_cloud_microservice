package com.aizhixin.cloud.paycallback.entity;

import com.aizhixin.cloud.paycallback.common.entity.AbstractUUIDEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;


@ApiModel(description="人员费用订单")
@Entity(name = "T_PERSONAL_COST_ORDER")
@NoArgsConstructor
@ToString
public class PersonalCostOrder extends AbstractUUIDEntity implements java.io.Serializable {
    @ApiModelProperty(value = "人员费用")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSONAL_COST_ID")
    @Getter @Setter private PersonalCost personalCost;
    @ApiModelProperty(value = "订单号(缴费)")
    @Column(name = "ORDER_NO")
    @Getter @Setter private String orderNo;
    @ApiModelProperty(value = "订单类型(10付款，20退款)")
    @Column(name = "ORDER_TYPE")
    @Getter @Setter private Integer orderType;
    @ApiModelProperty(value = "订单标题")
    @Column(name = "ORDER_SUBJECT")
    @Getter @Setter private String orderSubject;
    @ApiModelProperty(value = "订单内容说明")
    @Column(name = "ORDER_CONTENT")
    @Getter @Setter private String orderContent;
    @ApiModelProperty(value = "订单金额")
    @Column(name = "ORDER_AMOUNT")
    @Getter @Setter private Double orderAmount;
    @ApiModelProperty(value = "订单状态(10初始(待付款/待退款)，20已付款/已退款, 30已超时/已取消, 40已完成)")
    @Column(name = "ORDER_STATUS")
    @Getter @Setter private Integer orderStatus;
    @ApiModelProperty(value = "支付宝交易号")
    @Column(name = "ALI_TRADE_NO")
    @Getter @Setter private String aliTradeNo;
    @ApiModelProperty(value = "缴费科目ID，冗余字段，方便查询")
    @Column(name = "PAYMENT_SUBJECT_ID")
    @Getter @Setter private String paymentSubjectId;
    @ApiModelProperty(value = "收款时间")
    @Column(name = "PAY_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter private Date payTime;
}
