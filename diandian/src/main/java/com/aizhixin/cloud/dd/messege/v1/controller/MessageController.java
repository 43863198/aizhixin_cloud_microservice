package com.aizhixin.cloud.dd.messege.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.messege.domain.MessageResultDomain;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTOV2;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/v1/message")
@RestController
@Api(value = "消息服务API", description = "消息服务API")
public class MessageController {

    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/push", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "发送消息", response = Void.class, notes = "发送消息<br>@author hsh")
    public ResponseEntity<?> push(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "消息") @RequestBody MessageDTO messageDTO) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            messageService.push(messageDTO);
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception ex) {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/pushMessage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "发送消息", response = Void.class, notes = "发送消息<br>@author hsh")
    public ResponseEntity<?> pushMessage(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "消息") @RequestBody MessageDTOV2 messageDTO) {
//        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
//        if (account == null) {
//            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
//        }
        MessageResultDomain result = messageService.pushMessage(messageDTO);
        if (result == null) {
            result = new MessageResultDomain();
            result.setSuccess(false);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

}
