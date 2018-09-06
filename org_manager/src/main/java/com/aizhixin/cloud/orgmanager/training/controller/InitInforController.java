package com.aizhixin.cloud.orgmanager.training.controller;

import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.training.entity.Enterprise;
import com.aizhixin.cloud.orgmanager.training.service.EnterpriseService;
import com.aizhixin.cloud.orgmanager.training.service.TrainingGroupService;
import com.aizhixin.cloud.orgmanager.training.service.TrainingManageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/v1/groupinit")
@Api(value = "实践信息初始化API", description = "实践信息初始化")
public class InitInforController {
    @Autowired
    private TrainingGroupService trainingGroupService;
    @Autowired
    private TrainingManageService trainingManageService;
    
    @RequestMapping(value = "/stumsg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "初始化学生端实践首页信息", response = Void.class, notes = "初始化学生端实践首页信息<br><br><b>@author 郑宁</b>")
    public ResponseEntity<String> initStuMsg(
            @ApiParam(value = "orgId 学校id", required = false) @RequestParam(value = "orgId", required = false) Long orgId) {
    	trainingGroupService.initMsg(orgId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/callgroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "初始化实践计划对应的辅导员点名", response = Void.class, notes = "初始化实践计划对应的辅导员点名<br><br><b>@author 郑宁</b>")
    public ResponseEntity<String> initCallGroup(
            @ApiParam(value = "groupId 实践计划id", required = true) @RequestParam(value = "groupId", required = true) Long groupId) {
    	trainingManageService.initCallGroupByGroupId(groupId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }



}
