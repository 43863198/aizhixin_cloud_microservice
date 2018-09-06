package com.aizhixin.cloud.studentpractice.task.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.domain.CorporateMentorsInfoByStudentDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.domain.TrainingRelationInfoDTO;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.DistributeLock;
import com.aizhixin.cloud.studentpractice.common.service.QueryService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.CountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.EnterpriseCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.GroupStuDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskStatisticsDomain;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCount;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;
import com.aizhixin.cloud.studentpractice.task.entity.TaskStatistical;
import com.aizhixin.cloud.studentpractice.task.repository.PeopleCountDetailRepository;
import com.aizhixin.cloud.studentpractice.task.repository.PeopleCountRepository;
import com.aizhixin.cloud.studentpractice.task.repository.TaskStatisticalRepository;

@Transactional
@Service
public class TaskStatisticalService {
	private static Logger log = LoggerFactory
			.getLogger(TaskStatisticalService.class);

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private StudentTaskService studentTaskService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private TaskStatisticalRepository taskStatisticalRepository;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private PeopleCountDetailRepository peopleCountDetailRepository;
	@Autowired
	private PeopleCountRepository peopleCountRepository;
	@Autowired
	private DistributeLock distributeLock;
	@Autowired
	@Lazy
	private QueryService queryService;
	
	
	

	RowMapper<StuTaskStatisticsDomain> rm = new RowMapper<StuTaskStatisticsDomain>() {

		@Override
		public StuTaskStatisticsDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			StuTaskStatisticsDomain domain = new StuTaskStatisticsDomain();
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setEnterpriseName(rs.getString("ENTERPRISE_NAME"));
			domain.setMentorName(rs.getString("MENTOR_NAME"));
			domain.setTotalNum(rs.getInt("TOTAL_NUM"));
			domain.setPassNum(rs.getInt("PASS_NUM"));
			domain.setNotPassNum(rs.getInt("NOT_PASS_NUM"));
			domain.setBackToNum(rs.getInt("BACK_TO_NUM"));
			domain.setCheckPendingNum(rs.getInt("CHECK_PENDING_NUM"));
			domain.setUncommitNum(rs.getInt("UNCOMMIT_NUM"));
			return domain;
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
	public Map<String, Object> stuTaskStatistics(QueryStuPageDomain domain,
			Long orgId) {

		String querySql = "SELECT JOB_NUM,STUDENT_NAME,ENTERPRISE_NAME,MENTOR_NAME,TOTAL_NUM,PASS_NUM,NOT_PASS_NUM,BACK_TO_NUM,CHECK_PENDING_NUM,UNCOMMIT_NUM from `SP_TASK_STATISTICAL` ts where ts.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `SP_TASK_STATISTICAL` ts where ts.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(domain.getStuName())) {
			querySql += " and ts.STUDENT_NAME like '%" + domain.getStuName()
					+ "%'";
			countSql += " and ts.STUDENT_NAME like '%" + domain.getStuName()
					+ "%'";
		}
		if (!StringUtils.isEmpty(domain.getEnterpriseName())) {
			querySql += " and ts.ENTERPRISE_NAME = '"
					+ domain.getEnterpriseName() + "'";
			countSql += " and ts.ENTERPRISE_NAME = '"
					+ domain.getEnterpriseName() + "'";
		}
		querySql += " and ts.ORG_ID =" + orgId;
		countSql += " and ts.ORG_ID =" + orgId;
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("ts.ORG_ID");
		dto.setAsc(true);
		sort.add(dto);
		dto = new SortDTO();
		dto.setKey("ts.JOB_NUM");
		dto.setAsc(true);

		return pageJdbcUtil.getPageInfor(domain.getPageSize(),
				domain.getPageNumber(), rm, sort, querySql, countSql);
	}

	RowMapper<StuInforDomain> stuRm = new RowMapper<StuInforDomain>() {

		@Override
		public StuInforDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			StuInforDomain domain = new StuInforDomain();
			domain.setId(rs.getLong("STUDENT_ID"));
			domain.setMentorCompanyName(rs.getString("ENTERPRISE_NAME"));
			return domain;
		}
	};

	/**
	 * 查询学生实践信息
	 * 
	 * @return
	 */
	public List<StuInforDomain> findStuInfor() {

		String querySql = " SELECT DISTINCT STUDENT_ID,ENTERPRISE_NAME FROM `sp_student_task` where DELETE_FLAG = 0 and org_id is not null and DATE_SUB(CURDATE(), INTERVAL 6 MONTH) <= date(CREATED_DATE) ";

		return pageJdbcUtil.getInfo(querySql, stuRm);
	}

	RowMapper<StuTaskStatisticsDomain> countRm = new RowMapper<StuTaskStatisticsDomain>() {

		@Override
		public StuTaskStatisticsDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			StuTaskStatisticsDomain domain = new StuTaskStatisticsDomain();
			domain.setTotalNum(rs.getInt("taskNum"));
			domain.setStudentTaskStatus(rs.getString("STUDENT_TASK_STATUS"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setEnterpriseName(rs.getString("ENTERPRISE_NAME"));
			domain.setMentorName(rs.getString("MENTOR_NAME"));
			domain.setOrgId(rs.getLong("ORG_ID"));
			domain.setStudentId(rs.getLong("STUDENT_ID"));
			domain.setClassId(rs.getLong("CLASS_ID"));
			domain.setCollegeId(rs.getLong("COLLEGE_ID"));
			domain.setProfessionalId(rs.getLong("PROFESSIONAL_ID"));
			domain.setGroupId(rs.getLong("group_id"));
			return domain;
		}
	};

	/**
	 * 统计学生实践任务数
	 * 
	 * @param userId
	 * @return
	 */
	public TaskStatistical countStuTaskStatusNum(Long userId,
			Long groupId) {
		TaskStatistical domain = new TaskStatistical();
		String sql = "select count(id) as taskNum,st.CLASS_ID,st.COLLEGE_ID,st.PROFESSIONAL_ID,st.ORG_ID,st.STUDENT_ID,st.student_task_status,JOB_NUM,STUDENT_NAME,ENTERPRISE_NAME,(select DISTINCT mentor_name from `sp_mentor_task` mt where mt.id =st.MENTOR_TASK_ID ) as MENTOR_NAME,st.group_id  from `sp_student_task` st where st.DELETE_FLAG = 0 and st.student_id ="
				+ userId
				+ " and st.group_id ="
				+ groupId
				+ " GROUP BY st.student_task_status ";
		List<StuTaskStatisticsDomain> list = pageJdbcUtil.getInfo(sql, countRm);
		if (null != list && list.size() > 0) {
			int backToNum = 0;
			int checkPendingNum = 0;
			int notPassNum = 0;
			int passNum = 0;
			int uncommitNum = 0;
			
			for (StuTaskStatisticsDomain dto : list) {

				BeanUtils.copyProperties(dto, domain);
				if (TaskStatusCode.TASK_STATUS_BACK_TO.equals(dto
						.getStudentTaskStatus())) {
					backToNum = dto.getTotalNum();
				} else if (TaskStatusCode.TASK_STATUS_CHECK_PENDING.equals(dto
						.getStudentTaskStatus())) {
					checkPendingNum = dto.getTotalNum();
				} else if (TaskStatusCode.TASK_STATUS_NOT_PASS.equals(dto
						.getStudentTaskStatus())) {
					notPassNum = dto.getTotalNum();
				} else if (TaskStatusCode.TASK_STATUS_PASS.equals(dto
						.getStudentTaskStatus())) {
					passNum = dto.getTotalNum();
				} else if (TaskStatusCode.TASK_STATUS_UNCOMMIT.equals(dto
						.getStudentTaskStatus())) {
					uncommitNum = dto.getTotalNum();
				}else if (TaskStatusCode.TASK_STATUS_FINISH.equals(dto
						.getStudentTaskStatus())) {
					passNum = dto.getTotalNum();
				}
			}
			
			domain.setBackToNum(backToNum);
			domain.setCheckPendingNum(checkPendingNum);
			domain.setNotPassNum(notPassNum);
			domain.setPassNum(passNum);
			domain.setUncommitNum(uncommitNum);
		}
		domain.setTotalNum(domain.getBackToNum() + domain.getCheckPendingNum()
				+ domain.getNotPassNum() + domain.getPassNum()
				+ domain.getUncommitNum());
		return domain;
	}

	/**
	 * 统计学生实践任务数量
	 */
	public void taskStatistics(Long groupId) {

		List<TaskStatistical> tsList = new ArrayList<TaskStatistical>();
//		List<StuInforDomain> stuList = this.findStuInfor();
//		for (StuInforDomain stu : stuList) {
//			TaskStatistical domain = this.countStuTaskStatusNum(stu.getId(),
//					stu.getMentorCompanyName());
//			List<TaskStatistical> statisticalList = findByStuIdAndEnterpriseName(stu);
//			if (statisticalList.size() == 0) {
//				tsList.add(domain);
//			} else {
//				TaskStatistical tmp = statisticalList.get(0);
//				tmp.setTotalNum(domain.getTotalNum());
//				tmp.setBackToNum(domain.getBackToNum());
//				tmp.setCheckPendingNum(domain.getCheckPendingNum());
//				tmp.setNotPassNum(domain.getNotPassNum());
//				tmp.setPassNum(domain.getPassNum());
//				tmp.setUncommitNum(domain.getUncommitNum());
//				tmp.setLastModifiedDate(new Date());
//				tsList.add(tmp);
//			}
//		}
		
		List<GroupStuDomain> stuList =  null;
		if(null == groupId){
			stuList = queryService.getAllGroupStuValid();
		}else{
			stuList = queryService.getGroupStuValid(groupId);
		}
		for (GroupStuDomain stu : stuList) {
			TaskStatistical domain = this.countStuTaskStatusNum(stu.getStuId(),
					stu.getGroupId());
			if(domain.getTotalNum() == 0){
				continue;
			}
			Double totalScore = getTaskScore(stu.getGroupId(), stu.getStuId());
			BigDecimal bg = new BigDecimal(totalScore/domain.getTotalNum()).setScale(2, RoundingMode.HALF_UP);
			domain.setAvgScore(bg.doubleValue());
			
			TaskStatistical statistical = findByStuIdAndGroupId(stu.getStuId(),stu.getGroupId());
			if (null == statistical) {
				tsList.add(domain);
			} else {
				statistical.setTotalNum(domain.getTotalNum());
				statistical.setBackToNum(domain.getBackToNum());
				statistical.setCheckPendingNum(domain.getCheckPendingNum());
				statistical.setNotPassNum(domain.getNotPassNum());
				statistical.setPassNum(domain.getPassNum());
				statistical.setUncommitNum(domain.getUncommitNum());
				statistical.setAvgScore(domain.getAvgScore());
				statistical.setLastModifiedDate(new Date());
				if(null == statistical.getGroupId()){
					statistical.setGroupId(stu.getGroupId());
				}
				tsList.add(statistical);
			}
		}

		taskStatisticalRepository.save(tsList);
	}

	public TaskStatistical findByStuIdAndGroupId(
			Long stuId,Long groupId) {
		TaskStatistical statistical = taskStatisticalRepository
				.findOneByDeleteFlagAndStudentIdAndGroupId(
						DataValidity.VALID.getIntValue(), stuId,
						groupId);
		return statistical;
	}
	
	public List<TaskStatistical> findByStuId(Long stuId) {
		List<TaskStatistical> statisticalList = taskStatisticalRepository
				.findAllByDeleteFlagAndStudentId(
						DataValidity.VALID.getIntValue(), stuId);
		return statisticalList;
	}

	RowMapper<CountDomain> chartRm = new RowMapper<CountDomain>() {

		@Override
		public CountDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			CountDomain domain = new CountDomain();
			domain.setBackToNum(rs.getLong("BACK_TO_NUM"));
			domain.setCheckPendingNum(rs.getLong("CHECK_PENDING_NUM"));
			domain.setNotPassNum(rs.getLong("NOT_PASS_NUM"));
			domain.setPassNum(rs.getLong("PASS_NUM"));
			domain.setUncommitNum(rs.getLong("UNCOMMIT_NUM"));
			return domain;
		}
	};

	/**
	 * 按条件统计实践任务数量
	 * 
	 * @return
	 */
	public List<CountDomain> countChart(Long orgId, Long classId,
			Long professionId, Long collegeId) {

		String querySql = "SELECT SUM(ts.BACK_TO_NUM) as BACK_TO_NUM,SUM(ts.CHECK_PENDING_NUM) as CHECK_PENDING_NUM,SUM(ts.PASS_NUM) as PASS_NUM,SUM(ts.NOT_PASS_NUM) as NOT_PASS_NUM,SUM(ts.UNCOMMIT_NUM) as UNCOMMIT_NUM FROM `sp_task_statistical` ts WHERE ts.delete_Flag=0 and ORG_ID="
				+ orgId + " ";
		if (null != classId && classId.longValue() > 0L) {
			querySql += " and CLASS_ID=" + classId + "";
		}
		if (null != professionId && professionId.longValue() > 0L) {
			querySql += " and PROFESSIONAL_ID=" + professionId + "";
		}
		if (null != collegeId && collegeId.longValue() > 0L) {
			querySql += " and COLLEGE_ID=" + collegeId + "";
		}

		return pageJdbcUtil.getInfo(querySql, chartRm);
	}

	RowMapper<PeopleCount> collegeCountRm = new RowMapper<PeopleCount>() {

		@Override
		public PeopleCount mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			PeopleCount domain = new PeopleCount();
			domain.setCollegeId(rs.getLong("COLLEGE_ID"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setPraticeNum(rs.getInt("PRACTICE_NUM"));
			domain.setNotPraticeNum(rs.getInt("NOT_PRACTICE_NUM"));
			domain.setStuNum(rs.getInt("STU_NUM"));
			return domain;
		}
	};
	
	
	RowMapper<PeopleCount> perfessionalCountRm = new RowMapper<PeopleCount>() {

		@Override
		public PeopleCount mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			PeopleCount domain = new PeopleCount();
			domain.setCollegeId(rs.getLong("COLLEGE_ID"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setProfessionalId(rs.getLong("PROFESSIONAL_ID"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setPraticeNum(rs.getInt("PRACTICE_NUM"));
			domain.setNotPraticeNum(rs.getInt("NOT_PRACTICE_NUM"));
			domain.setStuNum(rs.getInt("STU_NUM"));
			return domain;
		}
	};
	
	/**
	 * 按院系，专业，班级人数统计
	 * @param orgId
	 * @param professionId
	 * @param collegeId
	 * @return
	 */
	public List<PeopleCount> queryPeopleCount(Long orgId,
			Long professionId, Long collegeId){
		
		String querySql ="";
		if(null != collegeId && collegeId.longValue() > 0){
			if(null != professionId && professionId.longValue() > 0){
				//按班级统计
				querySql ="SELECT * FROM `sp_people_count` where DELETE_FLAG = 0 and ORG_ID="+orgId+" and COLLEGE_ID ="+collegeId+" and PROFESSIONAL_ID ="+professionId+"";
				
				return pageJdbcUtil.getInfo(querySql, peopleCountRm);
			}else{
				//按专业统计
				querySql ="SELECT COLLEGE_ID,COLLEGE_NAME,PROFESSIONAL_ID,PROFESSIONAL_NAME,sum(PRACTICE_NUM) as PRACTICE_NUM,sum(NOT_PRACTICE_NUM) as NOT_PRACTICE_NUM,sum(STU_NUM) as STU_NUM FROM `sp_people_count` where DELETE_FLAG = 0 and ORG_ID="+orgId+" and COLLEGE_ID ="+collegeId+" group by PROFESSIONAL_ID ";
				return pageJdbcUtil.getInfo(querySql, perfessionalCountRm);
			}
			
		}else{
			//按院系统计
			querySql = "SELECT COLLEGE_ID,COLLEGE_NAME,sum(PRACTICE_NUM) as PRACTICE_NUM,sum(NOT_PRACTICE_NUM) as NOT_PRACTICE_NUM,sum(STU_NUM) as STU_NUM FROM `sp_people_count` where DELETE_FLAG = 0 and ORG_ID="+orgId+" group by COLLEGE_ID ";
			
			return pageJdbcUtil.getInfo(querySql, collegeCountRm);
		}
		
	}
	
	
	RowMapper<PeopleCount> peopleCountRm = new RowMapper<PeopleCount>() {

		@Override
		public PeopleCount mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			PeopleCount domain = new PeopleCount();
			domain.setClassId(rs.getLong("CLASS_ID"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setCollegeId(rs.getLong("COLLEGE_ID"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setProfessionalId(rs.getLong("PROFESSIONAL_ID"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setOrgId(rs.getLong("ORG_ID"));
			domain.setStuNum(rs.getInt("STU_NUM"));
			domain.setPraticeNum(rs.getInt("PRACTICE_NUM"));
			domain.setNotPraticeNum(rs.getInt("NOT_PRACTICE_NUM"));
			return domain;
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
	public Map<String, Object> getPeopleCountPage(QueryStuPageDomain domain,
			Long orgId) {

		String querySql = "SELECT * from `SP_PEOPLE_COUNT` where DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `SP_PEOPLE_COUNT` where DELETE_FLAG = 0 ";
		if (null != domain.getProfessionalId()
				&& domain.getProfessionalId().longValue() > 0L) {
			querySql += " and PROFESSIONAL_ID = " + domain.getProfessionalId()
					+ "";
			countSql += " and PROFESSIONAL_ID = " + domain.getProfessionalId()
					+ "";
		}
		if (null != domain.getCollegeId()
				&& domain.getCollegeId().longValue() > 0L) {
			querySql += " and COLLEGE_ID = " + domain.getCollegeId() + "";
			countSql += " and COLLEGE_ID = " + domain.getCollegeId() + "";
		}
		querySql += " and ORG_ID =" + orgId;
		countSql += " and ORG_ID =" + orgId;
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("ORG_ID");
		dto.setAsc(true);
		sort.add(dto);
		dto = new SortDTO();
		dto.setKey("CLASS_ID");
		dto.setAsc(true);

		return pageJdbcUtil
				.getPageInfor(domain.getPageSize(), domain.getPageNumber(),
						peopleCountRm, sort, querySql, countSql);
	}

	RowMapper<PeopleCountDetail> peopleCountDetailRm = new RowMapper<PeopleCountDetail>() {

		@Override
		public PeopleCountDetail mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			PeopleCountDetail domain = new PeopleCountDetail();
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentId(rs.getLong("STUDENT_ID"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setEnterpriseName(rs.getString("ENTERPRISE_NAME"));
			domain.setMentorId(rs.getLong("MENTOR_ID"));
			domain.setMentorName(rs.getString("MENTOR_NAME"));
			domain.setStudentSex(rs.getString("STUDENT_SEX"));
			domain.setStudentPhone(rs.getString("STUDENT_PHONE"));
			domain.setMentorPhone(rs.getString("MENTOR_PHONE"));
			domain.setWhetherPractice(rs.getString("WHETHER_PRACTICE"));
			return domain;
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
	public Map<String, Object> getPeopleCountDetailPage(
			QueryStuPageDomain domain, Long orgId) {

		String querySql = "SELECT * from `SP_PEOPLE_COUNT_DETAIL` where DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `SP_PEOPLE_COUNT_DETAIL`  where DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(domain.getStuName())) {
			querySql += " and STUDENT_NAME like '%" + domain.getStuName()
					+ "%'";
			countSql += " and STUDENT_NAME like '%" + domain.getStuName()
					+ "%'";
		}
		querySql += " and CLASS_ID=" + domain.getClassId() + "";
		countSql += " and CLASS_ID=" + domain.getClassId() + "";
//		querySql += " and ORG_ID =" + orgId;
//		countSql += " and ORG_ID =" + orgId;
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("ORG_ID");
		dto.setAsc(true);
		sort.add(dto);
		dto = new SortDTO();
		dto.setKey("JOB_NUM");
		dto.setAsc(true);

		return pageJdbcUtil.getPageInfor(domain.getPageSize(),
				domain.getPageNumber(), peopleCountDetailRm, sort, querySql,
				countSql);
	}

	RowMapper<IdNameDomain> orgIdRm = new RowMapper<IdNameDomain>() {

		@Override
		public IdNameDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			IdNameDomain domain = new IdNameDomain();
			domain.setId(rs.getLong("ORG_ID"));
			return domain;
		}
	};
	
	
	RowMapper<IdNameDomain> stuPracticeRm = new RowMapper<IdNameDomain>() {

		@Override
		public IdNameDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			IdNameDomain domain = new IdNameDomain();
			domain.setId(rs.getLong("STUDENT_ID"));
			return domain;
		}
	};
	
	
	RowMapper<PeopleCount> classInforRm = new RowMapper<PeopleCount>() {

		@Override
		public PeopleCount mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			PeopleCount domain = new PeopleCount();
			domain.setClassId(rs.getLong("CLASS_ID"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setCollegeId(rs.getLong("COLLEGE_ID"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setProfessionalId(rs.getLong("PROFESSIONAL_ID"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setOrgId(rs.getLong("ORG_ID"));
			return domain;
		}
	};
	
	RowMapper<PeopleCountDetail> stuInforRm = new RowMapper<PeopleCountDetail>() {

		@Override
		public PeopleCountDetail mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			PeopleCountDetail domain = new PeopleCountDetail();
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentId(rs.getLong("STUDENT_ID"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setStudentSex(rs.getString("STUDENT_SEX"));
			domain.setStudentPhone(rs.getString("STUDENT_PHONE"));
			domain.setGrade(rs.getString("TEACHING_YEAR"));
			return domain;
		}
	};
	
	RowMapper<PeopleCountDetail> mentorInforRm = new RowMapper<PeopleCountDetail>() {

		@Override
		public PeopleCountDetail mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			PeopleCountDetail domain = new PeopleCountDetail();
			domain.setEnterpriseName(rs.getString("ENTERPRISE_NAME"));
			domain.setMentorId(rs.getLong("MENTOR_ID"));
			domain.setMentorName(rs.getString("MENTOR_NAME"));
			domain.setMentorPhone(rs.getString("MENTOR_PHONE"));
			domain.setProvince(rs.getString("PROVINCE"));
			domain.setCity(rs.getString("CITY"));
			return domain;
		}
	};
	
	/**
	 * 实践人数统计定时任务
	 */
//	public void countPeopleNum() {
//		
//		//查询开启企业实践的机构
//		String orgIds ="";
//		String org_sql = "SELECT DISTINCT(org_id) FROM `sp_student_task` where DELETE_FLAG = 0 and DATE_SUB(CURDATE(), INTERVAL 6 MONTH) <= date(CREATED_DATE) ";
//		List<IdNameDomain> orgIdList = pageJdbcUtil.getInfo(org_sql, orgIdRm);
//		if(null != orgIdList && orgIdList.size() > 0){
//			for(IdNameDomain tmp:orgIdList){
//				if(StringUtils.isEmpty(orgIds)){
//					orgIds += String.valueOf(tmp.getId());
//				}else{
//					orgIds += ","+String.valueOf(tmp.getId());
//				}
//			}
//		
//			String orgDbName = authUtilService.getOrgDbName();
//			String class_sql = "SELECT DISTINCT(cl.ORG_ID),cl.ID as CLASS_ID,cl.`NAME` as CLASS_NAME,cl.PROFESSIONAL_ID,p.`NAME` as PROFESSIONAL_NAME,cl.COLLEGE_ID,co.`NAME` as COLLEGE_NAME FROM "+orgDbName+".`t_college` co LEFT JOIN "+orgDbName+".`t_classes` cl ON cl.COLLEGE_ID = co.ID LEFT JOIN "+orgDbName+".`t_professional` p ON cl.PROFESSIONAL_ID = p.ID where cl.DELETE_FLAG = 0 and co.DELETE_FLAG = 0 and p.DELETE_FLAG = 0 and cl.org_id in("+orgIds+") order by cl.COLLEGE_ID,cl.PROFESSIONAL_ID,cl.ID";
//			List<PeopleCount> peopleCountList = pageJdbcUtil.getInfo(class_sql, classInforRm);
//			
//			HashMap<Long,PeopleCountDetail> detailMap = new HashMap<Long,PeopleCountDetail>();
//			if(null != peopleCountList && peopleCountList.size() > 0){
//				
//				HashSet<PeopleCountDetail> detailList = new HashSet<PeopleCountDetail>();
//				for(PeopleCount peopleCount :peopleCountList){
//					//查询班级下所有学生信息
//					String stu_sql = "select u.JOB_NUMBER as job_num,u.ACCOUNT_ID as STUDENT_ID,u.`NAME` as STUDENT_NAME,u.SEX as STUDENT_SEX,u.PHONE as STUDENT_PHONE,c.TEACHING_YEAR from "+orgDbName+".`t_user` u LEFT JOIN "+orgDbName+".`t_classes` c ON u.CLASSES_ID = c.ID where u.DELETE_FLAG = 0 and USER_TYPE = 70 and u.org_id ="+peopleCount.getOrgId()+" and u.classes_id ="+peopleCount.getClassId()+" and u.PROFESSIONAL_ID="+peopleCount.getProfessionalId()+" and u.college_id ="+peopleCount.getCollegeId()+"";
//					List<PeopleCountDetail> peopleDetailList = pageJdbcUtil.getInfo(stu_sql, stuInforRm);
//					if(null != peopleDetailList && peopleDetailList.size() > 0){
//						String userIds = "";
//						for(PeopleCountDetail detail :peopleDetailList){
//							detail.setWhetherPractice(TaskStatusCode.NOT_JOIN_PRACTICE);
//							detailMap.put(detail.getStudentId(), detail);
//							if(StringUtils.isEmpty(userIds)){
//								userIds += String.valueOf(detail.getStudentId());
//							}else{
//								userIds += ","+String.valueOf(detail.getStudentId());
//							}
//						}
//						//参加实践学生人数统计
//						String practice_stu_sql = "SELECT DISTINCT(USER_ID) as STUDENT_ID FROM "+orgDbName+".`t_group_relation_user` where DELETE_FLAG=0 AND USER_ID in("+userIds+");";
//						List<IdNameDomain> stuPracticeList = pageJdbcUtil.getInfo(practice_stu_sql, stuPracticeRm);
//						if(null != stuPracticeList && stuPracticeList.size() > 0){
//							for(IdNameDomain tmp : stuPracticeList){
//								Long stuId = tmp.getId();
//								String mentorInfor_sql = "SELECT cmi.ACCOUNT_ID as MENTOR_ID,cmi.ENTERPRISE_NAME,cmi.`NAME` as MENTOR_NAME,cmi.PHONE as MENTOR_PHONE,cmi.PROVINCE,cmi.CITY FROM "+orgDbName+".`t_group_relation_user` gru LEFT JOIN "+orgDbName+".`t_training_group` tg ON gru.GROUP_ID = tg.ID LEFT JOIN "+orgDbName+".`t_corporate_mentors_info` cmi ON cmi.ID = tg.CORPORATE_MENTORS_ID  where  gru.DELETE_FLAG = 0 and tg.DELETE_FLAG =0 and cmi.DELETE_FLAG =0 and gru.USER_ID ="+stuId+" ";
//								List<PeopleCountDetail> mentorList = pageJdbcUtil.getInfo(mentorInfor_sql, mentorInforRm);
//								if(null != mentorList && mentorList.size() > 0){
//									PeopleCountDetail detail = detailMap.get(stuId);
//									if(null != detail){
//										for(PeopleCountDetail mentor :mentorList){
//											detail.setMentorId(mentor.getMentorId());
//											detail.setMentorName(mentor.getMentorName());
//											detail.setMentorPhone(mentor.getMentorPhone());
//											detail.setEnterpriseName(mentor.getEnterpriseName());
//											detail.setOrgId(peopleCount.getOrgId());
//											detail.setProvince(mentor.getProvince());
//											detail.setCity(mentor.getCity());
//											detail.setWhetherPractice(TaskStatusCode.JOIN_PRACTICE);
//											detail.setCollegeId(peopleCount.getCollegeId());
//											detail.setCollegeName(peopleCount.getCollegeName());
//											detail.setProfessionalId(peopleCount.getProfessionalId());
//											detail.setProfessionalName(peopleCount.getProfessionalName());
//											detail.setClassId(peopleCount.getClassId());
//											detail.setClassName(peopleCount.getClassName());
//											detailList.add(detail);
//										}
//										detailMap.remove(stuId);
//									}
//								}
//							}
//							peopleCount.setPraticeNum(stuPracticeList.size());
//						}
//
//						peopleCount.setStuNum(peopleDetailList.size());
//						peopleCount.setNotPraticeNum(peopleCount.getStuNum()-peopleCount.getPraticeNum());
//					}
//				}
//				
//				//保存实践人数统计信息
//				for(PeopleCount peopleCount :peopleCountList){
//					PeopleCount peopleCountDb = peopleCountRepository.findOneByDeleteFlagAndClassIdAndCollegeIdAndProfessionalId(DataValidity.VALID.getIntValue(), peopleCount.getClassId(), peopleCount.getCollegeId(), peopleCount.getProfessionalId());
//					if(null != peopleCountDb){
//						peopleCount.setId(peopleCountDb.getId());
//					}
//				}
//				peopleCountRepository.save(peopleCountList);
//				
//				//删除实践人数统计详情表数据
//				jdbcTemplate.execute("TRUNCATE table `sp_people_count_detail`;");
//				
//				detailList.addAll(detailMap.values());
//				//保存实践人数统计详情表数据
//				peopleCountDetailRepository.save(detailList);
//			}
//		}
//	}
	
	
	public void countPeopleNum(Long groupId) {
		
		List<IdNameDomain> groupIdList =  null;
		if(null == groupId){
			groupIdList = queryService.getAllGroupValid();
		}else{
			groupIdList = new ArrayList<IdNameDomain>();
			IdNameDomain id = new IdNameDomain();
			id.setId(groupId);
			groupIdList.add(id);
		}
		if(null != groupIdList && !groupIdList.isEmpty()){
			List<Long> groupIds = new ArrayList<Long>();
			for(IdNameDomain domain :groupIdList){
				groupIds.add(domain.getId());
			}
			HashSet<Long> orgIdSet = new HashSet<Long>();
			HashMap<Long,Integer> practiseStuNumMap = new HashMap<Long,Integer>();
			List<TrainingRelationInfoDTO> groupInfoList = authUtilService.getGroupInfoListByIds(groupIds);
			for(TrainingRelationInfoDTO dto :groupInfoList){
				orgIdSet.add(dto.getOrgId());
				ArrayList<PeopleCountDetail> saveList = new ArrayList<PeopleCountDetail>();
				if(null != dto.getStudents() && !dto.getStudents().isEmpty()){
					for(CorporateMentorsInfoByStudentDTO stu :dto.getStudents()){
						if(null == practiseStuNumMap.get(stu.getClassesId())){
							practiseStuNumMap.put(stu.getClassesId(), 1);
						}else{
							practiseStuNumMap.put(stu.getClassesId(), practiseStuNumMap.get(stu.getClassesId())+1);
						}
						PeopleCountDetail detail = peopleCountDetailRepository.findOneByStudentIdAndGroupId(stu.getSid(), dto.getId());
						if(null != detail){
							detail.setMentorId(dto.getAccountId());
							detail.setMentorName(dto.getCorporateMentorsName());
							detail.setMentorPhone(dto.getCorporateMentorsPhone());
							detail.setEnterpriseId(dto.getEnterpriseId());
							detail.setEnterpriseName(dto.getEnterpriseName());
							detail.setCollegeId(stu.getCollegeId());
							detail.setCollegeName(stu.getCollegeName());
							detail.setProfessionalId(stu.getProfessionalId());
							detail.setProfessionalName(stu.getProfessionalName());
							detail.setClassId(stu.getClassesId());
							detail.setClassName(stu.getClassesName());
							detail.setCounselorId(dto.getTeacherId());
							detail.setCounselorName(dto.getTeacherName());
							detail.setJobNum(stu.getSjobNumber());
							detail.setStudentId(stu.getSid());
							detail.setStudentName(stu.getSname());
							detail.setStudentSex(stu.getSex());
							detail.setStudentPhone(stu.getSphone());
							saveList.add(detail);
						}else{
							detail = new PeopleCountDetail();
							detail.setMentorId(dto.getAccountId());
							detail.setMentorName(dto.getCorporateMentorsName());
							detail.setMentorPhone(dto.getCorporateMentorsPhone());
							detail.setEnterpriseId(dto.getEnterpriseId());
							detail.setEnterpriseName(dto.getEnterpriseName());
							detail.setOrgId(dto.getOrgId());
							detail.setWhetherPractice(TaskStatusCode.JOIN_PRACTICE);
							detail.setCollegeId(stu.getCollegeId());
							detail.setCollegeName(stu.getCollegeName());
							detail.setProfessionalId(stu.getProfessionalId());
							detail.setProfessionalName(stu.getProfessionalName());
							detail.setClassId(stu.getClassesId());
							detail.setClassName(stu.getClassesName());
							detail.setGroupId(dto.getId());
							detail.setGroupName(dto.getName());
							detail.setCounselorId(dto.getTeacherId());
							detail.setCounselorName(dto.getTeacherName());
							detail.setJobNum(stu.getSjobNumber());
							detail.setStudentId(stu.getSid());
							detail.setStudentName(stu.getSname());
							detail.setStudentSex(stu.getSex());
							detail.setStudentPhone(stu.getSphone());
							saveList.add(detail);
						}
					}
				}
				if(!saveList.isEmpty()){
					peopleCountDetailRepository.save(saveList);
				}
			}
			
			String orgIds = "";
			for(Long orgId : orgIdSet){
				if(StringUtils.isEmpty(orgIds)){
					orgIds = String.valueOf(orgId);
				}else{
					orgIds += ","+String.valueOf(orgId);
				}
			}
			
			String orgDbName = authUtilService.getOrgDbName();
			String stu_sql = "select u.CLASSES_ID,count(1) as total_num from "+orgDbName+".`t_user` u where u.DELETE_FLAG = 0 and u.CLASSES_ID is not null and USER_TYPE = 70 and u.org_id in("+orgIds+") GROUP BY u.CLASSES_ID;";
			//每个班级学生总人数
			List<com.aizhixin.cloud.studentpractice.common.domain.CountDomain> stuCountlList = pageJdbcUtil.getInfo(stu_sql, stuCountRm);
			HashMap<Long,Long> classStuTotalMap = new HashMap<Long,Long>();
			for(com.aizhixin.cloud.studentpractice.common.domain.CountDomain cdomain : stuCountlList){
				classStuTotalMap.put(cdomain.getId(), cdomain.getCount());
			}
			
			String class_sql = "SELECT DISTINCT(cl.ORG_ID),cl.ID as CLASS_ID,cl.`NAME` as CLASS_NAME,cl.PROFESSIONAL_ID,p.`NAME` as PROFESSIONAL_NAME,cl.COLLEGE_ID,co.`NAME` as COLLEGE_NAME FROM "+orgDbName+".`t_college` co LEFT JOIN "+orgDbName+".`t_classes` cl ON cl.COLLEGE_ID = co.ID LEFT JOIN "+orgDbName+".`t_professional` p ON cl.PROFESSIONAL_ID = p.ID where cl.DELETE_FLAG = 0 and co.DELETE_FLAG = 0 and p.DELETE_FLAG = 0 and cl.org_id in("+orgIds+") order by cl.COLLEGE_ID,cl.PROFESSIONAL_ID,cl.ID";
			//参与实践学校所有班级信息
			List<PeopleCount> peopleCountList = pageJdbcUtil.getInfo(class_sql, classInforRm);
			
			if(null != peopleCountList && !peopleCountList.isEmpty()){
				for(PeopleCount pc :peopleCountList){
					PeopleCount dbPc = peopleCountRepository.findOneByDeleteFlagAndClassIdAndCollegeIdAndProfessionalId(DataValidity.VALID.getIntValue(), pc.getClassId(), pc.getCollegeId(), pc.getProfessionalId());
					if(null != dbPc){
						pc.setId(dbPc.getId());
					}
					if(null != classStuTotalMap.get(pc.getClassId())){
						pc.setStuNum(classStuTotalMap.get(pc.getClassId()).intValue());
					}
					if(null != practiseStuNumMap.get(pc.getClassId())){
						pc.setPraticeNum(practiseStuNumMap.get(pc.getClassId()).intValue());
					}
					pc.setNotPraticeNum(pc.getStuNum()-pc.getPraticeNum());
				}
				peopleCountRepository.save(peopleCountList);
			}
		}
	}
	
	RowMapper<com.aizhixin.cloud.studentpractice.common.domain.CountDomain> stuCountRm = new RowMapper<com.aizhixin.cloud.studentpractice.common.domain.CountDomain>() {

		@Override
		public com.aizhixin.cloud.studentpractice.common.domain.CountDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			com.aizhixin.cloud.studentpractice.common.domain.CountDomain domain = new com.aizhixin.cloud.studentpractice.common.domain.CountDomain();
			domain.setId(rs.getLong("CLASSES_ID"));
			domain.setCount(rs.getLong("total_num"));
			return domain;
		}
	};
	
	
	RowMapper<CountDomain> peopleNumChartRm = new RowMapper<CountDomain>() {

		@Override
		public CountDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			CountDomain domain = new CountDomain();
			domain.setPracticeNum(rs.getLong("PRACTICE_NUM"));
			domain.setNotPracticeNum(rs.getLong("NOT_PRACTICE_NUM"));
			return domain;
		}
	};

	/**
	 * 按条件统计实践人数数量
	 * 
	 * @return
	 */
	public List<CountDomain> peopleCountChart(Long orgId, Long classId,
			Long professionId, Long collegeId) {

		String querySql = "SELECT SUM(PRACTICE_NUM) as PRACTICE_NUM ,SUM(NOT_PRACTICE_NUM) as NOT_PRACTICE_NUM FROM `sp_people_count` ts WHERE delete_Flag=0 and ORG_ID="
				+ orgId + " ";
		if (null != classId && classId.longValue() > 0L) {
			querySql += " and CLASS_ID=" + classId + "";
		}
		if (null != professionId && professionId.longValue() > 0L) {
			querySql += " and PROFESSIONAL_ID=" + professionId + "";
		}
		if (null != collegeId && collegeId.longValue() > 0L) {
			querySql += " and COLLEGE_ID=" + collegeId + "";
		}

		return pageJdbcUtil.getInfo(querySql, peopleNumChartRm);
	}
	
	
	RowMapper<EnterpriseCountDomain> enterpriseCountChartRm = new RowMapper<EnterpriseCountDomain>() {

		@Override
		public EnterpriseCountDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			EnterpriseCountDomain domain = new EnterpriseCountDomain();
			domain.setPracticeNum(rs.getLong("total_num"));
			domain.setEnterpriseName(rs.getString("ENTERPRISE_NAME"));
			return domain;
		}
	};

	
	public List<EnterpriseCountDomain> enterpriseCountChart(Long orgId){
		
		ArrayList<EnterpriseCountDomain> resultList = new ArrayList<EnterpriseCountDomain>();
		List<EnterpriseCountDomain> countList = getEnterpriseCount(orgId);
		if(null != countList && countList.size() > 0){
			if(countList.size() > 10){
				long otherCount = 0L;
				EnterpriseCountDomain domain = new EnterpriseCountDomain();
				domain.setEnterpriseName("其他");
				for(int i=0;i<countList.size();i++){
					EnterpriseCountDomain countDomain = countList.get(i);
					if(i > 9){
						otherCount += countDomain.getPracticeNum();
					}else{
						resultList.add(countDomain);
					}
				}
				domain.setPracticeNum(otherCount);
				resultList.add(domain);
				
			}else{
				resultList.addAll(countList);
			}
		}
		
		return resultList;
	}
	
	/**
	 * 按条件统计实践人数数量
	 * 
	 * @return
	 */
	public List<EnterpriseCountDomain> getEnterpriseCount(Long orgId) {

		String querySql = "SELECT count(1) as total_num,ENTERPRISE_NAME FROM `sp_people_count_detail` where DELETE_FLAG = 0 and ENTERPRISE_NAME is not null and WHETHER_PRACTICE = 'join' and ORG_ID= "+orgId+" GROUP BY ENTERPRISE_NAME order by total_num desc";

		return pageJdbcUtil.getInfo(querySql, enterpriseCountChartRm);
	}
	
	
	RowMapper<EnterpriseCountDomain> enterpriseLocationChartRm = new RowMapper<EnterpriseCountDomain>() {

		@Override
		public EnterpriseCountDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			EnterpriseCountDomain domain = new EnterpriseCountDomain();
			domain.setPracticeNum(rs.getLong("total_num"));
			domain.setEnterpriseName(rs.getString("ENTERPRISE_NAME"));
			domain.setProvince(rs.getString("PROVINCE"));
			domain.setCity(rs.getString("CITY"));
			return domain;
		}
	};
	
	/**
	 * 按机构查询实践公司实践人数和公司地址
	 * 
	 * @return
	 */
	public List<EnterpriseCountDomain> getEnterpriseLocation(Long orgId) {

		String querySql = "SELECT count(1) as total_num,ENTERPRISE_NAME,PROVINCE,CITY FROM `sp_people_count_detail` where DELETE_FLAG = 0 and ENTERPRISE_NAME is not null and WHETHER_PRACTICE = 'join' and ORG_ID= "+orgId+" GROUP BY ENTERPRISE_NAME order by total_num desc";

		return pageJdbcUtil.getInfo(querySql, enterpriseLocationChartRm);
	}
	
	
	
	RowMapper<IdNameDomain> enterpriseListRm = new RowMapper<IdNameDomain>() {

		@Override
		public IdNameDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			IdNameDomain domain = new IdNameDomain();
			domain.setName(rs.getString("ENTERPRISE_NAME"));
			return domain;
		}
	};
	
	/**
	 * 查询公司名称列表
	 * 
	 * @return
	 */
	public List<IdNameDomain> getEnterpriseList(Long orgId) {

		String querySql = "SELECT DISTINCT(pcd.ENTERPRISE_NAME) FROM `sp_people_count_detail` pcd where pcd.ENTERPRISE_NAME is not null and pcd.ORG_ID = "+orgId+";";

		return pageJdbcUtil.getInfo(querySql, enterpriseListRm);
	}
	
	
	
	RowMapper<StuTaskStatisticsDomain> summaryDetailRm = new RowMapper<StuTaskStatisticsDomain>() {

		@Override
		public StuTaskStatisticsDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			StuTaskStatisticsDomain domain = new StuTaskStatisticsDomain();
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setGrade(rs.getString("GRADE"));
			domain.setDailyNum(rs.getInt("DAILY_NUM"));
			domain.setWeeklyNum(rs.getInt("WEEKLY_NUM"));
			domain.setMonthlyNum(rs.getInt("MONTHLY_NUM"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			return domain;
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
	public Map<String, Object> summaryDetailStatistics(QueryStuPageDomain domain,Long orgId) {

		String querySql = "SELECT JOB_NUM,STUDENT_NAME,CLASS_NAME,PROFESSIONAL_NAME,COLLEGE_NAME,GRADE,DAILY_NUM,WEEKLY_NUM,MONTHLY_NUM,pcd.COUNSELOR_NAME,pcd.GROUP_NAME from `sp_people_count_detail` pcd where pcd.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `sp_people_count_detail` pcd where pcd.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords()+ "%' or pcd.JOB_NUM like '%" + domain.getKeyWords()+ "%')";
			countSql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords()+ "%' or pcd.JOB_NUM like '%" + domain.getKeyWords()+ "%')";
		}
		if (null != domain.getClassId() && domain.getClassId().longValue() > 0L) {
			querySql += " and pcd.CLASS_ID=" + domain.getClassId() + "";
			countSql += " and pcd.CLASS_ID=" + domain.getClassId() + "";
		}
		if (null != domain.getProfessionalId() && domain.getProfessionalId().longValue() > 0L) {
			querySql += " and pcd.PROFESSIONAL_ID=" + domain.getProfessionalId() + "";
			countSql += " and pcd.PROFESSIONAL_ID=" + domain.getProfessionalId() + "";
		}
		if (null != domain.getCollegeId() && domain.getCollegeId().longValue() > 0L) {
			querySql += " and pcd.COLLEGE_ID=" + domain.getCollegeId() + "";
			countSql += " and pcd.COLLEGE_ID=" + domain.getCollegeId() + "";
		}
		if (null != orgId && orgId.longValue() > 0L) {
			querySql += " and pcd.ORG_ID=" + orgId + "";
			countSql += " and pcd.ORG_ID=" + orgId + "";
		}
		if (null != domain.getStuId() && domain.getStuId() > 0L) {
			querySql += " and pcd.STUDENT_ID=" + domain.getStuId() + "";
			countSql += " and pcd.STUDENT_ID=" + domain.getStuId() + "";
		}
		if (null != domain.getCounselorId() && domain.getCounselorId() > 0L) {
			querySql += " and pcd.COUNSELOR_ID=" + domain.getCounselorId() + "";
			countSql += " and pcd.COUNSELOR_ID=" + domain.getCounselorId() + "";
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto = new SortDTO();
		dto.setKey("pcd.JOB_NUM");
		dto.setAsc(true);

		return pageJdbcUtil.getPageInfor(domain.getPageSize(),
				domain.getPageNumber(), summaryDetailRm, sort, querySql, countSql);
	}
	
	
	RowMapper<IdNameDomain> scoreRm = new RowMapper<IdNameDomain>() {

		@Override
		public IdNameDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			IdNameDomain domain = new IdNameDomain();
			domain.setName(rs.getString("REVIEW_SCORE"));
			return domain;
		}
	};
	
	/**
	 * 学生在实践参与计划中的平均分
	 * 
	 * @return
	 */
	public Double getTaskScore(Long groupId,Long stuId) {

		String querySql = "SELECT rt.REVIEW_SCORE FROM `sp_student_task` st LEFT JOIN `sp_review_task` rt ON rt.STUDENT_TASK_ID = st.ID where rt.DELETE_FLAG =0 and st.DELETE_FLAG = 0 and rt.REVIEW_RESULT = 'finish' and st.GROUP_ID ='"+groupId+"' and st.STUDENT_ID ="+stuId+";";
		List<IdNameDomain> list = pageJdbcUtil.getInfo(querySql, scoreRm);
		Double totalScore = 0d;
		if(null != list && !list.isEmpty()){
			for(IdNameDomain score : list){
				if(!StringUtils.isEmpty(score.getName())){
					totalScore += Double.parseDouble(score.getName());
				}
			}
		}
		
		return totalScore;
	}
	
	
	public void clearCount(){
		//删除sp_people_count_detail表数据
		jdbcTemplate.execute("TRUNCATE table `sp_people_count_detail`;");
		//删除sp_mentor_task_count表数据
		jdbcTemplate.execute("TRUNCATE table `sp_mentor_task_count`;");
		//删除sp_summary_count表数据
		jdbcTemplate.execute("TRUNCATE table `sp_summary_count`;");
		//删除sp_task_statistical表数据
		jdbcTemplate.execute("TRUNCATE table `sp_task_statistical`;");
	}

}
