
package com.aizhixin.cloud.school.schoolinfo.v1.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.school.common.PageDomain;
import com.aizhixin.cloud.school.common.core.ApiReturnConstants;
import com.aizhixin.cloud.school.common.core.PageUtil;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentTeacherDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentTeacher;
import com.aizhixin.cloud.school.schoolinfo.service.SchoolExcellentTeacherService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @ClassName: SchoolExcellentTeacherController
 * @Description:
 * @author xiagen
 * @date 2017年5月15日 下午1:24:27
 * 
 */
@RestController
@Api(description = "优秀教师管理")
@RequestMapping("/v1/schoolexcellentteacher")
public class SchoolExcellentTeacherController {
	@Autowired
	private SchoolExcellentTeacherService schoolExcellentTeacherService;

	@RequestMapping(value = "/saveteacher", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存优秀教师信息", response = Void.class, notes = "保存优秀教师信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> saveTeacher(
			@ApiParam(value = "<br><b>orgId:学校id必填</b><br><b>userId:操作者id必填</b><br><b>teacherId:教师id必填</b><br><b>introduction:教师介绍,必填</b><br><b>inUrl:教师介绍图片地址</b><br>") @RequestBody SchoolExcellentTeacherDomain schoolExcellentTeacherDomain) {
		Map<String, Object> result = new HashMap<>();
		if (schoolExcellentTeacherDomain.getUserId() == 0 || schoolExcellentTeacherDomain.getUserId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "当前用户id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolExcellentTeacherDomain.getOrgId() == 0 || schoolExcellentTeacherDomain.getOrgId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolExcellentTeacherDomain.getTeacherId() == 0 || schoolExcellentTeacherDomain.getTeacherId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "教师id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolExcellentTeacherDomain.getIntroduction() == ""
				|| "".equals(schoolExcellentTeacherDomain.getIntroduction())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "教师介绍不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolExcellentTeacher schoolExcellentTeacher=	schoolExcellentTeacherService.findByTeacherId(schoolExcellentTeacherDomain.getTeacherId());
		if(schoolExcellentTeacher!=null){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "优秀教师已存在");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		
		schoolExcellentTeacher = schoolExcellentTeacherService
				.saveSchoolExcellentTeacher(schoolExcellentTeacherDomain);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolExcellentTeacher.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateteacher", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改优秀教师信息", response = Void.class, notes = "修改优秀教师信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> updateTeacher(
			@ApiParam(value = "<b>userId:操作者id必填</b><br><b>introduction:教师介绍,必填</b><br><b>inUrl:教师介绍图片地址</b><br>") @RequestBody SchoolExcellentTeacherDomain schoolExcellentTeacherDomain) {
		Map<String, Object> result = new HashMap<>();
		if (schoolExcellentTeacherDomain.getId() == 0 || schoolExcellentTeacherDomain.getId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "优秀教师id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolExcellentTeacherDomain.getUserId() == 0 || schoolExcellentTeacherDomain.getUserId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "当前用户id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolExcellentTeacherDomain.getIntroduction() == ""
				|| "".equals(schoolExcellentTeacherDomain.getIntroduction())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "教师介绍不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		
		SchoolExcellentTeacher schoolExcellentTeacher = schoolExcellentTeacherService
				.findById(schoolExcellentTeacherDomain.getId());
		if (schoolExcellentTeacher == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "此id没有优秀教师信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		schoolExcellentTeacher = schoolExcellentTeacherService
				.updateSchoolExcellentTeacher(schoolExcellentTeacherDomain, schoolExcellentTeacher);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolExcellentTeacher.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteteacher/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除优秀教师信息", response = Void.class, notes = "删除优秀教师信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> deleteTeacher(@PathVariable Long id) {
		Map<String, Object> result = new HashMap<>();
		if (id == 0 || id == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "优秀教师id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolExcellentTeacher schoolExcellentTeacher = schoolExcellentTeacherService.findById(id);
		if (schoolExcellentTeacher == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "此id没有优秀教师信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		schoolExcellentTeacher = schoolExcellentTeacherService.deleteSchoolExcellentTeacher(schoolExcellentTeacher);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolExcellentTeacher.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/findteacherinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查询优秀教师信息", response = Void.class, notes = "查询优秀教师信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> findTeacherInfo(
			@ApiParam(value = "orgId:学校id必填", required = true) @RequestParam(value = "orgId", defaultValue = "0") Long orgId,
			@ApiParam(value = "pageNumber:起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize:每页数量", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "teacherName:教师名称", required = false) @RequestParam(value = "teacherName", required = false) String teacherName) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> result = new HashMap<>();
		if (orgId == null || orgId == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (null == pageNumber || pageNumber < 0) {
			pageNumber = 0;
		}
		if (null == pageSize || pageSize < 0) {
			pageSize = Integer.MAX_VALUE;
		}
		if(StringUtils.isEmpty(teacherName)){
			PageDomain pageDomain = new PageDomain();
			Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
			pageDomain.setPageNumber(page.getPageNumber());
			pageDomain.setPageSize(page.getPageSize());
			List<SchoolExcellentTeacherDomain> schoolExcellentTeacherDomainList = schoolExcellentTeacherService
					.findBySchoolId(page, orgId, pageDomain);
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, schoolExcellentTeacherDomainList);
			result.put(ApiReturnConstants.PAGE, pageDomain);
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		}else{
			System.out.println(321);
			PageDomain pageDomain = new PageDomain();
			Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
			pageDomain.setPageNumber(page.getPageNumber());
			pageDomain.setPageSize(page.getPageSize());
			List<SchoolExcellentTeacherDomain> schoolExcellentTeacherDomainList = schoolExcellentTeacherService.findBySchoolIdAndTeacherName(page, orgId, pageDomain, teacherName);
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, schoolExcellentTeacherDomainList);
			result.put(ApiReturnConstants.PAGE, pageDomain);
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		}

	}	
	@RequestMapping(value = "/findid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据id优秀教师信息", response = Void.class, notes = "根据id优秀教师信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> findInfoById(
			@ApiParam(value = "id:优秀教师id必填", required = true) @RequestParam(value = "id", defaultValue = "0",required=true) Long id) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> result = new HashMap<>();
		if (id == 0 || id == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "优秀教师id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolExcellentTeacherDomain schoolExcellentTeacherDomain=schoolExcellentTeacherService.findByid(id);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolExcellentTeacherDomain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

	}
}
