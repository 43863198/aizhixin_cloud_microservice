package com.aizhixin.cloud.paycallback.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@ApiModel(description="学生个人缴费数据详细数据视图对象")
@NoArgsConstructor
@ToString
public class PersonCostMobileDetailDomain {
    @ApiModelProperty(value = "个人缴费信息ID")
    @Getter @Setter private String personalCostId;
    @ApiModelProperty(value = "付款人姓名")
    @Getter @Setter private String buyerName;
    @ApiModelProperty(value = "身份证号码")
    @Getter @Setter private String idNumber;
    @ApiModelProperty(value = "付款人电话")
    @Getter @Setter private String buyerPhone;
    @ApiModelProperty(value = "收款方")
    @Getter @Setter private String sellerName = "四川长江职业学院";
    @ApiModelProperty(value = "费用标题")
    @Getter @Setter private String paymentSubjectName;
    @ApiModelProperty(value = "缴费方式(10全款，20分期)")
    @Getter @Setter private Integer paymentType;
    @ApiModelProperty(value = "分期频次(10首次，20每次)")
    @Getter @Setter private Integer installmentRate;
    @ApiModelProperty(value = "费用明细说明")
    @Getter @Setter private String payDesc;
    @ApiModelProperty(value = "最晚付款日期（截止日期）")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.DATE)
    @Getter @Setter private Date lastDate;
    @ApiModelProperty(value = "生效日期(发布日期)")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.DATE)
    @Getter @Setter private Date publishDate;
    @ApiModelProperty(value = "应缴费金额")
    @Getter @Setter private String shouldPay;
    @ApiModelProperty(value = "已缴费金额")
    @Getter @Setter private String hasPay;
    @ApiModelProperty(value = "欠费金额")
    @Getter @Setter private String owedPay;
    @ApiModelProperty(value = "最低缴费金额")
    @Getter @Setter private String smallPay;
    @ApiModelProperty(value = "缴费状态(10未缴费，20已欠费, 30已结清)")
    @Getter @Setter private Integer paymentState;
    @ApiModelProperty(value = "缴费科目发布状态(10待发布，20已发布, 30已取消, 70已过期)")
    @Getter @Setter private Integer publishState;

    @ApiModelProperty(value = "订单明细")
    @Getter @Setter List<PersonCostOrderListDomain> orderList;

//    public PersonCostMobileDetailDomain(String personalCostId, String paymentSubjectName, String paymentSubjectDesc, Date lastDate, Double smallPay, Double shouldPay, Double hasPay, Integer paymentState) {
//        this.personalCostId = personalCostId;
//        this.paymentSubjectName = paymentSubjectName;
//        this.payDesc = paymentSubjectDesc;
//        this.lastDate = lastDate;
//        this.smallPay = smallPay;
//        this.shouldPay = shouldPay;
//        if(null != hasPay) {
//            this.hasPay = hasPay;
//        } else {
//            this.hasPay = 0.00;
//        }
//        this.paymentState = paymentState;
//    }
}