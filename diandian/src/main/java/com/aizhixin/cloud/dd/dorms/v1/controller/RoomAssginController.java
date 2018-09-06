package com.aizhixin.cloud.dd.dorms.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.aizhixin.cloud.dd.dorms.domain.AssginDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomAssginDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomAssginOneDomain;
import com.aizhixin.cloud.dd.dorms.service.RoomAssginService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1/roomAssgin")
@Api(description = "宿舍分配API")
public class RoomAssginController {
	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private RoomAssginService roomAssginService;

	@RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "分配学生宿舍信息", response = Void.class, notes = "分配学生宿舍信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> save(@RequestBody RoomAssginDomain roomAssginDomain,
			@RequestHeader("Authorization") String accessToken) {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (roomAssginDomain.getProfId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "专业id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(roomAssginDomain.getProfName())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "专业名称不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (roomAssginDomain.getCollegeId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学院id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(roomAssginDomain.getCollegeName())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学院名称不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (roomAssginDomain.getSexType() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "性别类型不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (roomAssginDomain.getRoomIds().isEmpty()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "宿舍id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
//		if (roomAssginService.findByRoomIds(roomAssginDomain.getRoomIds(),roomAssginDomain.getProfId())) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CAUSE, "选择的宿舍中存在已开放给该专业的宿舍");
//			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
//		}
		roomAssginService.save(roomAssginDomain, account.getId(), account.getOrganId());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	@RequestMapping(value = "/put", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
	@ApiOperation(httpMethod = "PUT", value = "批量关闭", response = Void.class, notes = "批量关闭<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> put(@RequestBody List<Long> roomIds,
			@RequestHeader("Authorization") String accessToken) {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (roomIds == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "宿舍id集合不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (roomIds.isEmpty()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "宿舍id集合不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		roomAssginService.batchRoom(roomIds);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/putOpen", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
	@ApiOperation(httpMethod = "PUT", value = "批量开放", response = Void.class, notes = "批量开放<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> putOpen(@RequestBody List<Long> roomIds,
			@RequestHeader("Authorization") String accessToken) {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (roomIds == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "宿舍id集合不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (roomIds.isEmpty()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "宿舍id集合不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		roomAssginService.batchRoomOpen(roomIds);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	@RequestMapping(value = "/getProfInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取分配专业信息", response = Void.class, notes = "获取分配专业信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getProfInfo(@RequestHeader("Authorization") String accessToken)
			throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		List<AssginDomain> adl = roomAssginService.findRoomAssginInfo(account.getOrganId());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, adl);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getRoomInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "单个房间分配信息", response = Void.class, notes = "单个房间分配信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getRoomInfo(
			@ApiParam(value="roomId 宿舍id",required=true)@RequestParam(value = "roomId", required = true) Long roomId,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		RoomAssginOneDomain rad=roomAssginService.findByRoomId(roomId);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, rad);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/saveAndUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "单个房间分配信息修改及添加", response = Void.class, notes = "单个房间分配信息修改及添加<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> saveAndUpdate(
			@RequestBody RoomAssginOneDomain roomAssginOneDomain,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		roomAssginService.updateAndSave(roomAssginOneDomain, account.getOrganId(),account.getId());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
}
