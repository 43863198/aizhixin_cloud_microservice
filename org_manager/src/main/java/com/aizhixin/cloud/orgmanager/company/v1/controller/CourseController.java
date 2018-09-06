/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.CourseRedisData;
import com.aizhixin.cloud.orgmanager.company.entity.Course;
import com.aizhixin.cloud.orgmanager.company.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 课程管理
 * @author zhen.pan
 *
 */
@RestController
@RequestMapping("/v1/course")
@Api(description = "课程管理API")
public class CourseController {
	private CourseService courseService;

	@Autowired
	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}
	/**
	 * 添加课程信息
	 * @param courseDomain		课程域对象
	 * @param bindingResult		验证错误的容器
	 * @return			成功标志/失败消息
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存课程信息", response = Void.class, notes = "保存课程信息<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<Map<String, Object>> add(
			@ApiParam(value = "<b>必填:<br />name、orgId") @Valid @RequestBody CourseDomain courseDomain,
			BindingResult bindingResult) {
		if (null == courseDomain.getUserId() || courseDomain.getUserId() <= 0) {
			throw new NoAuthenticationException();
		}
		Map<String, Object> result = new HashMap<>();
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		Course c = courseService.save(courseDomain.getUserId(), courseDomain);
		result.put(ApiReturnConstants.ID, c.getId());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	/**
	 * 修改课程
	 * @param courseDomain		课程域对象
	 * @param bindingResult		验证错误的容器
	 * @return			成功标志/失败消息
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改课程信息", response = Void.class, notes = "修改课程信息<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<Map<String, Object>> update(
			@ApiParam(value = "<b>必填:<br />id、name、orgId") @RequestBody CourseDomain courseDomain,
			BindingResult bindingResult) {
		if (null == courseDomain.getUserId() || courseDomain.getUserId() <= 0) {
			throw new NoAuthenticationException();
		}
		Map<String, Object> result = new HashMap<>();
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		Course c = courseService.update(courseDomain.getUserId(), courseDomain);
		result.put(ApiReturnConstants.ID, c.getId());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	/**
	 * 删除课程
	 * @param id		课程ID
	 * @param userId	接口调用时候的操作用户
	 * @return			成功标志/失败消息
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除课程信息", response = Void.class, notes = "删除课程信息<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<Map<String, Object>> update(
			@ApiParam(value = "ID", required = true) @PathVariable Long id,
			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam(value = "userId") Long userId
			) {
		Map<String, Object> result = new HashMap<>();
		if (null == userId || userId <= 0) {
			throw new NoAuthenticationException();
		}
		courseService.delete(userId, id);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 获取课程的详情信息
	 * @param id		课程ID
	 * @return			成功标志/失败消息
	 */
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取课程信息", response = Void.class, notes = "获取课程信息<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<CourseDomain> get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
		return new ResponseEntity<>(courseService.get(id), HttpStatus.OK);
	}

	/**
	 * 按照条件分页查询特定学校课程信息
	 * @param orgId		学校ID
	 * @param name		课程名称
	 * @param pageNumber	页码
	 * @param pageSize	每页条数
	 * @return			成功标志/失败消息
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学校课程信息", response = Void.class, notes = "根据查询条件分页查询指定学校课程信息<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<Map<String, Object>> list(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 课程名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
			) {
		Map<String, Object> result = new HashMap<>();
		result = courseService.queryList(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId,  name);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 按照指定学校分页获取课程ID和name列表
	 * @param orgId		学校ID
	 * @param name		课程名称
	 * @param pageNumber	页码
	 * @param pageSize	每页条数
	 * @return			成功标志/失败消息
	 */
	@RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "分页获取指定学校课程ID和name列表", response = Void.class, notes = "分页获取指定学校课程ID和name列表<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<Map<String, Object>> droplist(
			@ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
			@ApiParam(value = "name 课程名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
			) {
		Map<String, Object> result = new HashMap<>();
		result = courseService.dropList(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, name);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/getbyids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "根据Id列表获取课程信息", response = Void.class, notes = "根据Id列表获取课程信息<br><br><b>@author zhen.pan</b>")
	public List<CourseDomain> getByIds(
			@ApiParam(value = "ids 课程ID数组") @RequestBody Set<Long> ids) {
		if (null != ids && ids.size() > 0) {
			return courseService.queryByIds(ids);
		}
		return null;
	}

//	@RequestMapping(value = "/addall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "POST", value = "批量保存课程信息", response = Void.class, notes = "批量保存课程信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//	public List<CourseDomain> addAll(
//			@ApiParam(value = "<b>必填:name、code、orgId、userId</b>") @RequestBody List<CourseDomain> courseDomains) {
//		return courseService.saveAll(courseDomains);
//	}
//
//	@RequestMapping(value = "/updateall", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "PUT", value = "批量修改课程信息", response = Void.class, notes = "批量修改课程信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//	public List<CourseDomain> updateAll(
//			@ApiParam(value = "<b>必填:id、name、code、orgId、userId</b>") @RequestBody List<CourseDomain> courseDomains) {
//		return courseService.updateAll(courseDomains);
//	}
//
//	@RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "DELETE", value = "批量删除课程信息", response = Void.class, notes = "批量删除课程信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//	public List<CourseDomain> deleteAll(@ApiParam(value = "<b>必填:id、userId</b>") @RequestBody List<CourseDomain> courseDomains) {
//		return courseService.deleteAll(courseDomains);
//	}
	
	@RequestMapping(value = "/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "根据Excel模板批量导入课程信息", response = Void.class, notes = "根据Excel模板批量导入课程信息<br><br><b>@author bly</b>")
	public ResponseEntity<Void> importStudent(
			@ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
			@ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
		courseService.importCourseData(orgId, file, userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/importmsg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查看课程Excel导入进度及结果", response = Void.class, notes = "查看课程Excel导入进度及结果<br><br><b>@author bly</b>")
	public CourseRedisData importStudentMsg(
			@ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
		return courseService.importCourseMsg(orgId, userId);
	}
	
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	@ApiOperation(httpMethod = "GET", value = "课程数据Excel导入模版下载API", response = Void.class, notes = "课程数据Excel导入模版下载API<br><br><b>@author bly</b>")
	public ResponseEntity<byte[]> exportCollegeTemplate() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/CourseTemplate.xlsx"), output);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=CourseTemplate.xlsx").body(output.toByteArray());
	}
}
