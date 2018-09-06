package com.aizhixin.cloud.ew.lostAndFound.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;
import com.aizhixin.cloud.ew.lostAndFound.domain.LFDomain;
import com.aizhixin.cloud.ew.lostAndFound.service.LostAndFoundManagementService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Rigel.ma 2017-05-02
 *
 */
@RestController
@RequestMapping("/api/web/v1/lostAndFound/LFShow")
@Api(description = "APP失物招领相关的所有API")
public class LFController {

	@Autowired
	private LostAndFoundManagementService lostAndFoundManagementService;

	@Autowired
	private AuthUtilService authUtilService;

	/**
	 * 新增失物招领
	 * 
	 */
	@RequestMapping(value = "/addLostAndFound", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新增失物招领", response = Void.class, notes = "新增失物招领<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> addLostAndFound(
			@ApiParam(value = "新增失物招领必填项：<br/>address、contactNumber、contactWay、content、infoType、typeId", required = true) @RequestBody LFDomain lfDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByTokenForLF(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(lostAndFoundManagementService.addLF(lfDomain, account),
				HttpStatus.OK);
	}

	/**
	 * 失物招领列表
	 * 
	 */
	@RequestMapping(value = "/getList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "失物招领列表(按学校显示)", response = Void.class, notes = "按学校显示的列表<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> getList(
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null || account.getOrganId() == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		String sort = "desc";
		String proper = "id";
		if (account.getOrganName().equals("知新大学")) {
			return new ResponseEntity<Map<String, Object>>(
					lostAndFoundManagementService.getLists(result,
							PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, sort, proper), account),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<Map<String, Object>>(
					lostAndFoundManagementService.getList(result,
							PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, sort, proper), account),
					HttpStatus.OK);
		}
	}

	/**
	 * 失物招领（我的发布）
	 * 
	 */
	@RequestMapping(value = "/getMyList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "失物招领列表(我的发布)", response = Void.class, notes = "我发布的失物招领<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> getMyList(
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByTokenForLF(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(lostAndFoundManagementService.getMyList(account), HttpStatus.OK);
	}

	/**
	 * 失物招领列表
	 * 
	 */
	@RequestMapping(value = "/getLists", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "失物招领列表（所有）", response = Void.class, notes = "失物招领列表（所有）<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> getLists(
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		String sort = "desc";
		String proper = "id";
		return new ResponseEntity<Map<String, Object>>(
				lostAndFoundManagementService.getLists(result,
						PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, sort, proper), account),
				HttpStatus.OK);
	}

	/**
	 * 失物类型列表
	 * 
	 */
	@RequestMapping(value = "/typeList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "失物类型列表", response = Void.class, notes = "失物类型列表<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> typeList() {
		return new ResponseEntity<Map<String, Object>>(lostAndFoundManagementService.typeList(), HttpStatus.OK);
	}

	/**
	 * 表扬或心痛
	 * 
	 */
	@RequestMapping(value = "/ addPraise", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "增加表扬或心痛", response = Void.class, notes = "增加表扬或心痛<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> addPraise(
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "lostAndFoundId 失物招领ID", required = true) @RequestParam Long lostAndFoundId) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(lostAndFoundManagementService.addPraise(account, lostAndFoundId),
				HttpStatus.OK);
	}

	/**
	 * 取消表扬或心痛
	 * 
	 */
	@RequestMapping(value = "/ cancelPraise", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "取消表扬或心痛", response = Void.class, notes = "取消表扬或心痛<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> cancelPraise(
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "lostAndFoundId 失物招领ID", required = true) @RequestParam Long lostAndFoundId) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(
				lostAndFoundManagementService.cancelPraise(account, lostAndFoundId), HttpStatus.OK);
	}

	/**
	 * 删除失物招领
	 * 
	 */
	@RequestMapping(value = "/deleteLF", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除失物招领", response = Void.class, notes = "删除失物招领<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> deleteLF(
			@ApiParam(value = "lostAndFoundId 失物招领ID", required = true) @RequestParam Long lostAndFoundId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(lostAndFoundManagementService.deleteLF(lostAndFoundId, account),
				HttpStatus.OK);

	}

	/**
	 * 结束失物招领
	 * 
	 */
	@RequestMapping(value = "/finishLF", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "结束失物招领", response = Void.class, notes = "结束失物招领<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> finishLF(
			@ApiParam(value = "lostAndFoundId 失物招领ID", required = true) @RequestParam Long lostAndFoundId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(lostAndFoundManagementService.finishLF(lostAndFoundId, account),
				HttpStatus.OK);
	}

}
