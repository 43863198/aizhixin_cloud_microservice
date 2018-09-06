package com.aizhixin.cloud.studentpractice.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.aizhixin.cloud.studentpractice.task.entity.*;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.core.ErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.PushMessageConstants;
import com.aizhixin.cloud.studentpractice.common.core.RoleCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageDTO;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.PushMessageService;
import com.aizhixin.cloud.studentpractice.common.service.PushService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.task.core.FileCode;
import com.aizhixin.cloud.studentpractice.task.core.MessageCode;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
import com.aizhixin.cloud.studentpractice.task.domain.MentorTaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.PracticeTaskForSchoolDomain;
import com.aizhixin.cloud.studentpractice.task.domain.ReviewTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.WeekTaskDomain;
import com.aizhixin.cloud.studentpractice.task.repository.MentorTaskRepository;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


@Service
//@Transactional
public class MentorTaskService {

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private MentorTaskRepository mentorTaskRepository;
	@Autowired
	private StudentTaskService studentTaskService;
	@Autowired
	private TaskFileService taskFileService;
	@Autowired
	private FileService fileService;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private PushService pushService;
	@Autowired
	@Lazy
	private WeekTaskService weekTaskService;
	@Autowired
	private TaskService taskService;
	@Autowired
	@Lazy
	private PushMessageService pushMessageService;

	public MentorTask save(MentorTask menTask) {
		return mentorTaskRepository.save(menTask);
	}

	public MentorTask findById(String id) {
		return mentorTaskRepository.findOneByDeleteFlagAndId(
				DataValidity.VALID.getIntValue(), id);
	}

	public void logicDeleteTask(String[] ids, Long userId) {
		mentorTaskRepository.logicDeleteByIds(ids, userId);
	}
	
	public List<MentorTask> getTaskListByIds(String[] ids){
		return mentorTaskRepository.findAllByIds(ids);
	}
	
	public Long countByWeekTaskId(String weekTaskId){
		return mentorTaskRepository.countByDeleteFlagAndWeekTaskId(DataValidity.VALID.getIntValue(), weekTaskId);
	}
	
	public void updateTimeById(Date beginDate,Date deadLine,String id){
		mentorTaskRepository.updateTimeById(beginDate, deadLine, id);
	}

	@Transactional
	public MentorTask mentorTaskSave(TaskDomain domain, AccountDTO dto) {
        String imgJson=null;
		if (!domain.getImageList().isEmpty()){
			imgJson=JSON.toJSONString(domain.getImageList());
		}
		MentorTask menTask = new MentorTask();
		BeanUtils.copyProperties(domain, menTask);
		String menTaskId = UUID.randomUUID().toString();
		menTask.setId(menTaskId);
		menTask.setCreatedBy(dto.getId());
		menTask.setCreatorName(dto.getName());
		menTask.setProgress("0");
		menTask.setWeekTaskId(domain.getWeekTaskId());
		menTask.setDescribe(domain.getDescribe());
		menTask.setGroupId(domain.getGroupId());
		menTask.setGroupName(domain.getGroupName());
		if (!org.springframework.util.StringUtils.isEmpty(imgJson)){
			menTask.setImgList(imgJson);
		}
		
		List<Long> ids = new ArrayList<Long>();
		List<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
			menTask.setCreatorRole(RoleCode.ROLE_COM_TEACHER);
		}else if(roles.contains(RoleCode.ROLE_STUDENT)) {
			menTask.setCreatorRole(RoleCode.ROLE_STUDENT);
		}else{
			menTask.setCreatorRole(RoleCode.ROLE_TEACHER);
		}

		menTask = save(menTask);
		List<StudentTask> stuTaskLs = new ArrayList<StudentTask>();
		
		HashMap<Long,String> stuTaskIdMap = new HashMap<Long,String>();
		for (StuInforDomain stu : domain.getUserList()) {
			StudentTask stuTask = new StudentTask();
			
			String stuTaskId = UUID.randomUUID().toString();
			stuTask.setId(stuTaskId);
			
			stuTaskIdMap.put(stu.getId(), stuTaskId);
			
			stuTask.setMentorTaskId(menTaskId);
			stuTask.setMentorId(menTask.getMentorId());
			stuTask.setStudentId(stu.getId());
			stuTask.setStudentName(stu.getName());
			stuTask.setJobNum(stu.getJobNum());
			stuTask.setClassId(stu.getClassId());
			stuTask.setCollegeId(stu.getCollegeId());
			stuTask.setProfessionalId(stu.getProfessionalId());
			stuTask.setEnterpriseName(stu.getMentorCompanyName());
			stuTask.setOrgId(stu.getOrgId());
			stuTask.setCreatedBy(dto.getId());
			stuTask.setStudentTaskStatus(TaskStatusCode.TASK_STATUS_UNCOMMIT);
			stuTask.setWeekTaskId(domain.getWeekTaskId());
			stuTask.setGroupId(domain.getGroupId());
			stuTaskLs.add(stuTask);
			
			if(dto.getId().longValue() != stu.getId().longValue()){
				ids.add(stu.getId());
				stuTaskIdMap.put(stuTask.getStudentId(), stuTask.getId());
			}
		}
		studentTaskService.saveList(stuTaskLs);

		// 保存创建任务上传附件
		if (null != domain.getFileList() && domain.getFileList().size() > 0) {
			List<File> fileList = new ArrayList<File>();
			for (FileDomain fileDomain : domain.getFileList()) {
				File file = new File();
				String fileId = UUID.randomUUID().toString();
				file.setId(fileId);
				file.setFileName(fileDomain.getFileName());
				file.setSrcUrl(fileDomain.getSrcUrl());
				file.setSourceId(menTaskId);
				file.setCreatedBy(dto.getId());
				fileList.add(file);
			}
			fileService.saveList(fileList);
		}
		
         	PushMessageDTO msg = new PushMessageDTO();
         	
         	msg.setTaskName(domain.getTaskName());
         	if(!StringUtils.isEmpty(domain.getWeekTaskId())){
         		msg.setWeekTaskId(domain.getWeekTaskId());
         		WeekTask weekTask = weekTaskService.findById(domain.getWeekTaskId());
         		msg.setWeekTaskName(weekTask.getTaskTitle());
         	}
         	msg.setStuTaskStatus(TaskStatusCode.TASK_STATUS_UNCOMMIT);
         	msg.setMentorTaskId(menTaskId);
         	msg.setStuTaskIdMap(stuTaskIdMap);
         	
         	msg.setBusinessContent(menTaskId);
         	if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
         		msg.setContent(MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_MENTOR_ADD_TASK));
         	}else{
         		ids.add(menTask.getMentorId());
         		msg.setContent(MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_STUDENT_ADD_TASK).replace("{1}", dto.getName()));
         	}
			msg.setFunction(PushMessageConstants.MODULE_TASK);
			msg.setModule(PushMessageConstants.MODULE_TASK);
			msg.setTitle(MessageCode.MESSAGE_TITLE_ADD_TASK);
			msg.setUserIds(ids);
			pushService.addPushList(msg);

		return menTask;
	}
	
	
	@Transactional
	public void mentorTaskListSave(List<TaskDomain> domainList, AccountDTO dto) {

		for(TaskDomain domain : domainList){
			MentorTask menTask = new MentorTask();
			BeanUtils.copyProperties(domain, menTask);
			String menTaskId = UUID.randomUUID().toString();
			menTask.setId(menTaskId);
			menTask.setCreatedBy(dto.getId());
			if(StringUtils.isBlank(dto.getName())){
				menTask.setCreatorName(dto.getLogin());
			}else{
				menTask.setCreatorName(dto.getName());
			}
			menTask.setProgress("0");
			menTask.setWeekTaskId(domain.getWeekTaskId());
			menTask.setTaskId(domain.getTaskId());
			menTask.setGroupId(domain.getGroupId());
			menTask.setGroupName(domain.getGroupName());
			menTask.setDescribe(domain.getDescribe());
			List<String> roles = dto.getRoleNames();
			if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
				menTask.setCreatorRole(RoleCode.ROLE_COM_TEACHER);
			}else if(roles.contains(RoleCode.ROLE_STUDENT)) {
				menTask.setCreatorRole(RoleCode.ROLE_STUDENT);
			}else{
				menTask.setCreatorRole(RoleCode.ROLE_TEACHER);
			}
			menTask = save(menTask);
	
			List<StudentTask> stuTaskLs = new ArrayList<StudentTask>();
			List<Long> ids = new ArrayList<Long>();
			for (StuInforDomain stu : domain.getUserList()) {
				StudentTask stuTask = new StudentTask();
				stuTask.setId(UUID.randomUUID().toString());
				stuTask.setMentorTaskId(menTaskId);
				stuTask.setMentorId(menTask.getMentorId());
				stuTask.setStudentId(stu.getId());
				stuTask.setStudentName(stu.getName());
				stuTask.setJobNum(stu.getJobNum());
				stuTask.setClassId(stu.getClassId());
				stuTask.setCollegeId(stu.getCollegeId());
				stuTask.setProfessionalId(stu.getProfessionalId());
				stuTask.setEnterpriseName(stu.getMentorCompanyName());
				stuTask.setOrgId(stu.getOrgId());
				stuTask.setCreatedBy(dto.getId());
				stuTask.setStudentTaskStatus(TaskStatusCode.TASK_STATUS_UNCOMMIT);
				stuTask.setWeekTaskId(domain.getWeekTaskId());
				stuTask.setGroupId(domain.getGroupId());
				stuTask.setGroupName(stu.getTrainingGroupName());
				stuTaskLs.add(stuTask);
				ids.add(stu.getId());
			}
			studentTaskService.saveList(stuTaskLs);
		}
	}

	/**
	 * 学生创建实践任务
	 * 
	 * @param domain
	 * @param dto
	 * @return
	 */
//	@Transactional
//	public MentorTask studentSave(TaskDomain domain, AccountDTO dto, String token) {
//
//		MentorTask menTask = new MentorTask();
//		BeanUtils.copyProperties(domain, menTask);
//		String menTaskId = UUID.randomUUID().toString();
//		menTask.setId(menTaskId);
//		menTask.setCreatedBy(dto.getId());
//		menTask.setProgress("0");
//		menTask.setCreatorName(dto.getName());
//		menTask.setWeekTaskId(domain.getWeekTaskId());
//
//		menTask = save(menTask);
//
//		StudentTask stuTask = new StudentTask();
//		stuTask.setId(UUID.randomUUID().toString());
//		stuTask.setMentorTaskId(menTaskId);
//		stuTask.setMentorId(menTask.getMentorId());
//		stuTask.setStudentId(dto.getId());
//		stuTask.setStudentName(dto.getName());
//		stuTask.setClassId(dto.getClassId());
//		stuTask.setCollegeId(dto.getCollegeId());
//		stuTask.setProfessionalId(dto.getMajorId());
//		stuTask.setOrgId(dto.getOrgId());
//		stuTask.setJobNum(dto.getWorkNo());
//		stuTask.setEnterpriseName(domain.getEnterpriseName());
//		stuTask.setCreatedBy(dto.getId());
//		stuTask.setStudentTaskStatus(TaskStatusCode.TASK_STATUS_UNCOMMIT);
//		stuTask.setWeekTaskId(domain.getWeekTaskId());
//		studentTaskService.save(stuTask);
//
//		// 保存创建任务上传附件
//		if (null != domain.getFileList() && domain.getFileList().size() > 0) {
//			List<File> fileList = new ArrayList<File>();
//			for (FileDomain fileDomain : domain.getFileList()) {
//				File file = new File();
//				String fileId = UUID.randomUUID().toString();
//				file.setId(fileId);
//				file.setFileName(fileDomain.getFileName());
//				file.setSrcUrl(fileDomain.getSrcUrl());
//				file.setSourceId(menTaskId);
//				file.setCreatedBy(dto.getId());
//				fileList.add(file);
//			}
//			fileService.saveList(fileList);
//		}
//		
//		List<Long> ids = new ArrayList<Long>();
//		ids.add(menTask.getMentorId());
//		 pushMessageService.createPushMessage(menTaskId, MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_STUDENT_ADD_TASK.replace("{1}", dto.getName())),
//                 PushMessageConstants.FUNCTION_STUDENT_NOTICE,
//                 PushMessageConstants.MODULE_TASK, MessageCode.MESSAGE_TITLE_ADD_TASK,
//                 ids);
//         pushService.listPush(token, MessageCode.MESSAGE_ADD_TASK_PUSH, MessageCode.MESSAGE_TITLE_ADD_TASK, MessageCode.MESSAGE_TITLE_ADD_TASK,
//                 ids);
//
//		return menTask;
//	}
	
	public String edit(String token, TaskDomain domain, AccountDTO dto) {
		String imgJson=null;
		if (!domain.getImageList().isEmpty()){
			imgJson=JSON.toJSONString(domain.getImageList());
		}
		if(StringUtils.isEmpty(domain.getId())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "导师任务id不能为空");
		}
		MentorTask task = findById(domain.getId());
		if(null == task){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到导师任务信息");
		}

		if (!StringUtils.isEmpty(domain.getTaskName())) {
			task.setTaskName(domain.getTaskName());
		}

		if (!StringUtils.isEmpty(domain.getDescription())) {
			task.setDescription(domain.getDescription());
		} else {
			if (!StringUtils.isEmpty(task.getDescription())) {
				task.setDescription(null);
			}
		}

		if (null != domain.getDeadLine()) {
			task.setDeadLine(domain.getDeadLine());
		} else {
			if (null != task.getDeadLine()) {
				task.setDeadLine(null);
			}
		}
		task.setImgList(imgJson);
		task.setDescribe(domain.getDescribe());
		if (!org.springframework.util.StringUtils.isEmpty(task.getTaskId())){
	       Task t=taskService.findOne(task.getTaskId());
	       if (null!=t){
			   t.setDescribe(domain.getDescribe());
			   taskService.save(t);
		   }
		}
		
		save(task);
		// 删除之前上传附件
		if(StringUtils.isBlank(task.getWeekTaskId())){
			fileService.deleteBySourceId(task.getId());
		}else{
			fileService.deleteBySourceId(task.getTaskId());
		}
		// 保存创建任务上传附件
		if (null != domain.getFileList() && domain.getFileList().size() > 0) {
			List<File> fileList = new ArrayList<File>();
			for (FileDomain fileDomain : domain.getFileList()) {
				File file = new File();
				String fileId = UUID.randomUUID().toString();
				file.setId(fileId);
				file.setFileName(fileDomain.getFileName());
				file.setSrcUrl(fileDomain.getSrcUrl());
				if(StringUtils.isBlank(task.getWeekTaskId())){
					file.setSourceId(task.getId());
				}else{
					file.setSourceId(task.getTaskId());
				}
				file.setCreatedBy(dto.getId());
				fileList.add(file);
			}
			fileService.saveList(fileList);
		}
		
		
		//当前操作用户是否是任务创建者
		boolean isCreator = false;
		if(dto.getId().longValue() == task.getCreatedBy().longValue()){
			isCreator = true;
		}
		List<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
//			String id = task.getId();
//			id = "'"+id+"'";
//			Long commitNum = this.getCommitTaskNum(null, id);
//			if(null != commitNum && commitNum.longValue() > 0L){
//				throw new CommonException(ErrorCode.ID_IS_REQUIRED, "只有未提交的任务信息可以编辑");
//			}
			
			mentorEdit(domain, dto, task, token,roles);
			
		} else if (roles.contains(RoleCode.ROLE_STUDENT)) {
			if(isCreator){
//				String id = task.getId();
//				id = "'"+id+"'";
//				Long commitNum = this.getCommitTaskNum(dto.getId(),id);
//				if(null != commitNum && commitNum.longValue() > 0L){
//					throw new CommonException(ErrorCode.ID_IS_REQUIRED, "只有未提交的任务信息可以编辑");
//				}
				 this.mentorEdit(domain, dto, task, token,roles);
			}else{
				throw new CommonException(ErrorCode.PARAMS_CONFLICT, "没有修改该导师任务的权限");
			}
		}
		return task.getId();
	}

	public void mentorEdit(TaskDomain domain, AccountDTO dto,
			MentorTask menTask,String token,List<String> roles) {


		List<Long> ids = new ArrayList<Long>();
		if (null != domain.getUserList() && domain.getUserList().size() > 0) {
			// 删除之前学生任务
			studentTaskService.logicDeleteTask(
					new String[] { menTask.getId() }, dto.getId());

			List<StudentTask> stuTaskLs = new ArrayList<StudentTask>();
			HashMap<Long,String> stuTaskIdMap = new HashMap<Long,String>();
			for (StuInforDomain stu : domain.getUserList()) {
				StudentTask stuTask = new StudentTask();
				stuTask.setId(UUID.randomUUID().toString());
				stuTask.setMentorTaskId(menTask.getId());
				if(null != domain.getMentorId() && domain.getMentorId().longValue() > 0L){
					stuTask.setMentorId(domain.getMentorId());
				}else{
					stuTask.setMentorId(menTask.getMentorId());
				}
				stuTask.setStudentId(stu.getId());
				stuTask.setStudentName(stu.getName());
				stuTask.setJobNum(stu.getJobNum());
				stuTask.setClassId(stu.getClassId());
				stuTask.setCollegeId(stu.getCollegeId());
				stuTask.setProfessionalId(stu.getProfessionalId());
				stuTask.setEnterpriseName(stu.getMentorCompanyName());
				stuTask.setWeekTaskId(menTask.getWeekTaskId());
				stuTask.setOrgId(stu.getOrgId());
				stuTask.setCreatedBy(dto.getId());
				stuTask.setStudentTaskStatus(TaskStatusCode.TASK_STATUS_UNCOMMIT);
				stuTaskLs.add(stuTask);
				
				if(dto.getId().longValue() != stuTask.getCreatedBy().longValue()){
					ids.add(stu.getId());
					stuTaskIdMap.put(stuTask.getStudentId(), stuTask.getId());
				}
			}
			studentTaskService.saveList(stuTaskLs);
			
		    
	     	PushMessageDTO msg = new PushMessageDTO();
		 	
		 	msg.setTaskName(menTask.getTaskName());
	     	if(!StringUtils.isEmpty(menTask.getWeekTaskId())){
	     		msg.setWeekTaskId(menTask.getWeekTaskId());
	     		WeekTask weekTask = weekTaskService.findById(menTask.getWeekTaskId());
	     		msg.setWeekTaskName(weekTask.getTaskTitle());
	     	}
	     	msg.setStuTaskStatus(TaskStatusCode.TASK_STATUS_UNCOMMIT);
	     	msg.setMentorTaskId(menTask.getId());
	     	msg.setStuTaskIdMap(stuTaskIdMap);
		 	
	     	msg.setBusinessContent(menTask.getId());
	     	if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
	     		msg.setContent(MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_MENTOR_EDIT_TASK.replace("{1}", menTask.getTaskName())));
	     	}else{
	     		ids.add(menTask.getMentorId());
	     		msg.setContent(MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_STUDENT_EDIT_TASK.replace("{1}", dto.getName()).replace("{2}", menTask.getTaskName())));
	     	}
			msg.setFunction(PushMessageConstants.MODULE_TASK);
			msg.setModule(PushMessageConstants.MODULE_TASK);
			msg.setTitle(MessageCode.MESSAGE_TITLE_EDIT_TASK);
			msg.setUserIds(ids);
			pushService.addPushList(msg);
		}
	}


	/**
	 * 删除实践任务
	 * 
	 * @param taskIds
	 */
	@Transactional
	public void deleteTask(List<TaskDomain> taskList, AccountDTO dto,String token, boolean status) {

		String ids = "";
		String[] idArr = new String[taskList.size()];
		
		for (int i= 0;i<taskList.size();i++) {
			TaskDomain task = taskList.get(i);
			if (!StringUtils.isEmpty(task.getId())) {
				if (StringUtils.isEmpty(ids)) {
					ids += "'" + task.getId() + "'";
				} else {
					ids += ",'" + task.getId() + "'";
				}
				idArr[i] = task.getId();
			}
		}
		
		if(StringUtils.isEmpty(ids)){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未提供需要删除的导师任务id");
		}
		
		if(!status){
			Long countNum = getCommitTaskNum(dto.getId(), ids);
			if (countNum.longValue() > 0L) {
				throw new CommonException(ErrorCode.DELETE_CONFLICT,
						"只有未提交的任务信息可以删除");
			}
		}

		fileService.logicDeleteBySourceIds(idArr, dto.getId());
		studentTaskService.logicDeleteTask(idArr, dto.getId());
		logicDeleteTask(idArr, dto.getId());
		
	
		pushDelMessage(dto, idArr);

	}

	private void pushDelMessage(AccountDTO dto,String[] idArr) {
		
	
		List<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
			List<MentorTask> mentorTaskList = this.getTaskListByIds(idArr);
			pushMessageService.deleteAll(idArr);
			for(MentorTask menTask : mentorTaskList){
				List<Long> userIds = new ArrayList<Long>();
				List<StudentTask> stuTaskList = studentTaskService.findAllByMentorTaskId(menTask.getId());
				HashMap<Long,String> stuTaskIdMap = new HashMap<Long,String>();
				for(StudentTask stuTask : stuTaskList){
					userIds.add(stuTask.getStudentId());
					stuTaskIdMap.put(stuTask.getStudentId(), stuTask.getId());
				}
			 	PushMessageDTO msg = new PushMessageDTO();
			 	
			 	msg.setTaskName(menTask.getTaskName());
	         	if(!StringUtils.isEmpty(menTask.getWeekTaskId())){
	         		msg.setWeekTaskId(menTask.getWeekTaskId());
	         		WeekTask weekTask = weekTaskService.findById(menTask.getWeekTaskId());
	         		msg.setWeekTaskName(weekTask.getTaskTitle());
	         	}
	         	msg.setMentorTaskId(menTask.getId());
	         	msg.setStuTaskIdMap(stuTaskIdMap);
			 	
	         	msg.setBusinessContent(menTask.getId());
	         	msg.setContent( MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_MENTOR_DEL_TASK.replace("{1}", menTask.getTaskName())));
				msg.setFunction(PushMessageConstants.MODULE_TASK);
				msg.setModule(PushMessageConstants.MODULE_TASK);
				msg.setTitle(MessageCode.MESSAGE_TITLE_DEL_TASK);
				msg.setUserIds(userIds);
				pushService.addPushList(msg);
			}
		}else if (roles.contains(RoleCode.ROLE_STUDENT)) {
			List<MentorTask> mentorTaskList = this.getTaskListByIds(idArr);
			for(MentorTask menTask : mentorTaskList){
				List<Long> userIds = new ArrayList<Long>();
				userIds.add(menTask.getMentorId());
		         
				List<StudentTask> stuTaskList = studentTaskService.findAllByMentorTaskId(menTask.getId());
				HashMap<Long,String> stuTaskIdMap = new HashMap<Long,String>();
				for(StudentTask stuTask : stuTaskList){
					if(dto.getId().longValue() != stuTask.getCreatedBy().longValue()){
					userIds.add(stuTask.getStudentId());
					stuTaskIdMap.put(stuTask.getStudentId(), stuTask.getId());
					}
				}
				
		         PushMessageDTO msg = new PushMessageDTO();
		         
		     		msg.setTaskName(menTask.getTaskName());
		         	if(!StringUtils.isEmpty(menTask.getWeekTaskId())){
		         		msg.setWeekTaskId(menTask.getWeekTaskId());
		         		WeekTask weekTask = weekTaskService.findById(menTask.getWeekTaskId());
		         		msg.setWeekTaskName(weekTask.getTaskTitle());
		         	}
		         
		         	msg.setMentorTaskId(menTask.getId());
		         	msg.setBusinessContent(menTask.getId());
					msg.setContent( MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_STUDENT_DEL_TASK.replace("{1}", dto.getName()).replace("{2}", menTask.getTaskName())));
					msg.setFunction(PushMessageConstants.MODULE_TASK);
					msg.setModule(PushMessageConstants.MODULE_TASK);
					msg.setTitle(MessageCode.MESSAGE_TITLE_DEL_TASK);
					msg.setUserIds(userIds);
					pushService.addPushList(msg);
			}
		}
	}

	public Long getCommitTaskNum(Long userId, String ids) {
		String sql = "SELECT count(1) FROM `sp_student_task` st where st.DELETE_FLAG ="
				+ DataValidity.VALID.getIntValue()
				+ " and st.STUDENT_TASK_STATUS <> '"
				+ TaskStatusCode.TASK_STATUS_UNCOMMIT
				+ "' AND st.MENTOR_TASK_ID IN (" + ids + ")";
		if (null != userId && userId.longValue() > 0L) {
			sql += "  and st.CREATED_BY =" + userId + "";
		}
		Long countNum = pageJdbcUtil.getCount(sql);
		return countNum;
	}

	RowMapper<TaskDomain> menRm = new RowMapper<TaskDomain>() {

		@Override
		public TaskDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			TaskDomain task = new TaskDomain();
			task.setId(rs.getString("ID"));
			task.setTaskName(rs.getString("TASK_NAME"));
			task.setCreatedBy(rs.getLong("CREATED_BY"));
			task.setDeadLine(rs.getDate("DEAD_LINE"));
			task.setProgress(rs.getString("PROGRESS"));
			task.setScoreNum(rs.getInt("score_num"));
			task.setClassHour(rs.getInt("class_hour"));
			task.setDescription(rs.getString("DESCRIPTION"));
			return task;
		}
	};

	public PageData<TaskDomain>  findMenTaskPage(Integer pageSize,
			Integer offset, String taskName, String progress, Long mentorId,String weekTaskId,String sortFlag) {

		String querySql = "select mt.ID,mt.TASK_NAME,mt.DESCRIPTION,mt.class_hour,mt.CREATED_BY,mt.CREATED_DATE,mt.DEAD_LINE,mt.PROGRESS,mt.score_num from `sp_mentor_task` mt where mt.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `sp_mentor_task` mt where mt.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(taskName)) {
			querySql += " and mt.TASK_NAME like '%" + taskName + "%'";
			countSql += " and mt.TASK_NAME like '%" + taskName + "%'";
		}
		if (!StringUtils.isEmpty(progress)) {
			if ("100".equals(progress)) {
				querySql += " and mt.PROGRESS = '" + 1.0 + "' ";
				countSql += " and mt.PROGRESS = '" + 1.0 + "' ";
			} else {
				querySql += " and mt.PROGRESS <> '" + 1.0 + "' ";
				countSql += " and mt.PROGRESS <> '" + 1.0 + "' ";
			}
		}
		querySql += " and mt.MENTOR_ID =" + mentorId;
		countSql += " and mt.MENTOR_ID =" + mentorId;
		querySql += " and mt.WEEK_TASK_ID ='"+weekTaskId+"'";
		countSql += " and mt.WEEK_TASK_ID ='"+weekTaskId+"'";
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("mt.CREATED_DATE");
		if("asc".equals(sortFlag)){
			dto.setAsc(true);
		}else{
			dto.setAsc(false);
		}
		sort.add(dto);

		return pageJdbcUtil.getPageData(pageSize, offset, menRm, sort,
				querySql, countSql);
	}
	
	
	public PageData<TaskDomain>  findMenTaskPageByGroupId(Integer pageSize,
			Integer offset, String taskName, String progress,String groupId,String sortFlag) {

		String querySql = "select mt.ID,mt.TASK_NAME,mt.DESCRIPTION,mt.class_hour,mt.CREATED_BY,mt.CREATED_DATE,mt.DEAD_LINE,mt.PROGRESS,mt.score_num from `sp_mentor_task` mt where mt.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `sp_mentor_task` mt where mt.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(taskName)) {
			querySql += " and mt.TASK_NAME like '%" + taskName + "%'";
			countSql += " and mt.TASK_NAME like '%" + taskName + "%'";
		}
		if (!StringUtils.isEmpty(progress)) {
			if ("100".equals(progress)) {
				querySql += " and mt.PROGRESS = '" + 1.0 + "' ";
				countSql += " and mt.PROGRESS = '" + 1.0 + "' ";
			} else {
				querySql += " and mt.PROGRESS <> '" + 1.0 + "' ";
				countSql += " and mt.PROGRESS <> '" + 1.0 + "' ";
			}
		}
//		querySql += " and mt.MENTOR_ID =" + mentorId;
//		countSql += " and mt.MENTOR_ID =" + mentorId;
		String roleStr = "'"+RoleCode.ROLE_STUDENT+"','"+RoleCode.ROLE_COM_TEACHER+"'";
		querySql += " and mt.creator_role in (" + roleStr + ")";
		countSql += " and mt.creator_role in (" + roleStr + ")";
		querySql += " and mt.GROUP_ID in ("+groupId+")";
		countSql += " and mt.GROUP_ID in ("+groupId+")";
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("mt.CREATED_DATE");
		if("asc".equals(sortFlag)){
			dto.setAsc(true);
		}else{
			dto.setAsc(false);
		}
		sort.add(dto);

		return pageJdbcUtil.getPageData(pageSize, offset, menRm, sort,
				querySql, countSql);
	}

	public MentorTaskDetailDomain getMentorTaskDetail(String mentorTaskId) {

		MentorTask mentorTask = findById(mentorTaskId);
		if (null == mentorTask) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到相关导师任务信息");
		}
		MentorTaskDetailDomain menTaskDomain = new MentorTaskDetailDomain();

		// 导师任务详情信息
		BeanUtils.copyProperties(mentorTask, menTaskDomain);
		if (!org.springframework.util.StringUtils.isEmpty(mentorTask.getImgList())){
			List<String> imgList=JSON.parseArray(mentorTask.getImgList(),String.class);
			menTaskDomain.setImgList(imgList);
		}
		menTaskDomain.setDescribe(mentorTask.getDescribe());
		List<File> fileList = null;
		if(StringUtils.isEmpty(mentorTask.getWeekTaskId())){ 
			fileList = fileService.findAllByDeleteFlagAndSourceId(
					DataValidity.VALID.getIntValue(), mentorTask.getId());
		}else{
			fileList = fileService.findAllByDeleteFlagAndSourceId(
					DataValidity.VALID.getIntValue(), mentorTask.getTaskId());
		}
		if (null != fileList && fileList.size() > 0) {
			ArrayList<FileDomain> fileDomainList = new ArrayList<FileDomain>();
			for (File file : fileList) {
				FileDomain fileDomain = new FileDomain();
				BeanUtils.copyProperties(file, fileDomain);
				fileDomainList.add(fileDomain);
			}
			menTaskDomain.setFileList(fileDomainList);
		}

		// 学生任务详情信息
		List<StudentTask> stuTaskList = studentTaskService
				.findListByMentorTaskId(mentorTaskId);
		if (null != stuTaskList && stuTaskList.size() > 0) {
			List<StuTaskDomain> uncommitTaskList = new ArrayList<StuTaskDomain>();
			List<StuTaskDomain> commitTaskList = new ArrayList<StuTaskDomain>();
			List<StuInforDomain> stuInforList = new ArrayList<StuInforDomain>();
			String userIds = "";
			for (StudentTask stuTask : stuTaskList) {
				StuInforDomain stuDomain = new StuInforDomain();
				stuDomain.setId(stuTask.getStudentId());
				stuDomain.setJobNum(stuTask.getJobNum());
				stuInforList.add(stuDomain);
				if (StringUtils.isEmpty(userIds)) {
					userIds += stuTask.getStudentId().toString();
				} else {
					userIds += "," + stuTask.getStudentId().toString();
				}
				
				StuTaskDomain stuTaskDomain = new StuTaskDomain();
				BeanUtils.copyProperties(stuTask, stuTaskDomain);
				stuTaskDomain.setStuTaskid(stuTask.getId());
				if (TaskStatusCode.TASK_STATUS_UNCOMMIT.equals(stuTask
						.getStudentTaskStatus())) {
					uncommitTaskList.add(stuTaskDomain);
				} else {
					commitTaskList.add(stuTaskDomain);
				}
			}
			HashMap<Long, AccountDTO> avatarList = authUtilService
					.getavatarUsersInfo(userIds);
			for (StuInforDomain stu : stuInforList) {
				if (null != avatarList.get(stu.getId())) {
					stu.setAvatar(avatarList.get(stu.getId()).getAvatar());
					stu.setName(avatarList.get(stu.getId()).getName());
				}
			}
			for (StuTaskDomain stuTask : commitTaskList) {
				if (null != avatarList.get(stuTask.getStudentId())) {
					stuTask.setStudentName(avatarList.get(stuTask.getStudentId()).getName());
				}
			}
			for (StuTaskDomain stuTask : uncommitTaskList) {
				if (null != avatarList.get(stuTask.getStudentId())) {
					stuTask.setStudentName(avatarList.get(stuTask.getStudentId()).getName());
				}
			}
			menTaskDomain.setStudentList(stuInforList);
			menTaskDomain.setCommitTaskList(commitTaskList);
			menTaskDomain.setUncommitTaskList(uncommitTaskList);
		} else {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到相关学生任务信息");
		}
		
		return menTaskDomain;
	}

	public MentorTaskDetailDomain getMentorEditTaskDetail(String mentorTaskId,
			AccountDTO dto) {

		MentorTask mentorTask = findById(mentorTaskId);
		if (null == mentorTask) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到相关导师任务信息");
		}
		MentorTaskDetailDomain menTaskDomain = new MentorTaskDetailDomain();
		menTaskDomain.setCommitTaskList(null);
		menTaskDomain.setUncommitTaskList(null);

		// 导师任务详情信息
		BeanUtils.copyProperties(mentorTask, menTaskDomain);
		List<File> fileList = null;
		if(StringUtils.isEmpty(mentorTask.getWeekTaskId())){ 
			fileList = fileService.findAllByDeleteFlagAndSourceId(
					DataValidity.VALID.getIntValue(), mentorTask.getId());
		}else{
			fileList = fileService.findAllByDeleteFlagAndSourceId(
					DataValidity.VALID.getIntValue(), mentorTask.getTaskId());
		}
		if (null != fileList && fileList.size() > 0) {
			ArrayList<FileDomain> fileDomainList = new ArrayList<FileDomain>();
			for (File file : fileList) {
				FileDomain fileDomain = new FileDomain();
				BeanUtils.copyProperties(file, fileDomain);
				fileDomainList.add(fileDomain);
			}
			menTaskDomain.setFileList(fileDomainList);
		} else {
			menTaskDomain.setFileList(null);
		}

//		List<String> roles = dto.getRoleNames();
//		if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
			// 学生信息
			List<StudentTask> stuTaskList = studentTaskService
					.findListByMentorTaskId(mentorTaskId);
			if (null != stuTaskList && stuTaskList.size() > 0) {
				List<StuInforDomain> stuInforList = new ArrayList<StuInforDomain>();
				String userIds = "";
				for (StudentTask stuTask : stuTaskList) {
					StuInforDomain stuDomain = new StuInforDomain();
					stuDomain.setId(stuTask.getStudentId());
					stuDomain.setJobNum(stuTask.getJobNum());
					stuInforList.add(stuDomain);
					if (StringUtils.isEmpty(userIds)) {
						userIds += stuTask.getStudentId().toString();
					} else {
						userIds += "," + stuTask.getStudentId().toString();
					}
				}
				HashMap<Long, AccountDTO> avatarList = authUtilService
						.getavatarUsersInfo(userIds);
				for (StuInforDomain stu : stuInforList) {
					if (null != avatarList.get(stu.getId())) {
						stu.setAvatar(avatarList.get(stu.getId()).getAvatar());
						stu.setName(avatarList.get(stu.getId()).getName());
					}
				}
				menTaskDomain.setStudentList(stuInforList);
			}
//		} else {
//			menTaskDomain.setStudentList(null);
//		}

		return menTaskDomain;
	}
	
	
	RowMapper<PracticeTaskForSchoolDomain> rm = new RowMapper<PracticeTaskForSchoolDomain>() {

		@Override
		public PracticeTaskForSchoolDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			PracticeTaskForSchoolDomain domain = new PracticeTaskForSchoolDomain();
			domain.setId(rs.getString("ID"));
			domain.setWeekTaskName(rs.getString("TASK_TITLE"));
			domain.setTaskName(rs.getString("TASK_NAME"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setBeginDate(rs.getDate("BEGIN_DATE"));
			domain.setDeadLine(rs.getDate("DEAD_LINE"));
			domain.setClassHour(rs.getInt("CLASS_HOUR"));
			domain.setCreatorName(rs.getString("CREATOR_NAME"));
			return domain;
		}
	};
	
	/**
	 * 查询周任务分页列表信息
	 * @param pageSize
	 * @param offset
	 * @param taskName
	 * @param orgId
	 * @return
	 */
	public Map<String, Object> findPageForSchoolTeacher(Integer pageSize,
			Integer offset, String keyWord, Long orgId,Long userId) {

		String querySql = "SELECT mt.id,wt.TASK_TITLE,mt.TASK_NAME,mt.GROUP_NAME,mt.CLASS_HOUR,mt.BEGIN_DATE,mt.DEAD_LINE,mt.CREATOR_NAME from `sp_mentor_task` mt LEFT JOIN `sp_week_task` wt ON mt.WEEK_TASK_ID = wt.ID where mt.DELETE_FLAG =0 and wt.DELETE_FLAG =0 and GROUP_ID is not null ";
		String countSql = "SELECT count(1) from `sp_mentor_task` mt LEFT JOIN `sp_week_task` wt ON mt.WEEK_TASK_ID = wt.ID where mt.DELETE_FLAG =0 and GROUP_ID is not null ";
		if (!StringUtils.isEmpty(keyWord)) {
			querySql += " and (mt.TASK_NAME like '%" + keyWord + "%' or  mt.GROUP_NAME like '%" + keyWord + "%')";
			countSql += " and (mt.TASK_NAME like '%" + keyWord + "%' or  mt.GROUP_NAME like '%" + keyWord + "%')";
		}
		querySql += " and wt.ORG_ID =" + orgId;
		countSql += " and wt.ORG_ID =" + orgId;
		querySql += " and mt.CREATED_BY =" + userId;
		countSql += " and mt.CREATED_BY =" + userId;
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("mt.CREATED_DATE");
		dto.setAsc(false);
		sort.add(dto);

		return pageJdbcUtil.getPageInfor(pageSize, offset, rm, sort, querySql,
				countSql);
	}
	
	
	RowMapper<TaskCountDomain> countRm = new RowMapper<TaskCountDomain>() {

		@Override
		public TaskCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			TaskCountDomain domain = new TaskCountDomain();
			domain.setTaskCountNum(rs.getLong("countNum"));
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setWeekTaskId(rs.getString("WEEK_TASK_ID"));
			return domain;
		}
	};
	
	
	public List<TaskCountDomain> countMentorTaskNumByGroupId(String groupIds,String weekTaskIds) {

		String querySql = "SELECT count(1) as countNum,GROUP_ID,WEEK_TASK_ID FROM `sp_mentor_task` where DELETE_FLAG =0 and WEEK_TASK_ID in ("+weekTaskIds+") and GROUP_ID in ("+groupIds+");";
		

		return pageJdbcUtil.getInfo(querySql, countRm);
	}
	
	RowMapper<TaskCountDomain> countRm1 = new RowMapper<TaskCountDomain>() {

		@Override
		public TaskCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			TaskCountDomain domain = new TaskCountDomain();
			domain.setTaskCountNum(rs.getLong("countNum"));
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setTaskId(rs.getString("TASK_ID"));
			return domain;
		}
	};
	
	public List<TaskCountDomain> countMentorTaskNumByTaskId(String groupIds,String taskIds) {

		String querySql = "SELECT count(1) as countNum,GROUP_ID,TASK_ID FROM `sp_mentor_task` where DELETE_FLAG =0 and TASK_ID in ("+taskIds+") and GROUP_ID in ("+groupIds+");";
		

		return pageJdbcUtil.getInfo(querySql, countRm1);
	}
	
	
	public void initStuTaskGroupId(){
		List<MentorTask> list = mentorTaskRepository.findAllByDeleteFlagAndGroupIdIsNotNull(DataValidity.VALID.getIntValue());
		for(MentorTask mt : list){
			studentTaskService.initStuTaskGroupId(mt.getGroupId(), mt.getId());
		}
	}
}
