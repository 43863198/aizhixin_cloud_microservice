package com.aizhixin.cloud.dd.messege.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.messege.domain.PushMsgDomain;
import com.aizhixin.cloud.dd.messege.service.MsgService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/phone/v2")
@Api(description = "手机端消息模块V2")
public class PushMsgPhoneController {

    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private MsgService msgService;

    @RequestMapping(value = "/msg/getModule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "获取用户模块信息", httpMethod = "GET", response = Void.class)
    public ResponseEntity<Map<String, Object>> getMsg(@RequestHeader("Authorization") String accessToken) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO ad = ddUserService.getUserInfoWithLogin(accessToken);
        if (null == ad) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        List<PushMsgDomain> pmsdl = msgService.getListMsgModulev2(ad);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, pmsdl);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

}
