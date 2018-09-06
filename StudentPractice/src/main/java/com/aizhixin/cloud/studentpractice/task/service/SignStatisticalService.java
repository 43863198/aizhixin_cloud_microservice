package com.aizhixin.cloud.studentpractice.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.DistributeLock;
import com.aizhixin.cloud.studentpractice.common.service.QueryService;
import com.aizhixin.cloud.studentpractice.common.util.DateUtil;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
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
public class SignStatisticalService {
	private static Logger log = LoggerFactory
			.getLogger(SignStatisticalService.class);

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private SignDetailRepository signDetailRepository;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private PeopleCountDetailService peopleCountDetailService;
	@Autowired
	@Lazy
	private QueryService queryService;

	/**
	 * 同步实践考勤数据
	 */
	public void synSignInforTask() {

		String ddDbName = authUtilService.getDdDbName();

		List<GroupStuDomain> userList = queryService.getAllGroupStuValid();
		if (null != userList && !userList.isEmpty()) {
			HashMap<Long, String> userMap = new HashMap<Long, String>();
			HashMap<Long, GroupStuDomain> dateMap = new HashMap<Long, GroupStuDomain>();
			Set<Long> groupIds = new HashSet<Long>();
			for (GroupStuDomain domain : userList) {
				String userIds = "";
				userIds = userMap.get(domain.getGroupId());
				if (StringUtils.isEmpty(userIds)) {
					userIds = "'" + domain.getStuId() + "'";
					dateMap.put(domain.getGroupId(), domain);
					groupIds.add(domain.getGroupId());
				} else {
					userIds = userIds + ",'" + domain.getStuId() + "'";
				}
				userMap.put(domain.getGroupId(), userIds);
			}

			ArrayList<SignDetail> signSaveList = new ArrayList<SignDetail>();
			HashMap<Long, PeopleCountDetail> peopDetailMap = new HashMap<Long, PeopleCountDetail>();
			for (Map.Entry<Long, GroupStuDomain> entry : dateMap.entrySet()) {

				Long groupId = entry.getKey();
				GroupStuDomain domain = entry.getValue();
				String stuIds = userMap.get(domain.getGroupId());

				// 根据参与计划的学生id和开始结束时间区间查询该学生对应参与计划的实践考勤记录
				String sign_sql = "SELECT s.STUDENT_ID,s.SIGN_TIME,s.GPS_DETAIL,s.GPS_LOCATION,s.GPS_TYPE,s.`STATUS`,s.CREATED_DATE FROM  "
						+ ddDbName
						+ ".`dd_studentsignin` s LEFT JOIN "
						+ ddDbName
						+ ".`dd_counsellorrollcall` c ON c.id = s.COUNSERLLORROLLCALL_ID LEFT JOIN "
						+ ddDbName
						+ ".`dd_tempgroup` t ON t.id = c.TEMPGROUP_ID where s.DELETE_FLAG = 0 and c.DELETE_FLAG = 0 and t.DELETE_FLAG = 0 and s.STUDENT_ID in (" + stuIds + ") and t.rollcall_type = '40' and t.practice_id = "+groupId;
				List<SignDetailDomain> signList = pageJdbcUtil.getInfo(
						sign_sql, signRm);
				if (null != signList && !signList.isEmpty()) {

					for (SignDetailDomain signDomain : signList) {
						SignDetail signDetail = new SignDetail();
						BeanUtils.copyProperties(signDomain, signDetail);
						signDetail.setGroupId(groupId);
						signSaveList.add(signDetail);
						if (null == peopDetailMap
								.get(signDomain.getStudentId())) {
							PeopleCountDetail detail = peopleCountDetailService.findByStuIdAndGroupId(signDomain.getStudentId(), groupId);
							if(null == detail){
								continue;
							}else{
								detail.setSignInTotalNum(0);
								detail.setSignInNormalNum(0);
								detail.setLeaveNum(0);
							}
							detail.setSignInTotalNum(1);
							if ("40".equals(signDomain.getSignStatus())) {
								detail.setLeaveNum(1);
							}
							if ("20".equals(signDomain.getSignStatus())) {
								detail.setSignInNormalNum(1);
							}
							peopDetailMap
									.put(signDomain.getStudentId(), detail);
						} else {
							PeopleCountDetail detail = peopDetailMap
									.get(signDomain.getStudentId());
							detail.setSignInTotalNum(detail.getSignInTotalNum() + 1);
							if ("40".equals(signDomain.getSignStatus())) {
								if (null != detail.getLeaveNum()) {
									detail.setLeaveNum(detail.getLeaveNum() + 1);
								} else {
									detail.setLeaveNum(1);
								}
							}
							if ("20".equals(signDomain.getSignStatus())) {
								if (null != detail.getSignInNormalNum()) {
									detail.setSignInNormalNum(detail
											.getSignInNormalNum() + 1);
								} else {
									detail.setSignInNormalNum(1);
								}
							}
							peopDetailMap
									.put(signDomain.getStudentId(), detail);
						}
					}
				}
			}
			signDetailRepository.deleteByGroupIds(groupIds);
			signDetailRepository.save(signSaveList);

			ArrayList<PeopleCountDetail> detailList = new ArrayList<PeopleCountDetail>();
			for (Map.Entry<Long, PeopleCountDetail> entry : peopDetailMap
					.entrySet()) {
				Long stuId = entry.getKey();
				PeopleCountDetail tmpDetail = entry.getValue();
				PeopleCountDetail detail = peopleCountDetailService
						.findByStuIdAndGroupId(stuId,tmpDetail.getGroupId());
				if (null != detail) {
					detail.setSignInNormalNum(tmpDetail.getSignInNormalNum());
					detail.setSignInTotalNum(tmpDetail.getSignInTotalNum());
					detail.setLeaveNum(tmpDetail.getLeaveNum());
					detailList.add(detail);
				}
			}
			if (!detailList.isEmpty()) {
				peopleCountDetailService.saveList(detailList);
			}
		}
	}


	RowMapper<SignDetailDomain> signRm = new RowMapper<SignDetailDomain>() {
		@Override
		public SignDetailDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			SignDetailDomain domain = new SignDetailDomain();
			domain.setStudentId(rs.getLong("STUDENT_ID"));
			domain.setSignTime(rs.getString("SIGN_TIME"));
			domain.setGpsDetail(rs.getString("GPS_DETAIL"));
			domain.setGpsLocation(rs.getString("GPS_LOCATION"));
			domain.setGpsType(rs.getString("GPS_TYPE"));
			domain.setSignStatus(rs.getString("STATUS"));
			domain.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
			return domain;
		}
	};

	RowMapper<SignCountDomain> countRm = new RowMapper<SignCountDomain>() {

		@Override
		public SignCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			SignCountDomain domain = new SignCountDomain();
			domain.setSignInTotalNum(rs.getInt("SIGNIN_TOTAL_NUM"));
			domain.setSignInNormalNum(rs.getInt("SIGNIN_NORMAL_NUM"));
			domain.setLeaveNum(rs.getInt("LEAVE_NUM"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentId(rs.getLong("STUDENT_ID"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setGroupId(rs.getLong("GROUP_ID"));
			domain.setGrade(rs.getString("GRADE"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			return domain;
		}
	};

	public Map<String, Object> signDetailStatistics(QueryStuPageDomain domain,
			Long orgId) {

		String querySql = "SELECT JOB_NUM,STUDENT_ID,STUDENT_NAME,CLASS_NAME,PROFESSIONAL_NAME,COLLEGE_NAME,GRADE,SIGNIN_TOTAL_NUM,SIGNIN_NORMAL_NUM,LEAVE_NUM,pcd.COUNSELOR_NAME,pcd.group_id,pcd.group_name from `sp_people_count_detail` pcd where pcd.DELETE_FLAG = 0 ";
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
		dto.setKey("pcd.JOB_NUM");
		dto.setAsc(true);

		return pageJdbcUtil.getPageInfor(domain.getPageSize(),
				domain.getPageNumber(), countRm, sort, querySql, countSql);
	}

	RowMapper<SignDetailDomain> detailRm = new RowMapper<SignDetailDomain>() {

		@Override
		public SignDetailDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			SignDetailDomain domain = new SignDetailDomain();
			domain.setGpsDetail(rs.getString("GPS_DETAIL"));
			domain.setGpsLocation(rs.getString("GPS_LOCATION"));
			domain.setGpsType(rs.getString("GPS_TYPE"));
			domain.setSignTime(rs.getString("SIGN_TIME"));
			return domain;
		}
	};

	public Map<String, Object> signDetailPage(QueryStuPageDomain domain) {

		String querySql = "SELECT SIGN_TIME,GPS_DETAIL,GPS_LOCATION,GPS_TYPE from `sp_sign_detail` sd where SIGN_STATUS = '20' and STUDENT_ID ="
				+ domain.getStuId()+" and group_id ="+domain.getGroupId();
		String countSql = "SELECT count(1) FROM `sp_sign_detail` sd where SIGN_STATUS = '20' and STUDENT_ID ="
				+ domain.getStuId()+" and group_id ="+domain.getGroupId();
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto = new SortDTO();
		dto.setKey("sd.STUDENT_ID");
		dto.setAsc(true);

		return pageJdbcUtil.getPageInfor(domain.getPageSize(),
				domain.getPageNumber(), detailRm, sort, querySql, countSql);
	}
}
