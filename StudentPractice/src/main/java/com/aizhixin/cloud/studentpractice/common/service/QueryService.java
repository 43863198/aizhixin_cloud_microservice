package com.aizhixin.cloud.studentpractice.common.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.DistributeLock;
import com.aizhixin.cloud.studentpractice.common.util.DateUtil;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.score.domain.TrainingGropSetDTO;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryReplyCountDomain;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.CountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.EnterpriseCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.GroupStuDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.SignCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.SignDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskStatisticsDomain;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCount;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;
import com.aizhixin.cloud.studentpractice.task.entity.SignDetail;
import com.aizhixin.cloud.studentpractice.task.entity.TaskStatistical;
import com.aizhixin.cloud.studentpractice.task.repository.PeopleCountDetailRepository;
import com.aizhixin.cloud.studentpractice.task.repository.PeopleCountRepository;
import com.aizhixin.cloud.studentpractice.task.repository.SignDetailRepository;
import com.aizhixin.cloud.studentpractice.task.repository.TaskStatisticalRepository;

@Transactional
@Service
public class QueryService {
	private static Logger log = LoggerFactory
			.getLogger(QueryService.class);

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private AuthUtilService authUtilService;


	public List<GroupStuDomain> getAllGroupStuValid() {
		
		String orgDbName = authUtilService.getOrgDbName();
		// 查询未结束实践参与计划的所有学生
		String stu_sql = "SELECT tg.ID,tg.GROP_NAME,gru.USER_ID,tg.START_DATE,tg.END_DATE  FROM "
				+ orgDbName
				+ ".`t_group_relation_user` gru LEFT JOIN "
				+ orgDbName
				+ ".`t_training_group` tg ON tg.ID = gru.GROUP_ID WHERE tg.DELETE_FLAG = 0 and gru.DELETE_FLAG = 0 and tg.END_DATE > NOW()";
//		stu_sql += " and gru.USER_ID =169688";
		List<GroupStuDomain> userList = pageJdbcUtil.getInfo(stu_sql, groupRm);
		return userList;
	}
	
	public List<GroupStuDomain> getGroupStuValid(Long groupId) {
		
		String orgDbName = authUtilService.getOrgDbName();
		// 查询未结束实践参与计划的所有学生
		String stu_sql = "SELECT tg.ID,tg.GROP_NAME,gru.USER_ID,tg.START_DATE,tg.END_DATE  FROM "
				+ orgDbName
				+ ".`t_group_relation_user` gru LEFT JOIN "
				+ orgDbName
				+ ".`t_training_group` tg ON tg.ID = gru.GROUP_ID WHERE tg.DELETE_FLAG = 0 and gru.DELETE_FLAG = 0 and gru.GROUP_ID ="+groupId;
		List<GroupStuDomain> userList = pageJdbcUtil.getInfo(stu_sql, groupRm);
		return userList;
	}

	RowMapper<GroupStuDomain> groupRm = new RowMapper<GroupStuDomain>() {
		@Override
		public GroupStuDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			GroupStuDomain domain = new GroupStuDomain();
			domain.setGroupId(rs.getLong("id"));
			domain.setGroupName(rs.getString("GROP_NAME"));
			domain.setStuId(rs.getLong("USER_ID"));
			domain.setStartDate(rs.getDate("START_DATE"));
			domain.setEndDate(rs.getDate("END_DATE"));
			return domain;
		}
	};
	
	
	/**
	 * 获取目前未结束的实践参与计划列表
	 * @return
	 */
	public List<IdNameDomain> getAllGroupValid() {
		
		String orgDbName = authUtilService.getOrgDbName();
		// 查询未结束实践参与计划
		String stu_sql = "SELECT tg.ID,tg.GROP_NAME from "
				+ orgDbName
				+ ".`t_training_group` tg WHERE tg.DELETE_FLAG = 0 and tg.END_DATE > NOW()";
		List<IdNameDomain> userList = pageJdbcUtil.getInfo(stu_sql, gRm);
		return userList;
	}
	
	public String getAllGroupValidStr(){
		List<IdNameDomain> groupList = getAllGroupValid();
		String groupIds = "";
		if(null != groupList && !groupList.isEmpty()){
			for(IdNameDomain domain : groupList){
				if(StringUtils.isBlank(groupIds)){
					groupIds = domain.getId().toString();
				}else{
					groupIds += ","+domain.getId();
				}
			}
		}
		return groupIds;
	}
	
	public Set<Long> getAllGroupValidSet(){
		List<IdNameDomain> groupList = getAllGroupValid();
		Set<Long> groupIds = new HashSet<Long>();
		if(null != groupList && !groupList.isEmpty()){
			for(IdNameDomain domain : groupList){
				groupIds.add(domain.getId());
			}
		}
		return groupIds;
	}

	RowMapper<IdNameDomain> gRm = new RowMapper<IdNameDomain>() {
		@Override
		public IdNameDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			IdNameDomain domain = new IdNameDomain();
			domain.setId(rs.getLong("ID"));
			domain.setName(rs.getString("GROP_NAME"));
			return domain;
		}
	};
	
	
	public List<TrainingGropSetDTO> getGroupSetList(HashSet<Long> groupIds) {
		
		String orgDbName = authUtilService.getOrgDbName();
		String ids ="";
		for(Long id : groupIds){
			if(StringUtils.isEmpty(ids)){
				ids = String.valueOf(id);
			}else{
				ids += ","+id;
			}
		}
		String stu_sql = "SELECT * FROM `"+orgDbName+"`.`t_training_group_set` where GROUP_ID in ("+ids+");";
		List<TrainingGropSetDTO> userList = pageJdbcUtil.getInfo(stu_sql, groupSetRm);
		return userList;
	}

	RowMapper<TrainingGropSetDTO> groupSetRm = new RowMapper<TrainingGropSetDTO>() {
		@Override
		public TrainingGropSetDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			TrainingGropSetDTO domain = new TrainingGropSetDTO();
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setNeedDailyNum(rs.getInt("NEED_DAILY_NUM"));
			domain.setNeedWeeklyNum(rs.getInt("NEED_WEEKLY_NUM"));
			domain.setNeedMonthlyNum(rs.getInt("NEED_MONTHLY_NUM"));
			return domain;
		}
	};
}
