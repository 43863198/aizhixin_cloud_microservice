/**
 * 
 */
package com.aizhixin.cloud.orgmanager.comminication.v1.controller;

import com.aizhixin.cloud.orgmanager.comminication.domain.StudentContactDomain;
import com.aizhixin.cloud.orgmanager.comminication.service.StudentContactServiceImpl;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
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
 * 通讯录管理
 * 
 * @author zhen.pan
 *
 */
@RestController
@RequestMapping("/v1/contact")
@Api(description = "通讯录管理API")
public class StudentContactController {
	private StudentContactServiceImpl studentContactService;

	@Autowired
	public StudentContactController(StudentContactServiceImpl studentContactService) {
		this.studentContactService = studentContactService;
	}

	/**
	 * 按照条件分页查询指定教学班的所有学生通讯录
	 * @param teachingclassId			教学班
	 * @param pageNumber	第几页
	 * @param pageSize		每页条数
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "按照条件分页查询指定教学班的所有学生通讯录", response = Void.class, notes = "按照条件分页查询指定教学班的所有学生通讯录<br><br><b>@author zhen.pan</b>")
	public PageData<StudentContactDomain> list(
			@ApiParam(value = "teachingclassId 教学班ID", required = true) @RequestParam(value = "teachingclassId") Long teachingclassId,
			@ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		return studentContactService.getTeachingclassStudentContact(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), teachingclassId);
	}
}
