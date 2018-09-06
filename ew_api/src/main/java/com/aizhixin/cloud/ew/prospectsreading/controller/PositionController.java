package com.aizhixin.cloud.ew.prospectsreading.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aizhixin.cloud.ew.common.PageInfo;
import com.aizhixin.cloud.ew.common.RestResult;
import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;
import com.aizhixin.cloud.ew.prospectsreading.domain.PositionAbilityListDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.PositionDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.PositionQueryListDomain;
import com.aizhixin.cloud.ew.prospectsreading.service.PositionService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/web/v1/prospects/position")
@Api(description = "职位相关的所有API")
public class PositionController {

    private AuthUtilService authUtilService;
    private PositionService positionService;
    @Autowired
    public PositionController(AuthUtilService authUtilService, PositionService positionService) {
        this.authUtilService = authUtilService;
        this.positionService = positionService;
    }
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存职位信息", response = Void.class, notes = "保存职位信息")
    public ResponseEntity<RestResult> save(
            @ApiParam(value = "职位信息的结构体，<br/>必填项：name、type、desc", required = true) @RequestBody PositionDomain positionDomain,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            RestResult r = new RestResult("fail", "无效用户");
            return new ResponseEntity<>(r, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.save(positionDomain, account.getId()), HttpStatus.OK);
    }
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改职位信息", response = Void.class, notes = "修改职位信息")
    public ResponseEntity<RestResult> update(
            @ApiParam(value = "职位信息的结构体，<br/>必填项：id、name、type、desc", required = true) @RequestBody PositionDomain positionDomain,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            RestResult r = new RestResult("fail", "无效用户");
            return new ResponseEntity<>(r, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.upadate(positionDomain, account.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除职位信息", response = Void.class, notes = "删除职位信息")
    public ResponseEntity<RestResult> update(
            @ApiParam(value = "id 职位ID", required = true) @PathVariable(value = "id") Long id,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            RestResult  r = new RestResult("fail", "无效用户");
            return new ResponseEntity<>(r, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.delete(id, account.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页查询职位的列表信息", response = Void.class, notes = "分页查询职位的列表信息")
    public ResponseEntity<PageInfo<PositionQueryListDomain>> queryLiveList(
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "职位名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws Exception {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(new PageInfo<>(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.list(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), name), HttpStatus.OK);
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看职位详细信息", response = Void.class, notes = "查看职位详细信息")
    public ResponseEntity<PositionDomain> get(
            @ApiParam(value = "id 职位ID", required = true) @PathVariable(value = "id") Long id,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(new PositionDomain(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.get(id), HttpStatus.OK);
    }
    @RequestMapping(value = "/professionalquality", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看历史的职素能力名称列表", response = Void.class, notes = "查看历史的职素能力名称列表")
    public ResponseEntity<List<PositionAbilityListDomain>> getProfessionalQualitys(
            @ApiParam(value = "id 职位ID") @RequestParam(value = "id", required = false) Long id,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.findProfessionalQualitys(id), HttpStatus.OK);
    }
    @RequestMapping(value = "/technicalability", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看历史的技术能力名称列表", response = Void.class, notes = "查看历史的技术能力名称列表")
    public ResponseEntity<List<PositionAbilityListDomain>> getTechnicalability(
            @ApiParam(value = "id 职位ID") @RequestParam(value = "id", required = false) Long id,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.findTechnicalAbilityss(id), HttpStatus.OK);
    }
    @RequestMapping(value = "/knowledges", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看历史的知识能力名称列表", response = Void.class, notes = "查看历史的知识能力名称列表")
    public ResponseEntity<List<PositionAbilityListDomain>> getKnowledges(
            @ApiParam(value = "id 职位ID") @RequestParam(value = "id", required = false) Long id,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.findKnowledges(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/publish/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "发布职位信息", response = Void.class, notes = "发布职位信息")
    public ResponseEntity<RestResult> publish(
            @ApiParam(value = "id 职位ID", required = true) @PathVariable(value = "id") Long id,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(new RestResult("fail", "无效用户"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.publish(id, account.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/unpublish/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "取消发布职位信息", response = Void.class, notes = "取消发布职位信息")
    public ResponseEntity<RestResult> unPublish(
            @ApiParam(value = "id 职位ID", required = true) @PathVariable(value = "id") Long id,
            @ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(new RestResult("fail", "无效用户"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.unPublish(id, account.getId()), HttpStatus.OK);
    }

	@RequestMapping(value = "/name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据职位名称查看职位详细信息", response = Void.class, notes = "根据职位名称查看职位详细信息，如果该名称不存在，返回http code 404")
	public ResponseEntity<PositionDomain> getKnowledges(
			@ApiParam(value = "name 职位名称", required = true) @RequestParam(value = "name", required = true) String name,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.getByName(name), HttpStatus.NOT_FOUND);
    }
}
