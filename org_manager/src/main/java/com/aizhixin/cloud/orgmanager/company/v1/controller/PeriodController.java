/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.orgmanager.company.domain.PeriodDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Period;
import com.aizhixin.cloud.orgmanager.company.service.PeriodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 学周管理
 * @author 郑宁
 *
 */
@RestController
@RequestMapping("/v1/period")
@Api(description="课程节管理API")
public class PeriodController {
	@Autowired
	private PeriodService periodService;
	/**
	 * 添加课程节
	 * @param periodDomain
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存课程节信息", response = Void.class, notes = "保存课程节信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> add(
			@ApiParam(value = "<b>必填:、</b><br>startTime、<br>endTime、<br>no、<br>orgId、<br>userId") @Valid @RequestBody PeriodDomain periodDomain,
			BindingResult bindingResult) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		Period c = periodService.save(periodDomain);
		result.put(ApiReturnConstants.ID, c.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/**
	 * 修改课程节
	 * @param periodDomain
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改课程节信息", response = Void.class, notes = "修改课程节信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> update(
			@ApiParam(value = "<b>必填:、</b><br>id、<br>startTime、<br>endTime、<br>no、<br>orgId、<br>userId") @RequestBody PeriodDomain periodDomain,
			BindingResult bindingResult) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		Period c = periodService.update(periodDomain);
		result.put(ApiReturnConstants.ID, c.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/**
	 * 删除课程节
	 * @param id
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除课程节信息", response = Void.class, notes = "删除课程节信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> delete(
			@ApiParam(value = "ID", required = true) @PathVariable Long id,
			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (null == userId || userId <= 0) {
			throw new NoAuthenticationException();
		}
		periodService.delete(userId, id);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "按id查询课程节信息", response = Void.class, notes = "按id查询课程节信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Period> getOne(
			@ApiParam(value = "ID", required = true) @PathVariable Long id) {
		return new ResponseEntity(periodService.getOne(id), HttpStatus.OK);
	}

	/**
	 * 按照条件分页查询特定学校课程节的课程节信息
	 * @param no
	 * @param orgId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学校课程节的学周信息", response = Void.class, notes = "根据查询条件分页查询指定学校课程节的学周信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> list(
			@ApiParam(value = "orgId 结构id") @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "no 第几节") @RequestParam(value = "no", required = false) Integer no,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		result = periodService.queryList(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize),orgId,no);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	/**
	 * 按照指定学校学期分页获取课程节ID和no列表
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "分页获取指定学校学期的学周ID和name列表", response = Void.class, notes = "分页获取指定学校学期的学周ID和name列表<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> droplist(
			@ApiParam(value = "orgId 结构id") @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		result = periodService.dropList(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}


//	@RequestMapping(value = "/addall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "POST", value = "批量保存课程节信息", response = Void.class, notes = "批量保存课程节信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//	public List<PeriodDomain> addAll(
//			@ApiParam(value = "<b>必填:name、no、startTime、endTime、orgId、userId</b>") @RequestBody List<PeriodDomain> periodDomains) {
//		return periodService.saveAll(periodDomains);
//	}
//
//	@RequestMapping(value = "/updateall", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "PUT", value = "批量修改课程节信息", response = Void.class, notes = "批量修改课程节信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//	public List<PeriodDomain> updateAll(
//			@ApiParam(value = "<b>必填:id、name、no、startTime、endTime、orgId、userId</b>") @RequestBody List<PeriodDomain> periodDomains) {
//		return periodService.updateAll(periodDomains);
//	}
//
//	@RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "DELETE", value = "批量删除课程节信息", response = Void.class, notes = "批量删除课程节信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//	public List<PeriodDomain> deleteAll(@ApiParam(value = "<b>必填:id、userId</b>") @RequestBody List<PeriodDomain> periodDomains) {
//		return periodService.deleteAll(periodDomains);
//	}
}
