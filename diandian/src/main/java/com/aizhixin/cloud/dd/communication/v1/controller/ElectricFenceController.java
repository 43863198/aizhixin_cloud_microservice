package com.aizhixin.cloud.dd.communication.v1.controller;


import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;
import com.aizhixin.cloud.dd.rollcall.repository.PushOutRecodeRepository;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;

/**
 * @author Created by jianwei.wu on ${date} ${time}
 * @E-mail wujianwei@aizhixin.com
 */
@Slf4j
@RestController
@RequestMapping("/api/web/v1/electricFence")
@Api(value = "电子围栏API", description = "针对电子围栏web端在点点业务的API")
public class ElectricFenceController {
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private DDUserService userService;
    @Autowired
    private PushService pushService;
    @Autowired
    private PushMessageRepository pushMessageRepository;
    @Autowired
    private PushOutRecodeRepository pushOutRecodeRepository;

    /**
     * 电子围栏超出范围学生给班主任推送消息
     *
     * @throws DlEduException
     */

    @RequestMapping(value = "/assigned", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "电子围栏超出范围学生给班主任推送消息", response = Void.class, notes = "电子围栏超出范围学生给班主任推送消息<br>@author HUM")
    public Map <String, Object> assigned(
            @ApiParam(value = "学生id") @RequestParam(value = "userId", required = false) Long userId,
            @RequestHeader("Authorization") String accessToken) throws URISyntaxException, DlEduException {
        Map <String, Object> result = new HashMap <String, Object>();
        try {
            result.put("success", true);
        } catch (Exception e) {
            log.warn("Exception", e);
            result.put("success", false);
            result.put("errorCode", 100001);
            return result;
        }

        return result;
    }

}
