package com.aizhixin.cloud.orgmanager.common.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="id及成功标志信息")
@ToString
@NoArgsConstructor
public class SuccessDomain {
    @ApiModelProperty(value = "ID")
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "message")
    @Getter @Setter private String message = "SUCCESS";
}
