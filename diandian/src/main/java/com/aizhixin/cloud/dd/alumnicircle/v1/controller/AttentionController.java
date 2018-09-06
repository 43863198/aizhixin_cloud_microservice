package com.aizhixin.cloud.dd.alumnicircle.v1.controller;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.alumnicircle.domain.AttentionDomain;
import com.aizhixin.cloud.dd.alumnicircle.entity.Attention;
import com.aizhixin.cloud.dd.alumnicircle.service.AttentionService;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(description = "关注API")
@RequestMapping("/api/phone/v1")
public class AttentionController {
	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private AttentionService attentionService;

	@RequestMapping(value = "/attention/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "关注", response = Void.class, notes = "关注<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> save(@RequestHeader("Authorization") String accessToken,
			@RequestBody AttentionDomain attentionDomain) {
		Map<String, Object> result = new HashMap<>();
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		attentionDomain.setAttentionUserId(adt.getId());
		attentionDomain.setAttentionUserName(adt.getName());
		if (null == attentionDomain.getFollowedUserId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "被关注人id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(attentionDomain.getFollowedUserName())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "被关注人名称不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		Attention a = attentionService.save(attentionDomain);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, a.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/attention/put", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "取消关注", response = Void.class, notes = "取消关注<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> put(@RequestHeader("Authorization") String accessToken,
			@RequestBody AttentionDomain attentionDomain) {
		Map<String, Object> result = new HashMap<>();
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		attentionDomain.setAttentionUserId(adt.getId());
		attentionDomain.setAttentionUserName(adt.getName());
		if (null == attentionDomain.getFollowedUserId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "被关注人id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		Attention a = attentionService.findByAttention(attentionDomain);
		if(null==a){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "没有此条关注信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		a=attentionService.update(a);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, a.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
