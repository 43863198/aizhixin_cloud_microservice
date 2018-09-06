package com.aizhixin.cloud.dd.dorms.v1.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.dorms.domain.FloorDomain;
import com.aizhixin.cloud.dd.dorms.entity.Floor;
import com.aizhixin.cloud.dd.dorms.service.FloorSerivce;
import com.aizhixin.cloud.dd.dorms.service.RoomService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1/floor")
@Api(description = "楼栋管理API")
public class FloorController {
	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private FloorSerivce floorSerivce;
	@Autowired
	private RoomService roomService;

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存楼栋信息", response = Void.class, notes = "保存楼栋信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> save(@RequestBody FloorDomain floorDomain,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (floorDomain.getFloorType() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "楼栋类型不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(floorDomain.getName())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "楼栋名称不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		Floor f = floorSerivce.findByNameAndOrgId(floorDomain.getName(), account.getOrganId());
		if (f != null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "楼栋名称不能重复");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		f = floorSerivce.save(floorDomain, account);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, f.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/put", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改楼栋信息", response = Void.class, notes = "修改楼栋信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> update(@RequestBody FloorDomain floorDomain,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (floorDomain.getId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "修改信息id不能为kong");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(floorDomain.getName())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "楼栋名称不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		Floor f = floorSerivce.findByNameAndOrgId(floorDomain.getName(), account.getOrganId(),floorDomain.getId());
		if (f != null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "楼栋名称不能重复");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		f = floorSerivce.update(floorDomain, account);
		if (null != f) {
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, f.getId());
		} else {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "修改失败，不存在楼栋信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除楼栋信息", response = Void.class, notes = "删除楼栋信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> delete(
			@ApiParam(value = "id 楼栋信息id", required = true) @RequestParam(value = "id", required = true) Long id,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (id == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "楼栋id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		Floor f = floorSerivce.findById(id);
		if (null == f) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "楼栋信息不存在");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		// if(floorSerivce.findByFloorId(id)){
		// result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
		// result.put(ApiReturnConstants.CAUSE, "楼栋已存在宿舍,请先删除宿舍");
		// return new ResponseEntity<Map<String, Object>>(result,
		// HttpStatus.EXPECTATION_FAILED);
		// }
		f = floorSerivce.deleteFloor(f);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, f.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取楼栋详情信息", response = Void.class, notes = "获取楼栋详情信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> get(
			@ApiParam(value = "id 楼栋信息id", required = true) @RequestParam(value = "id", required = true) Long id,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (id == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "楼栋id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		Floor f = floorSerivce.findById(id);
		FloorDomain fd = new FloorDomain();
		if (null != f) {
			BeanUtils.copyProperties(f, fd);
		}
		boolean isEditor = roomService.judgeByFloorId(id);
		fd.setEditor(isEditor);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, fd);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/getPage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取楼栋信息", response = Void.class, notes = "获取楼栋信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getPage(
			@ApiParam(value = "pageNumber 起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "name 宿舍楼名称", required = false) @RequestParam(value = "name", required = false) String name,
			@ApiParam(value = "pageSize 分页大小", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (pageNumber == null) {
			pageNumber = 1;
		}
		if (pageSize == null) {
			pageSize = 10;
		}
		result = floorSerivce.findByFloorInfo(pageNumber, pageSize, account.getOrganId(), result, name);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
