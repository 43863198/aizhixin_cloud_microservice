package com.aizhixin.cloud.dd.alumnicircle.v1.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.alumnicircle.domain.DotZanDomain;
import com.aizhixin.cloud.dd.alumnicircle.entity.AlumniCircle;
import com.aizhixin.cloud.dd.alumnicircle.entity.DotZan;
import com.aizhixin.cloud.dd.alumnicircle.service.AlumniCircleService;
import com.aizhixin.cloud.dd.alumnicircle.service.DotZanService;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/phone/v1")
@Api(description = "手机端点赞API")
public class DotZanController {
	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private DotZanService dotZanService;
	@Autowired
	private AlumniCircleService alumniCircleService;

	@RequestMapping(value = "/dotZan/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "校友圈点赞", response = Void.class, notes = "校友圈点赞<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> save(
			@RequestHeader("Authorization") String accessToken,
			@RequestBody DotZanDomain dotZanDomain) {
		Map<String, Object> result = new HashMap<>();
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		dotZanDomain.setUserId(adt.getId());
		dotZanDomain.setUserName(adt.getName());
		if (null == dotZanDomain.getAlumniCircleId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "校友圈不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		AlumniCircle ac=alumniCircleService.findByAlumniCircle(dotZanDomain.getAlumniCircleId());
		if(null==ac){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "校友圈信息不存在");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		DotZan dz=dotZanService.save(dotZanDomain,ac);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, dz.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/dotZan/put", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "校友圈取消点赞", response = Void.class, notes = "校友圈取消点赞<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> put(
			@RequestHeader("Authorization") String accessToken,
			@RequestBody DotZanDomain dotZanDomain) {
		Map<String, Object> result = new HashMap<>();
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		dotZanDomain.setUserId(adt.getId());
		dotZanDomain.setUserName(adt.getName());
		if (null == dotZanDomain.getAlumniCircleId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "校友圈不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		AlumniCircle ac=alumniCircleService.findByAlumniCircle(dotZanDomain.getAlumniCircleId());
		if(null==ac){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "校友圈信息不存在");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		DotZan dz=dotZanService.findDotZan(dotZanDomain);
		if(null==dz){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "此用户没有点赞信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		dz=dotZanService.deleteDotZan(dz,ac);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, dz.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
