package com.aizhixin.cloud.paycallback.domain;


import com.aizhixin.cloud.paycallback.entity.PaymentSubject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@ApiModel(description="缴费科目信息导入及设置对象")
@NoArgsConstructor
@ToString
public class PaymentSubjectDomain {
    @ApiModelProperty(value = "ID")
    @Getter @Setter private String id;

    @ApiModelProperty(value = "上传文件")
    @Getter @Setter private MultipartFile file;//上传文件

    @ApiModelProperty(value = "缴费科目名称")
    @Getter @Setter private String name;

    @ApiModelProperty(value = "缴费方式(10全款，20分期)")
    @Getter @Setter private Integer paymentType;

    @ApiModelProperty(value = "分期的最低额度")
    @Getter @Setter private Double smallAmount;

    @ApiModelProperty(value = "分期频次(10首次，20每次)")
    @Getter @Setter private Integer installmentRate;

    @ApiModelProperty(value = "最晚付款日期（截止日期）")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date lastDate;

    @ApiModelProperty(value = "userId操作人ID")
    @Getter @Setter private Long userId;

    @ApiModelProperty(value = "缴费科目发布状态(10待发布，20已发布, 70已过期)", position=6)
    @Getter @Setter private Integer publishState;

    public PaymentSubjectDomain (PaymentSubject paymentSubject) {
        if (null == paymentSubject) return;
        this.id = paymentSubject.getId();
        this.name = paymentSubject.getName();
        this.lastDate = paymentSubject.getLastDate();
        this.paymentType = paymentSubject.getPaymentType();
        this.installmentRate = paymentSubject.getInstallmentRate();
        this.smallAmount = paymentSubject.getSmallAmount();
        this.publishState = paymentSubject.getPublishState();
    }
}
