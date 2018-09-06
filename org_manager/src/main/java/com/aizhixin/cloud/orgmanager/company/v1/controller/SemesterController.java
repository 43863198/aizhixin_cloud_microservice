/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.domain.IdDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.orgmanager.company.domain.SemesterDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.service.SemesterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 学期管理
 * @author 郑宁
 *
 */
@RestController
@RequestMapping("/v1/semester")
@Api(description="学期管理API")
public class SemesterController {
	@Autowired
	private SemesterService semesterService;

	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存学期信息", response = Void.class, notes = "保存学期信息<br><br><b>@author zhen.pan</b>")
	public IdDomain add(
			@ApiParam(value = "<b>必填:name、code、startDate、endDate、orgId、userId</b>") @RequestBody SemesterDomain semesterDomain) {
		IdDomain d = new IdDomain ();
		Semester semester = semesterService.save(semesterDomain);
		d.setId(semester.getId());
		return d;
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改学期信息", response = Void.class, notes = "修改学期信息<br><br><b>@author zhen.pan</b>")
	public IdDomain updateAll(
			@ApiParam(value = "<b>必填:id、name、code、startDate、endDate、orgId、userId</b>") @RequestBody SemesterDomain semesterDomain) {
		IdDomain d = new IdDomain ();
		Semester semester = semesterService.update(semesterDomain);
		d.setId(semester.getId());
		return d;
	}
	
	/**
	 * 删除学期，假删除操作
	 * @param id
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除学期信息", response = Void.class, notes = "删除学期信息<br><br><b>@author 郑宁</b>")
	public IdDomain delete(
			@ApiParam(value = "ID", required = true) @PathVariable Long id,
			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
		if (null == userId || userId <= 0) {
			throw new NoAuthenticationException();
		}
		IdDomain d = new IdDomain ();
		Semester semester = semesterService.delete(userId, id);
		d.setId(semester.getId());
		return d;
	}

	/**
	 * 按照条件分页查询特定学校的学期信息
	 * @param orgId
	 * @param name
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学校的学期信息", response = Void.class, notes = "根据查询条件分页查询指定学校的学期信息<br><br><b>@author 郑宁</b>")
	public PageData<SemesterDomain> list(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 学期名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		return semesterService.queryList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId,  name);
	}

	/**
	 * 按照学校分页获取学期ID和name列表
	 * @param orgId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "分页获取指定学校的学期ID和name列表", response = Void.class, notes = "分页获取指定学校的学期ID和name列表<br><br><b>@author 郑宁</b>")
	public PageData<IdNameDomain> droplist(
			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
			) {
		return semesterService.dropList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId);
	}

	/**
	 * 获取学期的详情信息
	 * @param id		学期ID
	 * @return			成功标志/失败消息
	 */
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据ID获取学期信息", response = Void.class, notes = "根据ID获取学期信息<br><br><b>@author zhen.pan</b>")
	public SemesterDomain get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
		return semesterService.get(id);
	}
	
	/**
	 * 获取指定日期是所属机构下那个学期
	 * @param orgId
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "/getsemester", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取指定时间是那个学期", response = Void.class, notes = "获取指定时间是那个学期<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> getSemester(
			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "date 指定日期(若为空则为当前日期)<br></br>时间格式：yyyy-MM-dd") @RequestParam(value = "date", required = false) Date date
			) {
		Map<String, Object> result = new HashMap<>();
		result = semesterService.getSemesterByOrgIdAndDate(result, orgId, date);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 获取指定日期是所属机构下那个学期
	 * @param orgId
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "/getorgsemester", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取指定时间是那个学期，用来获取数据", response = Void.class, notes = "获取指定时间是那个学期，用来获取数据<br><br><b>@author 郑宁</b>")
	public SemesterDomain getOrgSemester(
			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "date 指定日期(若为空则为当前日期)<br></br>时间格式：yyyy-MM-dd") @RequestParam(value = "date", required = false) Date date) {
		return semesterService.getSemesterByOrgIdAndDateForOut(orgId, date);
	}
	/**
	 * 根据orgId集合批量获取学期
	 * @param orgIds
	 * @return
	 */
	@RequestMapping(value = "/getorgssemesterlist", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "根据orgId集合批量获取当前学期", response = Void.class, notes = "根据orgId集合批量获取当前学期<br><br><b>@author jianwei.wu</b>")
	public List<SemesterDomain> getOrgsSemesterList(
			@ApiParam(value = "orgIds 学校ID集合", required = true) @RequestBody  List<Long> orgIds){
		return semesterService.getSemesterListByOrgIdsAndDate(orgIds, null);
	}
}
