package com.aizhixin.cloud.io.v1.controller;


import com.aizhixin.cloud.io.service.PolyvVedioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/polyv")
@Api("保利威视相关API")
public class PolyvVedioController {

    private PolyvVedioService polyvVedioService;
    @Autowired
    public PolyvVedioController (PolyvVedioService polyvVedioService) {
        this.polyvVedioService = polyvVedioService;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据保利威视上传视频完成以后返回的VID查询视频的详细信息", response = Void.class, notes = "根据保利威视上传视频完成以后返回的VID查询视频的详细信息<br>@author panzhen@aizhixin.com")
    public ResponseEntity<String> upload(@ApiParam(value = "vid 保利威视上传视频完成以后返回的VID", required = true) @RequestParam(value = "vid") String vid,
                                         @ApiParam(value = "appId 应用ID", required = true) @RequestParam(value = "appId") String appId,
                                         @ApiParam(value = "token 应用token", required = true) @RequestParam(value = "token") String token) {
        return polyvVedioService.getPolyvVideoMsgByVid(vid, appId, token);
    }
}
