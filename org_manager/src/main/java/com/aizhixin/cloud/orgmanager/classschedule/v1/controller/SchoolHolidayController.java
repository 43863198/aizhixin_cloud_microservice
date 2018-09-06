/**
 * 
 */
package com.aizhixin.cloud.orgmanager.classschedule.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.SchoolHolidayDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.SchoolHoliday;
import com.aizhixin.cloud.orgmanager.classschedule.service.SchoolHolidayService;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 节假日管理
 * 
 * @author zhen.pan
 *
 */
@RestController
@RequestMapping("/v1/schoolholiday")
@Api(description = "节假日管理API")
public class SchoolHolidayController {
	private SchoolHolidayService schoolHolidayService;

	@Autowired
	public SchoolHolidayController(SchoolHolidayService schoolHolidayService) {
		this.schoolHolidayService = schoolHolidayService;
	}

	/**
	 * 添加节假日
	 * 
	 * @param schoolHolidayDomain		节假日对象
	 * @return		成功标志/失败消息
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存节假日信息", response = Void.class, notes = "保存节假日信息<br><br><b>@author zhen.pan</b>")
	public Long add(
			@ApiParam(value = "<b>必填:orgId、startDate、endDate</br>") @RequestBody SchoolHolidayDomain schoolHolidayDomain) {
		Map<String, Object> result = new HashMap<>();
		if (null == schoolHolidayDomain.getUserId() || schoolHolidayDomain.getUserId() <= 0) {
			throw new NoAuthenticationException();
		}
		SchoolHoliday schoolHoliday = schoolHolidayService.save(schoolHolidayDomain);
		if (null != schoolHoliday) {
			return schoolHoliday.getId();
		}
		return null;
	}

	/**
	 * 修改节假日
	 * 
	 * @param schoolHolidayDomain		节假日域对象
	 * @return		成功标志/失败消息
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改节假日信息", response = Void.class, notes = "修改节假日信息<br><br><b>@author zhen.pan</b>")
	public Long update(
			@ApiParam(value = "<b>必填:id、name、professionalId、userId</br>")  @RequestBody SchoolHolidayDomain schoolHolidayDomain) {
		Map<String, Object> result = new HashMap<>();
		if (null == schoolHolidayDomain.getUserId() || schoolHolidayDomain.getUserId() <= 0) {
			throw new NoAuthenticationException();
		}
		SchoolHoliday schoolHoliday = schoolHolidayService.update(schoolHolidayDomain);
		if (null != schoolHoliday) {
			return schoolHoliday.getId();
		}
		return null;
	}

	/**
	 * 删除节假日，假删除操作
	 * 
	 * @param id		节假日ID
	 * @param userId	操作用户ID
	 * @return		成功标志/失败消息
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除节假日信息", response = Void.class, notes = "删除节假日信息<br><br><b>@author zhen.pan</b>")
	public Long update(@ApiParam(value = "ID", required = true) @PathVariable Long id,
			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
		if (null == userId || userId <= 0) {
			throw new NoAuthenticationException();
		}
		SchoolHoliday schoolHoliday = schoolHolidayService.delete(id, userId);
		if (null != schoolHoliday) {
			return schoolHoliday.getId();
		}
		return null;
	}


	/**
	 * 获取节假日信息
	 *
	 * @param id		节假日ID
	 * @return		成功标志/失败消息
	 */
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取节假日信息", response = Void.class, notes = "获取节假日信息<br><br><b>@author zhen.pan</b>")
	public SchoolHolidayDomain get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
		return schoolHolidayService.get(id);
	}

	/**
	 * 按照条件分页查询指定查询条件的节假日信息
	 * @param orgId			学校
	 * @param pageNumber	第几页
	 * @param pageSize		每页条数
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定查询条件的节假日信息", response = Void.class, notes = "根据查询条件分页查询指定查询条件的节假日信息<br><br><b>@author zhen.pan</b>")
	public PageData<SchoolHolidayDomain> list(
			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
			@ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		return schoolHolidayService.list(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId);
	}
}
