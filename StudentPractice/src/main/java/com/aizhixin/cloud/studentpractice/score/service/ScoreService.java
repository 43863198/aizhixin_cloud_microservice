package com.aizhixin.cloud.studentpractice.score.service;

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
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.PageDomain;
import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.QueryService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.score.core.GroupSetCore;
import com.aizhixin.cloud.studentpractice.score.domain.GroupSetDomain;
import com.aizhixin.cloud.studentpractice.score.domain.QueryScoreDomain;
import com.aizhixin.cloud.studentpractice.score.domain.ScoreDetailDomain;
import com.aizhixin.cloud.studentpractice.score.domain.ScoreDomain;
import com.aizhixin.cloud.studentpractice.score.domain.TrainingGropSetDTO;
import com.aizhixin.cloud.studentpractice.score.entity.CounselorCount;
import com.aizhixin.cloud.studentpractice.score.entity.Score;
import com.aizhixin.cloud.studentpractice.score.repository.ScoreRepository;
import com.aizhixin.cloud.studentpractice.summary.core.ReportStatusCode;
import com.aizhixin.cloud.studentpractice.summary.domain.QueryReportDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.ReportDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryReplyCountDomain;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStatisticalPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StatisticalDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskAssginDomain;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCount;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;
import com.aizhixin.cloud.studentpractice.task.entity.TaskStatistical;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountDetailService;
import com.aizhixin.cloud.studentpractice.task.service.TaskStatisticalService;

@Transactional
@Service
public class ScoreService {
	@Autowired
	private ScoreRepository scoreRepository;
	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private PeopleCountDetailService peopleCountDetailService;
	@Autowired
	@Lazy
	private TaskStatisticalService taskStatisticalService;
	@Autowired
	@Lazy
	private QueryService queryService;
	

	public void saveList(List<Score> scoreList) {
		scoreRepository.save(scoreList);
	}

	public void save(Score score) {
		scoreRepository.save(score);
	}

	@Transactional
	public void deleteAllByGroupId(Long groupId) {
		scoreRepository.deleteByGroupId(groupId);
	}

	public Score findById(String id) {
		return scoreRepository.findOne(id);
	}

	public Score changeScore(ScoreDomain domain, AccountDTO dto) {
		Score score = findById(domain.getId());
		if (null != score) {
			BigDecimal bg = new BigDecimal(domain.getTotalScore()).setScale(2,
					RoundingMode.HALF_UP);
			score.setTotalScore(bg.doubleValue());
			score.setLastModifiedBy(dto.getId());
			score.setLastModifiedDate(new Date());
			save(score);
		}
		return score;
	}

	public void autoScoreTask(Long groupId) {

		AutoScoreThread thread = new AutoScoreThread(groupId);
		thread.start();
	}

	class AutoScoreThread extends Thread {
		private Long groupId;

		public AutoScoreThread(Long groupId) {
			this.groupId = groupId;
		}

		public void run() {
			scoreTask(groupId);
		}
	}

	public void scoreTask(Long groupId) {

		String orgDbName = authUtilService.getOrgDbName();
		String set_sql = "SELECT tgs.GROUP_ID,tgs.IS_NEED_SIGN,tgs.NEED_SIGN_NUM,tgs.IS_NEED_SUMMARY,tgs.NEED_DAILY_NUM,tgs.NEED_WEEKLY_NUM,tgs.NEED_MONTHLY_NUM,tgs.IS_NEED_REPORT,tgs.SIGN_WEIGHT,tgs.SUMMARY_WEIGHT,tgs.REPORT_WEIGHT,tgs.TASK_WEIGHT FROM  `"
				+ orgDbName
				+ "`.`t_training_group_set` tgs where 1=1 ";
		if (null != groupId && groupId.longValue() > 0) {
			set_sql += " and tgs.GROUP_ID=" + groupId;
		}else{
			set_sql += " and tgs.SCORE_DATE = CURDATE() ";
		}
		List<TrainingGropSetDTO> setList = pageJdbcUtil.getInfo(set_sql, setRm);
		if (null != setList && !setList.isEmpty()) {
			for (TrainingGropSetDTO set : setList) {
				List<PeopleCountDetail> peopleDetailList = peopleCountDetailService
						.findAllByGroupId(set.getGroupId());
				// 删除该小组之前计算的成绩
				deleteAllByGroupId(set.getGroupId());
				ArrayList<Score> scoreList = new ArrayList<Score>();
				for (PeopleCountDetail detail : peopleDetailList) {
					Score score = new Score();
					score.setId(UUID.randomUUID().toString());
					// 复制学生基本信息
					BeanUtils.copyProperties(detail, score);
					if (set.getIsNeedSign()) {// 要求考勤
						if(null != detail.getSignInNormalNum()){
						int result = detail.getSignInNormalNum()
								- set.getNeedSignNum();
						if (result >= 0) {// 正常考勤天数大于等于设置要求天数
							BigDecimal bg = new BigDecimal(set.getSignWeight())
									.setScale(2, RoundingMode.HALF_UP);
							score.setSignScore(bg.doubleValue());
						} else {
							Double signScore = (set.getSignWeight() / set
									.getNeedSignNum())
									* detail.getSignInNormalNum();
							BigDecimal bg = new BigDecimal(signScore).setScale(
									2, RoundingMode.HALF_UP);
							score.setSignScore(bg.doubleValue());
						}
					   }else{
						   score.setSignScore(0d);
					   }
					}
					if (set.getIsNeedSummary()) {// 要求日志周志
						boolean dailyFullCredit =false;
						boolean weeklyFullCredit =false;
						boolean monthlyFullCredit =false;
						int avgNum = 0;
						if (null != set.getNeedDailyNum()
								&& set.getNeedDailyNum() > 0) {
							avgNum += 1;
						}
						if (null != set.getNeedWeeklyNum()
								&& set.getNeedWeeklyNum() > 0) {
							avgNum += 1;
						}
						if (null != set.getNeedMonthlyNum()
								&& set.getNeedMonthlyNum() > 0) {
							avgNum += 1;
						}
						Double avgSummaryWeight = set.getSummaryWeight()
								/ avgNum;// 实践日志平均权重
						BigDecimal bg = new BigDecimal(avgSummaryWeight)
								.setScale(2, RoundingMode.HALF_UP);
						avgSummaryWeight = bg.doubleValue();
						// 日志成绩
						if (null != set.getNeedDailyNum()
								&& set.getNeedDailyNum() > 0) {
							if (null != detail.getDailyNum()
									&& detail.getDailyNum() > 0) {
								int result = detail.getDailyNum()
										- set.getNeedDailyNum();
								if (result >= 0) {
									dailyFullCredit = true;
									score.setSummaryScore(avgSummaryWeight);
								} else {
									Double summaryScore = (avgSummaryWeight / set
											.getNeedDailyNum())
											* detail.getDailyNum();
									BigDecimal bg1 = new BigDecimal(
											summaryScore).setScale(2,
											RoundingMode.HALF_UP);
									score.setSummaryScore(bg1.doubleValue());
								}
							}
						}
						// 周志成绩
						if (null != set.getNeedWeeklyNum()
								&& set.getNeedWeeklyNum() > 0) {
							if (null != detail.getWeeklyNum()
									&& detail.getWeeklyNum() > 0) {
								int result = detail.getWeeklyNum()
										- set.getNeedWeeklyNum();
								if (result >= 0) {
									BigDecimal bg1 = new BigDecimal(
											score.getSummaryScore()
													+ avgSummaryWeight)
											.setScale(2, RoundingMode.HALF_UP);
									score.setSummaryScore(bg1.doubleValue());
									weeklyFullCredit = true;
								} else {
									Double summaryScore = (avgSummaryWeight / set
											.getNeedWeeklyNum())
											* detail.getWeeklyNum();
									BigDecimal bg1 = new BigDecimal(
											score.getSummaryScore()
													+ summaryScore).setScale(2,
											RoundingMode.HALF_UP);
									score.setSummaryScore(bg1.doubleValue());
								}
							}
						}
						// 月志成绩
						if (null != set.getNeedMonthlyNum()
								&& set.getNeedMonthlyNum() > 0) {
							if (null != detail.getMonthlyNum()
									&& detail.getMonthlyNum() > 0) {
								int result = detail.getMonthlyNum()
										- set.getNeedMonthlyNum();
								if (result >= 0) {
									BigDecimal bg1 = new BigDecimal(
											score.getSummaryScore()
													+ avgSummaryWeight)
											.setScale(2, RoundingMode.HALF_UP);
									score.setSummaryScore(bg1.doubleValue());
									monthlyFullCredit = true;
								} else {
									Double summaryScore = (avgSummaryWeight / set
											.getNeedMonthlyNum())
											* detail.getMonthlyNum();
									BigDecimal bg1 = new BigDecimal(
											score.getSummaryScore()
													+ summaryScore).setScale(2,
											RoundingMode.HALF_UP);
									score.setSummaryScore(bg1.doubleValue());
								}
							}
						}
						
						if(null == score.getSummaryScore()){
							score.setSummaryScore(0d);
						}
						if(dailyFullCredit && weeklyFullCredit && monthlyFullCredit){
							BigDecimal fullDg = new BigDecimal(set.getSummaryWeight())
							.setScale(2, RoundingMode.HALF_UP);
							score.setSummaryScore(fullDg.doubleValue());
						}
					}
					if (set.getIsNeedReport()) {// 要求实践报告
						if (!StringUtils.isEmpty(detail.getReportStatus())
								&& ReportStatusCode.REPORT_STATUS_FINISH
										.equals(detail.getReportStatus())) {// 正常考勤天数大于等于设置要求天数
							BigDecimal bg = new BigDecimal(
									set.getReportWeight()).setScale(2,
									RoundingMode.HALF_UP);
							score.setReportScore(bg.doubleValue());
						} else {
							score.setReportScore(0D);
						}
					}
					if (null != set.getTaskWeight() && set.getTaskWeight() > 0) {
						TaskStatistical taskStatistical = taskStatisticalService
								.findByStuIdAndGroupId(detail.getStudentId(),
										detail.getGroupId());
						if (null != taskStatistical) {
							if (null != taskStatistical.getAvgScore()
									&& taskStatistical.getAvgScore()
											.doubleValue() > 0) {
								BigDecimal bg = new BigDecimal(
										set.getTaskWeight()
												* (taskStatistical
														.getAvgScore() / 100.00D))
										.setScale(2, RoundingMode.HALF_UP);
								score.setTaskScore(bg.doubleValue());
							} else {
								score.setTaskScore(0D);
							}
						} else {
							score.setTaskScore(0D);
						}
					}
					Double totalScore = 0D;
					if (null != score.getSignScore()) {
						totalScore += score.getSignScore();
					}
					if (null != score.getSummaryScore()) {
						totalScore += score.getSummaryScore();
					}
					if (null != score.getReportScore()) {
						totalScore += score.getReportScore();
					}
					if (null != score.getTaskScore()) {
						totalScore += score.getTaskScore();
					}
					BigDecimal bg = new BigDecimal(totalScore).setScale(2,
							RoundingMode.HALF_UP);
					score.setTotalScore(bg.doubleValue());
					scoreList.add(score);
				}
				saveList(scoreList);
			}
		}

	}

	RowMapper<TrainingGropSetDTO> setRm = new RowMapper<TrainingGropSetDTO>() {

		@Override
		public TrainingGropSetDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			TrainingGropSetDTO domain = new TrainingGropSetDTO();
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setIsNeedSign(rs.getBoolean("IS_NEED_SIGN"));
			domain.setNeedSignNum(rs.getInt("NEED_SIGN_NUM"));
			domain.setIsNeedSummary(rs.getBoolean("IS_NEED_SUMMARY"));
			domain.setNeedDailyNum(rs.getInt("NEED_DAILY_NUM"));
			domain.setNeedWeeklyNum(rs.getInt("NEED_WEEKLY_NUM"));
			domain.setNeedMonthlyNum(rs.getInt("NEED_MONTHLY_NUM"));
			domain.setIsNeedReport(rs.getBoolean("IS_NEED_REPORT"));
			domain.setSignWeight(rs.getDouble("SIGN_WEIGHT"));
			domain.setSummaryWeight(rs.getDouble("SUMMARY_WEIGHT"));
			domain.setReportWeight(rs.getDouble("REPORT_WEIGHT"));
			domain.setTaskWeight(rs.getDouble("TASK_WEIGHT"));
			return domain;
		}
	};

	RowMapper<ScoreDomain> rm = new RowMapper<ScoreDomain>() {

		@Override
		public ScoreDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			ScoreDomain domain = new ScoreDomain();
			domain.setId(rs.getString("ID"));
			domain.setStudentId(rs.getLong("STUDENT_ID"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			domain.setEnterpriseName(rs.getString("ENTERPRISE_NAME"));
			domain.setSignScore(rs.getDouble("SIGN_SCORE"));
			domain.setSummaryScore(rs.getDouble("SUMMARY_SCORE"));
			domain.setReportScore(rs.getDouble("REPORT_SCORE"));
			domain.setTaskScore(rs.getDouble("TASK_SCORE"));
			domain.setTotalScore(rs.getDouble("TOTAL_SCORE"));
			domain.setMentorName(rs.getString("MENTOR_NAME"));
			return domain;
		}
	};

	public PageData<ScoreDomain> queryInforPage(QueryScoreDomain domain,Long orgId) {
		String querySql = "SELECT ID,STUDENT_ID,GROUP_ID,COLLEGE_NAME,PROFESSIONAL_NAME,CLASS_NAME,JOB_NUM,STUDENT_NAME,GROUP_NAME,MENTOR_NAME,COUNSELOR_NAME,ENTERPRISE_NAME,SIGN_SCORE,SUMMARY_SCORE,REPORT_SCORE,TASK_SCORE,TOTAL_SCORE FROM `sp_score` sr where sr.DELETE_FLAG =0 ";
		String countSql = "SELECT count(1) FROM `sp_score` sr where sr.DELETE_FLAG =0 ";

		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (sr.student_name like '%" + domain.getKeyWords()
					+ "%' or sr.JOB_NUM like '%" + domain.getKeyWords()
					+ "%' or sr.GROUP_NAME like '%" + domain.getKeyWords()
					+ "%' )";
			countSql += " and (sr.student_name like '%" + domain.getKeyWords()
					+ "%' or sr.JOB_NUM like '%" + domain.getKeyWords()
					+ "%' or sr.GROUP_NAME like '%" + domain.getKeyWords()
					+ "%' )";
		}
		if (null != orgId
				&& orgId > 0L) {
			querySql += " and sr.ORG_ID=" + orgId + "";
			countSql += " and sr.ORG_ID=" + orgId + "";
		}
		if (null != domain.getStuId() && domain.getStuId() > 0L) {
			querySql += " and sr.STUDENT_ID =" + domain.getStuId();
			countSql += " and sr.STUDENT_ID =" + domain.getStuId();
		}
		if (null != domain.getCounselorId() && domain.getCounselorId() > 0L) {
			querySql += " and sr.COUNSELOR_ID =" + domain.getCounselorId();
			countSql += " and sr.COUNSELOR_ID =" + domain.getCounselorId();
		}
		if (null != domain.getClassId() && domain.getClassId().longValue() > 0L) {
			querySql += " and sr.CLASS_ID=" + domain.getClassId() + "";
			countSql += " and sr.CLASS_ID=" + domain.getClassId() + "";
		}
		if (null != domain.getProfessionalId()
				&& domain.getProfessionalId().longValue() > 0L) {
			querySql += " and sr.PROFESSIONAL_ID=" + domain.getProfessionalId()
					+ "";
			countSql += " and sr.PROFESSIONAL_ID=" + domain.getProfessionalId()
					+ "";
		}
		if (null != domain.getCollegeId()
				&& domain.getCollegeId().longValue() > 0L) {
			querySql += " and sr.COLLEGE_ID=" + domain.getCollegeId() + "";
			countSql += " and sr.COLLEGE_ID=" + domain.getCollegeId() + "";
		}
		if (null != domain.getGroupId()
				&& domain.getGroupId().longValue() > 0L) {
			querySql += " and sr.GROUP_ID=" + domain.getGroupId() + "";
			countSql += " and sr.GROUP_ID=" + domain.getGroupId() + "";
		}

		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		if(StringUtils.isEmpty(domain.getSortField())){
			dto.setKey("sr.CREATED_DATE");
			dto.setAsc(false);
		}else{
			dto.setKey(domain.getSortField());
			if(StringUtils.isEmpty(domain.getSortFlag())){
				dto.setAsc(false);
			}else{
				if("asc".equals(domain.getSortFlag())){
					dto.setAsc(true);
				}else{
					dto.setAsc(false);
				}
			}
		}
		sort.add(dto);

		return pageJdbcUtil.getPageData(domain.getPageSize(),
				domain.getPageNumber(), rm, sort, querySql, countSql);
	}

	public ScoreDetailDomain findByStuIdAndGroupId(Long studentId, Long groupId) {

		ScoreDetailDomain domain = new ScoreDetailDomain();
		PeopleCountDetail detail = peopleCountDetailService
				.findByStuIdAndGroupId(studentId, groupId);
		if (null != detail) {
			domain.setReportStatus(detail.getReportStatus());
			domain.setDailyNum(detail.getDailyNum());
			domain.setWeeklyNum(detail.getWeeklyNum());
			domain.setMonthlyNum(detail.getMonthlyNum());
			domain.setSignInNormalNum(detail.getSignInNormalNum());
			domain.setLeaveNum(detail.getLeaveNum());
		}
		TaskStatistical taskStatistical = taskStatisticalService
				.findByStuIdAndGroupId(studentId, groupId);
		if (null != taskStatistical) {
			domain.setBackToNum(taskStatistical.getBackToNum());
			domain.setCheckPendingNum(taskStatistical.getCheckPendingNum());
			domain.setPassNum(taskStatistical.getPassNum());
			domain.setUncommitNum(taskStatistical.getUncommitNum());
			domain.setAvgScore(taskStatistical.getAvgScore());
		}
		return domain;
	}

	RowMapper<ScoreDetailDomain> scoreRm = new RowMapper<ScoreDetailDomain>() {

		@Override
		public ScoreDetailDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			ScoreDetailDomain domain = new ScoreDetailDomain();
			domain.setSignInNormalNum(rs.getInt("SIGNIN_NORMAL_NUM"));
			domain.setPassNum(rs.getInt("PASS_NUM"));
			domain.setUncommitNum(rs.getInt("UNCOMMIT_NUM"));
			domain.setCheckPendingNum(rs.getInt("CHECK_PENDING_NUM"));
			domain.setBackToNum(rs.getInt("BACK_TO_NUM"));
			domain.setLeaveNum(rs.getInt("LEAVE_NUM"));
			domain.setDailyNum(rs.getInt("DAILY_NUM"));
			domain.setWeeklyNum(rs.getInt("WEEKLY_NUM"));
			domain.setMonthlyNum(rs.getInt("MONTHLY_NUM"));
			domain.setAvgScore(rs.getDouble("AVG_SCORE"));
			domain.setReportStatus(rs.getString("REPORT_STATUS"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setSigninTotalNum(rs.getInt("SIGNIN_TOTAL_NUM"));
			return domain;
		}
	};

	public ScoreDetailDomain findStatistics(Long studentId, Long groupId) {

		String querySql = "SELECT pcd.SIGNIN_TOTAL_NUM,pcd.GROUP_NAME,ts.UNCOMMIT_NUM,ts.CHECK_PENDING_NUM,ts.BACK_TO_NUM,pcd.SIGNIN_TOTAL_NUM,pcd.LEAVE_NUM,pcd.DAILY_NUM,pcd.WEEKLY_NUM,pcd.MONTHLY_NUM,pcd.GROUP_ID,pcd.STUDENT_ID,pcd.JOB_NUM,pcd.STUDENT_NAME,pcd.SUMMARY_TOTAL_NUM,pcd.SIGNIN_NORMAL_NUM,pcd.REPORT_STATUS,ts.PASS_NUM,ts.AVG_SCORE FROM `sp_people_count_detail` pcd LEFT JOIN `sp_task_statistical` ts ON pcd.STUDENT_ID = ts.STUDENT_ID and pcd.GROUP_ID = ts.GROUP_ID where ts.STUDENT_ID = "+studentId+" and ts.GROUP_ID ="+groupId+" ";

		
		List<ScoreDetailDomain> resultList = pageJdbcUtil.getInfo(querySql, scoreRm);
		if(null != resultList && !resultList.isEmpty()){
			return resultList.get(0);
		}else{
			return null;
		}
	}
	
}
