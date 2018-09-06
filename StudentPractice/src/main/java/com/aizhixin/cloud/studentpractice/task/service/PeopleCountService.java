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
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.summary.domain.QuerySummaryDomain;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.JoinStuNumCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.PeopleCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskStatisticsDomain;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCount;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;
import com.aizhixin.cloud.studentpractice.task.entity.TaskStatistical;
import com.aizhixin.cloud.studentpractice.task.repository.PeopleCountDetailRepository;
import com.aizhixin.cloud.studentpractice.task.repository.PeopleCountRepository;

@Transactional
@Service
public class PeopleCountService {
	@Autowired
	private PeopleCountRepository peopleCountRepository;
	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	@Lazy
	private PeopleCountDetailService peopleCountDetailService;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;

	public void saveList(List<PeopleCount> fileList) {
		peopleCountRepository.save(fileList);
	}

	public List<PeopleCount> findAllByClassIds(Set<Long> classIds) {
		return peopleCountRepository.findAllByClassIds(classIds);
	}

	public void updateJoinStuNumTask() {

		List<JoinStuNumCountDomain> detailList = peopleCountDetailService
				.countJoinStuNumByClass();
		if (null != detailList && !detailList.isEmpty()) {
			HashSet<Long> classIds = new HashSet<Long>();
			HashMap<Long, JoinStuNumCountDomain> countMap = new HashMap<Long, JoinStuNumCountDomain>();
			for (JoinStuNumCountDomain domain : detailList) {
				classIds.add(domain.getClassId());
				if (null != countMap.get(domain.getClassId())) {
					JoinStuNumCountDomain tmp = countMap.get(domain
							.getClassId());
					if (null != domain.getJoinNum()
							&& domain.getJoinNum().intValue() > 0) {
						tmp.setJoinNum(domain.getJoinNum());
					}
					if (null != domain.getNotJoinNum()
							&& domain.getNotJoinNum().intValue() > 0) {
						tmp.setNotJoinNum(domain.getNotJoinNum());
					}
				} else {
					countMap.put(domain.getClassId(), domain);
				}
			}

			List<PeopleCount> peopleCountList = findAllByClassIds(classIds);
			for (PeopleCount peopleCount : peopleCountList) {
				if (null != countMap.get(peopleCount.getClassId())) {
					JoinStuNumCountDomain tmp = countMap.get(peopleCount
							.getClassId());
					peopleCount.setJoinNum(tmp.getJoinNum());
					peopleCount.setNotJoinNum(tmp.getNotJoinNum());
				}
			}

			this.saveList(peopleCountList);
		}
	}

	public void updateSummaryNumTask() {

		List<CountDomain> classCountList = peopleCountDetailService
				.countSummaryNumByClass();

		if (null != classCountList && !classCountList.isEmpty()) {
			HashSet<Long> classIds = new HashSet<Long>();
			HashMap<Long, CountDomain> countMap = new HashMap<Long, CountDomain>();
			for (CountDomain domain : classCountList) {
				classIds.add(domain.getId());
				countMap.put(domain.getId(), domain);
			}
			List<PeopleCount> peopleCountList = findAllByClassIds(classIds);
			for (PeopleCount peopleCount : peopleCountList) {
				CountDomain tmp = countMap.get(peopleCount.getClassId());
				peopleCount.setSummaryNum(tmp.getCount().intValue());
			}
			this.saveList(peopleCountList);
		}
	}

	public void updateReportNumTask() {

		List<CountDomain> classCountList = peopleCountDetailService
				.countReportNumByClass();

		if (null != classCountList && !classCountList.isEmpty()) {
			HashSet<Long> classIds = new HashSet<Long>();
			HashMap<Long, CountDomain> countMap = new HashMap<Long, CountDomain>();
			for (CountDomain domain : classCountList) {
				classIds.add(domain.getId());
				countMap.put(domain.getId(), domain);
			}
			List<PeopleCount> peopleCountList = findAllByClassIds(classIds);
			for (PeopleCount peopleCount : peopleCountList) {
				CountDomain tmp = countMap.get(peopleCount.getClassId());
				peopleCount.setReportNum(tmp.getCount().intValue());
			}
			this.saveList(peopleCountList);
		}
	}
	
	
	RowMapper<PeopleCountDomain> totalCountRm = new RowMapper<PeopleCountDomain>() {

		@Override
		public PeopleCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			PeopleCountDomain domain = new PeopleCountDomain();
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setStuNum(rs.getInt("STU_NUM"));
			domain.setPraticeNum(rs.getInt("PRACTICE_NUM"));
			domain.setNotPraticeNum(rs.getInt("NOT_PRACTICE_NUM"));
			domain.setJoinNum(rs.getInt("JOIN_NUM"));
			domain.setNotJoinNum(rs.getInt("NOT_JOIN_NUM"));
			domain.setSummaryNum(rs.getInt("SUMMARY_NUM"));
			domain.setReportNum(rs.getInt("REPORT_NUM"));
			return domain;
		}
	};

	public Map<String, Object> practiceTotalStatistics(
			QueryStuPageDomain domain, Long orgId) {

		String querySql = "SELECT CLASS_NAME,PROFESSIONAL_NAME,COLLEGE_NAME,STU_NUM,PRACTICE_NUM,NOT_PRACTICE_NUM,JOIN_NUM,NOT_JOIN_NUM,SUMMARY_NUM,REPORT_NUM from `sp_people_count` pc where 1 = 1  ";
		String countSql = "SELECT count(1) FROM `sp_people_count` pc where 1 = 1 ";
		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (pc.CLASS_NAME like '%" + domain.getKeyWords()
					+ "%' )";
			countSql += " and (pc.CLASS_NAME like '%" + domain.getKeyWords()
					+ "%' )";
		}
		if (null != domain.getClassId() && domain.getClassId().longValue() > 0L) {
			querySql += " and pc.CLASS_ID=" + domain.getClassId() + "";
			countSql += " and pc.CLASS_ID=" + domain.getClassId() + "";
		}
		if (null != domain.getProfessionalId()
				&& domain.getProfessionalId().longValue() > 0L) {
			querySql += " and pc.PROFESSIONAL_ID="
					+ domain.getProfessionalId() + "";
			countSql += " and pc.PROFESSIONAL_ID="
					+ domain.getProfessionalId() + "";
		}
		if (null != domain.getCollegeId()
				&& domain.getCollegeId().longValue() > 0L) {
			querySql += " and pc.COLLEGE_ID=" + domain.getCollegeId() + "";
			countSql += " and pc.COLLEGE_ID=" + domain.getCollegeId() + "";
		}
		if (null != orgId && orgId.longValue() > 0L) {
			querySql += " and pc.ORG_ID=" + orgId + "";
			countSql += " and pc.ORG_ID=" + orgId + "";
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto = new SortDTO();
		dto.setKey("pc.CLASS_ID");
		dto.setAsc(true);

		return pageJdbcUtil.getPageInfor(domain.getPageSize(),
				domain.getPageNumber(), totalCountRm, sort, querySql,
				countSql);
	}

}
