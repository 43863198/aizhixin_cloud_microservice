package com.aizhixin.cloud.paycallback.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="需交费的用户信息")
@ToString
@NoArgsConstructor
public class FeeUserInfoDomain {
    @ApiModelProperty(value = "缴费者id（学号/工号等），我这边使用身份证号")
    @Getter @Setter private String user_no;
    @ApiModelProperty(value = "用户姓名")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "账号ID")
    @Getter @Setter private String major;
    @ApiModelProperty(value = "登录账号")
    @Getter @Setter private String id_card;

    public FeeUserInfoDomain (String name, String id_card, String major) {
        this.user_no = id_card;
        this.name = name;
        this.major = major;
        this.id_card = id_card;
    }
}