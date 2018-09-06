package com.aizhixin.cloud.studentpractice.score.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.QueryService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.score.core.GroupSetCore;
import com.aizhixin.cloud.studentpractice.score.domain.CounselorCountDomain;
import com.aizhixin.cloud.studentpractice.score.domain.GroupSetDomain;
import com.aizhixin.cloud.studentpractice.score.domain.QueryScoreDomain;
import com.aizhixin.cloud.studentpractice.score.entity.CounselorCount;
import com.aizhixin.cloud.studentpractice.score.repository.CounselorCountRepository;
import com.aizhixin.cloud.studentpractice.summary.core.ReportStatusCode;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountDetailService;

@Transactional
@Service
public class CounselorCountService {

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;
	@Autowired
	private CounselorCountRepository counselorCountRepository;
	@Autowired
	private PeopleCountDetailService peopleCountDetailService;
	@Autowired
	private QueryService queryService;

	public void save(List<CounselorCount> counselorCountList) {
		counselorCountRepository.save(counselorCountList);
	}

	public CounselorCount findByGroupIdAndCounselorId(Long groupId,
			Long counselorId) {
		return counselorCountRepository.findOneByGroupIdAndCounselorId(groupId,
				counselorId);
	}

	RowMapper<GroupSetDomain> groupSetRm = new RowMapper<GroupSetDomain>() {

		@Override
		public GroupSetDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			GroupSetDomain domain = new GroupSetDomain();
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setGroupName(rs.getString("GROP_NAME"));
			domain.setIsNeedSummary(rs.getBoolean("IS_NEED_SUMMARY"));
			domain.setIsNeedReport(rs.getBoolean("IS_NEED_REPORT"));
			domain.setCounselorId(rs.getLong("TEACHER_ID"));
			domain.setCounselorName(rs.getString("teacher_name"));
			domain.setJobNum(rs.getString("JOB_NUMBER"));
			domain.setCounselorCollegeId(rs.getLong("COLLEGE_ID"));
			domain.setCounselorCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setOrgId(rs.getLong("ORG_ID"));
			return domain;
		}
	};

	/**
	 * 辅导员信息统计任务
	 */
	public void counselorCountTask() {

		String orgDbName = authUtilService.getOrgDbName();
		String set_sql = "SELECT tg.ORG_ID,tg.TEACHER_ID,t.`NAME` AS teacher_name,t.JOB_NUMBER,t.COLLEGE_ID,c.`NAME` as COLLEGE_NAME,tg.ID AS GROUP_ID,tg.GROP_NAME,gs.IS_NEED_SUMMARY,IS_NEED_REPORT from `"
				+ orgDbName
				+ "`.`t_training_group` tg LEFT JOIN `"
				+ orgDbName
				+ "`.`t_training_group_set` gs ON gs.GROUP_ID = tg.ID LEFT JOIN `"
				+ orgDbName
				+ "`.`t_user` t ON t.ID =tg.TEACHER_ID LEFT JOIN `"
				+ orgDbName
				+ "`.`t_college` c ON c.ID = t.COLLEGE_ID WHERE tg.DELETE_FLAG = 0 and tg.TEACHER_ID is not null and tg.END_DATE > NOW();";
		List<GroupSetDomain> setList = pageJdbcUtil
				.getInfo(set_sql, groupSetRm);
		if (null != setList && !setList.isEmpty()) {
			ArrayList<CounselorCount> countList = new ArrayList<CounselorCount>();
			for (GroupSetDomain domain : setList) {
				List<PeopleCountDetail> detailList = peopleCountDetailService
						.findAllByGroupId(domain.getGroupId());
				if (null != detailList && !detailList.isEmpty()) {
					CounselorCount count = findByGroupIdAndCounselorId(
							domain.getGroupId(), domain.getCounselorId());
					if (null == count) {
						count = new CounselorCount();
					}
					count.setGroupId(domain.getGroupId());
					count.setGroupName(domain.getGroupName());
					if (null != domain.getCounselorCollegeId()
							&& domain.getCounselorCollegeId() > 0) {
						count.setCounselorCollegeId(domain
								.getCounselorCollegeId());
					}
					count.setCounselorCollegeName(domain
							.getCounselorCollegeName());
					if (null != domain.getCounselorId()
							&& domain.getCounselorId() > 0) {
						count.setCounselorId(domain.getCounselorId());
					}
					count.setCounselorName(domain.getCounselorName());
					count.setJobNum(domain.getJobNum());
					count.setGroupStuNum(detailList.size());
					count.setOrgId(domain.getOrgId());
					if (!domain.getIsNeedSummary() && !domain.getIsNeedReport()) {
						count.setDailyNum(GroupSetCore.SET_NO_NEED);
						count.setWeeklyNum(GroupSetCore.SET_NO_NEED);
						count.setMonthlyNum(GroupSetCore.SET_NO_NEED);
						count.setReportNum(GroupSetCore.SET_NO_NEED);
					} else {
						int dailyNum = 0;
						int weeklyNum = 0;
						int monthlyNum = 0;
						int reviewReportNum = 0;
						for (PeopleCountDetail detail : detailList) {
							if (null != detail.getDailyNum()
									&& detail.getDailyNum().intValue() > 0) {
								dailyNum += detail.getDailyNum();
							}
							if (null != detail.getWeeklyNum()
									&& detail.getWeeklyNum().intValue() > 0) {
								weeklyNum += detail.getWeeklyNum();
							}
							if (null != detail.getMonthlyNum()
									&& detail.getMonthlyNum().intValue() > 0) {
								monthlyNum += detail.getMonthlyNum();
							}
							if (null != detail.getReportStatus()) {
								if (ReportStatusCode.REPORT_STATUS_BACK_TO
										.equals(detail.getReportStatus())
										|| ReportStatusCode.REPORT_STATUS_FINISH
												.equals(detail
														.getReportStatus())) {
									reviewReportNum = reviewReportNum + 1;
								}
							}
						}
						if (domain.getIsNeedSummary()) {
							count.setDailyNum(String.valueOf(dailyNum));
							count.setWeeklyNum(String.valueOf(weeklyNum));
							count.setMonthlyNum(String.valueOf(monthlyNum));
						}
						if (domain.getIsNeedReport()) {
							count.setReviewReportNum(reviewReportNum);
						}
					}
					countList.add(count);
				}
			}
			save(countList);
		}
	}

	public PageData<CounselorCountDomain> queryInforPage(
			QueryScoreDomain domain, Long orgId) {
		String querySql = "SELECT * from `SP_COUNSELOR_COUNT` cc where 1 = 1 ";
		String countSql = "SELECT count(1) FROM `SP_COUNSELOR_COUNT` cc where 1 = 1 ";

		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (cc.COUNSELOR_NAME like '%"
					+ domain.getKeyWords() + "%' or cc.JOB_NUM like '%"
					+ domain.getKeyWords() + "%' or cc.GROUP_NAME like '%"
					+ domain.getKeyWords() + "%' )";
			countSql += " and (cc.COUNSELOR_NAME like '%"
					+ domain.getKeyWords() + "%' or cc.JOB_NUM like '%"
					+ domain.getKeyWords() + "%' or cc.GROUP_NAME like '%"
					+ domain.getKeyWords() + "%' )";
		}

		if (null != orgId && orgId > 0L) {
			querySql += " and cc.ORG_ID =" + orgId;
			countSql += " and cc.ORG_ID =" + orgId;
		}
		if (null != domain.getCollegeId()
				&& domain.getCollegeId().longValue() > 0L) {
			querySql += " and cc.COUNSELOR_COLLEGE_ID=" + domain.getCollegeId()
					+ "";
			countSql += " and cc.COUNSELOR_COLLEGE_ID=" + domain.getCollegeId()
					+ "";
		}

		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("cc.ID");
		dto.setAsc(false);
		sort.add(dto);

		return pageJdbcUtil.getPageData(domain.getPageSize(),
				domain.getPageNumber(), rm, sort, querySql, countSql);
	}

	RowMapper<CounselorCountDomain> rm = new RowMapper<CounselorCountDomain>() {

		@Override
		public CounselorCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			CounselorCountDomain domain = new CounselorCountDomain();
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setCounselorId(rs.getLong("COUNSELOR_ID"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setOrgId(rs.getLong("ORG_ID"));
			domain.setCounselorCollegeId(rs.getLong("COUNSELOR_COLLEGE_ID"));
			domain.setCounselorCollegeName(rs
					.getString("COUNSELOR_COLLEGE_NAME"));
			domain.setGroupStuNum(rs.getInt("GROUP_STU_NUM"));
			domain.setDailyNum(rs.getString("DAILY_NUM"));
			domain.setWeeklyNum(rs.getString("WEEKLY_NUM"));
			domain.setMonthlyNum(rs.getString("MONTHLY_NUM"));
			domain.setReportNum(rs.getString("REPORT_NUM"));
			domain.setReviewReportNum(rs.getInt("REVIEW_REPORT_NUM"));
			return domain;
		}
	};

	RowMapper<IdNameDomain> IdRm = new RowMapper<IdNameDomain>() {

		@Override
		public IdNameDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			IdNameDomain domain = new IdNameDomain();
			domain.setId(rs.getLong("id"));
			return domain;
		}
	};

	public void openCounselorCallTask() {

		String groupIds = queryService.getAllGroupValidStr();
		if (StringUtils.isNotBlank(groupIds)) {
			String dianDianDbName = authUtilService.getDdDbName();
			String sql = "SELECT id FROM `"
					+ dianDianDbName
					+ "`.`dd_tempgroup` WHERE `STATUS` = 0 AND DELETE_FLAG = 0 AND practice_id IN ("+groupIds+");";
			List<IdNameDomain> list = pageJdbcUtil.getInfo(sql, IdRm);
			if (null != list && !list.isEmpty()) {
				for (IdNameDomain domain : list) {
					authUtilService.openCallGroup(domain.getId());
				}
			}
		}
	}

	public void closeCounselorCallTask() {

		String orgDbName = authUtilService.getOrgDbName();
		String query_close_sql = "SELECT ID FROM `"
				+ orgDbName
				+ "`.`t_training_group` where DELETE_FLAG = 0 and END_DATE >= (curdate() - INTERVAL 7 DAY) and END_DATE < curdate() ORDER BY END_DATE DESC;";
		List<IdNameDomain> closeList = pageJdbcUtil.getInfo(query_close_sql,
				IdRm);
		String groupIds = "";
		if (null != closeList && !closeList.isEmpty()) {
			for (IdNameDomain domain : closeList) {
				if (StringUtils.isBlank(groupIds)) {
					groupIds = domain.getId().toString();
				} else {
					groupIds += "," + domain.getId();
				}
			}
		}
		if (StringUtils.isNotBlank(groupIds)) {
			String dianDianDbName = authUtilService.getDdDbName();
			String query_call_sql = "SELECT id FROM `"
					+ dianDianDbName
					+ "`.`dd_tempgroup` WHERE `STATUS` = 1 AND DELETE_FLAG = 0 AND practice_id IN ("+groupIds+");";
			List<IdNameDomain> list = pageJdbcUtil
					.getInfo(query_call_sql, IdRm);
			if (null != list && !list.isEmpty()) {
				for (IdNameDomain domain : list) {
					authUtilService.closeCallGroup(domain.getId());
				}
			}
		}
	}

}
