package com.aizhixin.cloud.studentpractice.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.domain.CountDomain;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.QueryService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.summary.core.ReportStatusCode;
import com.aizhixin.cloud.studentpractice.summary.domain.QuerySummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.entity.Report;
import com.aizhixin.cloud.studentpractice.summary.service.ReportService;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.JoinStuNumCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskStatisticsDomain;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCount;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;
import com.aizhixin.cloud.studentpractice.task.entity.TaskStatistical;
import com.aizhixin.cloud.studentpractice.task.repository.PeopleCountDetailRepository;

@Transactional
@Service
public class PeopleCountDetailService {
	@Autowired
	private PeopleCountDetailRepository peopleCountDetailRepository;
	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	@Lazy
	private TaskStatisticalService taskStatisticalService;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;
	@Autowired
	@Lazy
	private ReportService reportService;
	@Autowired
	@Lazy
	private QueryService queryService;

	public void saveList(List<PeopleCountDetail> fileList) {
		peopleCountDetailRepository.save(fileList);
	}

	public List<PeopleCountDetail> findAllByStuIds(Set<Long> stuIds) {
		return peopleCountDetailRepository.findAllByStuIds(stuIds);
	}
	
	public List<PeopleCountDetail> findAllByStuIdsAndGroupId(Set<Long> stuIds,Long groupId) {
		return peopleCountDetailRepository.findAllByStuIdsAndGroupId(stuIds,groupId);
	}
	
	public PeopleCountDetail findByStuIdAndGroupId(Long studentId,Long groupId){
		return peopleCountDetailRepository.findOneByStudentIdAndGroupId(studentId,groupId);
	}

	public List<PeopleCountDetail> findAllByJoinPractice(Set<Long> groupIds) {
		return peopleCountDetailRepository
				.findAllByGroupIds(groupIds);
	}
	
	public List<PeopleCountDetail> findAllByGroupId(Long groupId) {
		return peopleCountDetailRepository
				.findAllByDeleteFlagAndGroupId(
						DataValidity.VALID.getIntValue(),
						groupId);
	}

	RowMapper<StuTaskStatisticsDomain> practiceDetailRm = new RowMapper<StuTaskStatisticsDomain>() {

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
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			String whetherPractice = rs.getString("WHETHER_PRACTICE");
			if (!StringUtils.isEmpty(whetherPractice)) {
				if (TaskStatusCode.JOIN_PRACTICE.equals(whetherPractice)) {
					domain.setActive(true);
				}
			}

			return domain;
		}
	};

	public Map<String, Object> practiceDetailStatistics(
			QueryStuPageDomain domain, Long orgId) {

		String querySql = "SELECT JOB_NUM,STUDENT_NAME,CLASS_NAME,PROFESSIONAL_NAME,COLLEGE_NAME,GRADE,WHETHER_PRACTICE,GROUP_NAME,COUNSELOR_NAME from `sp_people_count_detail` pcd where pcd.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `sp_people_count_detail` pcd where pcd.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords()
					+ "%' or pcd.JOB_NUM like '%" + domain.getKeyWords()
					+ "%')";
			countSql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords()
					+ "%' or pcd.JOB_NUM like '%" + domain.getKeyWords()
					+ "%')";
		}
		if (null != domain.getClassId() && domain.getClassId().longValue() > 0L) {
			querySql += " and pcd.CLASS_ID=" + domain.getClassId() + "";
			countSql += " and pcd.CLASS_ID=" + domain.getClassId() + "";
		}
		if (null != domain.getProfessionalId()
				&& domain.getProfessionalId().longValue() > 0L) {
			querySql += " and pcd.PROFESSIONAL_ID="
					+ domain.getProfessionalId() + "";
			countSql += " and pcd.PROFESSIONAL_ID="
					+ domain.getProfessionalId() + "";
		}
		if (null != domain.getCollegeId()
				&& domain.getCollegeId().longValue() > 0L) {
			querySql += " and pcd.COLLEGE_ID=" + domain.getCollegeId() + "";
			countSql += " and pcd.COLLEGE_ID=" + domain.getCollegeId() + "";
		}
		if (null != orgId && orgId.longValue() > 0L) {
			querySql += " and pcd.ORG_ID=" + orgId + "";
			countSql += " and pcd.ORG_ID=" + orgId + "";
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto = new SortDTO();
		dto.setKey("pcd.JOB_NUM");
		dto.setAsc(true);

		return pageJdbcUtil.getPageInfor(domain.getPageSize(),
				domain.getPageNumber(), practiceDetailRm, sort, querySql,
				countSql);
	}

	public void updateJoinPracticeTask() {
		
		Set<Long> groupIds = queryService.getAllGroupValidSet();
		if(null != groupIds && !groupIds.isEmpty()){
		List<PeopleCountDetail> joinList = this.findAllByJoinPractice(groupIds);
		for (PeopleCountDetail detail : joinList) {
			TaskStatistical taskStatistical = taskStatisticalService
					.findByStuIdAndGroupId(detail.getStudentId(),detail.getGroupId());
			if (null != taskStatistical) {
				if (taskStatistical.getCheckPendingNum() > 0) {
					detail.setWhetherCommit(TaskStatusCode.PRACTICE_COMMIT);
				} else if (taskStatistical.getBackToNum() > 0) {
					detail.setWhetherCommit(TaskStatusCode.PRACTICE_COMMIT);
				} else if (taskStatistical.getPassNum() > 0) {
					detail.setWhetherCommit(TaskStatusCode.PRACTICE_COMMIT);
				}

//				IdNameDomain cDomain = authUtilService.getClassTeacherId(detail
//						.getClassId());
//				if (null != cDomain.getId() && cDomain.getId().longValue() > 0L) {
//					detail.setCounselorId(cDomain.getId());
//					detail.setCounselorName(cDomain.getName());
//				}
			}
		}

		this.saveList(joinList);
		}

	}

	RowMapper<StuTaskStatisticsDomain> practiceJoinRm = new RowMapper<StuTaskStatisticsDomain>() {

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
			String whetherPractice = rs.getString("WHETHER_COMMIT");
			if (!StringUtils.isEmpty(whetherPractice)) {
				if (TaskStatusCode.PRACTICE_COMMIT.equals(whetherPractice)) {
					domain.setJoin(true);
				}
			}
			domain.setEnterpriseName(rs.getString("ENTERPRISE_NAME"));
			domain.setMentorName(rs.getString("MENTOR_NAME"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));

			return domain;
		}
	};

	public Map<String, Object> practiceJoinStatistics(
			QueryStuPageDomain domain, Long orgId) {

		String querySql = "SELECT JOB_NUM,STUDENT_NAME,CLASS_NAME,PROFESSIONAL_NAME,COLLEGE_NAME,GRADE,WHETHER_COMMIT,COUNSELOR_NAME,ENTERPRISE_NAME,MENTOR_NAME,GROUP_NAME from `sp_people_count_detail` pcd where pcd.DELETE_FLAG = 0  ";
		String countSql = "SELECT count(1) FROM `sp_people_count_detail` pcd where pcd.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords()
					+ "%' or pcd.JOB_NUM like '%" + domain.getKeyWords()
					+ "%')";
			countSql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords()
					+ "%' or pcd.JOB_NUM like '%" + domain.getKeyWords()
					+ "%')";
		}
		if (null != domain.getClassId() && domain.getClassId().longValue() > 0L) {
			querySql += " and pcd.CLASS_ID=" + domain.getClassId() + "";
			countSql += " and pcd.CLASS_ID=" + domain.getClassId() + "";
		}
		if (null != domain.getProfessionalId()
				&& domain.getProfessionalId().longValue() > 0L) {
			querySql += " and pcd.PROFESSIONAL_ID="
					+ domain.getProfessionalId() + "";
			countSql += " and pcd.PROFESSIONAL_ID="
					+ domain.getProfessionalId() + "";
		}
		if (null != domain.getCollegeId()
				&& domain.getCollegeId().longValue() > 0L) {
			querySql += " and pcd.COLLEGE_ID=" + domain.getCollegeId() + "";
			countSql += " and pcd.COLLEGE_ID=" + domain.getCollegeId() + "";
		}
		if (null != orgId && orgId.longValue() > 0L) {
			querySql += " and pcd.ORG_ID=" + orgId + "";
			countSql += " and pcd.ORG_ID=" + orgId + "";
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto = new SortDTO();
		dto.setKey("pcd.JOB_NUM");
		dto.setAsc(true);

		return pageJdbcUtil.getPageInfor(domain.getPageSize(),
				domain.getPageNumber(), practiceJoinRm, sort, querySql,
				countSql);
	}

	RowMapper<JoinStuNumCountDomain> countJoinNumRm = new RowMapper<JoinStuNumCountDomain>() {

		@Override
		public JoinStuNumCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			JoinStuNumCountDomain domain = new JoinStuNumCountDomain();
			domain.setClassId(rs.getLong("CLASS_ID"));
			String whetherCommit = rs.getString("WHETHER_COMMIT");
			Integer stuNum = rs.getInt("stu_num");
			if (StringUtils.isEmpty(whetherCommit)) {
				domain.setNotJoinNum(stuNum);
			} else {
				domain.setJoinNum(stuNum);
			}
			return domain;
		}
	};

	/**
	 * 按班级统计是否参与实践(提交过日志任务)的学生人数
	 * 
	 * @param domain
	 * @return
	 */
	public List<JoinStuNumCountDomain> countJoinStuNumByClass() {

		String countSql = "SELECT CLASS_ID,WHETHER_COMMIT,count(distinct(STUDENT_ID)) as stu_num FROM `sp_people_count_detail` where DELETE_FLAG = 0 and CLASS_ID is not null GROUP BY CLASS_ID,WHETHER_COMMIT ORDER BY CLASS_ID;";

		return pageJdbcUtil.getInfo(countSql, countJoinNumRm);

	}

	RowMapper<CountDomain> countSummaryRm = new RowMapper<CountDomain>() {

		@Override
		public CountDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			CountDomain domain = new CountDomain();
			domain.setId(rs.getLong("CLASS_ID"));
			domain.setCount(rs.getLong("summary_num"));
			return domain;
		}
	};

	/**
	 * 按班级统计提交日志周志总数
	 * 
	 * @param domain
	 * @return
	 */
	public List<CountDomain> countSummaryNumByClass() {

		String countSql = "SELECT CLASS_ID,sum(SUMMARY_TOTAL_NUM) as summary_num FROM `sp_people_count_detail` where DELETE_FLAG = 0 and CLASS_ID is not null GROUP BY CLASS_ID ORDER BY CLASS_ID;";

		return pageJdbcUtil.getInfo(countSql, countSummaryRm);

	}

	/**
	 * 统计学生提交实践报告状态   
	 */
//	public void updateStuReportStatusTask() {
//
//		List<IdNameDomain> groupList = queryService.getAllGroupValid();
//		if(null != groupList && !groupList.isEmpty()){
//			for(IdNameDomain groupDTO : groupList){
//			List<CountDomain> reportCountList = reportService.countStuReportNum(groupDTO.getId());
//			if (null != reportCountList && !reportCountList.isEmpty()) {
//				HashSet<Long> stuIds = new HashSet<Long>();
//				HashMap<Long, CountDomain> countMap = new HashMap<Long, CountDomain>();
//				for (CountDomain domain : reportCountList) {
//					stuIds.add(domain.getId());
//					countMap.put(domain.getId(), domain);
//				}
//				List<PeopleCountDetail> peopleCountDetailList = findAllByStuIdsAndGroupId(stuIds,groupDTO.getId());
//				for (PeopleCountDetail detail : peopleCountDetailList) {
//					CountDomain tmp = countMap.get(detail.getStudentId());
//				}
//				this.saveList(peopleCountDetailList);
//			}
//			}
//		}
//	}
	
	public void updateStuReportStatusTask() {

		List<IdNameDomain> groupList = queryService.getAllGroupValid();
		if(null != groupList && !groupList.isEmpty()){
			for(IdNameDomain groupDTO : groupList){
			List<Report> reportList = reportService.findAllByGroupId(groupDTO.getId());
			if (null != reportList && !reportList.isEmpty()) {
				HashSet<Long> stuIds = new HashSet<Long>();
				HashMap<Long, String> reportStatusMap = new HashMap<Long, String>();
				for (Report report : reportList) {
					stuIds.add(report.getCreatedBy());
					reportStatusMap.put(report.getCreatedBy(), report.getStatus());
				}
				List<PeopleCountDetail> peopleCountDetailList = findAllByStuIdsAndGroupId(stuIds,groupDTO.getId());
				for (PeopleCountDetail detail : peopleCountDetailList) {
					if(null != reportStatusMap.get(detail.getStudentId())){
						detail.setReportStatus(reportStatusMap.get(detail.getStudentId()));
					}else{
						detail.setReportStatus(ReportStatusCode.REPORT_STATUS_UNCOMMIT);
					}
				}
				this.saveList(peopleCountDetailList);
			}
			}
		}
	}

	RowMapper<CountDomain> countReportRm = new RowMapper<CountDomain>() {

		@Override
		public CountDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			CountDomain domain = new CountDomain();
			domain.setId(rs.getLong("CLASS_ID"));
			domain.setCount(rs.getLong("report_num"));
			return domain;
		}
	};

	/**
	 * 按班级统计提交实践报告总数
	 * 
	 * @param domain
	 * @return
	 */
	public List<CountDomain> countReportNumByClass() {

		String countSql = "SELECT CLASS_ID,count(1) as report_num FROM `sp_people_count_detail` where DELETE_FLAG = 0 and CLASS_ID is not null and REPORT_STATUS in('checkPending','backTo','finish') GROUP BY CLASS_ID ORDER BY CLASS_ID;";

		return pageJdbcUtil.getInfo(countSql, countReportRm);

	}

}
