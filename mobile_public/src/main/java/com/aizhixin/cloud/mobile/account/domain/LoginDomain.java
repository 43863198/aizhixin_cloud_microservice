package com.aizhixin.cloud.mobile.account.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description = "移动端用户登录、上传token、获取用户信息表单数据")
@NoArgsConstructor
@ToString
public class LoginDomain {
    @ApiModelProperty(value = "用户账号")
    @Getter @Setter private String username;
    @ApiModelProperty(value = "用户密码")
    @Getter @Setter private String pwd;
    @ApiModelProperty(value = "客户端密码")
    @Getter @Setter private String clientSecret;
    @ApiModelProperty(value = "客户端ID")
    @Getter @Setter private String clientId;
    @ApiModelProperty(value = "设备类型(10 IOS老师，20 IOS学生，30 Android老师，40 Android学生)")
    @Getter @Setter private Integer deviceTokenType;
    @ApiModelProperty(value = "设备码字符串")
    @Getter @Setter private String deviceToken;
}
