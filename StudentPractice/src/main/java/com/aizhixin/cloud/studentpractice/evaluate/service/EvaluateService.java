package com.aizhixin.cloud.studentpractice.evaluate.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.PageDomain;
import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.core.ErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.PushMessageConstants;
import com.aizhixin.cloud.studentpractice.common.core.RoleCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.CountDomain;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.QueryCommentTotalDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.domain.StudentDTO;
import com.aizhixin.cloud.studentpractice.common.domain.UserInfoDomain;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.remote.OrgMangerService;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.evaluate.core.EvaluateCode;
import com.aizhixin.cloud.studentpractice.evaluate.domain.EvaluateDomain;
import com.aizhixin.cloud.studentpractice.evaluate.domain.EvaluateStatisticsDomain;
import com.aizhixin.cloud.studentpractice.evaluate.domain.QueryEvaluateDomain;
import com.aizhixin.cloud.studentpractice.evaluate.domain.StudentInforDomain;
import com.aizhixin.cloud.studentpractice.evaluate.entity.Evaluate;
import com.aizhixin.cloud.studentpractice.evaluate.repository.EvaluateRepository;
import com.aizhixin.cloud.studentpractice.score.domain.CounselorCountDomain;
import com.aizhixin.cloud.studentpractice.score.domain.QueryScoreDomain;
import com.aizhixin.cloud.studentpractice.summary.core.ReportStatusCode;
import com.aizhixin.cloud.studentpractice.summary.core.SummaryCode;
import com.aizhixin.cloud.studentpractice.summary.domain.EnterpriseCountDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.QueryReportDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.QuerySummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.ReportDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.ReportStatisticsDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryNumCountDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryReplyCountDomain;
import com.aizhixin.cloud.studentpractice.summary.entity.Report;
import com.aizhixin.cloud.studentpractice.summary.entity.Summary;
import com.aizhixin.cloud.studentpractice.summary.entity.SummaryReplyCount;
import com.aizhixin.cloud.studentpractice.summary.repository.ReportRepository;
import com.aizhixin.cloud.studentpractice.summary.repository.SummaryRepository;
import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskCountDomain;
import com.aizhixin.cloud.studentpractice.task.domain.WeekTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;
import com.aizhixin.cloud.studentpractice.task.entity.TaskFile;
import com.aizhixin.cloud.studentpractice.task.repository.FileRepository;
import com.aizhixin.cloud.studentpractice.task.service.FileService;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountDetailService;

@Transactional
@Service
public class EvaluateService {

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;
	@Autowired
	private EvaluateRepository evaluateRepository;
	@Autowired
	private OrgMangerService orgMangerService;

	public Evaluate save(Evaluate evaluate) {
		return evaluateRepository.save(evaluate);
	}

	public Evaluate findById(String id) {
		return evaluateRepository.findOne(id);
	}

	public Evaluate findByEvaluateTypeAndGroupIdAndStuId(String evaluateType,
			Long groupId, Long studentId) {
		return evaluateRepository
				.findOneByDeleteFlagAndEvaluateTypeAndGroupIdAndStudentId(
						DataValidity.VALID.getIntValue(), evaluateType,
						groupId, studentId);
	}

	public List<Evaluate> findByGroupIdAndStuIds(Long groupId,
			Set<String> evaluateTypes, Set<Long> studentIds) {
		return this.evaluateRepository
				.findAllByDeleteFlagAndGroupIdAndEvaluateTypeInAndStudentIdIn(
						DataValidity.VALID.getIntValue(), groupId,
						evaluateTypes, studentIds);
	}

	/**
	 * 保存评价信息
	 * 
	 * @param domain
	 * @param dto
	 * @return
	 */
	public Evaluate save(EvaluateDomain domain, UserInfoDomain dto) {

		Evaluate evaluate = null;

		StuInforDomain stuDTO = authUtilService.getMentorInfo(domain
				.getStudentId());
		if (null != stuDTO) {
			domain.setGroupId(stuDTO.getTrainingGroupId());
			domain.setGroupName(stuDTO.getTrainingGroupName());
			if(StringUtils.isBlank(domain.getStuJobNum())){
				domain.setStuJobNum(stuDTO.getJobNum());
			}
		}

		if (StringUtils.isEmpty(domain.getId())) {// 新增
			evaluate = new Evaluate();
			BeanUtils.copyProperties(domain, evaluate);
			evaluate.setId(UUID.randomUUID().toString());
			evaluate.setCreatedBy(dto.getId());
			evaluate.setOrgId(dto.getOrgId());
		} else {// 修改
			evaluate = findById(domain.getId());
			if (null != evaluate) {
				evaluate.setLastModifiedBy(dto.getId());
				evaluate.setLastModifiedDate(new Date());
				if (null != domain.getFirstEvaluate()) {
					evaluate.setFirstEvaluate(domain.getFirstEvaluate());
				}
				if (null != domain.getSecondEvaluate()) {
					evaluate.setSecondEvaluate(domain.getSecondEvaluate());
				}
				if (!StringUtils.isBlank(domain.getAdvice())) {
					evaluate.setAdvice(domain.getAdvice());
				}
			} else {
				throw new CommonException(ErrorCode.ID_IS_REQUIRED,
						"被修改实践评价不存在");
			}
		}

		return save(evaluate);
	}

	public EvaluateDomain findDetail(String evalId) {
		if (!StringUtils.isBlank(evalId)) {
			Evaluate evaluate = findById(evalId);
			if (null != evaluate) {
				EvaluateDomain domain = new EvaluateDomain();
				BeanUtils.copyProperties(evaluate, domain);
				return domain;
			} else {
				throw new CommonException(ErrorCode.ID_IS_REQUIRED, "该实践评价不存在");
			}
		} else {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "实践评价id不能为空");
		}
	}

	public PageData page(QueryEvaluateDomain domain, UserInfoDomain dto) {

		Set<String> roles = dto.getRoleNames();
		PageData pageData = new PageData();
		if (roles.contains(RoleCode.ROLE_STUDENT)) {
			StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
			if (stuDTO != null) {
				List<StudentInforDomain> accountList = new ArrayList<StudentInforDomain>();
				String userIds = String.valueOf(dto.getId());
				Set<Long> stuSet = new HashSet<Long>();
				stuSet.add(dto.getId());

				if (EvaluateCode.EVALUATE_TYPE_I_EVALUATE.equals(domain
						.getFlag())) {
					StudentInforDomain self = new StudentInforDomain();
					self.setId(dto.getId());
					self.setName(dto.getName());
					self.setJobNum(dto.getWorkNo());
					self.setStuClassName(stuDTO.getStuClassName());
					self.setMentorCompanyName(stuDTO.getMentorCompanyName());
					accountList.add(self);
				}

				if (null != stuDTO.getMentorId() && stuDTO.getMentorId() > 0) {
					StudentInforDomain account = new StudentInforDomain();
					account.setMentorId(stuDTO.getMentorId());
					account.setMentorName(stuDTO.getMentorName());
					accountList.add(account);
					if (StringUtils.isEmpty(userIds)) {
						userIds += stuDTO.getMentorId().toString();
					} else {
						userIds += "," + stuDTO.getMentorId().toString();
					}
				}
				if (null != stuDTO.getCounselorId()
						&& stuDTO.getCounselorId() > 0) {
					StudentInforDomain account = new StudentInforDomain();
					account.setCounselorId(stuDTO.getCounselorId());
					account.setCounselorName(stuDTO.getCounselorName());
					account.setCounselorJobNum(stuDTO.getCounselorJobNum());
					accountList.add(account);
					if (StringUtils.isEmpty(userIds)) {
						userIds += stuDTO.getCounselorId().toString();
					} else {
						userIds += "," + stuDTO.getCounselorId().toString();
					}
				}
				HashMap<Long, AccountDTO> avatarList = authUtilService
						.getavatarUsersInfo(userIds);
				for (StudentInforDomain account : accountList) {
					if (null != avatarList.get(account.getId())) {
						account.setAvatar(avatarList.get(account.getId())
								.getAvatar());
					}
				}

				Set<String> evalTypes = new HashSet<String>();
				if (EvaluateCode.EVALUATE_TYPE_I_EVALUATE.equals(domain
						.getFlag())) {
					evalTypes.add(EvaluateCode.EVALUATE_STUDENT_SELF);
					evalTypes.add(EvaluateCode.EVALUATE_STUDENT_TO_COUNSELOR);
					evalTypes.add(EvaluateCode.EVALUATE_STUDENT_TO_MENTOR);
				} else {
					evalTypes.add(EvaluateCode.EVALUATE_COUNSELOR_TO_STUDENT);
					evalTypes.add(EvaluateCode.EVALUATE_MENTOR_TO_STUDENT);
				}
				List<Evaluate> evalList = findByGroupIdAndStuIds(
						stuDTO.getTrainingGroupId(), evalTypes, stuSet);
				HashMap<Long, Evaluate> evalMap = new HashMap<Long, Evaluate>();
				for (Evaluate eval : evalList) {
					if (EvaluateCode.EVALUATE_STUDENT_SELF.equals(eval
							.getEvaluateType())) {
						evalMap.put(eval.getStudentId(), eval);
					}
					if (EvaluateCode.EVALUATE_STUDENT_TO_COUNSELOR.equals(eval
							.getEvaluateType())) {
						evalMap.put(eval.getCounselorId(), eval);
					}
					if (EvaluateCode.EVALUATE_STUDENT_TO_MENTOR.equals(eval
							.getEvaluateType())) {
						evalMap.put(eval.getMentorId(), eval);
					}
					if (EvaluateCode.EVALUATE_COUNSELOR_TO_STUDENT.equals(eval
							.getEvaluateType())) {
						evalMap.put(eval.getCounselorId(), eval);
					}
					if (EvaluateCode.EVALUATE_MENTOR_TO_STUDENT.equals(eval
							.getEvaluateType())) {
						evalMap.put(eval.getMentorId(), eval);
					}
				}
				for (StudentInforDomain account : accountList) {
					Evaluate eval = null;
					if (null != evalMap.get(account.getId())) {
						eval = evalMap.get(account.getId());
					}
					if (null != evalMap.get(account.getCounselorId())) {
						eval = evalMap.get(account.getCounselorId());
					}
					if (null != evalMap.get(account.getMentorId())) {
						eval = evalMap.get(account.getMentorId());

					}
					if (null != eval) {
						account.setEvaluateId(eval.getId());
						account.setCreatedDate(eval.getCreatedDate());
					}
				}
				pageData.setData(accountList);
			}
		} else if (roles.contains(RoleCode.ROLE_CLASSROOMTEACHER)) {// 辅导员
			pageData = orgMangerService.queryInfoByGroupId(domain.getGroupId(),
					domain.getStuName(), domain.getPageNumber(),
					domain.getPageSize());
			List<StudentDTO> stuList = pageData.getData();
			String userIds = "";
			Set<Long> stuSet = new HashSet<Long>();
			ArrayList<StudentInforDomain> stuDomainList = new ArrayList<StudentInforDomain>();
			String enterpriseName = "";
			for (StudentDTO stu : stuList) {
				stuSet.add(stu.getId());
				if (StringUtils.isEmpty(userIds)) {
					userIds += stu.getId().toString();
					StuInforDomain stuDTO = authUtilService.getMentorInfo(stu.getId());
					if (stuDTO != null) {
						enterpriseName = stuDTO.getMentorCompanyName();
					}
				} else {
					userIds += "," + stu.getId().toString();
				}
				StudentInforDomain stuDomain = new StudentInforDomain();
				BeanUtils.copyProperties(stu, stuDomain);
				stuDomain.setJobNum(stu.getJobNumber());
				stuDomain.setStuClassName(stu.getClassesName());
				if(StringUtils.isNotBlank(enterpriseName)){
					stuDomain.setMentorCompanyName(enterpriseName);
				}
				stuDomainList.add(stuDomain);
			}
			HashMap<Long, AccountDTO> avatarList = authUtilService
					.getavatarUsersInfo(userIds);
			for (StudentInforDomain stu : stuDomainList) {
				if (null != avatarList.get(stu.getId())) {
					stu.setAvatar(avatarList.get(stu.getId()).getAvatar());
				}
			}

			Set<String> evalTypes = new HashSet<String>();
			if (EvaluateCode.EVALUATE_TYPE_I_EVALUATE.equals(domain.getFlag())) {
				evalTypes.add(EvaluateCode.EVALUATE_COUNSELOR_TO_STUDENT);
			} else {
				evalTypes.add(EvaluateCode.EVALUATE_STUDENT_TO_COUNSELOR);
			}

			List<Evaluate> evalList = findByGroupIdAndStuIds(
					domain.getGroupId(), evalTypes, stuSet);
			HashMap<Long, Evaluate> evalMap = new HashMap<Long, Evaluate>();
			for (Evaluate eval : evalList) {
				evalMap.put(eval.getStudentId(), eval);
			}
			for (StudentInforDomain account : stuDomainList) {
				Evaluate eval = evalMap.get(account.getId());
				if (null != eval) {
					account.setEvaluateId(eval.getId());
					account.setCreatedDate(eval.getCreatedDate());
				}
			}

			pageData.setData(stuDomainList);
		} else {
			pageData = authUtilService.getStudentInfo(dto.getId(),
					domain.getStuName(), domain.getPageNumber(),
					domain.getPageSize());
			List<StuInforDomain> stuList = pageData.getData();
			String userIds = "";
			Set<Long> stuSet = new HashSet<Long>();
			for (StuInforDomain stu : stuList) {
				stuSet.add(stu.getId());
				if (StringUtils.isEmpty(userIds)) {
					userIds += stu.getId().toString();
				} else {
					userIds += "," + stu.getId().toString();
				}
			}
			HashMap<Long, AccountDTO> avatarList = authUtilService
					.getavatarUsersInfo(userIds);
			ArrayList<StudentInforDomain> stuDomainList = new ArrayList<StudentInforDomain>();
			Long groupId = 0L;
			for (StuInforDomain stu : stuList) {
				if (null != avatarList.get(stu.getId())) {
					stu.setAvatar(avatarList.get(stu.getId()).getAvatar());
				}
				StudentInforDomain stuDomain = new StudentInforDomain();
				BeanUtils.copyProperties(stu, stuDomain);
				stuDomainList.add(stuDomain);
				if (0L == groupId) {
					groupId = stu.getTrainingGroupId();
				}
			}

			Set<String> evalTypes = new HashSet<String>();
			if (EvaluateCode.EVALUATE_TYPE_I_EVALUATE.equals(domain.getFlag())) {
				evalTypes.add(EvaluateCode.EVALUATE_MENTOR_TO_STUDENT);
			} else {
				evalTypes.add(EvaluateCode.EVALUATE_STUDENT_TO_MENTOR);
			}

			List<Evaluate> evalList = findByGroupIdAndStuIds(groupId,
					evalTypes, stuSet);
			HashMap<Long, Evaluate> evalMap = new HashMap<Long, Evaluate>();
			for (Evaluate eval : evalList) {
				evalMap.put(eval.getStudentId(), eval);
			}
			for (StudentInforDomain account : stuDomainList) {
				Evaluate eval = evalMap.get(account.getId());
				if (null != eval) {
					account.setEvaluateId(eval.getId());
					account.setCreatedDate(eval.getCreatedDate());
				}
			}
			pageData.setData(stuDomainList);
		}
		return pageData;
	}
	
	
	public PageData<EvaluateStatisticsDomain> queryInforPage(QueryEvaluateDomain domain,String roleName,Long userId) {
		String querySql = "SELECT pcd.STUDENT_NAME,pcd.JOB_NUM,e.MENTOR_NAME,e.COUNSELOR_NAME,e.COU_JOB_NUM,pcd.COLLEGE_NAME,pcd.PROFESSIONAL_NAME,pcd.CLASS_NAME,pcd.GROUP_NAME,e.FIRST_EVALUATE,e.SECOND_EVALUATE,e.ADVICE from `sp_evaluate` e LEFT JOIN `sp_people_count_detail` pcd ON pcd.GROUP_ID = e.GROUP_ID and pcd.STUDENT_ID = e.STUDENT_ID where e.DELETE_FLAG = 0 and pcd.GROUP_ID is not null";
		String countSql = "SELECT count(1) from `sp_evaluate` e LEFT JOIN `sp_people_count_detail` pcd ON pcd.GROUP_ID = e.GROUP_ID and pcd.STUDENT_ID = e.STUDENT_ID where e.DELETE_FLAG = 0 and pcd.GROUP_ID is not null";

		if (!StringUtils.isEmpty(domain.getStuName())) {
			querySql += " and (e.STUDENT_NAME like '%"+domain.getStuName()+"%' or e.STU_JOB_NUM like '%"+domain.getStuName()+"%')";
			countSql += " and (e.STUDENT_NAME like '%"+domain.getStuName()+"%' or e.STU_JOB_NUM like '%"+domain.getStuName()+"%')";
		}
		
		
		if (!StringUtils.isEmpty(domain.getFlag())) {
			if(!StringUtils.isEmpty(roleName)){
				if("ieval".equals(domain.getFlag())){
					if(RoleCode.ROLE_STUDENT.equals(roleName)){
						querySql += " and e.EVALUATE_TYPE in ('s','stc','stm')";
						countSql += " and e.EVALUATE_TYPE in ('s','stc','stm')";
					}else if(RoleCode.ROLE_CLASSROOMTEACHER.equals(roleName)){
						querySql += " and e.EVALUATE_TYPE ='cts'";
						countSql += " and e.EVALUATE_TYPE ='cts'";
						
						querySql += " and e.COUNSELOR_ID ="+userId;
						countSql += " and e.COUNSELOR_ID ="+userId;
					}else{
						querySql += " and e.EVALUATE_TYPE ='mts'";
						countSql += " and e.EVALUATE_TYPE ='mts'";
					}
				}else{
					if(RoleCode.ROLE_STUDENT.equals(roleName)){
						querySql += " and e.EVALUATE_TYPE in ('cts','mts')";
						countSql += " and e.EVALUATE_TYPE in ('cts','mts')";
					}else if(RoleCode.ROLE_CLASSROOMTEACHER.equals(roleName)){
						querySql += " and e.EVALUATE_TYPE ='stc'";
						countSql += " and e.EVALUATE_TYPE ='stc'";
						
						querySql += " and e.COUNSELOR_ID ="+userId;
						countSql += " and e.COUNSELOR_ID ="+userId;
					}else{
						querySql += " and e.EVALUATE_TYPE ='stm'";
						countSql += " and e.EVALUATE_TYPE ='stm'";
					}
				}
			}
		}
		if(null != domain.getGroupId() &&  domain.getGroupId() > 0){
			querySql += " and e.GROUP_ID ="+domain.getGroupId()+"";
			countSql += " and e.GROUP_ID ="+domain.getGroupId()+"";
		}
	
		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("e.STU_JOB_NUM");
		dto.setAsc(false);
		sort.add(dto);

		return pageJdbcUtil.getPageData(domain.getPageSize(),
				domain.getPageNumber(), rm, sort, querySql, countSql);
	}

	RowMapper<EvaluateStatisticsDomain> rm = new RowMapper<EvaluateStatisticsDomain>() {

		@Override
		public EvaluateStatisticsDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			EvaluateStatisticsDomain domain = new EvaluateStatisticsDomain();
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setStuJobNum(rs.getString("JOB_NUM"));
			domain.setMentorName(rs.getString("MENTOR_NAME"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			domain.setCouJobNum(rs.getString("COU_JOB_NUM"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			domain.setAdvice(rs.getString("ADVICE"));
			domain.setFirstEvaluate(rs.getInt("FIRST_EVALUATE"));
			domain.setSecondEvaluate(rs.getInt("SECOND_EVALUATE"));
			return domain;
		}
	};
}
