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
import com.aizhixin.cloud.studentpractice.task.domain.WeekTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTask;
import com.aizhixin.cloud.studentpractice.task.entity.ReviewTask;
import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTask;
import com.aizhixin.cloud.studentpractice.task.service.MentorTaskService;
import com.aizhixin.cloud.studentpractice.task.service.ReviewTaskService;
import com.aizhixin.cloud.studentpractice.task.service.StudentTaskService;
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
@RequestMapping("/v1/weektask")
@Api(description = "实践课程(周任务)管理API")
public class WeekTaskController {

	@Autowired
	private WeekTaskService weekTaskService;
	@Autowired
	private AuthUtilService authUtilService;
	
	/**
	 * 创建任务
	 * 
	 * @param domain
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新建实践课程(周任务)", response = Void.class, notes = "新建实践周任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> add(
			@RequestHeader("Authorization") String token,
			//,beginDate:开始时间,endDate:结束时间
			@ApiParam(value = "<b>必填:、</b><br>taskTitle:任务标题,classHour:学时<br><b>选填:、</b>remark:备注") @Valid @RequestBody WeekTaskDomain domain,
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
		
		WeekTask weekTask = weekTaskService.weekTaskSave(domain, dto);
		result.put(ApiReturnConstants.ID, weekTask.getId());
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}


	private void checkData(WeekTaskDomain domain, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(
					PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		if(StringUtils.isEmpty(domain.getTaskTitle())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践课程名称不能为空");
		}
//		if(domain.getBeginDate().getTime() > domain.getEndDate().getTime()){
//			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
//					"实践课程截至时间必须大于等于开始时间");
//		}
//		if(null == domain.getClassHour()){
//			throw new CommonException(ErrorCode.PARAMS_CONFLICT,
//					"学时不能为空");
//		}
	}
	
	
	@RequestMapping(value = "/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改实践周任务", response = Void.class, notes = "修改实践周任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> edit(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>id,weekNo:实践第几周,taskTitle:任务标题,beginDate:开始时间,endDate:结束时间<br><b>选填:、</b>remark:备注") @Valid @RequestBody WeekTaskDomain domain,
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
					"编辑的周任务的id不能为空");
		}
		
		WeekTask weekTask = weekTaskService.findById(domain.getId());
		weekTaskService.weekTaskEdit(domain, weekTask, dto.getId());
		
		result.put(ApiReturnConstants.ID, weekTask.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除实践周任务", response = Void.class, notes = "删除实践周任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> delete(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "id 周任务id", required = true) @RequestParam(value = "id", required = true) String id
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
		
		weekTaskService.deleteWeekTask(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分页查询实践课程信息", response = Void.class, notes = "分页查询实践课程信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> page(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>选填:</b><br>taskName:任务标题<br>pageNumber:第几页<br>pageSize:每页数据的数目<br>sortFlag:排序标识(asc:创建时间升序,desc:创建时间倒序)") @RequestBody QueryTaskPageDomain domain
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
		
		List<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
			List<IdNameDomain> groupList = authUtilService.getMentorGroupInfo(dto.getId());
			if (groupList.size() > 0) {
				String groupId = "";
				for(IdNameDomain groupInfor : groupList){
					if(StringUtils.isEmpty(groupId)){
						groupId = groupInfor.getId().toString();
					}else{
						groupId += ","+groupInfor.getId().toString();
					}
				}
				return new ResponseEntity<Map<String, Object>>(weekTaskService.findPageForMentor(domain, groupId,dto.getId()), HttpStatus.OK);
			}else{
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}
		} else if (roles.contains(RoleCode.ROLE_STUDENT)) {
			StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
			if (null != stuDTO.getTrainingGroupId() && stuDTO.getTrainingGroupId().longValue() > 0L) {
				return new ResponseEntity<Map<String, Object>>(weekTaskService.findPageForStudent(domain, stuDTO.getTrainingGroupId().toString(),dto.getId()), HttpStatus.OK);
			}else{
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}
		}else{//学校教务人员
			//查询结果：课程名称，学时,任务数
			return new ResponseEntity<Map<String, Object>>(weekTaskService.findPage(domain.getPageSize(), domain.getPageNumber(), domain.getTaskName(), dto.getOrgId(),domain.getSortFlag()), HttpStatus.OK);
		}
		
	}
	
	
	@RequestMapping(value = "/notissudepage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分页查询未下发的实践课程信息", response = Void.class, notes = "分页查询未下发的实践课程信息<br><br><b>@author 郑宁</b>")
	public List<WeekTaskDomain> notIssudePage(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>必填:</b><br>groupId:实践计划id<b>选填:</b><br>taskName:任务标题<br>)") @RequestBody QueryTaskPageDomain domain
			) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		if (StringUtils.isEmpty(domain.getGroupId())) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "实践计划Id不能为空");
		}
	
	   return weekTaskService.findNotIssued(domain);
		
	}
	
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "实践周任务详情", response = Void.class, notes = "实践周任务详情<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> detail(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "id 周任务id", required = true) @RequestParam(value = "id", required = true) String id
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
		
		result.put(ApiReturnConstants.DATA, weekTaskService.getDetail(id));
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
