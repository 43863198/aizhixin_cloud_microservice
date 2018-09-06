
package com.aizhixin.cloud.school.schoolinfo.v1.controller;

import com.aizhixin.cloud.school.common.PageDomain;
import com.aizhixin.cloud.school.common.core.ApiReturnConstants;
import com.aizhixin.cloud.school.common.core.PageUtil;
import com.aizhixin.cloud.school.common.core.PublicErrorCode;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentCourseDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentCourse;
import com.aizhixin.cloud.school.schoolinfo.service.SchoolExcellentCourseService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SchoolExcellentCourseController
 * @Description:
 * @author xiagen
 * @date 2017年5月16日 下午5:45:00
 * 
 */

@RestController
@Api(description = "精品课程管理")
@RequestMapping("/v1/schoolcourse")
public class SchoolExcellentCourseController {

	final static private Logger LOG = LoggerFactory.getLogger(SchoolExcellentCourseController.class);
	@Autowired
	private SchoolExcellentCourseService schoolExcellentCourseService;

	@RequestMapping(value = "/savecourse", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存课程信息", response = Void.class, notes = "保存课程信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> saveCourse(
			@ApiParam(value = "<b>id:精品课程id,必填</b><br><b>orgId:学校id必填</b><br><b>userId:用户id必填</b><br>") @RequestBody SchoolExcellentCourseDomain schoolExcellentCourseDomain) {
		Map<String, Object> result = new HashMap<>();
		if (schoolExcellentCourseDomain.getCourseId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "精品课程id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolExcellentCourseDomain.getOrgId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolExcellentCourseDomain.getUserId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "当前用户id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolExcellentCourse schoolExcellentCourse=schoolExcellentCourseService.findByCourseIdAndDeleteFlag(schoolExcellentCourseDomain.getCourseId(),schoolExcellentCourseDomain.getOrgId());
		if(null!=schoolExcellentCourse){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "精品课程已存在");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		 schoolExcellentCourse = schoolExcellentCourseService
				.saveSchoolExcellentCourse(schoolExcellentCourseDomain);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolExcellentCourse.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/updatecourse", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改精品课程信息", response = Void.class, notes = "修改精品课程信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> updateCourse(
			@ApiParam(value = "<b>id:精品课程id,必填</b><br><b>courseId:课程Id,必填</b><br><b>userId:用户Id,必填</b><br>") @RequestBody SchoolExcellentCourseDomain schoolExcellentCourseDomain) {
		Map<String, Object> result = new HashMap<>();
		if (schoolExcellentCourseDomain.getCourseId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "课程id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolExcellentCourseDomain.getId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "精品课程id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolExcellentCourseDomain.getUserId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "当前用户id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolExcellentCourse schoolExcellentCourse = schoolExcellentCourseService
				.findById(schoolExcellentCourseDomain.getId());
		if (schoolExcellentCourse == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "没有此id精品课程信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		schoolExcellentCourse = schoolExcellentCourseService.updateSchoolExcellentCourse(schoolExcellentCourseDomain,
				schoolExcellentCourse);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolExcellentCourse.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/deletecourse/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除精品课程信息", response = Void.class, notes = "删除精品课程信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> deleteCourse(@ApiParam(value = "id:精品课程id") @PathVariable Long id) {
		Map<String, Object> result = new HashMap<>();
		if (id == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "精品课程id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolExcellentCourse schoolExcellentCourse = schoolExcellentCourseService.findById(id);
		if (schoolExcellentCourse == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "没有此id精品课程信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			schoolExcellentCourse = schoolExcellentCourseService.deleteSchoolExcellentCourse(schoolExcellentCourse);
		} catch (Exception e) {
			// TODO: handle exception
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, PublicErrorCode.DELETE_EXCEPTION.getIntValue());
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}

		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolExcellentCourse.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/findcourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查询精品课程信息", response = Void.class, notes = "查询精品课程信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> findCourse(
			@ApiParam(value = "orgId:学校id必填", required = true) @RequestParam(value = "orgId", defaultValue = "0") Long orgId,
			@ApiParam(value = "pageNumber:起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize:每页数量", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "courseName:课程名称", required = false) @RequestParam(value = "courseName", required = false) String courseName)
			throws JsonParseException, JsonMappingException, IOException {
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
		try {
		if (courseName == null || "".equals(courseName)) {
			PageDomain pageDomain = new PageDomain();
			Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
			pageDomain.setPageNumber(page.getPageNumber());
			pageDomain.setPageSize(page.getPageSize());
			List<SchoolExcellentCourseDomain> schoolExcellentCourseDomainList = schoolExcellentCourseService
					.findExcellentCourseKj(page, orgId, pageDomain);
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, schoolExcellentCourseDomainList);
			result.put(ApiReturnConstants.PAGE, pageDomain);
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} else {
			PageDomain pageDomain = new PageDomain();
			Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
			pageDomain.setPageNumber(page.getPageNumber());
			pageDomain.setPageSize(page.getPageSize());
				List<SchoolExcellentCourseDomain> schoolExcellentCourseDomainList = schoolExcellentCourseService
						.findExcellentCourseLikeNameKj(page, orgId, pageDomain, courseName);
				result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
				result.put(ApiReturnConstants.DATA, schoolExcellentCourseDomainList);
				result.put(ApiReturnConstants.PAGE, pageDomain);
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		}
		} catch (Exception e) {
			LOG.warn("Exception:{}", e);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/findcourseinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据id查询精品课程信息", response = Void.class, notes = "根据id查询精品课程信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> findCourseInfo(
			@ApiParam(value = "id:精品课程id必填", required = true) @RequestParam(value = "id", defaultValue = "0", required = true) Long id) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> result = new HashMap<>();
		if(id==0||id==null){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "精品课程id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
      }
		SchoolExcellentCourseDomain schoolExcellentCourseDomain = schoolExcellentCourseService.findExcellentCourseInfo(id);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolExcellentCourseDomain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

}
