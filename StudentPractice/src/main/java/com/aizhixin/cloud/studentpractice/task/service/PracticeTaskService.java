package com.aizhixin.cloud.studentpractice.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.aizhixin.cloud.studentpractice.score.repository.CounselorCountRepository;
import com.aizhixin.cloud.studentpractice.score.repository.ScoreRepository;
import com.aizhixin.cloud.studentpractice.summary.repository.ReportRepository;
import com.aizhixin.cloud.studentpractice.summary.repository.SummaryReplyCountRepository;
import com.aizhixin.cloud.studentpractice.summary.repository.SummaryRepository;
import com.aizhixin.cloud.studentpractice.task.entity.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.core.ErrorCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.CorporateMentorsInfoByStudentDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.domain.TrainingRelationInfoDTO;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.PushMessageService;
import com.aizhixin.cloud.studentpractice.common.service.PushService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.task.domain.CountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
import com.aizhixin.cloud.studentpractice.task.domain.PracticeTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDetailForSchoolDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskAssginDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDomain;
import com.aizhixin.cloud.studentpractice.task.repository.MentorTaskRepository;
import com.aizhixin.cloud.studentpractice.task.repository.PeopleCountDetailRepository;
import com.aizhixin.cloud.studentpractice.task.repository.StudentTaskRepository;
import com.aizhixin.cloud.studentpractice.task.repository.TaskRepository;
import com.aizhixin.cloud.studentpractice.evaluate.repository.EvaluateRepository;

@Service
public class PracticeTaskService {

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private MentorTaskService mentorTaskService;
	@Autowired
	private FileService fileService;
	@Autowired
	private WeekTaskService weekTaskService;
	@Autowired
	private WeekTaskTeamService weekTaskTeamService;
	@Autowired
	@Lazy
    private EvaluateRepository evaluateRepository;
	@Autowired
	@Lazy
    private CounselorCountRepository counselorCountRepository;
    @Autowired
	@Lazy
    private ScoreRepository scoreRepository;
    @Autowired
	@Lazy
    private ReportRepository reportRepository;
    @Autowired
	@Lazy 
    private SummaryReplyCountRepository summaryReplyCountRepository;
    @Autowired
	@Lazy
    private MentorTaskRepository mentorTaskRepository;
    @Autowired
	@Lazy
    private StudentTaskRepository studentTaskRepository;
    @Autowired
	@Lazy
    private SummaryRepository summaryRepository;
    @Autowired
 	@Lazy
    private PeopleCountDetailRepository peopleCountDetailRepository;
    @Autowired
 	@Lazy
    private TaskStatisticalService taskStatisticalService;

	public Task save(Task task) {
		return taskRepository.save(task);
	}

	public Task findById(String taskId) {
		return taskRepository.findOne(taskId);
	}

	@Transactional
	public void editTaskTime(PracticeTaskDomain domain) {

		mentorTaskService.updateTimeById(domain.getBeginDate(),
				domain.getEndDate(), domain.getId());
	}

	public void taskAssign(TaskAssginDomain domain, AccountDTO dto) {

		TaskAssignThread thread = new TaskAssignThread(domain, dto);
		thread.start();
	}

	class TaskAssignThread extends Thread {
		private TaskAssginDomain domain;
		private AccountDTO dto;

		public TaskAssignThread(TaskAssginDomain domain, AccountDTO dto) {
			this.domain = domain;
			this.dto = dto;
		}

		public void run() {

			// 根据实践小组id集合获取实践小组的企业导师和学生对应关系
			List<TrainingRelationInfoDTO> groupInfoList = authUtilService
					.getGroupInfoListByIds(domain.getPracticeGroupIdList());

			if (null != groupInfoList && !groupInfoList.isEmpty()) {
				// 待分配的实践任务对应的实践课程id集合
				if (null != domain.getWeekTaskIdList()
						&& !domain.getWeekTaskIdList().isEmpty()) {

					List<WeekTask> weekTaskList = weekTaskService
							.findById(domain.getWeekTaskIdList());

					List<Task> taskList = taskRepository
							.findAllByWeekTaskIds(domain.getWeekTaskIdList());

					String groupIds = "";
					for (int i = 0; i < domain.getPracticeGroupIdList().size(); i++) {
						if (i == 0) {
							groupIds += domain.getPracticeGroupIdList().get(i);
						} else {
							groupIds += ","
									+ domain.getPracticeGroupIdList().get(i);
						}
					}
					String weekTaskIds = "";
					for (int i = 0; i < domain.getWeekTaskIdList().size(); i++) {
						if (i == 0) {
							weekTaskIds += "'"
									+ domain.getWeekTaskIdList().get(i) + "'";
						} else {
							weekTaskIds += ",'"
									+ domain.getWeekTaskIdList().get(i) + "'";
						}
					}
					List<TaskCountDomain> countList = mentorTaskService
							.countMentorTaskNumByGroupId(groupIds, weekTaskIds);
					HashMap<String, String> countMap = new HashMap<String, String>();
					if (null != countList && !countList.isEmpty()) {
						for (TaskCountDomain dto : countList) {

							if (0 == dto.getTaskCountNum()) {
								continue;
							}

							if (null == countMap.get(dto.getWeekTaskId())) {
								countMap.put(dto.getWeekTaskId(),
										"," + dto.getGroupId() + ",");
							} else {
								countMap.put(dto.getWeekTaskId(),
										countMap.get(dto.getWeekTaskId()) + ","
												+ dto.getGroupId() + ",");
							}
						}
					}

					// 实践课程与实践小组对应关系
					ArrayList<WeekTaskTeam> teamList = new ArrayList<WeekTaskTeam>();
					// 实践任务集合
					ArrayList<TaskDomain> taskDomainList = new ArrayList<TaskDomain>();
					for (TrainingRelationInfoDTO group : groupInfoList) {

						for (WeekTask weekTask : weekTaskList) {

							if (null != countMap.get(weekTask.getId())) {
								if (countMap.get(weekTask.getId()).indexOf(
										"," + group.getId() + ",") != -1) {
									continue;
								}
							}

							WeekTaskTeam team = new WeekTaskTeam();
							BeanUtils.copyProperties(weekTask, team);
							Date date = new Date();
							team.setCreatedDate(date);
							team.setLastModifiedDate(date);
							team.setId(UUID.randomUUID().toString());
							team.setWeekTaskId(weekTask.getId());
							team.setGroupId(group.getId());
							teamList.add(team);
						}

						for (Task task : taskList) {

							if (null != countMap.get(task.getWeekTaskId())) {
								if (countMap.get(task.getWeekTaskId()).indexOf(
										"," + group.getId() + ",") != -1) {
									continue;
								}
							}

							TaskDomain taskDomain = new TaskDomain();
							taskDomain.setClassHour(task.getClassHour());
							taskDomain.setBeginDate(domain.getBeginDate());
							taskDomain.setDeadLine(domain.getEndDate());
							taskDomain.setDescription(task.getDescription());
							taskDomain.setTaskName(task.getTaskName());
							taskDomain.setWeekTaskId(task.getWeekTaskId());
							taskDomain.setTaskId(task.getId());
							taskDomain.setMentorId(group.getAccountId());
							taskDomain.setMentorName(group
									.getCorporateMentorsName());
							taskDomain.setGroupId(group.getId());
							taskDomain.setGroupName(group.getName());
							taskDomain.setDescribe(task.getDescribe());
							ArrayList<StuInforDomain> stuList = copyStuInfo(group);
							taskDomain.setUserList(stuList);
							taskDomainList.add(taskDomain);
						}
					}

					weekTaskTeamService.saveList(teamList);
					mentorTaskService.mentorTaskListSave(taskDomainList, dto);

				} else if (null != domain.getPracticeTaskIdList()
						&& !domain.getPracticeTaskIdList().isEmpty()) {

					List<Task> taskList = taskRepository.findAllByIds(domain
							.getPracticeTaskIdList());
					HashSet<String> weekTaskIdList = new HashSet<String>();
					for (Task task : taskList) {
						weekTaskIdList.add(task.getWeekTaskId());
					}
					List<WeekTask> weekTaskList = weekTaskService
							.findById(new ArrayList<String>(weekTaskIdList));

					String teamIds = "";
					for (int i = 0; i < domain.getPracticeGroupIdList().size(); i++) {
						if (i == 0) {
							teamIds += domain.getPracticeGroupIdList().get(i);
						} else {
							teamIds += ","
									+ domain.getPracticeGroupIdList().get(i);
						}
					}
					String taskIds = "";
					for (int i = 0; i < domain.getPracticeTaskIdList().size(); i++) {
						if (i == 0) {
							taskIds += "'"
									+ domain.getPracticeTaskIdList().get(i)
									+ "'";
						} else {
							taskIds += ",'"
									+ domain.getPracticeTaskIdList().get(i)
									+ "'";
						}
					}
					List<TaskCountDomain> countList = mentorTaskService
							.countMentorTaskNumByTaskId(teamIds, taskIds);
					HashMap<String, String> countMap = new HashMap<String, String>();
					if (null != countList && !countList.isEmpty()) {
						for (TaskCountDomain dto : countList) {

							if (0 == dto.getTaskCountNum()) {
								continue;
							}

							if (null == countMap.get(dto.getTaskId())) {
								countMap.put(dto.getTaskId(),
										"," + dto.getGroupId() + ",");
							} else {
								countMap.put(dto.getTaskId(),
										countMap.get(dto.getTaskId()) + ","
												+ dto.getGroupId() + ",");
							}
						}
					}

					// 实践课程与实践小组对应关系
					ArrayList<WeekTaskTeam> teamList = new ArrayList<WeekTaskTeam>();
					// 实践任务集合
					ArrayList<TaskDomain> taskDomainList = new ArrayList<TaskDomain>();
					for (TrainingRelationInfoDTO group : groupInfoList) {

						for (WeekTask weekTask : weekTaskList) {

							Long countResult = weekTaskTeamService
									.countByDeleteFlagAndWeekTaskIdAndGroupId(
											weekTask.getId(), group.getId());
							if (null != countResult
									&& countResult.intValue() > 0) {
								continue;
							}

							WeekTaskTeam team = new WeekTaskTeam();
							BeanUtils.copyProperties(weekTask, team);
							team.setId(UUID.randomUUID().toString());
							team.setWeekTaskId(weekTask.getId());
							team.setGroupId(group.getId());
							teamList.add(team);
						}

						for (Task task : taskList) {

							if (null != countMap.get(task.getId())) {
								if (countMap.get(task.getId()).indexOf(
										"," + group.getId() + ",") != -1) {
									continue;
								}
							}

							TaskDomain taskDomain = new TaskDomain();
							taskDomain.setClassHour(task.getClassHour());
							taskDomain.setBeginDate(domain.getBeginDate());
							taskDomain.setDeadLine(domain.getEndDate());
							taskDomain.setDescription(task.getDescription());
							taskDomain.setTaskName(task.getTaskName());
							taskDomain.setWeekTaskId(task.getWeekTaskId());
							taskDomain.setTaskId(task.getId());
							taskDomain.setMentorId(group.getAccountId());
							taskDomain.setMentorName(group
									.getCorporateMentorsName());
							taskDomain.setGroupId(group.getId());
							taskDomain.setGroupName(group.getName());
							taskDomain.setDescribe(task.getDescribe());
							ArrayList<StuInforDomain> stuList = copyStuInfo(group);
							taskDomain.setUserList(stuList);
							taskDomainList.add(taskDomain);
						}
					}

					weekTaskTeamService.saveList(teamList);
					mentorTaskService.mentorTaskListSave(taskDomainList, dto);
				}
			}
		}

		private ArrayList<StuInforDomain> copyStuInfo(
				TrainingRelationInfoDTO group) {
			ArrayList<StuInforDomain> stuList = new ArrayList<StuInforDomain>();
			List<CorporateMentorsInfoByStudentDTO> students = group
					.getStudents();
			for (CorporateMentorsInfoByStudentDTO stuDomain : students) {
				StuInforDomain stuInfo = new StuInforDomain();
				stuInfo.setId(stuDomain.getSid());
				stuInfo.setName(stuDomain.getSname());
				stuInfo.setJobNum(stuDomain.getSjobNumber());
				stuInfo.setClassId(stuDomain.getClassesId());
				stuInfo.setCollegeId(stuDomain.getCollegeId());
				stuInfo.setProfessionalId(stuDomain.getProfessionalId());
				stuInfo.setMentorCompanyName(group.getEnterpriseName());
				stuInfo.setOrgId(stuDomain.getOrgId());
				stuInfo.setTrainingGroupName(group.getName());
				stuList.add(stuInfo);
			}
			return stuList;
		}
	}

	/**
	 * 任务新增
	 * 
	 * @param domain
	 * @param dto
	 * @return
	 */
	@Transactional
	public Task taskSave(PracticeTaskDomain domain, AccountDTO dto) {

		Task task = new Task();
		BeanUtils.copyProperties(domain, task);
		String taskId = UUID.randomUUID().toString();
		task.setId(taskId);
		task.setCreatedBy(dto.getId());
		task.setOrgId(dto.getOrgId());
		task.setDescribe(domain.getDescribe());
		if (null != domain.getFileList() && !domain.getFileList().isEmpty()) {
			List<File> fileList = new ArrayList<File>();
			for (FileDomain fileDomain : domain.getFileList()) {
				File file = new File();
				String fileId = UUID.randomUUID().toString();
				file.setId(fileId);
				file.setFileName(fileDomain.getFileName());
				file.setSrcUrl(fileDomain.getSrcUrl());
				file.setSourceId(taskId);
				file.setCreatedBy(dto.getId());
				fileList.add(file);
			}
			fileService.saveList(fileList);
		}

		return save(task);
	}

	/**
	 * 任务编辑
	 * 
	 * @param domain
	 * @param weekTask
	 * @param userId
	 */
	@Transactional
	public void taskEdit(PracticeTaskDomain domain, Task task, Long userId) {
		task.setWeekTaskId(domain.getWeekTaskId());
		task.setTaskName(domain.getTaskName());
		task.setDescription(domain.getDescription());
		task.setLastModifiedBy(userId);
		task.setClassHour(domain.getClassHour());
		task.setDescribe(domain.getDescribe());
		if (null != domain.getFileList() && domain.getFileList().size() > 0) {
			fileService.deleteBySourceId(task.getId());
			List<File> fileList = new ArrayList<File>();
			for (FileDomain fileDomain : domain.getFileList()) {
				File file = new File();
				String fileId = UUID.randomUUID().toString();
				file.setId(fileId);
				file.setFileName(fileDomain.getFileName());
				file.setSrcUrl(fileDomain.getSrcUrl());
				file.setSourceId(task.getId());
				file.setCreatedBy(userId);
				fileList.add(file);
			}
			fileService.saveList(fileList);
		}else{
			fileService.deleteBySourceId(task.getId());
		}
       MentorTask mentorTask= mentorTaskService.findById(task.getId());
		if (null!=mentorTask){
			mentorTask.setDescribe(domain.getDescribe());
			mentorTaskService.save(mentorTask);
		}
		save(task);
	}

	/**
	 * 删除任务
	 * 
	 * @param weekTaskId
	 */
	@Transactional
	public void deleteTask(String taskId) {

		Task task = findById(taskId);
		if (null != task) {
			task.setDeleteFlag(DataValidity.INVALID.getIntValue());
			save(task);
		}
	}

	/**
	 * 获取任务详情
	 * 
	 * @param weekTaskId
	 * @return
	 */
	public PracticeTaskDomain getDetail(String taskId) {

		Task task = findById(taskId);
		if (null != task) {
			PracticeTaskDomain domain = new PracticeTaskDomain();
			BeanUtils.copyProperties(task, domain);
			List<File> fileList = fileService.findAllByDeleteFlagAndSourceId(
					DataValidity.VALID.getIntValue(), taskId);
			if (null != fileList && fileList.size() > 0) {
				ArrayList<FileDomain> fileDomainList = new ArrayList<FileDomain>();
				for (File file : fileList) {
					FileDomain fileDomain = new FileDomain();
					BeanUtils.copyProperties(file, fileDomain);
					fileDomainList.add(fileDomain);
				}
				domain.setFileList(fileDomainList);
			}
			return domain;
		}

		return null;
	}

	RowMapper<PracticeTaskDomain> rm = new RowMapper<PracticeTaskDomain>() {

		@Override
		public PracticeTaskDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			PracticeTaskDomain domain = new PracticeTaskDomain();
			domain.setId(rs.getString("ID"));
			domain.setTaskName(rs.getString("TASK_NAME"));
			domain.setWeekTaskName(rs.getString("TASK_TITLE"));
			domain.setDescription(rs.getString("DESCRIPTION"));
			domain.setWeekTaskId(rs.getString("WEEK_TASK_ID"));
			domain.setClassHour(rs.getInt("CLASS_HOUR"));
			return domain;
		}
	};

	/**
	 * 查询周任务分页列表信息
	 * 
	 * @param pageSize
	 * @param offset
	 * @param taskName
	 * @param orgId
	 * @return
	 */
	public Map<String, Object> findPage(Integer pageSize, Integer offset,
			String taskName, Long orgId, String weekTaskId, String sortFlag) {

		String querySql = "SELECT t.*,wt.TASK_TITLE from `SP_TASK` t LEFT JOIN `sp_week_task` wt ON t.WEEK_TASK_ID = wt.ID where t.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `SP_TASK` t LEFT JOIN `sp_week_task` wt ON t.WEEK_TASK_ID = wt.ID where t.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(taskName)) {
			querySql += " and t.TASK_NAME like '%" + taskName + "%'";
			countSql += " and t.TASK_NAME like '%" + taskName + "%'";
		}
		querySql += " and t.ORG_ID =" + orgId;
		countSql += " and t.ORG_ID =" + orgId;
		if (!StringUtils.isEmpty(weekTaskId)) {
			querySql += " and t.WEEK_TASK_ID ='" + weekTaskId + "'";
			countSql += " and t.WEEK_TASK_ID ='" + weekTaskId + "'";
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("id");
		if ("asc".equals(sortFlag)) {
			dto.setAsc(true);
		} else {
			dto.setAsc(false);
		}
		sort.add(dto);

		return pageJdbcUtil.getPageInfor(pageSize, offset, rm, sort, querySql,
				countSql);
	}

	RowMapper<StuTaskDetailForSchoolDomain> stuTaskDetailRm = new RowMapper<StuTaskDetailForSchoolDomain>() {

		@Override
		public StuTaskDetailForSchoolDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			StuTaskDetailForSchoolDomain domain = new StuTaskDetailForSchoolDomain();
			domain.setBeginDate(rs.getDate("BEGIN_DATE"));
			domain.setDeadLine(rs.getDate("DEAD_LINE"));
			domain.setClassHour(rs.getInt("CLASS_HOUR"));
			domain.setDescription(rs.getString("DESCRIPTION"));
			domain.setReviewTaskId(rs.getString("REVIEW_ID"));
			domain.setStuTaskId(rs.getString("STUDENT_TASK_ID"));
			domain.setStuTaskStatus(rs.getString("STUDENT_TASK_STATUS"));
			domain.setTaskAdvice(rs.getString("REVIEW_ADVICE"));
			domain.setTaskId(rs.getString("TASK_ID"));
			domain.setTaskName(rs.getString("TASK_NAME"));
			domain.setTaskScore(rs.getString("REVIEW_SCORE"));
			domain.setWeekTaskName(rs.getString("TASK_TITLE"));
			domain.setResultDescription(rs.getString("RESULT_DESCRIPTION"));
			domain.setDescribe(rs.getString("describe_Info"));
			return domain;
		}
	};

	/**
	 * 按条件统计实践人数数量
	 * 
	 * @return
	 */
	public StuTaskDetailForSchoolDomain getStuTaskDetail(String stuTaskId) {

		String querySql = "SELECT wt.TASK_TITLE,mt.TASK_NAME,mt.DESCRIPTION,mt.CLASS_HOUR,mt.BEGIN_DATE,mt.DEAD_LINE,mt.TASK_ID,mt.describe_Info,st.ID as STUDENT_TASK_ID,rt.id as REVIEW_ID,st.RESULT_DESCRIPTION,st.REVIEW_SCORE,st.STUDENT_TASK_STATUS,rt.REVIEW_ADVICE from `sp_student_task` st LEFT JOIN `sp_review_task` rt ON st.id = rt.STUDENT_TASK_ID LEFT JOIN `sp_mentor_task` mt ON mt.ID = st.MENTOR_TASK_ID LEFT JOIN `sp_week_task` wt ON st.week_task_id = wt.id where st.DELETE_FLAG = 0 and st.id ='"
				+ stuTaskId + "' order by rt.CREATED_DATE desc ";

		StuTaskDetailForSchoolDomain domain = pageJdbcUtil.getInfo(querySql,
				stuTaskDetailRm).get(0);

		HashSet<String> sourceIds = new HashSet<String>();
		if (!StringUtils.isEmpty(domain.getTaskId())) {
			sourceIds.add(domain.getTaskId());
		}
		if (!StringUtils.isEmpty(domain.getStuTaskId())) {
			sourceIds.add(domain.getStuTaskId());
		}
		if (!StringUtils.isEmpty(domain.getReviewTaskId())) {
			sourceIds.add(domain.getReviewTaskId());
		}

		List<File> fileList = fileService.findAllBySourceIds(sourceIds);
		if (null != fileList && !fileList.isEmpty()) {
			for (File file : fileList) {
				if (!StringUtils.isEmpty(domain.getTaskId())
						&& file.getSourceId().equals(domain.getTaskId())) {
					domain.getTaskFileList().add(file);
				} else if (!StringUtils.isEmpty(domain.getStuTaskId())
						&& file.getSourceId().equals(domain.getStuTaskId())) {
					domain.getStuFileList().add(file);
				} else if (!StringUtils.isEmpty(domain.getReviewTaskId())
						&& file.getSourceId().equals(domain.getReviewTaskId())) {
					domain.getReviewFileList().add(file);
				}
			}
		}

		return domain;
	}
	
	public void synGroupName(IdNameDomain domain) {

		SynGroupNameThread thread = new SynGroupNameThread(domain);
		thread.start();
	}

	class SynGroupNameThread extends Thread {
		private IdNameDomain domain;

		public SynGroupNameThread(IdNameDomain domain) {
			this.domain = domain;
		}

		
		public void run() {
			evaluateRepository.updateGroupNameByGroupId(domain.getName(),domain.getId());
			counselorCountRepository.updateGroupNameByGroupId(domain.getName(),domain.getId());
			scoreRepository.updateGroupNameByGroupId(domain.getName(),domain.getId());
			reportRepository.updateGroupNameByGroupId(domain.getName(),domain.getId());
			summaryReplyCountRepository.updateGroupNameByGroupId(domain.getName(),domain.getId());
			mentorTaskRepository.updateGroupNameByGroupId(domain.getName(),domain.getId());
			studentTaskRepository.updateGroupNameByGroupId(domain.getName(),domain.getId());
			summaryRepository.updateGroupNameByGroupId(domain.getName(),domain.getId());
			peopleCountDetailRepository.updateGroupNameByGroupId(domain.getName(),domain.getId());
		}

	}
	
	
	public void synStuInfor(Long groupId) {

		SynStuInforThread thread = new SynStuInforThread(groupId);
		thread.start();
	}

	class SynStuInforThread extends Thread {
		private Long groupId;

		public SynStuInforThread(Long groupId) {
			this.groupId = groupId;
		}

		
		public void run() {
			 taskStatisticalService.countPeopleNum(groupId);
			 taskStatisticalService.taskStatistics(groupId);
		}
	}
}
