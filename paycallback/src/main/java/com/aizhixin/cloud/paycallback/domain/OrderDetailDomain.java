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

@ApiModel(description="订单明细查询列表")
@NoArgsConstructor
@ToString
public class OrderDetailDomain {
    @ApiModelProperty(value = "ID")
    @Getter @Setter private String id;
    @ApiModelProperty(value = "人员费用ID")
    @Getter @Setter private String personCostId;
    @ApiModelProperty(value = "订单号")
    @Getter @Setter private String orderNo;
    @ApiModelProperty(value = "姓名")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "身份证号码")
    @Getter @Setter private String idNumber;
    @ApiModelProperty(value = "录取编号")
    @Getter @Setter private String admissionNoticeNumber;
    @ApiModelProperty(value = "专业名称")
    @Getter @Setter private String professionalName;
    @ApiModelProperty(value = "付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter @Setter private Date payTime;
    @ApiModelProperty(value = "订单金额")
    @Getter @Setter private Double amount;
    @ApiModelProperty(value = "支付宝订单号")
    @Getter @Setter private String aliOrderNo;
    @ApiModelProperty(value = "订单状态(10未完成，20已完成, 30已过期，40已失败)")
    @Getter @Setter private Integer orderState;
    @ApiModelProperty(value = "人员状态(10正常，20自愿放弃)")
    @Getter @Setter private Integer personalState;

    public OrderDetailDomain (String id, String personCostId, String name, String idNumber, String admissionNoticeNumber, String professionalName, String orderNo, Date payTime, Double amount, String aliOrderNo, Integer personalState) {
        this.id = id;
        this.personCostId = personCostId;
        this.name = name;
        this.idNumber = idNumber;
        this.admissionNoticeNumber = admissionNoticeNumber;
        this.professionalName = professionalName;
        this.orderNo = orderNo;
        this.payTime = payTime;
        this.amount = amount;
        this.aliOrderNo = aliOrderNo;
        this.personalState = personalState;
    }
}
