package com.aizhixin.cloud.ew.announcement.controller;

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

import com.aizhixin.cloud.ew.announcement.domain.announcementListDomain;
import com.aizhixin.cloud.ew.announcement.domain.announcementsDomain;
import com.aizhixin.cloud.ew.announcement.service.announcementListService;
import com.aizhixin.cloud.ew.common.PageData;
import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.common.core.PublicErrorCode;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.exception.ExceptionMessage;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1/announcementList")
@Api(description = "师徒制公告发布相关的所有API")
public class announcementListController {

	private announcementListService listService;
	private AuthUtilService authUtilService;

	@Autowired
	public announcementListController(AuthUtilService authUtilService, announcementListService listService) {
		this.authUtilService = authUtilService;
		this.listService = listService;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存公告信息", response = Void.class, notes = "保存公告信息<br><br><b>@author bly</b>")
	public ResponseEntity<ExceptionMessage> save(
			@ApiParam(value = "公告信息的结构体，<br/>必填项：title", required = true) @RequestBody announcementListDomain listDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null || account.getOrganId() == null) {
			ExceptionMessage e = new ExceptionMessage(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "无效用户");
			return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
		}
		return listService.save(listDomain, account);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改公告信息", response = Void.class, notes = "修改公告信息<br><br><b>@author bly</b>")
	public ResponseEntity<ExceptionMessage> update(
			@ApiParam(value = "公告信息的结构体，<br/>必填项：id、title", required = true) @RequestBody announcementListDomain listDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null || account.getOrganId() == null) {
			ExceptionMessage e = new ExceptionMessage(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "无效用户");
			return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
		}
		return listService.update(listDomain, account);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除公告信息", response = Void.class, notes = "删除公告信息<br><br><b>@author bly</b>")
	public ResponseEntity<ExceptionMessage> delete(
			@ApiParam(value = "id 公告ID", required = true) @PathVariable(value = "id") Long id,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			ExceptionMessage e = new ExceptionMessage(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "无效用户");
			return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
		}
		return listService.delete(id, account.getId());
	}

	@RequestMapping(value = "/deletes/{ids}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "批量删除公告信息", response = Void.class, notes = "批量删除公告信息<br><br><b>@author bly</b>")
	public ResponseEntity<ExceptionMessage> deletes(
			@ApiParam(value = "id 公告ID数组", required = true) @PathVariable(value = "ids") Long[] ids,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			ExceptionMessage e = new ExceptionMessage(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "无效用户");
			return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
		}
		return listService.deletes(ids, account.getId());
	}

	@RequestMapping(value = "/publish/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "发布公告信息", response = Void.class, notes = "发布公告信息<br><br><b>@author bly</b>")
	public ResponseEntity<ExceptionMessage> publish(
			@ApiParam(value = "id 公告ID", required = true) @PathVariable(value = "id") Long id,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			ExceptionMessage e = new ExceptionMessage(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "无效用户");
			return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
		}
		return listService.publish(id, account.getId());
	}

	@RequestMapping(value = "/publishs/{ids}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "批量发布公告信息", response = Void.class, notes = "批量发布公告信息<br><br><b>@author bly</b>")
	public ResponseEntity<ExceptionMessage> publishs(
			@ApiParam(value = "ids 公告ID数组", required = true) @PathVariable(value = "ids") Long[] ids,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			ExceptionMessage e = new ExceptionMessage(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "无效用户");
			return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
		}
		return listService.publishs(ids, account.getId());
	}

	@RequestMapping(value = "/unPublish/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "取消发布公告信息", response = Void.class, notes = "取消发布公告信息<br><br><b>@author bly</b>")
	public ResponseEntity<ExceptionMessage> unPublish(
			@ApiParam(value = "id 公告ID", required = true) @PathVariable(value = "id") Long id,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			ExceptionMessage e = new ExceptionMessage(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "无效用户");
			return new ResponseEntity<>(e, HttpStatus.UNAUTHORIZED);
		}
		return listService.unPublish(id, account.getId());
	}

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查看公告详细信息", response = Void.class, notes = "查看公告详细信息<br><br><b>@author bly</b>")
	public ResponseEntity<announcementListDomain> get(
			@ApiParam(value = "id 公告ID", required = true) @PathVariable(value = "id") Long id) throws ParseException {
		return new ResponseEntity<>(listService.get(id), HttpStatus.OK);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "分页查询公告列表信息", response = Void.class, notes = "分页查询公告列表信息<br><br><b>@author bly</b>")
	public ResponseEntity<PageData<announcementsDomain>> queryList(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "title 公告标题") @RequestParam(value = "title", required = false) String title,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws Exception {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null || account.getOrganId() == null) {
			return new ResponseEntity<>(new PageData<announcementsDomain>(), HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(
				listService.list(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), title, account.getOrganId()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/listByPublishStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手机端分页查询公告列表信息", response = Void.class, notes = "手机端分页查询公告列表信息<br><br><b>@author bly</b>")
	public ResponseEntity<PageData<announcementsDomain>> queryListByPublishStatus(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) throws Exception {
		AccountDTO account = authUtilService.getByTokenForOrg(token);
		if (account == null || account.getOrganId() == null) {
			return new ResponseEntity<>(new PageData<announcementsDomain>(), HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(
				listService.listByPublishStatus(PageUtil.createNoErrorPageRequest(pageNumber, pageSize),
						account.getOrganId()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/listByOrganIdAndPublishStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "分页查询已发布公告列表信息通过组织机构Id", response = Void.class, notes = "分页查询已发布公告列表信息通过组织机构Id<br><br><b>@author bly</b>")
	public ResponseEntity<PageData<announcementsDomain>> queryListByOrganIdAndPublishStatusAndType(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "type 公告类型(10顶岗实习公告,20学校最新活动)") @RequestParam(value = "type", required = false) String type,
			@ApiParam(value = "organId 组织机构ID", required = true) @RequestParam(value = "organId", required = true) Long organId)
			throws Exception {
		return new ResponseEntity<>(listService.listByOrganIdAndPublishStatusAndType(
				PageUtil.createNoErrorPageRequest(pageNumber, pageSize), organId, type), HttpStatus.OK);
	}
}
