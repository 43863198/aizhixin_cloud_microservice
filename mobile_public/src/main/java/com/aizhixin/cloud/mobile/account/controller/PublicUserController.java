package com.aizhixin.cloud.mobile.account.controller;


import com.aizhixin.cloud.mobile.account.domain.EncodingDomain;
import com.aizhixin.cloud.mobile.account.domain.UserInfoDomain;
import com.aizhixin.cloud.mobile.account.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Api(description = "公共手机用户相关API")
public class PublicUserController {
    private AccountService accountService;

    @Autowired
    public PublicUserController (AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取当前token的用户信息（含学校信息），可以取代知新API的相应API", response = Void.class, notes = "获取当前token的用户信息（含学校信息），可以取代知新API的相应API<br>@author panzhen")
    public UserInfoDomain getUserInfo(@RequestHeader("Authorization") String authorization) {
        return accountService.getCurrentUserInfo(authorization);
    }


    @RequestMapping(value = "/decoder", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "解密密文", response = Void.class, notes = "解密密文<br>@author panzhen")
    public EncodingDomain doDecoder(@RequestParam("密文") String encodeString) {
        return new EncodingDomain(accountService.decoderString(encodeString), encodeString);
    }


    @RequestMapping(value = "/encoding", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "加密文本", response = Void.class, notes = "加密文本<br>@author panzhen")
    public EncodingDomain doEncoder(@RequestParam("明文") String plainString) {
        return new EncodingDomain(plainString, accountService.encoderString(plainString));
    }
}
