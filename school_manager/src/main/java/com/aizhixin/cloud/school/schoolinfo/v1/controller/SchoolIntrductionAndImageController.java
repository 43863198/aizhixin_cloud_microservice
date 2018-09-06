
package com.aizhixin.cloud.school.schoolinfo.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.school.common.core.ApiReturnConstants;
import com.aizhixin.cloud.school.common.core.PublicErrorCode;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolIntrductionDomain;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolShuffImageDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolIntrduction;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolShuffImage;
import com.aizhixin.cloud.school.schoolinfo.service.SchoolIntroductionAndImageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @ClassName: SchoolIntrductionAndImageController
 * @Description: 学校简介轮播图管理
 * @author xiagen
 * @date 2017年5月12日 下午1:06:17
 */

@RestController
@RequestMapping("/v1/school/intrductionandimage")
@Api(description = "学校简介、轮播图管理api")
public class SchoolIntrductionAndImageController {
	@Autowired
	private SchoolIntroductionAndImageService schoolIntroductionAndImageService;

	@RequestMapping(value = "/findintroduction", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据学校id查询学校简介", response = Void.class, notes = "根据学校id查询学校简介<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> findIntroduction(
			@ApiParam(value = "<b>学校id必填</br>", required = true) @RequestParam(name = "orgId", required = true, defaultValue = "0") Long orgId) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (orgId == 0 || orgId == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolIntrductionDomain schoolIntrductionDomain = schoolIntroductionAndImageService.findSchoolintroduction(orgId);				
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolIntrductionDomain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/findimage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据学校id查询学校轮播图信息", response = Void.class, notes = "根据学校id查询学校轮播图信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> findImage(
			@ApiParam(value = "<b>学校id必填</br>", required = true) @RequestParam(name = "orgId", required = true, defaultValue = "0") Long orgId) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (orgId == 0 || orgId == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		List<SchoolShuffImageDomain> SchoolShuffImageDomainList = schoolIntroductionAndImageService.findSchoolImage(orgId);				
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, SchoolShuffImageDomainList);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	

	@RequestMapping(value = "/savaandupdateinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "添加和修改学校简介信息", response = Void.class, notes = "添加和修改学校简介信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> saveAndUpdateInfo(
			@ApiParam(value = "<b>学校id必填</br><b>学校简介必填</br><b>当前操作用户id必填</br>") @RequestBody SchoolIntrductionDomain schoolIntrductionDomain) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (schoolIntrductionDomain.getOrgId() == 0 || schoolIntrductionDomain.getOrgId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolIntrductionDomain.getUserId() == 0 || schoolIntrductionDomain.getUserId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "当前操作用户id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (schoolIntrductionDomain.getIntroduction() == "" || "".equals(schoolIntrductionDomain.getIntroduction())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校简介不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		SchoolIntrduction schoolIntrduction = schoolIntroductionAndImageService
				.findSchoolIntroduction(schoolIntrductionDomain.getOrgId());
		if (schoolIntrduction == null) {
			schoolIntrduction = schoolIntroductionAndImageService.saveSchoolIntrduction(schoolIntrductionDomain);
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, schoolIntrduction.getId());
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} else {
			schoolIntrduction = schoolIntroductionAndImageService.updateSchoolIntrduction(schoolIntrductionDomain,
					schoolIntrduction);
			result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
			result.put(ApiReturnConstants.DATA, schoolIntrduction.getId());
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/savaschoolshuffimageinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "添加学校轮播图信息", response = Void.class, notes = "添加学校轮播图信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> saveSchoolShuffImageInfo(@ApiParam(value = "<b>imageUrl：图片地址必填</br><b>schoolId：学校id必填</br>")
			@RequestBody SchoolShuffImageDomain schoolShuffImageDomain) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (schoolShuffImageDomain.getOrgId() == 0 || schoolShuffImageDomain.getOrgId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		
		
		SchoolShuffImage schoolShuffImage = schoolIntroductionAndImageService
				.saveSchoolShuffImage(schoolShuffImageDomain);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolShuffImage.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/updateschoolshuffimageinfo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改学校轮播图信息", response = Void.class, notes = "修改学校轮播图信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> updateSchoolShuffImageInfo(@ApiParam(value = "<b>schoolShuffImageId：轮播图id必填</br><b>imageUrl:图片地址必填</br>")
			@RequestBody SchoolShuffImageDomain schoolShuffImageDomain) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (schoolShuffImageDomain.getSchoolShuffImageId() == 0 || schoolShuffImageDomain.getSchoolShuffImageId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "学校轮播图id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		
		SchoolShuffImage schoolShuffImage = schoolIntroductionAndImageService.findByid(schoolShuffImageDomain.getSchoolShuffImageId());
		if(schoolShuffImage==null){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "没有此id轮播图信息");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		
		schoolShuffImage=schoolIntroductionAndImageService.updateSchoolShuffImage(schoolShuffImageDomain, schoolShuffImage);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, schoolShuffImage.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/deleteschoolshuffimageinfo/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除学校轮播图信息", response = Void.class, notes = "删除学校轮播图信息<br><br><b>@author xiagen</b>")
	public ResponseEntity<Map<String, Object>> deleteSchoolShuffImageInfo(@ApiParam(value = "<b>学校轮播图id必填</br>", required = true)@PathVariable Long id){
		Map<String, Object> result = new HashMap<String, Object>();
		if(id==0||id==null){
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "轮播图id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			schoolIntroductionAndImageService.deleteSchoolShuffImage(id);
		} catch (Exception e) {
			// TODO: handle exception
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, PublicErrorCode.DELETE_EXCEPTION.getIntValue());
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, id);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
