package com.aizhixin.cloud.dd.messege.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.messege.domain.MsgModuleDomain;
import com.aizhixin.cloud.dd.messege.entity.MsgModule;
import com.aizhixin.cloud.dd.messege.service.MsgService;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/api/web/v1")
@Api(description = "消息模块管理")
public class PushMsgWebController {

	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private MsgService msgService;

	@RequestMapping(value = "/msg/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "保存模块信息", httpMethod = "POST", response = Void.class)
	public ResponseEntity<Map<String, Object>> save(
			@RequestBody MsgModuleDomain mmd) {
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isEmpty(mmd.getJumpType())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "跳转类型不能为空");
			return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(mmd.getModule())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "模块不能为空");
			return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(mmd.getModuleName())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "模块名称不能为空");
			return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
		}
		MsgModule mm = msgService.save(mmd);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, mm.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/msg/put", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "修改模块信息", httpMethod = "PUT", response = Void.class)
	public ResponseEntity<Map<String, Object>> put(
			@RequestBody MsgModuleDomain mmd) {
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isEmpty(mmd.getJumpType())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "跳转类型不能为空");
			return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(mmd.getModule())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "模块不能为空");
			return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(mmd.getModuleName())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "模块名称不能为空");
			return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
		}
		MsgModule mm = msgService.put(mmd);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, mm.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/msg/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "获取模块信息列表", httpMethod = "GET", response = Void.class)
	public ResponseEntity<Map<String, Object>> put(
			@ApiParam(value = "pageNumber 起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 分页大小", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> result = new HashMap<>();
		if (null == pageNumber || 0 == pageNumber) {
			pageNumber = 1;
		}
		if (null == pageSize) {
			pageSize = 10;
		}
		result = msgService.getListMsgModuleInfo(pageSize, pageNumber, result);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/msg/del", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "批量删除模块信息", httpMethod = "DELETE", response = Void.class, notes = "批量删除模块信息")
	public ResponseEntity<Map<String, Object>> deleteMsg(
		@ApiParam(value="ids 批量集合",required=true)	@RequestParam(value="ids",required=true) List<Long> ids) {
		Map<String, Object> result = new HashMap<>();
		if (null == ids || 0 == ids.size()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "模块id不能为空");
			return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
		}
		msgService.deleteByMsg(ids);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/msg/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "获取单个模块信息", httpMethod = "GET", response = Void.class)
	public ResponseEntity<Map<String, Object>> put(
			@ApiParam(value = "id 模块id") @RequestParam(value = "id") Long id) {
		Map<String, Object> result = new HashMap<>();
		if (null == id) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "模块id不能为空");
			return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
		}
		MsgModuleDomain mmd = msgService.get(id);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, mmd);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
