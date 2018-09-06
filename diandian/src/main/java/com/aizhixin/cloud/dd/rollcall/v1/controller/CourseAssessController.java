package com.aizhixin.cloud.dd.rollcall.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.common.domain.UserDomain;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.CourseAssessDTO;
import com.aizhixin.cloud.dd.rollcall.dto.CourseAssessOneDTO;
import com.aizhixin.cloud.dd.rollcall.service.CourseAssessService;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1")
@Api(description = "课程评教API")
public class CourseAssessController {
	@Autowired
	private CourseAssessService courseAssessService;
	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private OrgManagerRemoteClient orgManagerRemoteService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/courseAssess/queryOneCourseAssess", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "单节课评分详情", response = Void.class, notes = "单节课评分详情<br>@author bly")
	public ResponseEntity<?> queryOneCourseAssess(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "排课id") @RequestParam(value = "scheduleId", required = true) Long scheduleId,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		PageData<CourseAssessOneDTO> pageData = courseAssessService.queryOneCourseAssess(pageNumber, pageSize,
				scheduleId, account);
		return new ResponseEntity<>(pageData, HttpStatus.OK);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/courseAssess/queryCourseAssessDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "课程评分详情", response = Void.class, notes = "课程评分详情<br>@author bly")
	public ResponseEntity<?> queryCourseAssessDetails(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "教学班id") @RequestParam(value = "teachingClassId", required = true) Long teachingClassId,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		PageData pageData = courseAssessService.queryCourseAssessDetails(pageNumber, pageSize, teachingClassId,
				account);
		return new ResponseEntity<>(pageData, HttpStatus.OK);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/courseAssess/queryCourseAssess", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "课程评分", response = Void.class, notes = "课程评分<br>@author bly")
	public ResponseEntity<?> queryCourseAssess(
			@ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = false) Long semesterId,
			@ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
			@ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		if (account.getOrganId() == null) {
			Map<String, Object> result = new HashMap<>();
			result.put("error", "No organId");
			return new ResponseEntity<Object>(result, HttpStatus.OK);
		}
		PageData<CourseAssessDTO> pageInfo = courseAssessService.queryCourseAssess(account.getOrganId(), semesterId, courseName, teacherName,
				pageNumber, pageSize, account);
		return new ResponseEntity<PageData>(pageInfo, HttpStatus.OK);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/courseAssess/queryCourseAssessList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "课程评分", response = Void.class, notes = "课程评分<br>@author bly")
	public ResponseEntity<?> queryCourseAssessList(
			@ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
			@ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = false) Long semesterId,
			@ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
			@ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "Authorization") @RequestParam(value = "accessToken", required = true) String accessToken
			) throws DlEduException {
		List<Long> teacherIds = null;
		List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
		if(userRoles.size()>0 && userRoles.contains("ROLE_COLLEGE_ADMIN")){
			UserDomain userInfo = orgManagerRemoteService.getUser(managerId);
			if(null != userInfo){
				Long collegeId = userInfo.getCollegeId();
				teacherIds = orgManagerRemoteService.getTeacherIds(collegeId);
			}
		}
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		if (account.getOrganId() == null) {
			Map<String, Object> result = new HashMap<>();
			result.put("error", "No organId");
			return new ResponseEntity<Object>(result, HttpStatus.OK);
		}
		PageData<CourseAssessDTO> pageInfo = courseAssessService.queryCourseAssessList(account.getOrganId(),teacherIds, semesterId, courseName, teacherName,
				pageNumber, pageSize, account);
		return new ResponseEntity<PageData>(pageInfo, HttpStatus.OK);
	}
}
