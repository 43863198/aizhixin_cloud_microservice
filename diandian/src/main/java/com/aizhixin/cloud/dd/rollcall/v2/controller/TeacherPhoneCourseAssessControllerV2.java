package com.aizhixin.cloud.dd.rollcall.v2.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.TeacherPhoneCourseAssessDetailsDTOV2;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.serviceV2.CourseAssessServiceV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/phone/v2")
@Api(value = "手机教师端课程评分详情查询", description = "针对手机教师端的课程评分详情查询相关API")
public class TeacherPhoneCourseAssessControllerV2 {
	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private CourseAssessServiceV2 courseAssessServiceV2;

	@RequestMapping(value = "/courseAssess/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "教师端获取课程详情信息", response = Void.class, notes = "教师端获取课程详情信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> findAssessAndRevert(@RequestHeader("Authorization") String accessToken,
			@ApiParam(value = "teachingClassId 教学班id") @RequestParam(value = "teachingClassId", required = true) Long teachingClassId) {
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		Map<String, Object> result = new HashMap<>();
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		TeacherPhoneCourseAssessDetailsDTOV2 teacherPhoneCourseAssessDetailsDTOV2 = courseAssessServiceV2
				.findByTeachingClassId(teachingClassId);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, teacherPhoneCourseAssessDetailsDTOV2);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	@RequestMapping(value = "/teacher/AssessAndRevert/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "教师端获取课程评论及回复信息", response = Void.class, notes = "教师端获取课程详情信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> findAssessAndRevert(@RequestHeader("Authorization") String accessToken,
			@ApiParam(value = "teachingClassId 教学班id",required=true) @RequestParam(value = "teachingClassId", required = true) Long teachingClassId,
			@ApiParam(value = "sortType 排序类型，1：按时间倒序，2：按热度倒序",required=true) @RequestParam(value = "sortType", required = true) Integer sortType,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
		Map<String, Object> result = new HashMap<>();
		if (null == adt) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "无权限");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
		}
		if (null == pageNumber || 1 <pageNumber) {
			pageNumber = 1;
		}
		if (null == pageSize || 0 == pageSize) {
			pageSize = 10;
		}
		result = courseAssessServiceV2.findTeacherAssessAndRevert(teachingClassId, sortType, pageNumber, pageSize,
				result);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
