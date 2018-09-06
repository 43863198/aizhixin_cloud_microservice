package com.aizhixin.cloud.dd.rollcall.v1.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;

@RestController
@RequestMapping("/v1/student")
@Api(description = "学生API")
public class StudentController {

	/**
	 * 
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "学生信息", response = Void.class, notes = "学生信息<br><br><b>@author meihua.li</b>")
	public ResponseEntity<Map<String, Object>> list(
			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId) {
		Map<String, Object> result = new HashMap<>();

		result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
