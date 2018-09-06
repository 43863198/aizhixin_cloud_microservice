package com.aizhixin.cloud.ew.prospectsreading.controller;


import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.ew.common.PageInfo;
import com.aizhixin.cloud.ew.common.RestResult;
import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;
import com.aizhixin.cloud.ew.prospectsreading.domain.ArticleCollectionDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.IdNameDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.ProspectTypeDomain;
import com.aizhixin.cloud.ew.prospectsreading.service.ArticleCollectionService;
import com.aizhixin.cloud.ew.prospectsreading.service.MajorService;
import com.aizhixin.cloud.ew.prospectsreading.service.PositionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/phone/v1/prospects/")
@Api(description = "职位专业APP相关的所有API")
public class ProspectsPhoneController {

    private AuthUtilService authUtilService;
    private PositionService positionService;
    private MajorService majorService;
    private ArticleCollectionService aticleCollectionService;
    @Autowired
    public ProspectsPhoneController(AuthUtilService authUtilService, PositionService positionService, MajorService majorService, ArticleCollectionService aticleCollectionService) {
        this.authUtilService = authUtilService;
        this.positionService = positionService;
        this.majorService = majorService;
        this.aticleCollectionService = aticleCollectionService;
    }
    @RequestMapping(value = "/positions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取所有的职位分类及职位的id、name列表", response = Void.class, notes = "获取所有的职位分类及职位的id、name列表")
    public ResponseEntity<List<ProspectTypeDomain>> positions(
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.getAllPositionAndType(), HttpStatus.OK);
    }

    @RequestMapping(value = "/majors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取所有的专业分类及专业的id、name列表", response = Void.class, notes = "获取所有的专业分类及专业的id、name列表")
    public ResponseEntity<List<ProspectTypeDomain>> majors(
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(majorService.getAllMajorAndType(), HttpStatus.OK);
    }


    @RequestMapping(value = "/fivepositions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取5条职位的id、name列表", response = Void.class, notes = "获取5条职位的id、name列表")
    public ResponseEntity<List<IdNameDomain>> fivePositions(
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(positionService.getFivePosition(), HttpStatus.OK);
    }

    @RequestMapping(value = "/fivemajors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取5条专业的id、name列表", response = Void.class, notes = "获取5条专业的id、name列表")
    public ResponseEntity<List<IdNameDomain>> fiveMajors(
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(majorService.getFiveMajor(), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/saveArticleCollection", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存文章收藏", response = Void.class, notes = "保存文章收藏")
    public ResponseEntity<RestResult> saveArticleCollection(
    		@ApiParam(value = "articleId 文章Id") @RequestParam(value = "articleId", required = true) Long articleId,
    		@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
    	AccountDTO account = authUtilService.getByToken(token);
    	if (account == null) {
    		RestResult r = new RestResult("fail", "无效用户");
           return new ResponseEntity<>(r, HttpStatus.UNAUTHORIZED);
    	}
    	return new ResponseEntity<>(aticleCollectionService.save(articleId, account.getId()), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/offSaveArticleCollection", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "取消文章收藏", response = Void.class, notes = "取消文章收藏")
    public ResponseEntity<RestResult> offSaveArticleCollection(
    		@ApiParam(value = "articleId 文章Id") @RequestParam(value = "articleId", required = true) Long articleId,
    		@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
    	AccountDTO account = authUtilService.getByToken(token);
    	if (account == null) {
    		RestResult r = new RestResult("fail", "无效用户");
           return new ResponseEntity<>(r, HttpStatus.UNAUTHORIZED);
    	}
    	return new ResponseEntity<>(aticleCollectionService.offSave(articleId, account.getId()), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页查询文章收藏", response = Void.class, notes = "分页查询文章收藏")
    public ResponseEntity<PageInfo<ArticleCollectionDomain>> queryLiveList(
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws Exception {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            return new ResponseEntity<>(new PageInfo<>(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(aticleCollectionService.list(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), account.getId()), HttpStatus.OK);
    }
}
