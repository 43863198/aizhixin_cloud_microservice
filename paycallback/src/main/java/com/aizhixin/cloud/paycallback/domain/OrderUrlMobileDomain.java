package com.aizhixin.cloud.paycallback.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="唤起支付宝APP的支付URL")
@NoArgsConstructor
@ToString
public class OrderUrlMobileDomain {
    @ApiModelProperty(value = "支付宝支付URL")
    @Getter @Setter private String payUrl;
}