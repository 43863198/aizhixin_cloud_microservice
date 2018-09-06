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

@ApiModel(description="学生个人缴费数据视图简单对象")
@NoArgsConstructor
@ToString
public class PersonCostMobileListDomain {
    @ApiModelProperty(value = "个人缴费信息ID")
    @Getter @Setter private String personalCostId;

    @ApiModelProperty(value = "收款方")
    @Getter @Setter private String sellerName = "四川长江职业学院";

    @ApiModelProperty(value = "费用标题")
    @Getter @Setter private String paymentSubjectName;

    @ApiModelProperty(value = "缴费方式(10全款，20分期)")
    @Getter @Setter private Integer paymentType;

    @ApiModelProperty(value = "最晚付款日期（截止日期）")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.DATE)
    @Getter @Setter private Date lastDate;

    @ApiModelProperty(value = "总应缴费")
    @Getter @Setter private Double shouldPay;

    @ApiModelProperty(value = "需缴费")
    @Getter @Setter private Double needPay;

    @ApiModelProperty(value = "已缴费")
    @Getter @Setter private Double hasPay;

    @ApiModelProperty(value = "缴费状态(10未缴费，20已欠费, 30已结清, 70已过期)")
    @Getter @Setter private Integer paymentState;
    @ApiModelProperty(value = "缴费科目发布状态(10待发布，20已发布, 70已过期)")
    @Getter @Setter private Integer publishState;

    public PersonCostMobileListDomain(String personalCostId, String paymentSubjectName, Date lastDate, Integer paymentType, Integer publishState, Double shouldPay, Double hasPay, Integer paymentState) {
        this.personalCostId = personalCostId;
        this.paymentSubjectName = paymentSubjectName;
        this.lastDate = lastDate;
        this.paymentType = paymentType;
        this.publishState = publishState;
        this.shouldPay = shouldPay;
        if (null == this.shouldPay) {
            this.shouldPay = 0.0;
        }
        if(null != hasPay) {
            this.hasPay = hasPay;
        } else {
            this.hasPay = 0.00;
        }
        needPay = this.shouldPay - this.hasPay;
        this.paymentState = paymentState;
    }
}