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

import com.aizhixin.cloud.studentpractice.task.entity.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.core.ErrorCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.CorporateMentorsInfoByStudentDTO;
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
import com.aizhixin.cloud.studentpractice.task.domain.QueryStatisticalPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StatisticalDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDetailForSchoolDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskAssginDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDomain;
import com.aizhixin.cloud.studentpractice.task.repository.TaskRepository;

@Service
public class StatisticalService {

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private AuthUtilService authUtilService;


	RowMapper<StatisticalDomain> rm = new RowMapper<StatisticalDomain>() {

		@Override
		public StatisticalDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			StatisticalDomain domain = new StatisticalDomain();
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setStudentId(rs.getLong("STUDENT_ID"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setSummaryTotalNum(rs.getInt("SUMMARY_TOTAL_NUM"));
			domain.setSigninNormalNum(rs.getInt("SIGNIN_NORMAL_NUM"));
			domain.setPassNum(rs.getInt("PASS_NUM"));
			domain.setUncommitNum(rs.getInt("UNCOMMIT_NUM"));
			domain.setCheckPendingNum(rs.getInt("CHECK_PENDING_NUM"));
			domain.setBackToNum(rs.getInt("BACK_TO_NUM"));
			domain.setSigninTotalNum(rs.getInt("SIGNIN_TOTAL_NUM"));
			domain.setLeaveNum(rs.getInt("LEAVE_NUM"));
			domain.setDailyNum(rs.getInt("DAILY_NUM"));
			domain.setWeeklyNum(rs.getInt("WEEKLY_NUM"));
			domain.setMonthlyNum(rs.getInt("MONTHLY_NUM"));
			return domain;
		}
	};

	public PageData<StatisticalDomain> findStatisticsPage(QueryStatisticalPageDomain domain) {

		String querySql = "SELECT ts.UNCOMMIT_NUM,ts.CHECK_PENDING_NUM,ts.BACK_TO_NUM,pcd.SIGNIN_TOTAL_NUM,pcd.LEAVE_NUM,pcd.DAILY_NUM,pcd.WEEKLY_NUM,pcd.MONTHLY_NUM,pcd.GROUP_ID,pcd.STUDENT_ID,pcd.JOB_NUM,pcd.STUDENT_NAME,pcd.SUMMARY_TOTAL_NUM,pcd.SIGNIN_NORMAL_NUM,ts.PASS_NUM,pcd.GROUP_NAME FROM `sp_people_count_detail` pcd LEFT JOIN `sp_task_statistical` ts ON pcd.STUDENT_ID = ts.STUDENT_ID and pcd.GROUP_ID = ts.GROUP_ID where 1=1  ";
		String countSql = "SELECT count(1) FROM `sp_people_count_detail` pcd LEFT JOIN `sp_task_statistical` ts ON pcd.STUDENT_ID = ts.STUDENT_ID and pcd.GROUP_ID = ts.GROUP_ID where 1 = 1 ";
		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords() + "%' or pcd.JOB_NUM like '%" + domain.getKeyWords() + "%' )";
			countSql += " and (pcd.STUDENT_NAME like '%" + domain.getKeyWords() + "%' or pcd.JOB_NUM like '%" + domain.getKeyWords() + "%' )";
		}
		if (null != domain.getGroupId() && domain.getGroupId()> 0L) {
			querySql += " and pcd.GROUP_ID =" + domain.getGroupId() ;
			countSql += " and pcd.GROUP_ID =" + domain.getGroupId() ;
		}
		if (null != domain.getStuId() && domain.getStuId()> 0L) {
			querySql += " and pcd.STUDENT_ID =" + domain.getStuId() ;
			countSql += " and pcd.STUDENT_ID =" + domain.getStuId() ;
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		if(StringUtils.isEmpty(domain.getSortField())){
			dto.setKey("pcd.JOB_NUM");
		}else{
			dto.setKey(domain.getSortField());
		}
		if(StringUtils.isEmpty(domain.getSortFlag())){
			dto.setAsc(true);
		}else{
			if ("asc".equals(domain.getSortFlag())) {
				dto.setAsc(true);
			} else {
				dto.setAsc(false);
			}
		}
		sort.add(dto);

		return pageJdbcUtil.getPageData(domain.getPageSize(),domain.getPageNumber(), rm, sort, querySql,
				countSql);
	}

}
