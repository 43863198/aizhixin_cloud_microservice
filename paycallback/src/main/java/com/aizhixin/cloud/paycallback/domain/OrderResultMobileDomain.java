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

@ApiModel(description="本次订单支付立即查询结果")
@NoArgsConstructor
@ToString
public class OrderResultMobileDomain {
    @ApiModelProperty(value = "个人缴费信息ID")
    @Getter @Setter private String personalCostId;
    @ApiModelProperty(value = "收款方")
    @Getter @Setter private String sellerName = "四川长江职业学院";
    @ApiModelProperty(value = "费用标题")
    @Getter @Setter private String paymentSubjectName;
    @ApiModelProperty(value = "费用明细说明")
    @Getter @Setter private String payDesc;
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
}