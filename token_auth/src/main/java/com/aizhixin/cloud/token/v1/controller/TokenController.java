package com.aizhixin.cloud.token.v1.controller;

import com.aizhixin.cloud.token.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.token.service.AccessTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhen.pan on 2017/6/9.
 */
@RestController
@RequestMapping("/v1/token")
@Api("Token管理API")
public class TokenController {
    private AccessTokenService accessTokenService;
    @Autowired
    public TokenController(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "获取应用Token", response = Void.class, notes = "获取应用Token<br><br><b>@author zhen.pan</b>")
    public Map<String, String> auth(@ApiParam(value = "appId 应用ID", required = true) @RequestParam(value = "appId") String appId,
                                    @ApiParam(value = "appSecurity 应用密码", required = true) @RequestParam(value = "appSecurity") String appSecurity,
                                    @ApiParam(value = "auth 权限验证码(base64编码[appId:appSecurity])", required = true) @RequestParam(value = "auth") String auth,
                                    @ApiParam(value = "ttl token的有效时长，单位秒；缺省86400秒") @RequestParam(value = "ttl", required = false) Integer ttl) {
        Map<String, String> res = new HashMap<>();
        String token = accessTokenService.createToken(appId, appSecurity, auth, ttl);
        if (StringUtils.isEmpty(token)) {
            throw new NoAuthenticationException();
        } else {
            res.put("token", token);
        }
        return res;
    }

    @RequestMapping(value = "/authtokean", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "验证应用Token http code 200成功，否则失败", response = Void.class, notes = "验证应用Token http code 200成功，否则失败<br><br><b>@author zhen.pan</b>")
    public Map<String, String> authTokean(@ApiParam(value = "appId 应用ID", required = true) @RequestParam(value = "appId") String appId,
                                    @ApiParam(value = "token 应用token", required = true) @RequestParam(value = "token") String token) {
        Map<String, String> res = new HashMap<>();
        boolean r = accessTokenService.validateToken(appId, token);
        if (!r) {
            throw new NoAuthenticationException();
        }
        return res;
    }
}
