package com.aizhixin.cloud.paycallback.domain.third;


import com.aizhixin.cloud.paycallback.common.util.NumberUtil;
import com.aizhixin.cloud.paycallback.core.PaymentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ApiModel(description="学生缴费项查询条目对象")
@NoArgsConstructor
@ToString
public class StudentPaySubjectDomain {
    @ApiModelProperty(value = "缴费科目ID")
    @Getter @Setter private String item_id;

    @ApiModelProperty(value = "缴费科目名称")
    @Getter @Setter private String name;

    @ApiModelProperty(value = "缴费项时间，时间格式为2017-01-09")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date date;

    @ApiModelProperty(value = "是否支持修改金额支付（多次支付），=0不支持，为默认值，=1支持")
    @Getter @Setter private String allow_modify;

    @ApiModelProperty(value = "缴费项总金额，单位为分，例如1.99元的金额该参数为19")
    @Getter @Setter private String total_fee;

    @ApiModelProperty(value = "未缴费金额，单位为分，例如1.99元的金额该参数为199")
    @Getter @Setter private String fee;

    @ApiModelProperty(value = "=1未支付，=2已支付")
    @Getter @Setter private String status;

    public StudentPaySubjectDomain(String item_id, String name, Date date, Integer paymentType, Double shouldPay, Double hasPay) {
        this.item_id = item_id;
        this.name = name;
        this.date = date;
        if(PaymentType.ALL.getState().intValue() == paymentType) {
            allow_modify = "0";
        } else {
            allow_modify = "1";
        }
        if (null == shouldPay) {
            shouldPay = 0.0;
        }
        total_fee = NumberUtil.doubleToIntString(shouldPay * 100);
        if (null == hasPay) {
            hasPay = 0.0;
        }
        fee = NumberUtil.doubleToIntString(hasPay * 100);
        if (hasPay < shouldPay) {
            status = "1";
        } else {
            status = "2";
        }
    }
}
