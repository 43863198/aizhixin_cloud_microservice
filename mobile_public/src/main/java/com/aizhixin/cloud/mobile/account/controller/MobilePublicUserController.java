package com.aizhixin.cloud.mobile.account.controller;


import com.aizhixin.cloud.mobile.account.domain.LoginDomain;
import com.aizhixin.cloud.mobile.account.domain.OAuth2TokenUserInfo;
import com.aizhixin.cloud.mobile.account.service.AccountService;
import com.aizhixin.cloud.mobile.common.exception.NoAuthenticationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phone/v1")
@Api(description = "公共手机用户相关API")
public class MobilePublicUserController {
    private AccountService accountService;
    @Autowired
    public MobilePublicUserController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "认证、上传设备token并且获取用户信息", response = Void.class, notes = "认证、上传设备token并且获取用户信息<br>@author panzhen")
    public OAuth2TokenUserInfo login(@RequestHeader("Authorization") String authorization, @RequestBody LoginDomain loginDomain) {
        if (null == loginDomain || StringUtils.isEmpty(authorization)) {
            throw new NoAuthenticationException();
        }
        return accountService.loginMutilOperate(loginDomain.getUsername(), loginDomain.getPwd(), authorization, loginDomain.getClientSecret(), loginDomain.getClientId(), loginDomain.getDeviceTokenType(), loginDomain.getDeviceToken());
    }
}
