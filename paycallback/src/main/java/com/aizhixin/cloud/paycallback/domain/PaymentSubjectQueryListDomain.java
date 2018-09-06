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

@ApiModel(description="id和name信息")
@NoArgsConstructor
@ToString
public class PaymentSubjectQueryListDomain {
    @ApiModelProperty(value = "ID", position=1)
    @Getter @Setter private String id;

    @ApiModelProperty(value = "名称", position=2)
    @Getter @Setter private String name;

    @ApiModelProperty(value = "最晚付款日期（截止日期）")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date lastDate;

    @ApiModelProperty(value = "缴费方式(10全款，20分期)")
    @Getter @Setter private Integer paymentType;

    @ApiModelProperty(value = "缴费科目发布状态(10待发布，20已发布, 30已终止, 70已过期)", position=6)
    @Getter @Setter private Integer publishState;

    public PaymentSubjectQueryListDomain(String id, String name, Integer publishState, Date lastDate, Integer paymentType) {
        this.id = id;
        this.name = name;
        this.publishState = publishState;
        this.lastDate = lastDate;
        this.paymentType = paymentType;
    }
}
