package com.aizhixin.cloud.studentpractice.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
import com.aizhixin.cloud.studentpractice.task.core.MessageCode;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.CountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
import com.aizhixin.cloud.studentpractice.task.domain.ReviewTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTask;
import com.aizhixin.cloud.studentpractice.task.entity.ReviewTask;
import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTask;
import com.aizhixin.cloud.studentpractice.task.repository.StudentTaskRepository;

@Transactional
@Service
public class StudentTaskService {
	private static Logger log = LoggerFactory
			.getLogger(StudentTaskService.class);

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private StudentTaskRepository studentTaskRepository;
	@Autowired
	private FileService fileService;
	@Autowired
	private MentorTaskService mentorTaskService;
	@Autowired
	private ReviewTaskService reviewTaskService;
	@Autowired
	private PushService pushService;
	@Autowired
	@Lazy
	private WeekTaskService weekTaskService;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;
	@Autowired
	private MentorTaskCountService mentorTaskCountService;

	public void initStuTaskGroupId(Long groupId,String mentorTaskId){
		studentTaskRepository.initGroupIdByMentorTaskId(groupId, mentorTaskId);
	}
	
	public StudentTask save(StudentTask stuTask) {
		return studentTaskRepository.save(stuTask);
	}

	public List<StudentTask> saveList(List<StudentTask> stuTaskList) {
		return studentTaskRepository.save(stuTaskList);
	}

	public StudentTask findById(String stuTaskId) {
		return studentTaskRepository.findOneByDeleteFlagAndId(
				DataValidity.VALID.getIntValue(), stuTaskId);
	}

	public StudentTask findByMentorTaskId(String menTaskId, Long stuId) {
		return studentTaskRepository
				.findOneByDeleteFlagAndStudentIdAndMentorTaskId(
						DataValidity.VALID.getIntValue(), stuId, menTaskId);
	}

	public List<StudentTask> findListByMentorTaskId(String menTaskId) {
		return studentTaskRepository.findAllByDeleteFlagAndMentorTaskId(
				DataValidity.VALID.getIntValue(), menTaskId);
	}
	
	public Page<StudentTask> findListByMentorTaskId(Pageable pageable,String menTaskId) {
		return studentTaskRepository.findAllByDeleteFlagAndMentorTaskId(
				pageable,DataValidity.VALID.getIntValue(), menTaskId);
	}

	public List<StudentTask> findAllByMentorTaskId(String menTaskId) {
		return studentTaskRepository.findAllByMentorTaskId(menTaskId);
	}

	public void logicDeleteTask(String[] ids, Long userId) {
		studentTaskRepository.logicDeleteByMentorTaskId(ids, userId);
	}

	public Long countScoreNotNullByMentorTaskId(String mentorTaskId) {
		return studentTaskRepository
				.countByDeleteFlagAndReviewScoreNotNullAndMentorTaskId(
						DataValidity.VALID.getIntValue(), mentorTaskId);
	}

	public Long countAllByMentorTaskId(String mentorTaskId) {
		return studentTaskRepository.countByDeleteFlagAndMentorTaskId(
				DataValidity.VALID.getIntValue(), mentorTaskId);
	}

	RowMapper<StuTaskDomain> rm = new RowMapper<StuTaskDomain>() {

		@Override
		public StuTaskDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			StuTaskDomain stuTask = new StuTaskDomain();
			stuTask.setStuTaskid(rs.getString("ID"));
			stuTask.setStudentTaskStatus(rs.getString("STUDENT_TASK_STATUS"));
			stuTask.setReviewScore(rs.getString("REVIEW_SCORE"));
			stuTask.setCreatedBy(rs.getLong("CREATED_BY"));
			stuTask.setCreateDate(rs.getDate("CREATED_DATE"));
			stuTask.setDeadLine(rs.getDate("DEAD_LINE"));
			stuTask.setTaskName(rs.getString("TASK_NAME"));
			stuTask.setMentorTaskid(rs.getString("MENTOR_TASK_ID"));
			stuTask.setClassHour(rs.getInt("CLASS_HOUR"));
			stuTask.setDescription(rs.getString("DESCRIPTION"));
			return stuTask;
		}
	};

	/**
	 * 学生任务分页列表
	 * 
	 * @param pageSize
	 * @param offset
	 * @param taskName
	 * @param taskStatus
	 * @param stuId
	 * @return
	 */
	public Map<String, Object> findStuTaskPage(Integer pageSize,
			Integer offset, String taskName, String taskStatus, Long stuId,
			String weekTaskId,String sortFlag) {

		String querySql = "SELECT st.MENTOR_TASK_ID,mt.CREATED_BY,mt.CLASS_HOUR,mt.CREATED_DATE,mt.DEAD_LINE,mt.TASK_NAME,mt.DESCRIPTION,st.ID,st.STUDENT_TASK_STATUS,st.REVIEW_SCORE FROM `sp_student_task` as st LEFT JOIN `sp_mentor_task` as mt ON st.MENTOR_TASK_ID = mt.ID where st.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `sp_student_task` as st LEFT JOIN `sp_mentor_task` as mt ON st.MENTOR_TASK_ID = mt.ID where st.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(taskName)) {
			querySql += " and mt.TASK_NAME like '%" + taskName + "%'";
			countSql += " and mt.TASK_NAME like '%" + taskName + "%'";
		}
		if (!StringUtils.isEmpty(taskStatus)) {
			String taskStatusLs = "";
			if (taskStatus.indexOf(",") > 0) {
				String[] statusArr = taskStatus.split(",");
				for (int i = 0; i < statusArr.length; i++) {
					if (!StringUtils.isEmpty(statusArr[i])) {
						if (StringUtils.isEmpty(taskStatusLs)) {
							taskStatusLs = "'" + statusArr[i] + "'";
						} else {
							taskStatusLs += ",'" + statusArr[i] + "'";
						}
					}
				}
			} else {
				taskStatusLs = "'" + taskStatus + "'";
			}
			querySql += " and st.STUDENT_TASK_STATUS in (" + taskStatusLs + ")";
			countSql += " and st.STUDENT_TASK_STATUS in (" + taskStatusLs + ")";
		}
		querySql += " and st.STUDENT_ID =" + stuId;
		countSql += " and st.STUDENT_ID =" + stuId;
		querySql += " and mt.WEEK_TASK_ID ='" + weekTaskId + "'";
		countSql += " and mt.WEEK_TASK_ID ='" + weekTaskId + "'";
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("mt.CREATED_DATE");
		if("asc".equals(sortFlag)){
			dto.setAsc(true);
		}else{
			dto.setAsc(false);
		}
		sort.add(dto);

		return pageJdbcUtil.getPageInfor(pageSize, offset, rm, sort, querySql,
				countSql);
	}

	public Map<String, Object> findStuTaskPageByGroupId(Integer pageSize,
			Integer offset, String taskName, String taskStatus, Long stuId,
			Long groupId,String sortFlag) {

		String querySql = "SELECT st.MENTOR_TASK_ID,mt.CLASS_HOUR,mt.CREATED_BY,mt.CREATED_DATE,mt.DEAD_LINE,mt.TASK_NAME,mt.DESCRIPTION,st.ID,st.STUDENT_TASK_STATUS,st.REVIEW_SCORE FROM `sp_student_task` as st LEFT JOIN `sp_mentor_task` as mt ON st.MENTOR_TASK_ID = mt.ID where st.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `sp_student_task` as st LEFT JOIN `sp_mentor_task` as mt ON st.MENTOR_TASK_ID = mt.ID where st.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(taskName)) {
			querySql += " and mt.TASK_NAME like '%" + taskName + "%'";
			countSql += " and mt.TASK_NAME like '%" + taskName + "%'";
		}
		if (!StringUtils.isEmpty(taskStatus)) {
			String taskStatusLs = "";
			if (taskStatus.indexOf(",") > 0) {
				String[] statusArr = taskStatus.split(",");
				for (int i = 0; i < statusArr.length; i++) {
					if (!StringUtils.isEmpty(statusArr[i])) {
						if (StringUtils.isEmpty(taskStatusLs)) {
							taskStatusLs = "'" + statusArr[i] + "'";
						} else {
							taskStatusLs += ",'" + statusArr[i] + "'";
						}
					}
				}
			} else {
				taskStatusLs = "'" + taskStatus + "'";
			}
			querySql += " and st.STUDENT_TASK_STATUS in (" + taskStatusLs + ")";
			countSql += " and st.STUDENT_TASK_STATUS in (" + taskStatusLs + ")";
		}
		String roleStr = "'" + RoleCode.ROLE_STUDENT + "','"
				+ RoleCode.ROLE_COM_TEACHER+ "'";
		querySql += " and mt.creator_role in (" + roleStr + ")";
		countSql += " and mt.creator_role in (" + roleStr + ")";
		querySql += " and st.STUDENT_ID =" + stuId;
		countSql += " and st.STUDENT_ID =" + stuId;
		querySql += " and mt.GROUP_ID =" + groupId + "";
		countSql += " and mt.GROUP_ID =" + groupId + "";
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("mt.CREATED_DATE");
		if("asc".equals(sortFlag)){
			dto.setAsc(true);
		}else{
			dto.setAsc(false);
		}
		sort.add(dto);

		return pageJdbcUtil.getPageInfor(pageSize, offset, rm, sort, querySql,
				countSql);
	}

	RowMapper<IdNameDomain> countRm = new RowMapper<IdNameDomain>() {

		@Override
		public IdNameDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			IdNameDomain domain = new IdNameDomain();
			domain.setId(rs.getLong("taskNum"));
			domain.setName(rs.getString("STUDENT_TASK_STATUS"));
			return domain;
		}
	};

	/**
	 * 统计学生实践任务数
	 * 
	 * @param userId
	 * @return
	 */
	public CountDomain countStuTaskStatusNum(Long userId) {
		CountDomain domain = new CountDomain();
		String sql = "SELECT st.STUDENT_TASK_STATUS,count(1) as taskNum FROM `sp_student_task` st where st.DELETE_FLAG =0 and st.STUDENT_ID ="
				+ userId + " GROUP BY st.STUDENT_TASK_STATUS;";
		List<IdNameDomain> list = pageJdbcUtil.getInfo(sql, countRm);
		if (null != list && list.size() > 0) {
			for (IdNameDomain dto : list) {
				if (TaskStatusCode.TASK_STATUS_BACK_TO.equals(dto.getName())) {
					domain.setBackToNum(dto.getId());
				} else if (TaskStatusCode.TASK_STATUS_CHECK_PENDING.equals(dto
						.getName())) {
					domain.setCheckPendingNum(dto.getId());
				} else if (TaskStatusCode.TASK_STATUS_NOT_PASS.equals(dto
						.getName())) {
					domain.setNotPassNum(dto.getId());
				} else if (TaskStatusCode.TASK_STATUS_PASS
						.equals(dto.getName())) {
					domain.setPassNum(dto.getId());
				} else if (TaskStatusCode.TASK_STATUS_UNCOMMIT.equals(dto
						.getName())) {
					domain.setUncommitNum(dto.getId());
				} else if (TaskStatusCode.TASK_STATUS_FINISH.equals(dto
						.getName())) {
					domain.setFinishNum(dto.getId());
				}
			}
		}
		return domain;
	}

	/**
	 * 统计导师实践任务数
	 * 
	 * @param userId
	 * @return
	 */
	public CountDomain countTaskStatusNum(Long userId) {
		CountDomain domain = new CountDomain();
		String sql = "SELECT count(1) as taskNum,st.STUDENT_TASK_STATUS FROM `sp_student_task` st where st.DELETE_FLAG =0 and st.MENTOR_ID = "
				+ userId + " GROUP BY st.STUDENT_TASK_STATUS";
		List<IdNameDomain> list = pageJdbcUtil.getInfo(sql, countRm);
		if (null != list && list.size() > 0) {
			for (IdNameDomain dto : list) {
				if (TaskStatusCode.TASK_STATUS_BACK_TO.equals(dto.getName())) {
					domain.setBackToNum(dto.getId());
				} else if (TaskStatusCode.TASK_STATUS_CHECK_PENDING.equals(dto
						.getName())) {
					domain.setCheckPendingNum(dto.getId());
				} else if (TaskStatusCode.TASK_STATUS_NOT_PASS.equals(dto
						.getName())) {
					domain.setNotPassNum(dto.getId());
				} else if (TaskStatusCode.TASK_STATUS_PASS
						.equals(dto.getName())) {
					domain.setPassNum(dto.getId());
				} else if (TaskStatusCode.TASK_STATUS_UNCOMMIT.equals(dto
						.getName())) {
					domain.setUncommitNum(dto.getId());
				}
			}
		}
		return domain;
	}

	public StuTaskDetailDomain getStuTaskDetail(String stuTaskId) {

		StudentTask studentTask = this.findById(stuTaskId);
		if (null == studentTask) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到相关学生任务信息");
		}
		StuTaskDomain stuTaskDomain = new StuTaskDomain();
		StuTaskDetailDomain detailTaskDomain = new StuTaskDetailDomain();

		// 学生任务详情信息
		BeanUtils.copyProperties(studentTask, stuTaskDomain);
		stuTaskDomain.setStuTaskid(stuTaskId);
		List<File> fileList = fileService.findAllByDeleteFlagAndSourceId(
				DataValidity.VALID.getIntValue(), stuTaskId);
		if (null != fileList && fileList.size() > 0) {
			ArrayList<FileDomain> fileDomainList = new ArrayList<FileDomain>();
			for (File file : fileList) {
				FileDomain fileDomain = new FileDomain();
				BeanUtils.copyProperties(file, fileDomain);
				fileDomainList.add(fileDomain);
			}
			stuTaskDomain.setStufileList(fileDomainList);
		}

		// 导师任务详情信息
		MentorTask mentorTask = mentorTaskService.findById(studentTask
				.getMentorTaskId());
		if (null == mentorTask) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到相关导师任务信息");
		}
		BeanUtils.copyProperties(mentorTask, detailTaskDomain);
		if (!org.springframework.util.StringUtils.isEmpty(mentorTask.getImgList())){
			List<String> imgList= JSON.parseArray(mentorTask.getImgList(),String.class);
			detailTaskDomain.setImgList(imgList);
		}
		detailTaskDomain.setDescribe(mentorTask.getDescribe());
		
		List<File> mentorFileList = null;
		if(StringUtils.isEmpty(mentorTask.getWeekTaskId())){ 
			mentorFileList = fileService.findAllByDeleteFlagAndSourceId(
					DataValidity.VALID.getIntValue(), mentorTask.getId());
		}else{
			mentorFileList = fileService.findAllByDeleteFlagAndSourceId(
					DataValidity.VALID.getIntValue(), mentorTask.getTaskId());
		}
		if (null != mentorFileList && mentorFileList.size() > 0) {
			ArrayList<FileDomain> fileDomainList = new ArrayList<FileDomain>();
			for (File file : mentorFileList) {
				FileDomain fileDomain = new FileDomain();
				BeanUtils.copyProperties(file, fileDomain);
				fileDomainList.add(fileDomain);
			}
			detailTaskDomain.setFileList(fileDomainList);
		}

		// 学生未做任务时不加载学生任务信息
		if (!StringUtils.isEmpty(stuTaskDomain.getResultDescription())
				|| stuTaskDomain.getStufileList().size() > 0) {
			detailTaskDomain.setStuTask(stuTaskDomain);
		} else {
			detailTaskDomain.setStuTask(null);
		}

		// 学生任务未审核时不加载审核信息
		if (!TaskStatusCode.TASK_STATUS_UNCOMMIT.equals(studentTask
				.getStudentTaskStatus())) {
			
			HashMap<Long, AccountDTO> avatarMap = authUtilService
					.getavatarUsersInfo(mentorTask.getMentorId().toString());
			AccountDTO stu = avatarMap.get(mentorTask.getMentorId());
			if(null != stu){
				detailTaskDomain.setMentorAvatar(stu.getAvatar());
			}
			
			List<ReviewTask> reviewTaskList = reviewTaskService
					.findByStuTaskId(studentTask.getId());

			if (null != reviewTaskList && !reviewTaskList.isEmpty()) {
				ArrayList<ReviewTaskDomain> reviewDomainList = new ArrayList<ReviewTaskDomain>();
				for (ReviewTask reviewTask : reviewTaskList) {
					ReviewTaskDomain reviewDomain = new ReviewTaskDomain();
					BeanUtils.copyProperties(reviewTask, reviewDomain);
					List<File> reFileList = fileService
							.findAllByDeleteFlagAndSourceId(
									DataValidity.VALID.getIntValue(),
									reviewTask.getId());
					if (null != reFileList && reFileList.size() > 0) {
						ArrayList<FileDomain> fileDomainList = new ArrayList<FileDomain>();
						for (File file : reFileList) {
							FileDomain fileDomain = new FileDomain();
							BeanUtils.copyProperties(file, fileDomain);
							fileDomainList.add(fileDomain);
						}
						reviewDomain.setFileList(fileDomainList);
					}
					reviewDomainList.add(reviewDomain);
				}
				detailTaskDomain.setReviewTaskList(reviewDomainList);
			} else {
				detailTaskDomain.setReviewTaskList(null);
			}

		} else {
			detailTaskDomain.setReviewTaskList(null);
		}

		return detailTaskDomain;
	}

	public TaskDetailDomain getTaskDetail(String stuTaskId) {

		StudentTask studentTask = this.findById(stuTaskId);
		if (null == studentTask) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到相关学生任务信息");
		}
		StuTaskDomain stuTaskDomain = new StuTaskDomain();
		BeanUtils.copyProperties(studentTask, stuTaskDomain);
		TaskDetailDomain detailTaskDomain = new TaskDetailDomain();
		detailTaskDomain.setStuTask(stuTaskDomain);

		// 学生任务未审核时不加载审核信息
		if (!TaskStatusCode.TASK_STATUS_UNCOMMIT.equals(studentTask
				.getStudentTaskStatus())) {
			List<ReviewTask> reviewTaskList = reviewTaskService
					.findByStuTaskId(studentTask.getId());

			if (null != reviewTaskList && !reviewTaskList.isEmpty()) {
				ArrayList<ReviewTaskDomain> reviewDomainList = new ArrayList<ReviewTaskDomain>();
				for (ReviewTask reviewTask : reviewTaskList) {
					ReviewTaskDomain reviewDomain = new ReviewTaskDomain();
					BeanUtils.copyProperties(reviewTask, reviewDomain);
					List<File> reFileList = fileService
							.findAllByDeleteFlagAndSourceId(
									DataValidity.VALID.getIntValue(),
									reviewTask.getId());
					if (null != reFileList && reFileList.size() > 0) {
						ArrayList<FileDomain> fileDomainList = new ArrayList<FileDomain>();
						for (File file : reFileList) {
							FileDomain fileDomain = new FileDomain();
							BeanUtils.copyProperties(file, fileDomain);
							fileDomainList.add(fileDomain);
						}
						reviewDomain.setFileList(fileDomainList);
					}
					reviewDomainList.add(reviewDomain);
				}
				detailTaskDomain.setReviewTaskList(reviewDomainList);
			} else {
				detailTaskDomain.setReviewTaskList(null);
			}

		} else {
			detailTaskDomain.setReviewTaskList(null);
		}

		return detailTaskDomain;
	}

	public StudentTask doTask(StuTaskDomain domain, String status, String token) {
		StudentTask stuTask = findByMentorTaskId(domain.getMentorTaskid(),
				domain.getStudentId());
		if (null == stuTask) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到学生任务信息");
		} else {
			if (TaskStatusCode.TASK_STATUS_UNCOMMIT.equals(stuTask
					.getStudentTaskStatus())
					|| TaskStatusCode.TASK_STATUS_BACK_TO.equals(stuTask
							.getStudentTaskStatus())) {
			} else {
				throw new CommonException(ErrorCode.PARAMS_CONFLICT,
						"只有待提交和打回状态的任务才能提交");
			}
		}
		if (!StringUtils.isEmpty(domain.getResultDescription())) {
			stuTask.setResultDescription(domain.getResultDescription());
		} else {
			if (!StringUtils.isEmpty(stuTask.getResultDescription())) {
				stuTask.setResultDescription(null);
			}
		}

		if (!StringUtils.isEmpty(status)
				&& TaskStatusCode.DO_TASK_COMMIT.equals(status)) {
			stuTask.setStudentTaskStatus(TaskStatusCode.TASK_STATUS_CHECK_PENDING);
		}

		if (null != domain.getStufileList()
				&& domain.getStufileList().size() > 0) {
			fileService.deleteBySourceId(stuTask.getId());
			List<File> fileList = new ArrayList<File>();
			for (FileDomain fileDomain : domain.getStufileList()) {
				File file = new File();
				String fileId = UUID.randomUUID().toString();
				file.setId(fileId);
				file.setFileName(fileDomain.getFileName());
				file.setSrcUrl(fileDomain.getSrcUrl());
				file.setSourceId(stuTask.getId());
				fileList.add(file);
			}
			fileService.saveList(fileList);
		}

		stuTask = save(stuTask);

		if (!StringUtils.isEmpty(status)
				&& TaskStatusCode.DO_TASK_COMMIT.equals(status)) {
			pushCommitMessage(stuTask);
		}

		return stuTask;
	}

	private void pushCommitMessage(StudentTask stuTask) {

		List<Long> ids = new ArrayList<Long>();
		MentorTask menTask = mentorTaskService.findById(stuTask
				.getMentorTaskId());
		ids.add(menTask.getMentorId());

		PushMessageDTO msg = new PushMessageDTO();
		
		msg.setTaskName(menTask.getTaskName());
     	msg.setWeekTaskId(menTask.getWeekTaskId());
     	if(!StringUtils.isEmpty(menTask.getWeekTaskId())){
     		msg.setWeekTaskId(menTask.getWeekTaskId());
     		WeekTask weekTask = weekTaskService.findById(menTask.getWeekTaskId());
     		msg.setWeekTaskName(weekTask.getTaskTitle());
     	}
     	msg.setStuTaskStatus(stuTask.getStudentTaskStatus());
     	msg.setMentorTaskId(menTask.getId());
     	msg.setStuTaskId(stuTask.getId());
		
		msg.setBusinessContent(menTask.getId());
		msg.setContent(MessageCode.MESSAGE_HEAD
				.concat(MessageCode.MESSAGE_STUDENT_COMMIT_TASK.replace("{1}",
						stuTask.getStudentName()).replace("{2}",
						menTask.getTaskName())));
		msg.setFunction(PushMessageConstants.MODULE_TASK);
		msg.setModule(PushMessageConstants.MODULE_TASK);
		msg.setTitle(MessageCode.MESSAGE_COMMIT_TASK_PUSH);
		msg.setUserIds(ids);
		pushService.addPushList(msg);

	}

}
