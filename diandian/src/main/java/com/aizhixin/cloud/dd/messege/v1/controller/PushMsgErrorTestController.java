package com.aizhixin.cloud.dd.messege.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.messege.service.ErrorTestService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/web/v1")
@Api(description="处理评论回复错误消息数据")
public class PushMsgErrorTestController {
     @Autowired
     private ErrorTestService errorTestService;
     
     @GetMapping(value="/delete/msg",produces=MediaType.APPLICATION_JSON_VALUE)
     public Object chuliErrorMsg() {
    	 errorTestService.chuli();
		return new String("true");
     }
}
