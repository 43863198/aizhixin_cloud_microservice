package com.aizhixin.cloud.ew.prospectsreading.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.aizhixin.cloud.ew.prospectsreading.domain.MajorDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.MajorQueryListDomain;
import com.aizhixin.cloud.ew.prospectsreading.service.MajorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1/prospects/major")
@Api(description = "专业相关的所有API")
public class MajorController {

	private AuthUtilService authUtilService;
	private MajorService majorService;

	@Autowired
	public MajorController(AuthUtilService authUtilService, MajorService majorService) {
		this.authUtilService = authUtilService;
		this.majorService = majorService;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存专业信息", response = Void.class, notes = "保存专业信息")
	public ResponseEntity<RestResult> save(
			@ApiParam(value = "专业信息的结构体，<br/>必填项：name、type、desc", required = true) @RequestBody MajorDomain majorDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token)
			throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			RestResult r = new RestResult("fail", "无效用户");
			return new ResponseEntity<>(r, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(majorService.save(majorDomain, account.getId()), HttpStatus.OK);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改专业信息", response = Void.class, notes = "修改专业信息")
	public ResponseEntity<RestResult> update(
			@ApiParam(value = "专业信息的结构体，<br/>必填项：id、name、type、desc", required = true) @RequestBody MajorDomain majorDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token)
			throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			RestResult r = new RestResult("fail", "无效用户");
			return new ResponseEntity<>(r, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(majorService.upadate(majorDomain, account.getId()), HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除专业信息", response = Void.class, notes = "删除专业信息")
	public ResponseEntity<RestResult> update(
			@ApiParam(value = "id 专业ID", required = true) @PathVariable(value = "id") Long id,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token)
			throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			RestResult r = new RestResult("fail", "无效用户");
			return new ResponseEntity<>(r, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(majorService.delete(id, account.getId()), HttpStatus.OK);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "分页查询专业的列表信息", response = Void.class, notes = "分页查询专业的列表信息")
	public ResponseEntity<PageInfo<MajorQueryListDomain>> queryLiveList(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "专业名称") @RequestParam(value = "name", required = false) String name,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token)
			throws Exception {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			return new ResponseEntity<>(new PageInfo<>(), HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(majorService.list(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), name),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查看专业详细信息", response = Void.class, notes = "查看专业详细信息")
	public ResponseEntity<MajorDomain> get(
			@ApiParam(value = "id 职位ID", required = true) @PathVariable(value = "id") Long id,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token)
			throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			return new ResponseEntity<>(new MajorDomain(), HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(majorService.get(id), HttpStatus.OK);
	}

	@RequestMapping(value = "/publish/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "发布专业信息", response = Void.class, notes = "发布专业信息")
	public ResponseEntity<RestResult> publish(
			@ApiParam(value = "id 专业ID", required = true) @PathVariable(value = "id") Long id,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token)
			throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			return new ResponseEntity<>(new RestResult("fail", "无效用户"), HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(majorService.publish(id, account.getId()), HttpStatus.OK);
	}

	@RequestMapping(value = "/unpublish/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "取消发布专业信息", response = Void.class, notes = "取消发布专业信息")
	public ResponseEntity<RestResult> unPublish(
			@ApiParam(value = "id 专业ID", required = true) @PathVariable(value = "id") Long id,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token)
			throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			return new ResponseEntity<>(new RestResult("fail", "无效用户"), HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(majorService.unPublish(id, account.getId()), HttpStatus.OK);
	}
}
