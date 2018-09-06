/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.domain.IdDomain;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.orgmanager.company.domain.WeekDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Week;
import com.aizhixin.cloud.orgmanager.company.service.WeekService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 学周管理
 * @author 郑宁
 *
 */
@RestController
@RequestMapping("/v1/week")
@Api(description="学周管理API")
public class WeekController {
	@Autowired
	private WeekService weekService;
//	/**
//	 * 添加学周
//	 * @param weekDomain
//	 * @param bindingResult
//	 * @return
//	 */
//	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "POST", value = "保存学周信息", response = Void.class, notes = "保存学周信息<br><br><b>@author 郑宁</b>")
//	public IdDomain add(
//			@ApiParam(value = "<b>必填:、</b><br>semesterId、<br>startDate、<br>endDate、<br>no、<br>userId") @Valid @RequestBody WeekDomain weekDomain,
//			BindingResult bindingResult) {
//		IdDomain d = new IdDomain ();
//		if (null == weekDomain.getUserId() || weekDomain.getUserId() <= 0) {
//			throw new NoAuthenticationException();
//		}
//		if (bindingResult.hasErrors()) {
//			ObjectError e = bindingResult.getAllErrors().get(0);
//			throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
//		}
//		Week c = weekService.save(weekDomain);
//		d.setId(c.getId());
//		return d;
//	}
	
//	/**
//	 * 修改学周
//	 * @param weekDomain
//	 * @param bindingResult
//	 * @return
//	 */
//	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "PUT", value = "修改学周信息", response = Void.class, notes = "修改学周信息<br><br><b>@author 郑宁</b>")
//	public IdDomain update(
//			@ApiParam(value = "<b>必填:、</b><br>id、<br>name、<br>semesterId、<br>startDate、<br>endDate、<br>no、<br>userId") @RequestBody WeekDomain weekDomain,
//			BindingResult bindingResult) {
//		if (null == weekDomain.getUserId() || weekDomain.getUserId() <= 0) {
//			throw new NoAuthenticationException();
//		}
//		if (bindingResult.hasErrors()) {
//			ObjectError e = bindingResult.getAllErrors().get(0);
//			throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
//		}
//		Week c = weekService.update(weekDomain);
//		IdDomain d = new IdDomain ();
//		d.setId(c.getId());
//		return d;
//	}
	
	/**
	 * 删除学周
	 * @param id
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除学周信息", response = Void.class, notes = "删除学周信息<br><br><b>@author 郑宁</b>")
	public IdDomain delete(
			@ApiParam(value = "ID", required = true) @PathVariable Long id,
			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId
			) {
		if (null == userId || userId <= 0) {
			throw new NoAuthenticationException();
		}
		Week week = weekService.delete(userId, id);
		IdDomain d = new IdDomain ();
		d.setId(week.getId());
		return d;
	}

	/**
	 * 按照条件分页查询特定学校学期的学周信息
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学校学期的学周信息", response = Void.class, notes = "根据查询条件分页查询指定学校学期的学周信息<br><br><b>@author 郑宁</b>")
	public PageData<WeekDomain> list(
            @ApiParam(value = "semesterId 学期ID", required = true) @RequestParam(value = "semesterId") Long semesterId,
            @ApiParam(value = "no 第几周") @RequestParam(value = "no", required = false) Integer no,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
			) {
		return weekService.queryList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), semesterId,  no);
	}

	/**
	 * 按照指定学校学期分页获取学周ID和name列表
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "分页获取指定学校学期的学周ID和name列表", response = Void.class, notes = "分页获取指定学校学期的学周ID和name列表<br><br><b>@author 郑宁</b>")
	public PageData<WeekDomain> droplist(
			@ApiParam(value = "semesterId 学期ID", required = true) @RequestParam(value = "semesterId") Long semesterId,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
			) {
		return weekService.dropList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), semesterId);
	}
	
	
	@RequestMapping(value = "/addsemesterweek", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "生成学期的学周信息", response = Void.class, notes = "生成学期的学周信息<br><br><b>@author 郑宁</b>")
	public IdDomain addSemesterWeek(
			@ApiParam(value = "semesterId 学期ID", required = true) @RequestParam(value = "semesterId") Long semesterId,
			@ApiParam(value = "start 起始日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") @RequestParam(value = "start") Date start,
			@ApiParam(value = "end 结束日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") @RequestParam(value = "lastDate") Date end,
			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
		if (null == userId || userId <= 0) {
			throw new NoAuthenticationException();
		}
		Long c = weekService.createAllWeek(semesterId, start, end);
		IdDomain d = new IdDomain ();
		d.setId(c);
		return d;
	}
	
	
	@RequestMapping(value = "/getweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查询指定日期是第几学周", response = Void.class, notes = "查询指定日期是第几学周<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> getWeek(
			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "date 指定日期(若为空则为当前日期)<br></br>时间格式：yyyy-MM-dd") @RequestParam(value = "date", required = false) Date date
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		result = weekService.getWeekByOrgIdAndDate(result, orgId, date);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据id查询学周信息", response = Void.class, notes = "根据id查询学周信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Week> get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
		
		return new ResponseEntity(weekService.get(id), HttpStatus.OK);
	}
}
