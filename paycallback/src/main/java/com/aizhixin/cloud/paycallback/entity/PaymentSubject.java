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

@ApiModel(description="缴费科目信息")
@Entity(name = "T_PAYMENT_SUBJECT")
@NoArgsConstructor
@ToString
public class PaymentSubject extends AbstractUUIDEntity implements java.io.Serializable {
    @ApiModelProperty(value = "缴费科目名称")
    @Column(name = "NAME")
    @Getter @Setter private String name;

    @ApiModelProperty(value = "缴费方式(10全款，20分期)")
    @Column(name = "PAYMENT_TYPE")
    @Getter @Setter private Integer paymentType;

    @ApiModelProperty(value = "分期的最低额度")
    @Column(name = "SMALL_AMOUNT")
    @Getter @Setter private Double smallAmount;

    @ApiModelProperty(value = "分期频次(10首次，20每次)")
    @Column(name = "INSTALLMENT_RATE")
    @Getter @Setter private Integer installmentRate;

    @ApiModelProperty(value = "缴费科目发布状态(10待发布，20已发布, 70已过期)")
    @Column(name = "PUBLISH_STATE")
    @Getter @Setter private Integer publishState;

    @ApiModelProperty(value = "发布时间")
    @Column(name = "PUBLISH_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter private Date publishTime;

    @ApiModelProperty(value = "最晚付款日期（截止日期）")
    @Column(name = "LAST_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.DATE)
    @Getter @Setter private Date lastDate;

    @ApiModelProperty(value = "学校ID")
    @Column(name = "ORG_ID")
    @Getter @Setter private Long orgId;
}
