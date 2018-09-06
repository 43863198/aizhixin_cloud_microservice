/**
 * 
 */
package com.aizhixin.cloud.orgmanager.classschedule.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.SchoolUnifyAdjustCourseDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.SchoolUnifyAdjustCourseSchedule;
import com.aizhixin.cloud.orgmanager.classschedule.service.SchoolUnifyAdjustCourseService;
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
 * 整体调课
 * 
 * @author zhen.pan
 *
 */
@RestController
@RequestMapping("/v1/schooladjust")
@Api(description = "整体调课API")
public class SchoolUnifyAdjustCourseController {
	private SchoolUnifyAdjustCourseService schoolUnifyAdjustCourseService;

	@Autowired
	public SchoolUnifyAdjustCourseController(SchoolUnifyAdjustCourseService schoolUnifyAdjustCourseService) {
		this.schoolUnifyAdjustCourseService = schoolUnifyAdjustCourseService;
	}

	/**
	 * 添加整体调课
	 * 
	 * @param schoolUnifyAdjustCourseDomain		整体调课对象
	 * @return		成功标志/失败消息
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存整体调课信息", response = Void.class, notes = "保存整体调课信息<br><br><b>@author zhen.pan</b>")
	public Long add(
			@ApiParam(value = "<b>必填:orgId、startDate、endDate</br>") @RequestBody SchoolUnifyAdjustCourseDomain schoolUnifyAdjustCourseDomain) {
		Map<String, Object> result = new HashMap<>();
		if (null == schoolUnifyAdjustCourseDomain.getUserId() || schoolUnifyAdjustCourseDomain.getUserId() <= 0) {
			throw new NoAuthenticationException();
		}
		SchoolUnifyAdjustCourseSchedule schoolUnifyAdjustCourseSchedule = schoolUnifyAdjustCourseService.save(schoolUnifyAdjustCourseDomain);
		if (null != schoolUnifyAdjustCourseSchedule) {
			return schoolUnifyAdjustCourseSchedule.getId();
		}
		return null;
	}

	/**
	 * 修改整体调课
	 * 
	 * @param schoolUnifyAdjustCourseDomain		整体调课域对象
	 * @return		成功标志/失败消息
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改整体调课信息", response = Void.class, notes = "修改整体调课信息<br><br><b>@author zhen.pan</b>")
	public Long update(
			@ApiParam(value = "<b>必填:id、name、professionalId、userId</br>")  @RequestBody SchoolUnifyAdjustCourseDomain schoolUnifyAdjustCourseDomain) {
		Map<String, Object> result = new HashMap<>();
		if (null == schoolUnifyAdjustCourseDomain.getUserId() || schoolUnifyAdjustCourseDomain.getUserId() <= 0) {
			throw new NoAuthenticationException();
		}
		SchoolUnifyAdjustCourseSchedule schoolUnifyAdjustCourseSchedule = schoolUnifyAdjustCourseService.update(schoolUnifyAdjustCourseDomain);
		if (null != schoolUnifyAdjustCourseSchedule) {
			return schoolUnifyAdjustCourseSchedule.getId();
		}
		return null;
	}

	/**
	 * 删除整体调课，假删除操作
	 * 
	 * @param id		整体调课ID
	 * @param userId	操作用户ID
	 * @return		成功标志/失败消息
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除整体调课信息", response = Void.class, notes = "删除整体调课信息<br><br><b>@author zhen.pan</b>")
	public Long update(@ApiParam(value = "ID", required = true) @PathVariable Long id,
			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
		if (null == userId || userId <= 0) {
			throw new NoAuthenticationException();
		}
		SchoolUnifyAdjustCourseSchedule schoolUnifyAdjustCourseSchedule = schoolUnifyAdjustCourseService.delete(id, userId);
		if (null != schoolUnifyAdjustCourseSchedule) {
			return schoolUnifyAdjustCourseSchedule.getId();
		}
		return null;
	}


	/**
	 * 获取整体调课信息
	 *
	 * @param id		整体调课ID
	 * @return		成功标志/失败消息
	 */
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取整体调课信息", response = Void.class, notes = "获取整体调课信息<br><br><b>@author zhen.pan</b>")
	public SchoolUnifyAdjustCourseDomain get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
		return schoolUnifyAdjustCourseService.get(id);
	}

	/**
	 * 按照条件分页查询指定查询条件的整体调课信息
	 * @param orgId			学校
	 * @param pageNumber	第几页
	 * @param pageSize		每页条数
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定查询条件的整体调课信息", response = Void.class, notes = "根据查询条件分页查询指定查询条件的整体调课信息<br><br><b>@author zhen.pan</b>")
	public PageData<SchoolUnifyAdjustCourseDomain> list(
			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
			@ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		return schoolUnifyAdjustCourseService.list(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId);
	}
}
