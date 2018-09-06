/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.domain.BatchUserDomain;
import com.aizhixin.cloud.orgmanager.company.domain.ClassTeacherDomain;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain;
import com.aizhixin.cloud.orgmanager.company.service.ClassesTeacherService;
import com.aizhixin.cloud.orgmanager.training.core.GroupStatusConstants;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupListInfoDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 班主任管理
 * 
 * @author zhen.pan
 *
 */
@RestController
@RequestMapping("/v1/classesteacher")
@Api(description = "班主任管理API")
public class ClassesTeacherController {
	private ClassesTeacherService classesTeacherService;

	@Autowired
	public ClassesTeacherController(ClassesTeacherService classesTeacherService) {
		this.classesTeacherService = classesTeacherService;
	}

	/**
	 * 添加班主任
	 *
	 * @param classesTeachers
	 *            班级ID老师ID列表
	 * @return 成功标志/失败消息
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "给班级新增一到多个班主任", response = Void.class, notes = "给班级新增一到多个班主任<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<String> add(
			@ApiParam(value = "classesStudents 班级ID老师ID列表", required = true) @RequestBody BatchUserDomain classesTeachers) {
		if (null == classesTeachers) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"班级ID和老师ID列表是必须的");
		}
		if (null == classesTeachers.getClassesId()
				|| classesTeachers.getClassesId() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "班级ID是必须的");
		}
		if (null == classesTeachers.getIds()
				|| classesTeachers.getIds().size() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师ID是必须的");
		}
		classesTeacherService.save(classesTeachers.getClassesId(),
				classesTeachers.getIds());
		return new ResponseEntity<>("", HttpStatus.OK);
	}

	/**
	 * 删除班级的一个到多个班主任
	 * 
	 * @param classesTeachers
	 *            班级ID老师ID列表
	 * @return 成功标志/失败消息
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除班级的一个到多个班主任信息", response = Void.class, notes = "删除班级的一个到多个班主任信息<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<String> delete(
			@ApiParam(value = "classesStudents 班级ID老师ID列表", required = true) @RequestBody BatchUserDomain classesTeachers) {
		if (null == classesTeachers) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"班级ID和老师ID列表是必须的");
		}
		if (null == classesTeachers.getClassesId()
				|| classesTeachers.getClassesId() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "班级ID是必须的");
		}
		if (null == classesTeachers.getIds()
				|| classesTeachers.getIds().size() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师ID是必须的");
		}
		classesTeacherService.delete(classesTeachers.getClassesId(),
				classesTeachers.getIds());
		return new ResponseEntity<>("", HttpStatus.OK);
	}

	/**
	 * 根据班级ID查找班级对应的班主任列表
	 *
	 * @param classesId
	 *            班级ID
	 * @return 成功标志/失败消息
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据班级ID查找班级对应的班主任所有列表", response = Void.class, notes = "根据班级ID查找班级对应的所有班主任列表<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<Map<String, List<TeacherDomain>>> list(
			@ApiParam(value = "classesId 班级ID", required = true) @RequestParam("classesId") Long classesId) {
		return new ResponseEntity<>(
				classesTeacherService.findClassesTeachers(classesId),
				HttpStatus.OK);
	}

	/**
	 * 根据班主任查找班级的id、name信息
	 * @param teacherId		班主任id
	 * @return
	 */
	@RequestMapping(value = "/getclassesbyteacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据班主任查询班级信息", response = Void.class, notes = "根据班主任查询班级信息<br><br><b>@author zhen.pan</b>")
	public List<IdNameDomain> getClassesByTeacher(@ApiParam(value = "teacherId 班主任老师ID", required = true) @RequestParam(value = "teacherId") Long teacherId) {
		return classesTeacherService.findClassesByTeacher(teacherId);
	}

	@RequestMapping(value = "/countbyteacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "统计班主任所带班级的数量，可用来确定老师是否班级", response = Void.class, notes = "统计班主任所带班级的数量，可用来确定老师是否班级<br><br><b>@author zhen.pan</b>")
	public Long countByTeacher(@ApiParam(value = "teacherId 班主任老师ID", required = true) @RequestParam(value = "teacherId") Long teacherId) {
		return classesTeacherService.countByTeacher(teacherId);
	}

	@RequestMapping(value = "/getteacherids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取学校班主任老师ID", response = Void.class, notes = "获取学校班主任老师ID<br><br><b>@author jianwei.wu</b>")
	public List<Long> getClassTeacherIds(
			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
			@ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
			@ApiParam(value = "nj 姓名/学号") @RequestParam(value = "nj", required = false) String nj
			) {
		return classesTeacherService.getClassTeacherIds(orgId,collegeId,nj);
	}
	
	
	@RequestMapping(value = "/initorgid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "初始化辅导员所在机构id", response = Void.class, notes = "初始化辅导员所在机构id<br><br><b>@author zhengning</b>")
	public ResponseEntity<String> initOrgId() {
		 classesTeacherService.initOrgId();
		 return new ResponseEntity<>("", HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/pagebykeywords",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按条件分页查询辅导员信息", response = Void.class, notes = "按条件分页查询辅导员信息 <br>@author HUM")
    public PageData<ClassTeacherDomain> queryGroupList(
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "keyWords 辅导员姓名或工号") @RequestParam(value = "keyWords", required = false) String keyWords,
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId
    ) {
        return classesTeacherService.queryClassTeacherPage(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), keyWords,orgId);
    }
}
