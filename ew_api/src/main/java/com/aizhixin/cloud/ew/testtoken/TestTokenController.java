package com.aizhixin.cloud.ew.testtoken;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1/testToken")
@Api(description = "测试Token")
public class TestTokenController {
	
	@Autowired
	private AuthUtilService authUtilService;
	
	@RequestMapping(value = "/testToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "测试token", response = Void.class, notes = "测试token<br><br><b>@author bly</b>")
	public ResponseEntity<Map<String, Object>> testToken(
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		Map<String, Object> resBody = new HashMap<>();
		if (account == null) {
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		resBody.put("account", account);
		return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.OK);
	}
}
