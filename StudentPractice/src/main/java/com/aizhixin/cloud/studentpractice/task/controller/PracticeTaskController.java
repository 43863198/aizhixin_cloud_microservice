/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.PageDomain;
import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.ErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.PageUtil;
import com.aizhixin.cloud.studentpractice.common.core.PaginationCore;
import com.aizhixin.cloud.studentpractice.common.core.PublicErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.RoleCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.MentorTaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.PracticeTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryTaskPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.ReviewTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDetailForSchoolDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskAssginDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.WeekTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTask;
import com.aizhixin.cloud.studentpractice.task.entity.ReviewTask;
import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;
import com.aizhixin.cloud.studentpractice.task.entity.Task;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTask;
import com.aizhixin.cloud.studentpractice.task.service.MentorTaskService;
import com.aizhixin.cloud.studentpractice.task.service.ReviewTaskService;
import com.aizhixin.cloud.studentpractice.task.service.StudentTaskService;
import com.aizhixin.cloud.studentpractice.task.service.PracticeTaskService;
import com.aizhixin.cloud.studentpractice.task.service.WeekTaskService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

/**
 * 实践周任务
 * 
 * @author 郑宁
 *
 */
@RestController
@RequestMapping("/v1/practicetask")
@Api(description = "实践任务管理API")
public class PracticeTaskController {

	@Autowired
	private PracticeTaskService taskService;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private MentorTaskService mentorTaskService;
	@Autowired
	private StudentTaskService studentTaskService;
	
	/**
	 * 创建任务
	 * 
	 * @param domain
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新建实践任务", response = Void.class, notes = "新建实践任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> add(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>weekTaskId:实践课程id,taskName:任务名称,classHour:学时,description:任务内容<br>") @Valid @RequestBody PracticeTaskDomain domain,
			BindingResult bindingResult) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		checkData(domain, bindingResult);
		
		Task task = taskService.taskSave(domain, dto);
		result.put(ApiReturnConstants.ID, task.getId());
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}


	private void checkData(PracticeTaskDomain domain, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(
					PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		if(StringUtils.isEmpty(domain.getWeekTaskId())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"所属课程不能为空");
		}
		if(StringUtils.isEmpty(domain.getTaskName())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践任务名称不能为空");
		}
//		if(null == domain.getClassHour()){
//			throw new CommonException(ErrorCode.PARAMS_CONFLICT,
//					"学时不能为空");
//		}
	}
	
	
	@RequestMapping(value = "/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改实践任务", response = Void.class, notes = "修改实践任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> edit(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>id,weekTaskId:实践课程id,taskName:任务名称,classHour:学时,description:任务内容") @Valid @RequestBody PracticeTaskDomain domain,
			BindingResult bindingResult) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		checkData(domain, bindingResult);
		if(StringUtils.isEmpty(domain.getId())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"编辑的任务的id不能为空");
		}
		
		Task task = taskService.findById(domain.getId());
		taskService.taskEdit(domain, task, dto.getId());
		result.put(ApiReturnConstants.ID, task.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除实践任务", response = Void.class, notes = "删除实践任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> delete(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "id 实践任务id", required = true) @RequestParam(value = "id", required = true) String id
//			 @ApiParam(value = "status (false:不强制删除,true:强制删除。为空默认为false)") @RequestParam(value = "status", required = false) boolean status
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		if(StringUtils.isEmpty(id)){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"删除的周任务的id不能为空");
		}
		
		taskService.deleteTask(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分页查询实践任务信息", response = Void.class, notes = "分页查询实践任务信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> page(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>选填:</b>weekTaskId:实践课程(周任务)id<br>taskName:任务标题<br>pageNumber:第几页<br>pageSize:每页数据的数目") @RequestBody QueryTaskPageDomain domain
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
//		List<String> roles = dto.getRoleNames();
//		if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
//			List<IdNameDomain> groupList = authUtilService.getMentorGroupInfo(dto.getId());
//			if (groupList.size() > 0) {
//				String groupId = "";
//				for(IdNameDomain groupInfor : groupList){
//					if(StringUtils.isEmpty(groupId)){
//						groupId = groupInfor.getId().toString();
//					}else{
//						groupId += ","+groupInfor.getId().toString();
//					}
//				}
//				return new ResponseEntity<Map<String, Object>>(weekTaskService.findPageForMentor(domain, groupId,dto.getId()), HttpStatus.OK);
//			}else{
//				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//			}
//		} else if (roles.contains(RoleCode.ROLE_STUDENT)) {
//			StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
//			if (null != stuDTO.getTrainingGroupId() && stuDTO.getTrainingGroupId().longValue() > 0L) {
//				return new ResponseEntity<Map<String, Object>>(taskService.findPageForStudent(domain, stuDTO.getTrainingGroupId().toString(),dto.getId()), HttpStatus.OK);
//			}else{
//				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//			}
//		}
		
		return new ResponseEntity<Map<String, Object>>(taskService.findPage(domain.getPageSize(), domain.getPageNumber(), domain.getTaskName(), dto.getOrgId(),domain.getWeekTaskId(),domain.getSortFlag()), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "实践任务详情", response = Void.class, notes = "实践任务详情<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> detail(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "id 实践任务id", required = true) @RequestParam(value = "id", required = true) String id
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		if(StringUtils.isEmpty(id)){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践任务的id不能为空");
		}
		
		result.put(ApiReturnConstants.DATA, taskService.getDetail(id));
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/assign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "实践任务分配", response = Void.class, notes = "实践任务分配<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> taskAssign(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>practiceGroupIdList:实践小组id集合,beginDate:开始时间,endDate:结束时间,weekTaskIdList:实践课程id集合,practiceTaskIdList:实践任务id集合(实践课程id集合和任务id集合二选一不能同时为空)<br>") @Valid @RequestBody TaskAssginDomain domain,
			BindingResult bindingResult) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		checkData(domain, bindingResult);
		taskService.taskAssign(domain, dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	private void checkData(TaskAssginDomain domain, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(
					PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		if(null ==domain.getPracticeGroupIdList() ||  domain.getPracticeGroupIdList().isEmpty()){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践小组id集合为空");
		}
		int flag = 0;
		if(null ==domain.getWeekTaskIdList() ||  domain.getWeekTaskIdList().isEmpty()){
			flag = flag+1;
		}
		
		if(null ==domain.getPracticeTaskIdList() ||  domain.getPracticeTaskIdList().isEmpty()){
			flag = flag+1;
		}
		
		if(2 == flag){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践课程id集合和任务id集合不能同时为空");
		}
		
		if(domain.getBeginDate().getTime() > domain.getEndDate().getTime()){
		throw new CommonException(ErrorCode.ID_IS_REQUIRED,
				"截至时间必须大于等于开始时间");
		}
		
	}
	
	
	@RequestMapping(value = "/edittasktime", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改实践任务", response = Void.class, notes = "修改实践任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> editTaskTime(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>id:任务id,beginDate:开始时间,endDate:结束时间") @RequestBody PracticeTaskDomain domain) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		if(StringUtils.isEmpty(domain.getId())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践任务id不能为空");
		}
		if(domain.getBeginDate().getTime() > domain.getEndDate().getTime()){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践任务截至时间必须大于等于开始时间");
		}
		
		taskService.editTaskTime(domain);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/pageforschool", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分页查询实践任务信息(实践任务详情)", response = Void.class, notes = "分页查询实践任务信息(实践任务详情)<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> pageForSchool(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>选填:</b><br>keyWord:任务名称或小组名称<br>pageNumber:第几页<br>pageSize:每页数据的数目") @RequestBody QueryTaskPageDomain domain
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		
		return new ResponseEntity<Map<String, Object>>(mentorTaskService.findPageForSchoolTeacher(domain.getPageSize(), domain.getPageNumber(), domain.getKeyWord(), dto.getOrgId(), dto.getId()), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/stutaskpage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分页查询实践任务信息(实践任务详情)", response = Void.class, notes = "分页查询实践任务信息(实践任务详情)<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> pageForStuTask(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>id:任务id") @RequestBody PracticeTaskDomain domain) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (domain.getPageSize() == null || domain.getPageSize() <= 0)
			domain.setPageSize(PaginationCore.DEFAULT_LIMIT);
		if (domain.getPageNumber() == null || domain.getPageNumber() <= 0)
			domain.setPageNumber(PaginationCore.DEFAULT_OFFSET);
		
		// 学生任务详情信息
		Page<StudentTask> page = studentTaskService
						.findListByMentorTaskId(PageUtil.createNoErrorPageRequest(domain.getPageNumber(), domain.getPageSize()),domain.getId());
		 
		 PageDomain p = new PageDomain();
		 p.setPageNumber(domain.getPageNumber());
		 p.setPageSize(domain.getPageSize());
		 p.setTotalElements(page.getTotalElements());
		 p.setTotalPages(page.getTotalPages());
		 result.put(ApiReturnConstants.PAGE, p);
		 result.put(ApiReturnConstants.DATA, page.getContent());
		 return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/stutaskdetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "查询实践任务信息详情(实践任务详情)", response = Void.class, notes = "查询实践任务信息详情(实践任务详情)<br><br><b>@author 郑宁</b>")
	public StuTaskDetailForSchoolDomain pageForStuTaskDetail(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "id 实践任务id", required = true) @RequestParam(value = "id", required = true) String id) {
		
		 return taskService.getStuTaskDetail(id);
			
	}
}
