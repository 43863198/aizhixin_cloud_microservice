package com.aizhixin.cloud.dd.orgStructure.v1.controller;

import java.util.HashMap;
import java.util.List;
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

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.orgStructure.client.SynchronizedDataService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1")
@Api(description = "手机端组织架构API手动刷新")
public class SynDataController {
	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private SynchronizedDataService  sds;
	
	@RequestMapping(value = "/org/synData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "刷新组织架构数据", response = Void.class, notes = "刷新组织架构数据<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> synData(@RequestHeader("Authorization") String accessToken){
		AccountDTO adt=	ddUserService.getUserInfoWithLogin(accessToken);
		Map<String, Object> result=new HashMap<>();
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		new Thread(){
			public void run() {
				sds.synData();
			};
		}.start();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/org/synData/orgId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "刷新一个学校组织架构数据", response = Void.class, notes = "刷新组织架构数据<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> synData(@RequestHeader("Authorization") String accessToken,
		@ApiParam(value="orgId",required=true)@RequestParam(value="orgId",required=true)Long orgId
			){
		AccountDTO adt=	ddUserService.getUserInfoWithLogin(accessToken);
		Map<String, Object> result=new HashMap<>();
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		new Thread(){
			public void run() {
				sds.refOrg(orgId);
			};
		}.start();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
}
