package com.aizhixin.cloud.dd.imgManager.v1.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.imgManager.domain.ImgDomain;
import com.aizhixin.cloud.dd.imgManager.entity.ImageManager;
import com.aizhixin.cloud.dd.imgManager.service.ImgManagerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value="/api/web/v1")
@Api(description="web端广告管理地址")
public class ImgWebController {

	@Autowired
	private ImgManagerService imgManagerService;
	
	@RequestMapping(value="/img/save",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存广告信息", response = Void.class, notes = "保存广告信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> save(@RequestBody ImgDomain imgDomain) {
		Map<String, Object> result=new HashMap<>();
		if(StringUtils.isEmpty(imgDomain.getImgSrc())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "广告图片地址不能为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(StringUtils.isEmpty(imgDomain.getModule())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "广告所属模块不为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(StringUtils.isEmpty(imgDomain.getRedirectUrl())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "广告图片跳转地址不能为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(imgDomain.getOrgInfoList().isEmpty()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "分配学校不能为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		ImageManager im=imgManagerService.save(imgDomain);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, im.getId());
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
		
	}
	
	
	
	@RequestMapping(value="/img/update",method=RequestMethod.PUT,produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改广告信息", response = Void.class, notes = "修改广告信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> update(@RequestBody ImgDomain imgDomain) {
		Map<String, Object> result=new HashMap<>();
		if(StringUtils.isEmpty(imgDomain.getId())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "广告信息id不能为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(StringUtils.isEmpty(imgDomain.getImgSrc())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "广告图片地址不能为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(StringUtils.isEmpty(imgDomain.getModule())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "广告所属模块不为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(StringUtils.isEmpty(imgDomain.getRedirectUrl())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "广告图片跳转地址不能为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(imgDomain.getOrgInfoList().isEmpty()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "分配学校不能为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		ImageManager im=imgManagerService.update(imgDomain);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, im.getId());
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/img/get",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取广告信息", response = Void.class, notes = "获取广告信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> get(@ApiParam(value="id 广告信息id",required=true)@RequestParam(value="id",required=true)String id) {
		Map<String, Object> result=new HashMap<>();
		if(StringUtils.isEmpty(id)) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "广告信息id不能为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		ImgDomain im=imgManagerService.findOne(id);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, im);
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/img/delete",method=RequestMethod.DELETE,produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "获取广告信息", response = Void.class, notes = "获取广告信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> delete(@ApiParam(value="id 广告信息id",required=true)@RequestParam(value="id",required=true)String id) {
		Map<String, Object> result=new HashMap<>();
		if(StringUtils.isEmpty(id)) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "广告信息id不能为空");
			return new ResponseEntity<Map<String,Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		imgManagerService.deleteImgIdAll(id);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/img/getList",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取广告信息", response = Void.class, notes = "获取广告信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getList(@ApiParam(value="pageNumber 起始页",required=false)@RequestParam(value="pageNumber",required=false)Integer pageNumber
			,@ApiParam(value="pageSize 分页大小",required=false)@RequestParam(value="pageSize",required=false)Integer pageSize) {
		Map<String, Object> result=new HashMap<>();
		if(pageNumber==null||pageNumber==0) {
			pageNumber=1;
		}
		if(pageSize==null||pageSize==0) {
			pageSize=10;
		}
		result= imgManagerService.listImageDomainAll(pageNumber, pageSize,result);
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}
	
}
