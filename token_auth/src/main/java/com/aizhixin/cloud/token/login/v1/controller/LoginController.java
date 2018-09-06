package com.aizhixin.cloud.token.login.v1.controller;

import com.aizhixin.cloud.token.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.token.config.AppStoreConfig;
import com.aizhixin.cloud.token.config.ScanCodeConfig;
import com.aizhixin.cloud.token.login.common.ResultClass;
import com.aizhixin.cloud.token.login.service.LoginService;
import com.sun.jmx.snmp.Timestamp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wu on 2017/7/19.
 */
@RestController
@RequestMapping("/v1/phone")
@Api("登录API")
public class LoginController {
   @Autowired
   private LoginService loginService;

    /**
     * 扫码——二维码的相关验证
     * @param socketId
     * @param timeStamp
     * @param identification
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/scancode/check", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "校验扫码是否成功", response = Void.class, notes = "校验扫码是否成功<br><br><b>@author jianwei.wu</b>")
    public ResultClass checkCode(
            @ApiParam(value = "socketId 浏览器和socket连接的Id") @RequestParam(value = "socketId", required = true) String socketId,
            @ApiParam(value = "timeStamp 时间戳", required = true) @RequestParam(value = "timeStamp") Long timeStamp,
            @ApiParam(value = "identification 二维码的标识", required = true) @RequestParam(value = "identification") String identification,
            @RequestHeader("Authorization") String accessToken) {
        return loginService.checkCodeService(socketId,timeStamp,identification,accessToken);
    }

    /**
     * 扫码——成功后的确认登录
     * @param socketId
     * @param timeStamp
     * @param identification
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/confirm", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "确定登录", response = Void.class, notes = "确定登录<br><br><b>@author jianwei.wu</b>")
    public ResultClass confirmLogin(
            @ApiParam(value = "socketId 浏览器和socket连接的Id") @RequestParam(value = "socketId", required = true) String socketId,
            @ApiParam(value = "timeStamp 时间戳", required = true) @RequestParam(value = "timeStamp") Long timeStamp,
            @ApiParam(value = "identification 二维码的标识", required = true) @RequestParam(value = "identification") String identification,
            @RequestHeader("Authorization") String accessToken) {
        return loginService.confirmLoginService(socketId, timeStamp, identification, accessToken);

    }


}
