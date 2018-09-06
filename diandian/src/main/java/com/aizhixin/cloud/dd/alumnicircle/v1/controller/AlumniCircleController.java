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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.alumnicircle.domain.AlumniCircleDomain;
import com.aizhixin.cloud.dd.alumnicircle.domain.AlumniCircleDomainOne;
import com.aizhixin.cloud.dd.alumnicircle.entity.AlumniCircle;
import com.aizhixin.cloud.dd.alumnicircle.service.AlumniCircleService;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/phone/v1")
@Api(description = "手机端校友圈API")
public class AlumniCircleController {
	@Autowired
	private AlumniCircleService alumniCircleService;
	@Autowired
	private DDUserService ddUserService;

	@RequestMapping(value = "/alumniCircle/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "发布校友圈信息", response = Void.class, notes = "发布校友圈信息  <br>@author xiagen")
	public ResponseEntity<Map<String, Object>> save(@RequestHeader("Authorization") String accessToken,
			@RequestBody AlumniCircleDomain alumniCircleDomain) {
		Map<String, Object> result = new HashMap<>();
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		if (StringUtils.isEmpty(alumniCircleDomain.getContent())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "校友圈不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		AlumniCircle ac = alumniCircleService.save(alumniCircleDomain,adt);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, ac.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/alumniCircle/getBx", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查询本校校友圈", response = Void.class, notes = "查询本校校友圈<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getBx(@RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> result = new HashMap<>();
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		if(null==pageNumber){
			pageNumber=1;
		}
		if(null==pageSize){
			pageSize=10;
		}
		result=alumniCircleService.findByAlumniCircleInfo(pageNumber, pageSize, adt.getOrganId(), adt.getId(), result);
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/alumniCircle/getInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查询校友圈详情", response = Void.class, notes = "查询校友圈详情<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getInfo(@RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "alumniCircleId 校友圈信息id") @RequestParam(value = "alumniCircleId", required = true) Long alumniCircleId) {
		Map<String, Object> result = new HashMap<>();
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		if(null==alumniCircleId){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "校友圈id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		AlumniCircleDomainOne acdo=alumniCircleService.findByAlumniCircleInfoOne(alumniCircleId, adt.getId());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, acdo);
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/alumniCircle/getQg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "全国校友圈", response = Void.class, notes = "全国校友圈<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getQg(@RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> result = new HashMap<>();
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		if(null==pageNumber){
			pageNumber=1;
		}
		if(null==pageSize){
			pageSize=10;
		}
		result=alumniCircleService.findAlumniCircleQG(adt.getId(), pageNumber, pageSize, result);
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/alumniCircle/getGz", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "关注--校友圈", response = Void.class, notes = "关注--校友圈<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getGz(@RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> result = new HashMap<>();
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		if(null==pageNumber){
			pageNumber=1;
		}
		if(null==pageSize){
			pageSize=10;
		}
		result=alumniCircleService.findAttentionAlumniCircle(adt.getId(), pageNumber, pageSize, result);
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}
	
}
