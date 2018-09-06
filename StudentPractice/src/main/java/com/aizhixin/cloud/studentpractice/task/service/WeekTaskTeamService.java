package com.aizhixin.cloud.studentpractice.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.domain.CountDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.task.domain.MentorCountDomain;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTaskTeam;
import com.aizhixin.cloud.studentpractice.task.repository.WeekTaskTeamRepository;


@Service
public class WeekTaskTeamService {

	@Autowired
	private WeekTaskTeamRepository weekTaskTeamRepository;
	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	
	public void save(WeekTaskTeam weekTaskTeam){
		 weekTaskTeamRepository.save(weekTaskTeam);
	}
	
	public void saveList(List<WeekTaskTeam> weekTaskTeams){
		 weekTaskTeamRepository.save(weekTaskTeams);
	}
	
	public Long countByDeleteFlagAndWeekTaskIdAndGroupId(String weekTaskId,Long groupId){
		return weekTaskTeamRepository.countByDeleteFlagAndWeekTaskIdAndGroupId(DataValidity.VALID.getIntValue(), weekTaskId, groupId);
	}

	public List<CountDomain> findWeekTaskByGroupIds(String weekTaskId,Long orgId) {

		String querySql = "SELECT GROUP_ID,count(1) as count_num FROM `sp_week_task_team` where WEEK_TASK_ID ='"+weekTaskId+"' and ORG_ID ="+orgId+" GROUP BY WEEK_TASK_ID,GROUP_ID;";

		return pageJdbcUtil.getInfo(querySql, rm);
	}
	
	
	RowMapper<CountDomain> rm = new RowMapper<CountDomain>() {

		@Override
		public CountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			CountDomain domain = new CountDomain();
			domain.setId(rs.getLong("GROUP_ID"));
			domain.setCount(rs.getLong("count_num"));
			return domain;
		}
	};
}
