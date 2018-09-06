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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


/**
 * 参考文档：https://docs.open.alipay.com/204/105301/
 */
@ApiModel(description="阿里回调日志")
@Entity(name = "T_ALI_CALLBACK_LOG_RECORD")
@NoArgsConstructor
@ToString
public class AliCallBackLogRecord extends AbstractUUIDEntity implements java.io.Serializable {
    @ApiModelProperty(value = "通知时间")
    @Column(name = "NOTIFY_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter private Date notifyTime;
    @ApiModelProperty(value = "通知类型")
    @Column(name = "NOTIFY_TYPE")
    @Getter @Setter private String notifyType;
    @ApiModelProperty(value = "通知校验ID")
    @Column(name = "NOTIFY_ID")
    @Getter @Setter private String notifyId;
    @ApiModelProperty(value = "支付宝分配给开发者的应用Id")
    @Column(name = "APP_ID")
    @Getter @Setter private String appId;
    @ApiModelProperty(value = "编码格式")
    @Column(name = "F_CHARSET")
    @Getter @Setter private String charset;
    @ApiModelProperty(value = "接口版本")
    @Column(name = "F_VERSION")
    @Getter @Setter private String version;
    @ApiModelProperty(value = "签名类型")
    @Column(name = "SIGN_TYPE")
    @Getter @Setter private String signType;
    @ApiModelProperty(value = "签名")
    @Column(name = "SIGN")
    @Getter @Setter private String sign;
    @ApiModelProperty(value = "支付宝交易号")
    @Column(name = "TRADE_NO")
    @Getter @Setter private String tradeNo;
    @ApiModelProperty(value = "商户订单号")
    @Column(name = "OUT_TRADE_NO")
    @Getter @Setter private String outTradeNo;
    @ApiModelProperty(value = "商户业务号")
    @Column(name = "OUT_BIZ_NO")
    @Getter @Setter private String outBizNo;
    @ApiModelProperty(value = "买家支付宝用户号")
    @Column(name = "BUYER_ID")
    @Getter @Setter private String buyerId;
    @ApiModelProperty(value = "买家支付宝账号")
    @Column(name = "BUYER_LOGON_ID")
    @Getter @Setter private String buyerLogonId;
    @ApiModelProperty(value = "卖家支付宝用户号")
    @Column(name = "SELLER_ID")
    @Getter @Setter private String sellerId;
    @ApiModelProperty(value = "卖家支付宝账号")
    @Column(name = "SELLER_EMAIL")
    @Getter @Setter private String sellerEmail;
    @ApiModelProperty(value = "交易状态")
    @Column(name = "TRADE_STATUS")
    @Getter @Setter private String tradeStatus;
    @ApiModelProperty(value = "订单金额")
    @Column(name = "TOTAL_AMOUNT")
    @Getter @Setter private Double totalMmount;
    @ApiModelProperty(value = "实收金额")
    @Column(name = "RECEIPT_AMOUNT")
    @Getter @Setter private Double receiptAmount;
    @ApiModelProperty(value = "开票金额")
    @Column(name = "INVOICE_AMOUNT")
    @Getter @Setter private Double invoiceAmount;
    @ApiModelProperty(value = "付款金额")
    @Column(name = "BUYER_PAY_AMOUNT")
    @Getter @Setter private Double buyerPayAmount;
    @ApiModelProperty(value = "集分宝金额")
    @Column(name = "POINT_AMOUNT")
    @Getter @Setter private Double pointAmount;
    @ApiModelProperty(value = "总退款金额")
    @Column(name = "REFUND_FEE")
    @Getter @Setter private Double refundFee;
    @ApiModelProperty(value = "订单标题")
    @Column(name = "SUBJECT")
    @Getter @Setter private String subject;
    @ApiModelProperty(value = "商品描述")
    @Column(name = "BODY")
    @Getter @Setter private String body;
    @ApiModelProperty(value = "交易创建时间")
    @Column(name = "GMT_CREATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter private Date gmtCreate;
    @ApiModelProperty(value = "交易付款时间")
    @Column(name = "GMT_PAYMENT")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter private Date gmtPayment;
    @ApiModelProperty(value = "交易退款时间")
    @Column(name = "GMT_REFUND")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter private Date gmtRefund;
    @ApiModelProperty(value = "交易结束时间")
    @Column(name = "GMT_CLOSE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter private Date gmtClose;
    @ApiModelProperty(value = "支付金额信息")
    @Column(name = "FUND_BILL_LIST")
    @Getter @Setter private String fundBillList;
    @ApiModelProperty(value = "回传参数")
    @Column(name = "PASSBACK_PARAMS")
    @Getter @Setter private String passbackParams;
    @ApiModelProperty(value = "认证APP ID")
    @Column(name = "AUTH_APP_ID")
    @Getter @Setter private String authAppId;
}
