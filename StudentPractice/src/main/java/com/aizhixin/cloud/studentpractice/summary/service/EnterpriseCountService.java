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
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.summary.domain.EnterpriseCountDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryReplyCountDomain;
import com.aizhixin.cloud.studentpractice.summary.entity.EnterpriseCount;
import com.aizhixin.cloud.studentpractice.summary.entity.SummaryReplyCount;
import com.aizhixin.cloud.studentpractice.summary.repository.EnterpriseCountRepository;
import com.aizhixin.cloud.studentpractice.summary.repository.SummaryReplyCountRepository;
import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.TaskFile;
import com.aizhixin.cloud.studentpractice.task.repository.TaskFileRepository;

@Transactional
@Service
public class EnterpriseCountService {

	@Autowired
	private EnterpriseCountRepository enterpriseCountRepository;
	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;

	public void saveList(List<EnterpriseCount> fileList) {
		enterpriseCountRepository.save(fileList);
	}

	RowMapper<EnterpriseCount> countMentorNumRm = new RowMapper<EnterpriseCount>() {

		@Override
		public EnterpriseCount mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			EnterpriseCount domain = new EnterpriseCount();
			domain.setId(rs.getString("id"));
			domain.setName(rs.getString("NAME"));
			domain.setOrgId(rs.getLong("ORG_ID"));
			domain.setMailbox(rs.getString("MAILBOX"));
			domain.setTelephone(rs.getString("TELEPHONE"));
			domain.setMentorNum(rs.getLong("mentor_num"));
			domain.setProvince(rs.getString("PROVINCE"));
			domain.setCity(rs.getString("CITY"));
			domain.setCounty(rs.getString("COUNTY"));
			domain.setAddress(rs.getString("ADDRESS"));
			return domain;
		}
	};

	public List<EnterpriseCount> countMentorNum() {
		
		String orgDbName = authUtilService.getOrgDbName();
		String sql = "select e.id,e.`NAME`,cm.ORG_ID,e.MAILBOX,e.TELEPHONE,count(cm.ACCOUNT_ID) as mentor_num,e.PROVINCE,e.CITY,e.COUNTY,e.ADDRESS from "+orgDbName+".`t_enterprise` e LEFT JOIN "+orgDbName+".`t_corporate_mentors_info` cm ON e.ID = cm.ENTERPRISE_ID where e.DELETE_FLAG =0 and cm.DELETE_FLAG = 0 GROUP BY cm.ORG_ID";

		List<EnterpriseCount> list = pageJdbcUtil.getInfo(sql,
				countMentorNumRm);
		return list;
	}
	
	RowMapper<EnterpriseCountDomain> countStuNumRm = new RowMapper<EnterpriseCountDomain>() {

		@Override
		public EnterpriseCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			EnterpriseCountDomain domain = new EnterpriseCountDomain();
			domain.setId(rs.getString("ENTERPRISE_ID"));
			domain.setStuNum(rs.getLong("stu_num"));
			return domain;
		}
	};

	public List<EnterpriseCountDomain> countStuNum() {
		
		String orgDbName = authUtilService.getOrgDbName();
		String sql = "select count(gru.USER_ID) as stu_num,cm.ENTERPRISE_ID from "+orgDbName+".`t_corporate_mentors_info` cm LEFT JOIN "+orgDbName+".`t_training_group` g ON cm.ID = g.CORPORATE_MENTORS_ID LEFT JOIN  "+orgDbName+".`t_group_relation_user` gru ON gru.GROUP_ID = g.ID where cm.DELETE_FLAG =0 and g.DELETE_FLAG = 0 and cm.ENTERPRISE_ID is not null group by cm.ENTERPRISE_ID";

		List<EnterpriseCountDomain> list = pageJdbcUtil.getInfo(sql,
				countStuNumRm);
		return list;
	}

	
	public void enterpriseCountTask() {

		List<EnterpriseCount> list = this.countMentorNum();
		if (null != list && !list.isEmpty()) {
			List<EnterpriseCountDomain> stuList = this.countStuNum();
			HashMap<String,Long> stuNumMap = new HashMap<String,Long>();
			for(EnterpriseCountDomain domain:stuList){
				stuNumMap.put(domain.getId(), domain.getStuNum());
			}
			
			for(EnterpriseCount count:list){
				if(null != stuNumMap.get(count.getId())){
					count.setStuNum(stuNumMap.get(count.getId()));
				}
			}
			
			enterpriseCountRepository.save(list);
		}
	}

	RowMapper<EnterpriseCountDomain> rm = new RowMapper<EnterpriseCountDomain>() {

		@Override
		public EnterpriseCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			EnterpriseCountDomain domain = new EnterpriseCountDomain();
			domain.setName(rs.getString("NAME"));
			domain.setMailbox(rs.getString("MAILBOX"));
			domain.setTelephone(rs.getString("TELEPHONE"));
			domain.setMentorNum(rs.getLong("mentor_num"));
			domain.setProvince(rs.getString("PROVINCE"));
			domain.setCity(rs.getString("CITY"));
			domain.setCounty(rs.getString("COUNTY"));
			domain.setAddress(rs.getString("ADDRESS"));
			domain.setStuNum(rs.getLong("stu_num"));
			return domain;
		}
	};

	public PageData<EnterpriseCountDomain> enterpriseCountStatistics(
			QueryStuPageDomain domain, Long orgId) {

		String querySql = "SELECT NAME,PROVINCE,CITY,COUNTY,ADDRESS,TELEPHONE,MAILBOX,STU_NUM,MENTOR_NUM from `SP_ENTERPRISE_COUNT` ec where ec.DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `SP_ENTERPRISE_COUNT` ec where ec.DELETE_FLAG = 0 ";
		if (!StringUtils.isEmpty(domain.getEnterpriseName())) {
			querySql += " and (ec.NAME like '%" + domain.getEnterpriseName()+"%') ";
			countSql += " and (ec.NAME like '%" + domain.getEnterpriseName()+"%') ";
		}
		if (null != orgId && orgId.longValue() > 0L) {
			querySql += " and ec.ORG_ID=" + orgId + "";
			countSql += " and ec.ORG_ID=" + orgId + "";
		}
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto = new SortDTO();
		dto.setKey("ec.CREATED_DATE");
		dto.setAsc(true);

		return pageJdbcUtil.getPageData(domain.getPageSize(),
				domain.getPageNumber(), rm, sort, querySql, countSql);
	}

}
