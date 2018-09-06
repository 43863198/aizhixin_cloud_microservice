
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.school.common.PageDomain;
import com.aizhixin.cloud.school.common.core.ApiReturnConstants;
import com.aizhixin.cloud.school.common.core.PageUtil;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolHotSpecialtyDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolHotSpecialty;
import com.aizhixin.cloud.school.schoolinfo.service.SchoolHotSpecialtyService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @ClassName: SchoolHotSpecialtyController
 * @Description:
 * @author xiagen
 * @date 2017年5月12日 下午5:48:00
 * 
 */
@RestController
@Api(description = "学校热门专业管理")
@RequestMapping("/v1/schoolhotspecialty")
public class SchoolHotSpecialtyController {
	@Autowired
	private SchoolHotSpecialtyService schoolHotSpecialtyService;

	@RequestMapping(value = "/saveinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存热门专业信息", response = Void.class, notes = "保存热门专业信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> saveInfo(
			@ApiParam(value = "<b>orgId:学校id必填</b><br><b>collegeId:学院id必填</b><br><b>userId:操作者id必填</b><br><b>specialtyId:专业Id必填</b><br><b>introduction:热门专业介绍必填</b><br><b>inUrl:热门专业介绍图片必填</b><br>") @RequestBody SchoolHotSpecialtyDomain schoolHotSpecialtyDomain) {
		Map<String, Object> result = new HashMap<>();
		if (schoolHotSpecialtyDomain.getOrgId() == null || schoolHotSpecialtyDomain.getOrgId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolHotSpecialtyDomain.getCollegeId() == null || schoolHotSpecialtyDomain.getCollegeId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学院id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolHotSpecialtyDomain.getUserId() == null || schoolHotSpecialtyDomain.getUserId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "操作者id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolHotSpecialtyDomain.getSpecialtyId() == null || schoolHotSpecialtyDomain.getSpecialtyId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "专业id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolHotSpecialty schoolHotSpecialty = schoolHotSpecialtyService.findBySpecialtyId(schoolHotSpecialtyDomain.getSpecialtyId());
		if (null!=schoolHotSpecialty) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "热门专业已存在");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		 schoolHotSpecialty = schoolHotSpecialtyService
				.saveSchoolHotSpecialty(schoolHotSpecialtyDomain);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolHotSpecialty.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateinfo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改热门专业信息", response = Void.class, notes = "修改热门专业信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> updateInfo(
			@ApiParam(value = "<br><b>collegeId:学院id必填</b><br><b>userId:操作者id必填</b><br><b>introduction:热门专业介绍</b><br><b>inUrl:热门专业介绍图片地址</b><br>") @RequestBody SchoolHotSpecialtyDomain schoolHotSpecialtyDomain) {
		Map<String, Object> result = new HashMap<>();
		if (schoolHotSpecialtyDomain.getCollegeId() == null || schoolHotSpecialtyDomain.getCollegeId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学院id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolHotSpecialtyDomain.getUserId() == null || schoolHotSpecialtyDomain.getUserId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "操作者id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolHotSpecialtyDomain.getSpecialtyId() == null || schoolHotSpecialtyDomain.getSpecialtyId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "专业id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolHotSpecialtyDomain.getIntroduction() == "" || "".equals(schoolHotSpecialtyDomain.getIntroduction())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "专业介绍不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		
		SchoolHotSpecialty schoolHotSpecialty = schoolHotSpecialtyService.findById(schoolHotSpecialtyDomain.getId());
		if (schoolHotSpecialty == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "没有此id热门专业信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		schoolHotSpecialty = schoolHotSpecialtyService.updateSchoolHotSpecialty(schoolHotSpecialtyDomain,
				schoolHotSpecialty);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolHotSpecialty.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteinfo/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除热门专业信息", response = Void.class, notes = "删除热门专业信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> deleteInfo(
			@ApiParam(value = "<b>hotSpecialtyId:热门专业id必填</b><br>", required = true) @PathVariable Long id) {
		Map<String, Object> result = new HashMap<>();
		if (id == 0 || id == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "热门专业id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolHotSpecialty schoolHotSpecialty = schoolHotSpecialtyService.findById(id);
		if (schoolHotSpecialty == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "没有此id热门专业信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		schoolHotSpecialty = schoolHotSpecialtyService.deleteSchoolHotSpecialty(schoolHotSpecialty);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolHotSpecialty.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/findinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查询热门专业信息", response = Void.class, notes = "查询热门专业信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> findInfo(
			@ApiParam(value = "orgId:学校id必填", required = true) @RequestParam(value = "orgId", defaultValue = "0") Long orgId,
			@ApiParam(value = "pageNumber:起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize:每页数量", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "name:热门专业名称", required = false) @RequestParam(value = "name", required = false) String name)
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
		PageDomain pageDomain = new PageDomain();
		Pageable page = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize,"desc","id");
		if (name == "" || "".equals(name)) {
			List<SchoolHotSpecialtyDomain> schoolHotSpecialtyDomainList = schoolHotSpecialtyService
					.findSchoolHotSpecialty(page, orgId, pageDomain);
			pageDomain.setPageNumber(page.getPageNumber());
			pageDomain.setPageSize(page.getPageSize());
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, schoolHotSpecialtyDomainList);

		} else {
			List<SchoolHotSpecialtyDomain> schoolHotSpecialtyDomainList = schoolHotSpecialtyService
					.findSchoolHotSpecialtyLikeName(page, orgId, pageDomain, name);
			pageDomain.setPageNumber(page.getPageNumber());
			pageDomain.setPageSize(page.getPageSize());
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, schoolHotSpecialtyDomainList);
		}
		result.put(ApiReturnConstants.PAGE, pageDomain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/findid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据id查询热门专业信息", response = Void.class, notes = "根据id查询热门专业信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> findInfoById(
			@ApiParam(value = "id:热门专业id必填", required = true) @RequestParam(value = "id", defaultValue = "0",required=true) Long id) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> result = new HashMap<>();
		if (id == 0 || id == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "热门专业id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolHotSpecialtyDomain schoolHotSpecialtyDomain=schoolHotSpecialtyService.findByIdInfo(id);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolHotSpecialtyDomain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

	}

}
