package com.aizhixin.cloud.studentpractice.summary.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.PageDomain;
import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.PushMessageConstants;
import com.aizhixin.cloud.studentpractice.common.domain.QueryCommentTotalDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.QueryService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.score.domain.TrainingGropSetDTO;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryReplyCountDomain;
import com.aizhixin.cloud.studentpractice.summary.entity.SummaryReplyCount;
import com.aizhixin.cloud.studentpractice.summary.repository.SummaryReplyCountRepository;
import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.TaskFile;
import com.aizhixin.cloud.studentpractice.task.repository.TaskFileRepository;

@Transactional
@Service
public class SummaryReplyCountService {

	@Autowired
	private SummaryReplyCountRepository summaryReplyCountRepository;
	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;
	@Autowired
	@Lazy
	private QueryService queryService;

	public void saveList(List<SummaryReplyCount> fileList) {
		summaryReplyCountRepository.save(fileList);
	}

	RowMapper<SummaryReplyCount> countSummaryReplyRm = new RowMapper<SummaryReplyCount>() {

		@Override
		public SummaryReplyCount mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			SummaryReplyCount domain = new SummaryReplyCount();
			domain.setId(rs.getString("SUMMARY_ID"));
			domain.setSummaryType(rs.getString("SUMMARY_TYPE"));
			domain.setSummaryTitle(rs.getString("SUMMARY_TITLE"));
			domain.setClassId(rs.getLong("CLASS_ID"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setCollegeId(rs.getLong("COLLEGE_ID"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setProfessionalId(rs.getLong("PROFESSIONAL_ID"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setOrgId(rs.getLong("ORG_ID"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentId(rs.getLong("STUDENT_ID"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setGrade(rs.getString("GRADE"));
			domain.setMentorId(rs.getLong("MENTOR_ID"));
			domain.setCounselorId(rs.getLong("COUNSELOR_ID"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			domain.setCreatedDate(rs.getDate("CREATED_DATE"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setGroupId(rs.getLong("GROUP_ID"));
			return domain;
		}
	};

	public List<SummaryReplyCount> countSummaryReply() {
		
		String groupIdStr = queryService.getAllGroupValidStr();
		if(!StringUtils.isEmpty(groupIdStr)){
			String sql = "SELECT s.id as SUMMARY_ID,s.SUMMARY_TYPE,s.SUMMARY_TITLE,pcd.JOB_NUM,pcd.STUDENT_ID,pcd.STUDENT_NAME,pcd.ORG_ID,pcd.CLASS_ID,pcd.CLASS_NAME,pcd.PROFESSIONAL_ID,pcd.PROFESSIONAL_NAME,pcd.COLLEGE_ID,pcd.COLLEGE_NAME,s.MENTOR_ID,s.COUNSELOR_ID,s.COUNSELOR_NAME,pcd.GRADE,s.CREATED_DATE,s.GROUP_ID,s.GROUP_NAME FROM `sp_summary` s LEFT JOIN `sp_people_count_detail`  pcd ON s.created_by = pcd.STUDENT_ID where s.DELETE_FLAG = 0 and s.GROUP_ID = pcd.GROUP_ID and s.group_id in ("+groupIdStr+") ";
	
			List<SummaryReplyCount> list = pageJdbcUtil.getInfo(sql,
					countSummaryReplyRm);
			return list;
		}
		return null;
	}

	public void summaryReplyNumCountTask() {

		List<SummaryReplyCount> list = this.countSummaryReply();
		if (null != list && !list.isEmpty()) {
			HashSet<String> idList = new HashSet<String>();
			for(SummaryReplyCount replyCount : list){
				idList.add(replyCount.getId());
			}
			QueryCommentTotalDomain queryCountDTO = new QueryCommentTotalDomain();
			queryCountDTO.setSourceIds(idList);
			queryCountDTO.setModule(PushMessageConstants.MODULE_SUMMARY);
			HashMap<String, Integer> commentTotalMap = authUtilService
					.getCommentTotalCount(queryCountDTO,PushMessageConstants.MODULE_SUMMARY);
			if (null != commentTotalMap && !commentTotalMap.isEmpty()) {
				for (SummaryReplyCount replyCount : list) {
					if (null != commentTotalMap.get(replyCount.getId())) {
						replyCount.setReplyNum(commentTotalMap.get(replyCount.getId()));
					}
				}
			}
			saveList(list);
		}
	}

	RowMapper<SummaryReplyCountDomain> rm = new RowMapper<SummaryReplyCountDomain>() {

		@Override
		public SummaryReplyCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			SummaryReplyCountDomain domain = new SummaryReplyCountDomain();
			domain.setId(rs.getString("ID"));
			domain.setSummaryType(rs.getString("SUMMARY_TYPE"));
			domain.setSummaryTitle(rs.getString("SUMMARY_TITLE"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setGrade(rs.getString("GRADE"));
			domain.setCreatedDate(rs.getDate("CREATED_DATE"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			return domain;
		}
	};

	public PageData<SummaryReplyCountDomain> summaryReplyCountStatistics(
			QueryStuPageDomain domain, Long orgId) {

		String querySql = "SELECT ID,JOB_NUM,STUDENT_NAME,CLASS_NAME,PROFESSIONAL_NAME,COLLEGE_NAME,GRADE,SUMMARY_TITLE,SUMMARY_TYPE,CREATED_DATE,COUNSELOR_NAME,GROUP_ID,GROUP_NAME from `SP_SUMMARY_COUNT` sc where sc.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `SP_SUMMARY_COUNT` sc where sc.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (sc.STUDENT_NAME like '%" + domain.getKeyWords()
					+ "%' or sc.JOB_NUM like '%" + domain.getKeyWords() + "%')";
			countSql += " and (sc.STUDENT_NAME like '%" + domain.getKeyWords()
					+ "%' or sc.JOB_NUM like '%" + domain.getKeyWords() + "%')";
		}
		if (null != domain.getClassId() && domain.getClassId().longValue() > 0L) {
			querySql += " and sc.CLASS_ID=" + domain.getClassId() + "";
			countSql += " and sc.CLASS_ID=" + domain.getClassId() + "";
		}
		if (null != domain.getProfessionalId()
				&& domain.getProfessionalId().longValue() > 0L) {
			querySql += " and sc.PROFESSIONAL_ID=" + domain.getProfessionalId()
					+ "";
			countSql += " and sc.PROFESSIONAL_ID=" + domain.getProfessionalId()
					+ "";
		}
		if (null != domain.getCollegeId()
				&& domain.getCollegeId().longValue() > 0L) {
			querySql += " and sc.COLLEGE_ID=" + domain.getCollegeId() + "";
			countSql += " and sc.COLLEGE_ID=" + domain.getCollegeId() + "";
		}
		if (null != orgId && orgId.longValue() > 0L) {
			querySql += " and sc.ORG_ID=" + orgId + "";
			countSql += " and sc.ORG_ID=" + orgId + "";
		}
		if (null != domain.getStuId() && domain.getStuId() > 0L) {
			querySql += " and sc.STUDENT_ID=" + domain.getStuId() + "";
			countSql += " and sc.STUDENT_ID=" + domain.getStuId() + "";
		}
		if (null != domain.getCounselorId() && domain.getCounselorId() > 0L) {
			querySql += " and sc.COUNSELOR_ID=" + domain.getCounselorId() + "";
			countSql += " and sc.COUNSELOR_ID=" + domain.getCounselorId() + "";
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto = new SortDTO();
		dto.setKey("sc.JOB_NUM");
		dto.setAsc(true);

		return pageJdbcUtil.getPageData(domain.getPageSize(),
				domain.getPageNumber(), rm, sort, querySql, countSql);
	}
	
	
	RowMapper<SummaryReplyCountDomain> summaryRm = new RowMapper<SummaryReplyCountDomain>() {

		@Override
		public SummaryReplyCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			SummaryReplyCountDomain domain = new SummaryReplyCountDomain();
			domain.setId(rs.getString("ID"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setDailyNum(rs.getInt("DAILY_NUM"));
			domain.setWeeklyNum(rs.getInt("WEEKLY_NUM"));
			domain.setMonthlyNum(rs.getInt("MONTHLY_NUM"));
			return domain;
		}
	};

	public PageData<SummaryReplyCountDomain> summaryStatistics(
			QueryStuPageDomain domain, Long orgId) {

		String querySql = "SELECT ID,JOB_NUM,STUDENT_NAME,CLASS_NAME,PROFESSIONAL_NAME,COLLEGE_NAME,COUNSELOR_NAME,DAILY_NUM,WEEKLY_NUM,MONTHLY_NUM,GROUP_ID,GROUP_NAME from `sp_people_count_detail` pcd where pcd.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `sp_people_count_detail` pcd where pcd.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords()
					+ "%' or pcd.JOB_NUM like '%" + domain.getKeyWords() + "%')";
			countSql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords()
					+ "%' or pcd.JOB_NUM like '%" + domain.getKeyWords() + "%')";
		}
		if (null != domain.getClassId() && domain.getClassId().longValue() > 0L) {
			querySql += " and pcd.CLASS_ID=" + domain.getClassId() + "";
			countSql += " and pcd.CLASS_ID=" + domain.getClassId() + "";
		}
		if (null != domain.getProfessionalId()
				&& domain.getProfessionalId().longValue() > 0L) {
			querySql += " and pcd.PROFESSIONAL_ID=" + domain.getProfessionalId()
					+ "";
			countSql += " and pcd.PROFESSIONAL_ID=" + domain.getProfessionalId()
					+ "";
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
		if (null != domain.getStuId() && domain.getStuId() > 0L) {
			querySql += " and pcd.STUDENT_ID=" + domain.getStuId() + "";
			countSql += " and pcd.STUDENT_ID=" + domain.getStuId() + "";
		}
		if (null != domain.getCounselorId() && domain.getCounselorId() > 0L) {
			querySql += " and pcd.COUNSELOR_ID=" + domain.getCounselorId() + "";
			countSql += " and pcd.COUNSELOR_ID=" + domain.getCounselorId() + "";
		}
		if (null != domain.getGroupId() && domain.getGroupId() > 0L) {
			querySql += " and pcd.GROUP_ID=" + domain.getGroupId() + "";
			countSql += " and pcd.GROUP_ID=" + domain.getGroupId() + "";
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto = new SortDTO();
		dto.setKey("sc.JOB_NUM");
		dto.setAsc(true);

		return pageJdbcUtil.getPageData(domain.getPageSize(),
				domain.getPageNumber(), summaryRm, sort, querySql, countSql);
	}

	public Map<String, Object> summaryReplyPage(QueryStuPageDomain domain,
			Long orgId, String token) {

		PageData<SummaryReplyCountDomain> pageData = this
				.summaryReplyCountStatistics(domain, orgId);
		List<SummaryReplyCountDomain> dataList = pageData.getData();

		if (null != dataList && !dataList.isEmpty()) {
			HashSet<String> idList = new HashSet<String>();
			for (SummaryReplyCountDomain dto : dataList) {
				idList.add(dto.getId());
			}
			QueryCommentTotalDomain queryCountDTO = new QueryCommentTotalDomain();
			queryCountDTO.setSourceIds(idList);
			queryCountDTO.setModule(PushMessageConstants.MODULE_SUMMARY);

			HashMap<String, Integer> commentTotalMap = authUtilService
					.getCommentTotalCount(queryCountDTO, token);
			if (null != commentTotalMap && !commentTotalMap.isEmpty()) {
				for (SummaryReplyCountDomain dto : dataList) {
					if (null != commentTotalMap.get(dto.getId())) {
						dto.setReplyNum(commentTotalMap.get(dto.getId()));
					}
				}
			}
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		PageDomain p = new PageDomain();
		p.setPageNumber(pageData.getPage().getPageNumber());
		p.setPageSize(pageData.getPage().getPageSize());
		p.setTotalElements(pageData.getPage().getTotalElements());
		p.setTotalPages(pageData.getPage().getTotalPages());
		result.put(ApiReturnConstants.PAGE, p);
		result.put(ApiReturnConstants.DATA, dataList);

		return result;
	}
	
	
	public Map<String, Object> summaryPage(QueryStuPageDomain domain,
			Long orgId) {

		PageData<SummaryReplyCountDomain> pageData = this
				.summaryStatistics(domain, orgId);
		List<SummaryReplyCountDomain> dataList = pageData.getData();

		if (null != dataList && !dataList.isEmpty()) {
			HashSet<Long> idList = new HashSet<Long>();
			for (SummaryReplyCountDomain dto : dataList) {
				idList.add(dto.getGroupId());
			}
			List<TrainingGropSetDTO> setList = queryService.getGroupSetList(idList);
			if(null !=setList && !setList.isEmpty()){
				HashMap<Long,TrainingGropSetDTO> setMap = new HashMap<Long,TrainingGropSetDTO>();
				for(TrainingGropSetDTO setDto : setList){
					setMap.put(setDto.getGroupId(), setDto);
				}
				for (SummaryReplyCountDomain dto : dataList) {
					TrainingGropSetDTO set = setMap.get(dto.getGroupId());
					if(null != set){
						dto.setNeedDailyNum(set.getNeedDailyNum());
						dto.setNeedWeeklyNum(set.getNeedWeeklyNum());
						dto.setNeedMonthlyNum(set.getNeedMonthlyNum());
					}
				}
			}
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		PageDomain p = new PageDomain();
		p.setPageNumber(pageData.getPage().getPageNumber());
		p.setPageSize(pageData.getPage().getPageSize());
		p.setTotalElements(pageData.getPage().getTotalElements());
		p.setTotalPages(pageData.getPage().getTotalPages());
		result.put(ApiReturnConstants.PAGE, p);
		result.put(ApiReturnConstants.DATA, dataList);

		return result;
	}
}
