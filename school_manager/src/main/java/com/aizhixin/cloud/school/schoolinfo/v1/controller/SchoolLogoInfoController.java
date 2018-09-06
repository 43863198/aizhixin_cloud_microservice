
package com.aizhixin.cloud.school.schoolinfo.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.school.common.core.ApiReturnConstants;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolLogoInfoDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolLogoInfo;
import com.aizhixin.cloud.school.schoolinfo.service.SchoolLogoInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @ClassName: SchoolLogoInfoController
 * @Description:
 * @author xiagen
 * @date 2017年5月11日 下午6:48:51
 * 
 */
@RestController
@RequestMapping("/v1/school/infomanager")
@Api(description = "学校logo信息管理API")
public class SchoolLogoInfoController {
	@Autowired
	private SchoolLogoInfoService schoolLogoInfoService;

	@RequestMapping(value = "/saveandupdateschoollogoinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "学校logo信息保存与修改", response = Void.class, notes = "添加与修改学校logo信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> saveAndUpdateSchoolLogoInfo(
			@ApiParam(value = "<b>id：学校logo信息id，填的话表示修改操作</br><b>orgId：学校id，必填</br><b>logoUrl：学校logo地址，必填</br><b>description：学校logo描述</br><b>userId：操作者id，必填</br>") @RequestBody SchoolLogoInfoDomain schoolLogoInfoDomain) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (schoolLogoInfoDomain.getOrgId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		
		if (schoolLogoInfoDomain.getUserId() == 0) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "当前用户id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}

		if (schoolLogoInfoDomain.getId() == 0 || schoolLogoInfoDomain.getId() == null) {
			SchoolLogoInfo schoolLogoInfo = schoolLogoInfoService.saveSchoolLogoInfo(schoolLogoInfoDomain);
			schoolLogoInfoService.updateGetAdCache(schoolLogoInfoDomain.getOrgId());
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, schoolLogoInfo.getId());
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} else {
			SchoolLogoInfo schoolLogoInfo = schoolLogoInfoService.findById(schoolLogoInfoDomain.getId());
			if (schoolLogoInfo == null) {
				result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
				result.put(ApiReturnConstants.CAUSE, "不存在这个id的logo信息");
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
			} else {
				schoolLogoInfo = schoolLogoInfoService.updateSchoolLogoInfo(schoolLogoInfoDomain, schoolLogoInfo);
				schoolLogoInfoService.updateGetAdCache(schoolLogoInfoDomain.getOrgId());
				result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
				result.put(ApiReturnConstants.DATA, schoolLogoInfo.getId());
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}
		}

	}

	@RequestMapping(value = "/findschoollogoinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据学校id查询学校logo信息", response = Void.class, notes = "根据学校id查询学校logo信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> findSchoolLogoInfo(
			@ApiParam(value = "<b>学校id必填</br>",required = true) @RequestParam(name = "orgId", required = true, defaultValue = "0") Long orgId) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (orgId == 0 || orgId == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		} else {
			List<SchoolLogoInfoDomain> schoolLogoInfoDomainList = schoolLogoInfoService.findSchoolLogoInfo(orgId);
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, schoolLogoInfoDomainList);
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		}
	}

}
