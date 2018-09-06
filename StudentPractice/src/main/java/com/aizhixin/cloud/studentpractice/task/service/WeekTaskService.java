package com.aizhixin.cloud.studentpractice.task.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.core.ErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.PushMessageConstants;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.PushMessageService;
import com.aizhixin.cloud.studentpractice.common.service.PushService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.task.domain.MentorCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryTaskPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.WeekTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTask;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTaskTeam;
import com.aizhixin.cloud.studentpractice.task.repository.WeekTaskRepository;
import com.aizhixin.cloud.studentpractice.task.repository.WeekTaskTeamRepository;


@Service
public class WeekTaskService {

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;
	@Autowired
	private PushMessageService pushMessageService;
	@Autowired
	private PushService pushService;
	@Autowired
	private WeekTaskRepository weekTaskRepository;
	@Autowired
	private WeekTaskTeamRepository weekTaskTeamRepository;
	@Autowired
	@Lazy
	private MentorTaskService mentorTaskService;
	
	public WeekTask save(WeekTask weekTask){
		return weekTaskRepository.save(weekTask);
	}
	
	public WeekTask findById(String weekTaskId){
		return weekTaskRepository.findOne(weekTaskId);
	}
	
	public List<WeekTask> findById(ArrayList<String> ids){
		return weekTaskRepository.findAllByIds(ids);
	}

	/**
	 * 周任务新增
	 * @param domain
	 * @param dto
	 * @return
	 */
	@Transactional
	public WeekTask weekTaskSave(WeekTaskDomain domain, AccountDTO dto) {

		WeekTask weekTask = new WeekTask();
		BeanUtils.copyProperties(domain, weekTask);
		String weekTaskId = UUID.randomUUID().toString();
		weekTask.setId(weekTaskId);
		weekTask.setCreatedBy(dto.getId());
		weekTask.setOrgId(dto.getOrgId());

		weekTask = save(weekTask);
		
//		ArrayList<WeekTaskTeam> teamList = new ArrayList<WeekTaskTeam>();
//		for(Long teamId :domain.getPracticeTeamList()){
//			WeekTaskTeam weekTaskTeam = new WeekTaskTeam();
//			String weekTaskTeamId = UUID.randomUUID().toString();
//			weekTaskTeam.setId(weekTaskTeamId);
//			weekTaskTeam.setOrgId(dto.getOrgId());
//			weekTaskTeam.setWeekTaskId(weekTaskId);
//			weekTaskTeam.setTeamId(teamId);
//			teamList.add(weekTaskTeam);
//		}
//		
//		weekTaskTeamRepository.save(teamList);
		
//		 pushMessageService.createPushMessage(menTaskId, MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_MENTOR_ADD_TASK),
//                 PushMessageConstants.FUNCTION_STUDENT_NOTICE,
//                 PushMessageConstants.MODULE_TASK, MessageCode.MESSAGE_TITLE_ADD_TASK,
//                 ids);
//         pushService.listPush(token, MessageCode.MESSAGE_ADD_TASK_PUSH, MessageCode.MESSAGE_TITLE_ADD_TASK, MessageCode.MESSAGE_TITLE_ADD_TASK,
//                 ids);

		return weekTask;
	}
	
	/**
	 * 周任务编辑
	 * @param domain
	 * @param weekTask
	 * @param userId
	 */
	public void weekTaskEdit(WeekTaskDomain domain,WeekTask weekTask,Long userId) {

		weekTask.setBeginDate(domain.getBeginDate());
		weekTask.setEndDate(domain.getEndDate());
		weekTask.setTaskTitle(domain.getTaskTitle());
		weekTask.setRemark(domain.getRemark());
		weekTask.setLastModifiedBy(userId);
		weekTask.setClassHour(domain.getClassHour());
		
		save(weekTask);
		
//		weekTaskTeamRepository.logicDeleteByWeekTaskId(weekTask.getId());
//		ArrayList<WeekTaskTeam> teamList = new ArrayList<WeekTaskTeam>();
//		for(Long teamId :domain.getPracticeTeamList()){
//			WeekTaskTeam weekTaskTeam = new WeekTaskTeam();
//			String weekTaskTeamId = UUID.randomUUID().toString();
//			weekTaskTeam.setId(weekTaskTeamId);
//			weekTaskTeam.setOrgId(weekTask.getOrgId());
//			weekTaskTeam.setWeekTaskId(weekTask.getId());
//			weekTaskTeam.setTeamId(teamId);
//			teamList.add(weekTaskTeam);
//		}
//		weekTaskTeamRepository.save(teamList);
		
//		 pushMessageService.createPushMessage(menTaskId, MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_MENTOR_ADD_TASK),
//                 PushMessageConstants.FUNCTION_STUDENT_NOTICE,
//                 PushMessageConstants.MODULE_TASK, MessageCode.MESSAGE_TITLE_ADD_TASK,
//                 ids);
//         pushService.listPush(token, MessageCode.MESSAGE_ADD_TASK_PUSH, MessageCode.MESSAGE_TITLE_ADD_TASK, MessageCode.MESSAGE_TITLE_ADD_TASK,
//                 ids);

	}
	
	/**
	 * 删除周任务
	 * @param weekTaskId
	 */
	@Transactional
	public void deleteWeekTask(String weekTaskId){
		
		WeekTask weekTask = findById(weekTaskId);
		if(null != weekTask){
			//判断周任务下是否有子任务
			Long countNum = mentorTaskService.countByWeekTaskId(weekTaskId);
			if (countNum.longValue() > 0L) {
				throw new CommonException(ErrorCode.DELETE_CONFLICT,
						"只有未提交的任务信息可以删除");
			}
			
			weekTask.setDeleteFlag(DataValidity.INVALID.getIntValue());
			save(weekTask);
		}
	}
	
	/**
	 * 获取周任务详情
	 * @param weekTaskId
	 * @return
	 */
	public WeekTaskDomain getDetail(String weekTaskId){
		
		WeekTask weekTask = findById(weekTaskId);
		if(null != weekTask){
			WeekTaskDomain domain = new WeekTaskDomain();
			BeanUtils.copyProperties(weekTask, domain);
//			List<WeekTaskTeam> teamList = weekTaskTeamRepository.findAllByDeleteFlagAndWeekTaskId(DataValidity.VALID.getIntValue(), weekTaskId);
//			ArrayList<Long> teamIdList = new ArrayList<Long>();
//			if(null != teamList && teamList.size() > 0){
//				for(WeekTaskTeam team : teamList){
//					teamIdList.add(team.getTeamId());
//				}
//			}
//			domain.setPracticeTeamList(teamIdList);
			return domain;
		}
		
		return null;
	}
	
	
	RowMapper<WeekTaskDomain> rm = new RowMapper<WeekTaskDomain>() {

		@Override
		public WeekTaskDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			WeekTaskDomain domain = new WeekTaskDomain();
			domain.setId(rs.getString("ID"));
			domain.setTaskTitle(rs.getString("TASK_TITLE"));
			domain.setWeekNo(rs.getString("WEEK_NO"));
			domain.setBeginDate(rs.getDate("BEGIN_DATE"));
			domain.setEndDate(rs.getDate("END_DATE"));
			domain.setRemark(rs.getString("REMARK"));
			domain.setClassHour(rs.getInt("CLASS_HOUR"));
			return domain;
		}
	};
	
	RowMapper<WeekTaskDomain> schoolRm = new RowMapper<WeekTaskDomain>() {

		@Override
		public WeekTaskDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			WeekTaskDomain domain = new WeekTaskDomain();
			domain.setId(rs.getString("ID"));
			domain.setTaskTitle(rs.getString("TASK_TITLE"));
			domain.setWeekNo(rs.getString("WEEK_NO"));
			domain.setBeginDate(rs.getDate("BEGIN_DATE"));
			domain.setEndDate(rs.getDate("END_DATE"));
			domain.setRemark(rs.getString("REMARK"));
			domain.setTaskNum(rs.getLong("task_num"));
			domain.setClassHour(rs.getInt("CLASS_HOUR"));
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
	public Map<String, Object> findPage(Integer pageSize,
			Integer offset, String taskName, Long orgId,String sortFlag) {

		String querySql = "SELECT *,(select count(1) from `SP_TASK` t where t.DELETE_FLAG = 0 and t.WEEK_TASK_ID = wt.id ) as task_num from `SP_WEEK_TASK` wt where DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `SP_WEEK_TASK` wt where DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(taskName)) {
			querySql += " and wt.TASK_TITLE like '%" + taskName + "%'";
			countSql += " and wt.TASK_TITLE like '%" + taskName + "%'";
		}
		querySql += " and wt.ORG_ID =" + orgId;
		countSql += " and wt.ORG_ID =" + orgId;
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("wt.CREATED_DATE");
		if("asc".equals(sortFlag)){
			dto.setAsc(true);
		}else{
			dto.setAsc(false);
		}
		sort.add(dto);

		return pageJdbcUtil.getPageInfor(pageSize, offset, schoolRm, sort, querySql,
				countSql);
	}
	
	public List<WeekTaskDomain> findNotIssued(QueryTaskPageDomain domain) {

		String querySql = "SELECT *,(select count(1) from `SP_TASK` t where t.DELETE_FLAG = 0 and t.WEEK_TASK_ID = wt.id ) as task_num from `SP_WEEK_TASK` wt where wt.ID NOT in (select week_task_id from `sp_week_task_team` where GROUP_ID = "+domain.getGroupId()+") and wt.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(domain.getTaskName())) {
			querySql += " and wt.TASK_TITLE like '%" + domain.getTaskName() + "%'";
		}
		return pageJdbcUtil.getInfo(querySql, schoolRm);
	}
	
	/**
	 * 企业导师周任务查询
	 * @param domain
	 * @param teamIds
	 * @return
	 */
	public  Map<String, Object> findPageForMentor(QueryTaskPageDomain domain,String groupIds,Long userId){
		
		Map<String, Object> result = this.findWeekTaskByGroupIds(domain.getPageSize(), domain.getPageNumber(), domain.getTaskName(), groupIds, domain.getSortFlag());
		if(null != result.get(ApiReturnConstants.DATA)){
			List<WeekTaskDomain> weekTaskList =(List<WeekTaskDomain>) result.get(ApiReturnConstants.DATA);
			String weekTaskIds = "";
			for(WeekTaskDomain weekTaskDomain : weekTaskList){
				if(StringUtils.isEmpty(weekTaskIds)){
					weekTaskIds ="'"+weekTaskDomain.getId()+"'";
				}else{
					weekTaskIds +=",'"+weekTaskDomain.getId()+"'";
				}
			}
			if(!StringUtils.isEmpty(weekTaskIds)){
//			 List<MentorCountDomain> countList = this.mentorCount(weekTaskIds,userId);
//			 HashMap<String,Double> countMap = new HashMap<String,Double>();
//			 for(MentorCountDomain countDomain :countList){
//				 BigDecimal bd = new BigDecimal(countDomain.getProgress());
//				 Double progess = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
//				 countMap.put(countDomain.getWeekTaskId(), progess);
//			 }
//			 for(WeekTaskDomain weekTaskDomain : weekTaskList){
//				 if(null != countMap.get(weekTaskDomain.getId())){
//					 weekTaskDomain.setProgress(countMap.get(weekTaskDomain.getId()).toString());
//				 }
//			 }
			}
		}
		
		return result;
	}
	
	
	/**
	 * 学生周任务查询
	 * @param domain
	 * @param groupIds
	 * @return
	 */
	public  Map<String, Object> findPageForStudent(QueryTaskPageDomain domain,String groupIds,Long userId){
		
		Map<String, Object> result = this.findWeekTaskByGroupIds(domain.getPageSize(), domain.getPageNumber(), domain.getTaskName(), groupIds, domain.getSortFlag());
		if(null != result.get(ApiReturnConstants.DATA)){
			List<WeekTaskDomain> weekTaskList =(List<WeekTaskDomain>) result.get(ApiReturnConstants.DATA);
			String weekTaskIds = "";
			for(WeekTaskDomain weekTaskDomain : weekTaskList){
				if(StringUtils.isEmpty(weekTaskIds)){
					weekTaskIds ="'"+weekTaskDomain.getId()+"'";
				}else{
					weekTaskIds +=",'"+weekTaskDomain.getId()+"'";
				}
			}
			if(!StringUtils.isEmpty(weekTaskIds)){
//			 List<IdNameDomain> countList = this.studentCount(weekTaskIds, userId);
//			 HashMap<String,Long> countMap = new HashMap<String,Long>();
			 List<IdNameDomain> unfinishList = this.studentUnfinishCount(weekTaskIds, userId);
			 HashMap<String,Long> unfinishMap = new HashMap<String,Long>();
//			 for(IdNameDomain countDomain :countList){
//				 countMap.put(countDomain.getName(), countDomain.getId());
//			 }
			 for(IdNameDomain countDomain :unfinishList){
				 unfinishMap.put(countDomain.getName(), countDomain.getId());
			 }
			 for(WeekTaskDomain weekTaskDomain : weekTaskList){
//				 if(null != countMap.get(weekTaskDomain.getId())){
//					 weekTaskDomain.setTotalNum(countMap.get(weekTaskDomain.getId()));
//				 }
				 if(null != unfinishMap.get(weekTaskDomain.getId())){
					 weekTaskDomain.setUnfinishNum(unfinishMap.get(weekTaskDomain.getId()));
				 }
			 }
			}
		}
		
		return result;
	}
	
	/**
	 * 为企业导师查询周任务分页列表信息
	 * @param pageSize
	 * @param offset
	 * @param taskName
	 * @param orgId
	 * @return
	 */
	public Map<String, Object> findWeekTaskByGroupIds(Integer pageSize,
			Integer offset, String taskName, String groupIds,String sortFlag) {

		String querySql = "select wt.id,wt.CLASS_HOUR,wt.week_no,wt.remark,wt.TASK_TITLE,wt.BEGIN_DATE,wt.END_DATE from `sp_week_task` wt where wt.DELETE_FLAG =0 and wt.ID in (select week_task_id from `sp_week_task_team` wtt where wtt.DELETE_FLAG =0 and wtt.group_id in ("+groupIds+")) ";
		String countSql = "SELECT count(1) from `sp_week_task` wt where wt.DELETE_FLAG =0 and wt.ID in (select week_task_id from `sp_week_task_team` wtt where wtt.DELETE_FLAG =0 and wtt.group_id in ("+groupIds+")) ";
		if (!StringUtils.isEmpty(taskName)) {
			querySql += " and wt.TASK_TITLE like '%" + taskName + "%'";
			countSql += " and wt.TASK_TITLE like '%" + taskName + "%'";
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("BEGIN_DATE");
		if("asc".equals(sortFlag)){
			dto.setAsc(true);
		}else{
			dto.setAsc(false);
		}
		sort.add(dto);

		return pageJdbcUtil.getPageInfor(pageSize, offset, rm, sort, querySql,
				countSql);
	}
	
	
	RowMapper<MentorCountDomain> mentorCountRm = new RowMapper<MentorCountDomain>() {

		@Override
		public MentorCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			MentorCountDomain domain = new MentorCountDomain();
			domain.setWeekTaskId(rs.getString("WEEK_TASK_ID"));
			domain.setProgress(rs.getDouble("PROGRESS"));
			return domain;
		}
	};
	
	/**
	 * 统计导师周任务完成率
	 */
	public List<MentorCountDomain> mentorCount(String weekTaskId,Long userId) {

		String querySql = "select WEEK_TASK_ID,avg(PROGRESS) as PROGRESS from `sp_mentor_task` mt where mt.DELETE_FLAG =0 and mt.WEEK_TASK_ID in ("+weekTaskId+") and mt.mentor_id="+userId+" GROUP BY mt.WEEK_TASK_ID   ";
		
		return pageJdbcUtil.getInfo(querySql, mentorCountRm);
	}
	
	
	RowMapper<IdNameDomain> studentCountRm = new RowMapper<IdNameDomain>() {

		@Override
		public IdNameDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			IdNameDomain domain = new IdNameDomain();
			domain.setName(rs.getString("WEEK_TASK_ID"));
			domain.setId(rs.getLong("count_num"));
			return domain;
		}
	};
	
	/**
	 * 统计学生任务总数
	 */
	public List<IdNameDomain> studentCount(String weekTaskId,Long userId) {

		String querySql = "select WEEK_TASK_ID,COUNT(1) as count_num from `sp_student_task` st where st.DELETE_FLAG =0 and st.WEEK_TASK_ID in ("+weekTaskId+") and st.STUDENT_ID ="+userId+" GROUP BY st.WEEK_TASK_ID ";
		
		return pageJdbcUtil.getInfo(querySql, studentCountRm);
	}
	
	/**
	 * 统计学生任务未完成数
	 */
	public List<IdNameDomain> studentUnfinishCount(String weekTaskId,Long userId) {

		String querySql = "select WEEK_TASK_ID,COUNT(1) as count_num from `sp_student_task` st where st.DELETE_FLAG =0 and st.WEEK_TASK_ID in ("+weekTaskId+") and st.REVIEW_SCORE is null and st.STUDENT_ID ="+userId+" GROUP BY st.WEEK_TASK_ID";
		
		return pageJdbcUtil.getInfo(querySql, studentCountRm);
	}
	
	public List<IdNameDomain> checkPendingTaskCount(String weekTaskId,Long userId) {

		String querySql = "select WEEK_TASK_ID,COUNT(1) as count_num from `sp_student_task` st where st.DELETE_FLAG =0 and st.WEEK_TASK_ID in ("+weekTaskId+") and st.REVIEW_SCORE is null and st.STUDENT_ID ="+userId+" GROUP BY st.WEEK_TASK_ID";
		
		return pageJdbcUtil.getInfo(querySql, studentCountRm);
	}

}
