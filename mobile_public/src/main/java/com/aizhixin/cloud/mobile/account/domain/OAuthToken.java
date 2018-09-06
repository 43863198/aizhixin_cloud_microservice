package com.aizhixin.cloud.mobile.account.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@ApiModel(description = "OAuth2 认证Token")
public class OAuthToken {
    @ApiModelProperty(value = "访问token")
    @Getter @Setter private String access_token;
    @ApiModelProperty(value = "token 类型")
    @Getter @Setter private String token_type;
    @ApiModelProperty(value = "刷新token")
    @Getter @Setter private String refresh_token;
    @ApiModelProperty(value = "有效剩余期限")
    @Getter @Setter private Long expires_in;
    @ApiModelProperty(value = "范围")
    @Getter @Setter private String scope;
}
