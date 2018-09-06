package com.aizhixin.cloud.mobile.account.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@ToString
@ApiModel(description = "OAuth2 认证Token信息和用户信息")
public class OAuth2TokenUserInfo {
    @ApiModelProperty(value = "oauth2认证的token相关信息")
    @Getter @Setter private OAuthToken authToken;
    @ApiModelProperty(value = "认证成功后的用户信息")
    @Getter @Setter private MobileUserInfo userInfo;
}
