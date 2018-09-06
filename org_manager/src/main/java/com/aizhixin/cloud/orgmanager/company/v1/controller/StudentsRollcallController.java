/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.company.entity.StudentRollcallSet;
import com.aizhixin.cloud.orgmanager.company.service.StudentRollcallSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * B端学生考勤设置
 *
 * @author zhen.pan
 *
 */
@RestController
@RequestMapping("/v1/studentroolcall")
@Api(description = "B端学生考勤设置管理API")
public class StudentsRollcallController {
	private StudentRollcallSetService studentRollcallSetService;
	@Autowired
	public StudentsRollcallController(StudentRollcallSetService studentRollcallSetService) {
		this.studentRollcallSetService = studentRollcallSetService;
	}

	/**
	 *  暂停学生考勤
	 * @param studentId
	 * @param msg
	 * @param opratorId
	 * @return
	 */
	@RequestMapping(value = "/cansel", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "暂停学生考勤", response = Void.class, notes = "暂停学生考勤<br><br><b>@author zhen.pan</b>")
	public Long cansel(
			@ApiParam(value = "studentId 学生ID", required = true) @RequestParam(value = "studentId") Long studentId,
			@ApiParam(value = "msg 暂停或者恢复考勤的原因说明", required = true) @RequestParam(value = "msg") String msg,
			@ApiParam(value = "opratorId 操作人ID", required = true) @RequestParam(value = "opratorId") Long opratorId) {
		StudentRollcallSet e = studentRollcallSetService.canselRollcall(studentId, msg, opratorId);
		if (null != e) {
			return e.getId();
		}
		return null;
	}

	/**
	 *  恢复学生考勤
	 * @param studentId
	 * @param msg
	 * @param opratorId
	 * @return
	 */
	@RequestMapping(value = "/recover", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "恢复学生考勤", response = Void.class, notes = "恢复学生考勤<br><br><b>@author zhen.pan</b>")
	public Long recover(
			@ApiParam(value = "studentId 学生ID", required = true) @RequestParam(value = "studentId") Long studentId,
			@ApiParam(value = "msg 暂停或者恢复考勤的原因说明", required = true) @RequestParam(value = "msg") String msg,
			@ApiParam(value = "opratorId 操作人ID", required = true) @RequestParam(value = "opratorId") Long opratorId) {
		StudentRollcallSet e = studentRollcallSetService.recoverRollcall(studentId, msg, opratorId);
		if (null != e) {
			return e.getId();
		}
		return null;
	}

//	/**
//	 * 按照班级分页获取学生ID和name列表
//	 * @param orgId				学校ID
//	 * @param collegeId			学院ID
//	 * @param professionalId	专业ID
//	 * @param classesId			班级ID
//	 * @param name				学生姓名或者学号
//	 * @param pageNumber		起始页
//	 * @param pageSize			每页的限制数目
//	 * @return					查询结果
//	 */
//	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "分页按照学校查询学生列表", response = Void.class, notes = "分页按照学校查询学生列表<br><br><b>@author zhen.pan</b>")
//	public PageData<StudentDomain> list(
//			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
//			@ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
//			@ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
//			@ApiParam(value = "classesId 班级ID") @RequestParam(value = "classesId", required = false) Long classesId,
//			@ApiParam(value = "name 学生姓名或者学号") @RequestParam(value = "name", required = false) String name,
//			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
//		return userService.queryList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, collegeId, professionalId, classesId, name);
//	}
}