/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.aizhixin.cloud.studentpractice.common.core.PublicErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.RoleCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.MentorTaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryTaskPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.ReviewTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTask;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTaskCount;
import com.aizhixin.cloud.studentpractice.task.entity.ReviewTask;
import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;
import com.aizhixin.cloud.studentpractice.task.service.MentorTaskCountService;
import com.aizhixin.cloud.studentpractice.task.service.MentorTaskService;
import com.aizhixin.cloud.studentpractice.task.service.ReviewTaskService;
import com.aizhixin.cloud.studentpractice.task.service.StudentTaskService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

/**
 * 实践任务
 * 
 * @author 郑宁
 *
 */
@RestController
@RequestMapping("/v1/task")
@Api(description = "导师/学生实践任务管理API")
public class TaskController {

	@Autowired
	private MentorTaskService mentorTaskService;
	@Autowired
	private StudentTaskService stuTaskService;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private ReviewTaskService reviewTaskService;
	@Autowired
	private MentorTaskCountService mentorTaskCountService;
	
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
			@ApiParam(value = "<b>必填:、</b><br>taskName:任务名称、<br><b>老师必填:、</b><br>userList:任务分配人员集合、<br><b>选填:、</b><br>description:任务描述<br><br>deadLine:任务截至时间、<br>") @Valid @RequestBody TaskDomain domain,
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
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(
					PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		
//		if(StringUtils.isEmpty(domain.getWeekTaskId())){
//			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
//					"实践周任务id不能为空");
//		}
		
		MentorTask menTask = null;
		
		List<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
			domain.setMentorId(dto.getId());
			domain.setMentorName(dto.getName());
			
			List<IdNameDomain> groupList = authUtilService.getMentorGroupInfo(dto.getId());
			if (groupList.size() > 0) {
				domain.setGroupId(groupList.get(0).getId());
				domain.setGroupName(groupList.get(0).getName());
			}
			
			if(null != domain.getUserList() && domain.getUserList().size() > 0){
				menTask = mentorTaskService.mentorTaskSave(domain, dto);
			}else{
				throw new CommonException(ErrorCode.ID_IS_REQUIRED,
						"没有任务分配的学生信息");
			}

		} else if (roles.contains(RoleCode.ROLE_STUDENT)) {
			StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
			if (stuDTO != null && !StringUtils.isEmpty(stuDTO.getMentorId())) {
				domain.setMentorId(stuDTO.getMentorId());
				domain.setMentorName(stuDTO.getMentorName());
				domain.setEnterpriseName(stuDTO.getMentorCompanyName());
				domain.setGroupId(stuDTO.getTrainingGroupId());
				domain.setGroupName(stuDTO.getTrainingGroupName());
			} else {
				throw new CommonException(ErrorCode.ID_IS_REQUIRED,
						"未查询到相关学生的企业导师信息");
			}
			
			 menTask = mentorTaskService.mentorTaskSave(domain, dto);
			
		}
		if(null != menTask){
			result.put(ApiReturnConstants.ID, menTask.getId());
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改实践任务", response = Void.class, notes = "修改实践任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> edit(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>id:导师任务id、<br><b>老师选填:、</b><br>userList:任务分配人员集合(人员信息有变化时传递，若无变化无需传值)、<br><b>选填:、</b>taskName:任务名称、<br>description:任务描述<br><br>deadLine:任务截至时间、<br>") @Valid @RequestBody TaskDomain domain,
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
		
		String taskId = mentorTaskService.edit(token, domain, dto);
		
		result.put(ApiReturnConstants.ID, taskId);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}


	
	@RequestMapping(value = "/mentorpage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "导师端任务列表分页查询", response = Void.class, notes = "导师端任务列表分页查询<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> mentorTaskPage(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>选填:</b>weekTaskId:周任务id(为空时查询学生或老师自建任务)<br>taskName:任务名称<br>progress:任务进度(0:未完成[未达到100%],100:已完成[已达到100%])<br>pageNumber:第几页<br>pageSize:每页数据的数目<br>sortFlag:排序标识(asc:创建时间升序,desc:创建时间倒序)") @RequestBody QueryTaskPageDomain domain
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
		
		if(StringUtils.isEmpty(domain.getWeekTaskId())){
			String groupId = "";
			List<String> roles = dto.getRoleNames();
			if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
				List<IdNameDomain> groupList = authUtilService.getMentorGroupInfo(dto.getId());
				if (groupList.size() > 0) {
					for(IdNameDomain groupInfor : groupList){
						if(StringUtils.isEmpty(groupId)){
							groupId = groupInfor.getId().toString();
						}else{
							groupId += ","+groupInfor.getId().toString();
						}
					}
				}
			}
			
			if(StringUtils.isEmpty(groupId)){
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}else{
				return findMentorTaskPageByGroup(domain, result, groupId);
			}
		}
		
		return findMentorTaskPage(domain, result, dto);
	}


	private ResponseEntity<Map<String, Object>> findMentorTaskPage(
			QueryTaskPageDomain domain, Map<String, Object> result,
			AccountDTO dto) {
		PageData pageData = mentorTaskService.findMenTaskPage(domain.getPageSize(), domain.getPageNumber(), domain.getTaskName(), domain.getProgress(), dto.getId(),domain.getWeekTaskId(),domain.getSortFlag());
		List<TaskDomain> dataList = pageData.getData();
		if(null != dataList && !dataList.isEmpty()){
			Set<String> ids = new HashSet<String>();
			for(TaskDomain taskDomain:dataList){
				ids.add(taskDomain.getId());
			}
			HashMap<String,MentorTaskCount> countMap = mentorTaskCountService.findByMentorTaskIds(ids);
			for(TaskDomain taskDomain:dataList){
				if(null != countMap.get(taskDomain.getId())){
					MentorTaskCount countDomain = countMap.get(taskDomain.getId());
					taskDomain.setFinishNum(countDomain.getFinishNum());
					taskDomain.setCheckPendingNum(countDomain.getCheckPendingNum());
				}
			}
		}
		
		PageDomain p = new PageDomain();
		p.setPageNumber(domain.getPageNumber());
		p.setPageSize(domain.getPageSize());
		p.setTotalElements(pageData.getPage().getTotalElements());
		p.setTotalPages(pageData.getPage().getTotalPages());
		result.put(ApiReturnConstants.PAGE, p);
		result.put(ApiReturnConstants.DATA, dataList);
		
		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}


	private ResponseEntity<Map<String, Object>> findMentorTaskPageByGroup(
			QueryTaskPageDomain domain, Map<String, Object> result,
			String groupId) {
		PageData<TaskDomain> pageData = mentorTaskService.findMenTaskPageByGroupId(domain.getPageSize(), domain.getPageNumber(), domain.getTaskName(), domain.getProgress(), groupId,domain.getSortFlag());
		List<TaskDomain> dataList = pageData.getData();
		if(null != dataList && !dataList.isEmpty()){
			Set<String> ids = new HashSet<String>();
			for(TaskDomain taskDomain:dataList){
				ids.add(taskDomain.getId());
			}
			HashMap<String,MentorTaskCount> countMap = mentorTaskCountService.findByMentorTaskIds(ids);
			for(TaskDomain taskDomain:dataList){
				if(null != countMap.get(taskDomain.getId())){
					MentorTaskCount countDomain = countMap.get(taskDomain.getId());
					taskDomain.setFinishNum(countDomain.getFinishNum());
					taskDomain.setCheckPendingNum(countDomain.getCheckPendingNum());
				}
			}
		}
		
		PageDomain p = new PageDomain();
		p.setPageNumber(domain.getPageNumber());
		p.setPageSize(domain.getPageSize());
		p.setTotalElements(pageData.getPage().getTotalElements());
		p.setTotalPages(pageData.getPage().getTotalPages());
		result.put(ApiReturnConstants.PAGE, p);
		result.put(ApiReturnConstants.DATA, dataList);
		
		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/stupage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "学生端任务列表分页查询", response = Void.class, notes = "学生端任务列表分页查询<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> stuTaskPage(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>选填:</b>weekTaskId:周任务id(为空时查询学生或老师自建任务)<br>taskName:任务名称<br>taskStatus:任务状态(uncommit:未提交,checkPending:待审核,finish:已通过,backTo:被打回)[查询多个状态以英文,分割]<br>pageNumber:第几页<br>pageSize:每页数据的数目<br>sortFlag:排序标识(asc:创建时间升序,desc:创建时间倒序)") @RequestBody QueryTaskPageDomain domain
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
		
		if(StringUtils.isEmpty(domain.getWeekTaskId())){
			Long groupId = null;
			List<String> roles = dto.getRoleNames();
			 if (roles.contains(RoleCode.ROLE_STUDENT)) {
					StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
					if(null != stuDTO.getTrainingGroupId()){
						groupId = stuDTO.getTrainingGroupId();
					}
					if(null == groupId){
							return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
					}else{
							return  new ResponseEntity<Map<String, Object>>(stuTaskService.findStuTaskPageByGroupId(domain.getPageSize(), domain.getPageNumber(), domain.getTaskName(), domain.getTaskStatus(), dto.getId(),groupId,domain.getSortFlag()), HttpStatus.OK);
					}
				}
		}
		
		return  new ResponseEntity<Map<String, Object>>(stuTaskService.findStuTaskPage(domain.getPageSize(), domain.getPageNumber(), domain.getTaskName(), domain.getTaskStatus(), dto.getId(),domain.getWeekTaskId(),domain.getSortFlag()), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mentoreditdetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "任务内容", response = Void.class, notes = "任务的名称和描述和学生信息以及附件信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> mentorEditTaskDetail(
			@RequestHeader("Authorization") String token,
            @ApiParam(value = "mentorTaskId 导师任务Id", required = true) @RequestParam(value = "mentorTaskId", required = true) String mentorTaskId
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
		
		MentorTaskDetailDomain domain = mentorTaskService.getMentorEditTaskDetail(mentorTaskId,dto);
		result.put(ApiReturnConstants.DATA, domain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mentorcompletedetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "导师端完成任务详情", response = Void.class, notes = "导师端任务详情(包括导师任务信息，学生任务信息分为已提交和未提交2个集合)<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> mentorCompleteTaskDetail(
			@RequestHeader("Authorization") String token,
            @ApiParam(value = "mentorTaskId 导师任务Id", required = true) @RequestParam(value = "mentorTaskId", required = true) String mentorTaskId
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
		MentorTaskDetailDomain domain = mentorTaskService.getMentorTaskDetail(mentorTaskId);
		result.put(ApiReturnConstants.DATA, domain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "学生提交任务详情", response = Void.class, notes = "学生提交任务详情(包括完成任务信息和评审信息)<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> taskDetail(
			@RequestHeader("Authorization") String token,
            @ApiParam(value = "stuTaskId 学生任务Id", required = true) @RequestParam(value = "stuTaskId", required = true) String stuTaskId
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
		TaskDetailDomain domain = stuTaskService.getTaskDetail(stuTaskId);
		result.put(ApiReturnConstants.DATA, domain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/studetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "学生端任务详情", response = Void.class, notes = "学生端任务详情(包括导师任务信息,学生提交任务信息，审核任务信息)<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> stuTaskDetail(
			@RequestHeader("Authorization") String token,
            @ApiParam(value = "stuTaskId 学生任务Id", required = true) @RequestParam(value = "stuTaskId", required = true) String stuTaskId
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
		StuTaskDetailDomain domain = stuTaskService.getStuTaskDetail(stuTaskId);
		result.put(ApiReturnConstants.DATA, domain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/savecommit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "保存/提交实践任务", response = Void.class, notes = "保存/提交实践任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> saveCommit(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>mentorTaskid:导师任务id、<br><b>选填:、</b><br>resultDescription:任务结果描述、<br><br>StufileList:学生任务附件信息、<br>(2个属性不能同时为空)") @RequestBody StuTaskDomain domain,
			@ApiParam(value = "status (save:保存,commit:提交。为空默认为save)") @RequestParam(value = "status", required = false) String status
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
		//数据为空次数
		int errNum = 0;
		if(StringUtils.isEmpty(domain.getResultDescription())){
			errNum = ++errNum;
		}
		if(null == domain.getStufileList()){
			errNum = ++errNum;
		}else if(domain.getStufileList().size() == 0){
			errNum = ++errNum;
		}
		if(errNum == 2){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未提交做任务的信息");
		}
		
		if(StringUtils.isEmpty(domain.getMentorTaskid())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到导师任务信息");
		}
		domain.setStudentId(dto.getId());
		StudentTask stuTask = stuTaskService.doTask(domain, status, token);
		result.put(ApiReturnConstants.ID, stuTask.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/stuback", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "学生撤回实践任务", response = Void.class, notes = "学生撤回实践任务<br><br><b>@author 郑宁</b>")
	public StudentTask stuBack(
			@RequestHeader("Authorization") String token,
	          @ApiParam(value = "stuTaskId 学生任务Id", required = true) @RequestParam(value = "stuTaskId", required = true) String stuTaskId
				) {
			Map<String, Object> result = new HashMap<String, Object>();
			AccountDTO dto = authUtilService.getSsoUserInfo(token);
			if (null == dto || null == dto.getId()) {
				result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
				result.put(ApiReturnConstants.CODE,
						PublicErrorCode.AUTH_EXCEPTION.getIntValue());
				result.put(ApiReturnConstants.CAUSE, "未授权");
				if (null == dto || null == dto.getId()) {
					throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(),
							"未授权");
				}
			}
			StudentTask studentTask = stuTaskService.findById(stuTaskId);
			if(TaskStatusCode.TASK_STATUS_CHECK_PENDING.equals(studentTask.getStudentTaskStatus())){
				studentTask.setStudentTaskStatus(TaskStatusCode.TASK_STATUS_UNCOMMIT);
				studentTask = stuTaskService.save(studentTask);
			}
			
			return studentTask;
	}
	
	
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除实践任务", response = Void.class, notes = "删除实践任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> delete(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>必填:id(导师任务id)</b>") @RequestBody List<TaskDomain> taskList,
			 @ApiParam(value = "status (false:不强制删除,true:强制删除。为空默认为false)") @RequestParam(value = "status", required = false) boolean status
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
		if(null !=taskList && taskList.size() > 0){
		}else{
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未提供需要删除的导师任务id");
		}
		mentorTaskService.deleteTask(taskList,dto,token,status);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "学生实践任务数量统计", response = Void.class, notes = "学生实践任务数量统计<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> taskCount(
			@RequestHeader("Authorization") String token
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
		result.put(ApiReturnConstants.DATA, stuTaskService.countStuTaskStatusNum(dto.getId()));
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/summarycount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "企业导师实践任务汇总统计", response = Void.class, notes = "企业导师实践任务汇总统计<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> summaryCount(
			@RequestHeader("Authorization") String token
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
		result.put(ApiReturnConstants.DATA, stuTaskService.countTaskStatusNum(dto.getId()));
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/review", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "评审实践任务", response = Void.class, notes = "评审实践任务(评审通过，评审未通过，打回)<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> review(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>studentTaskId:学生任务id、<br>reviewResult:评审结果(finish:完成,backTo:被打回)、<br><b>选填:、</b>reviewScore:评审分数(审核通过和不通过时为必填)、<br><br>reviewAdvice:任务评审意见、<br><br>fileList:评审任务附件信息、<br>") @RequestBody ReviewTaskDomain domain
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
		
		if(StringUtils.isEmpty(domain.getStudentTaskId())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学生实践任务id不能为空");
		}
		if(StringUtils.isEmpty(domain.getReviewResult())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学生实践任务评审结果不能为空");
		}
		if(!TaskStatusCode.TASK_STATUS_BACK_TO.equals(domain.getReviewResult())  && StringUtils.isEmpty(domain.getReviewScore())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学生实践任务评审分数不能为空");
		}
//		domain.setCreatorName(dto.getName());
		ReviewTask reviewTask = reviewTaskService.review(domain,dto.getId(),token);
		result.put(ApiReturnConstants.ID, reviewTask.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/stuinfopage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分页查询导师所带学生信息", response = Void.class, notes = "分页查询导师所带学生信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> stuInfoPage(
			@RequestHeader("Authorization") String token,
//            @ApiParam(value = "stuName 学生名称") @RequestParam(value = "stuName", required = false) String stuName,
//            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
			 @ApiParam(value = "<b>选填:</b><br>stuName:学生名称<br>pageNumber:第几页<br>pageSize:每页数据的数目") @RequestBody QueryStuPageDomain domain
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
		Long mentorId = dto.getId();
		List<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_STUDENT)) {
			StuInforDomain stuInfor =authUtilService.getMentorInfo(dto.getId());
			mentorId = stuInfor.getMentorId();
		}
		
		PageData pageData =authUtilService.getStudentInfo(mentorId, domain.getStuName(), domain.getPageNumber(), domain.getPageSize());
		List<StuInforDomain> stuList = pageData.getData();
		String userIds = "";
		for(StuInforDomain stu :stuList){
			if(StringUtils.isEmpty(userIds)){
				userIds += stu.getId().toString();
			}else{
				userIds += ","+stu.getId().toString();
			}
		}
		HashMap<Long, AccountDTO> avatarList =authUtilService.getavatarUsersInfo(userIds);
		for(StuInforDomain stu :stuList){
			if(null != avatarList.get(stu.getId())){
				stu.setAvatar(avatarList.get(stu.getId()).getAvatar());
			}
		}
		result.put(ApiReturnConstants.PAGE, pageData.getPage());
		result.put(ApiReturnConstants.DATA, stuList);
		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/stuinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "学生和导师基本信息", response = Void.class, notes = "学生和导师基本信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> stuInfo(
			@RequestHeader("Authorization") String token,
            @ApiParam(value = "stuId 学生id") @RequestParam(value = "stuId", required = false) Long stuId
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
		Long studentId = 0L;
		if(null != stuId && stuId.longValue() > 0){
			studentId = stuId;
		}else{
			studentId = dto.getId();
		}
		StuInforDomain stuInfor =authUtilService.getMentorInfo(studentId);
		result.put(ApiReturnConstants.DATA, stuInfor);
		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	/**
	 * 查询学生是否分配有企业导师
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/isassignmentor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "学生是否分配有企业导师", response = Void.class, notes = "学生是否分配有企业导师<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> isAssignMentor(
			@RequestHeader("Authorization") String token) {
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
		
		boolean status = false;
		
		List<String> roles = dto.getRoleNames();
		 if (roles.contains(RoleCode.ROLE_STUDENT)) {
			StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
			if (stuDTO != null && !StringUtils.isEmpty(stuDTO.getMentorId())) {
				status = true;
				result.put("groupId", stuDTO.getTrainingGroupId());
			} 
		 }
		 result.put("status", status);
			
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
