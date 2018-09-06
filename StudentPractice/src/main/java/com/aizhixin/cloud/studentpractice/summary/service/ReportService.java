package com.aizhixin.cloud.studentpractice.summary.service;

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
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.CountDomain;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageDTO;
import com.aizhixin.cloud.studentpractice.common.domain.QueryCommentTotalDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.PushService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
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
import com.aizhixin.cloud.studentpractice.task.core.MessageCode;
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
public class ReportService {

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;
	@Autowired
	private ReportRepository reportRepository;
	@Autowired
	private FileService fileService;
	@Autowired
	@Lazy
	private PushService pushService;

	public Report save(Report report) {
		return reportRepository.save(report);
	}

	public List<Report> findAllByGroupId(Long groupId) {
		return reportRepository.findAllByDeleteFlagAndGroupId(DataValidity.VALID.getIntValue(), groupId);
	}
	
	public Report findById(String id) {
		return reportRepository.findOne(id);
	}

	public ReportDomain findDetail(String id) {

		Report report = reportRepository.findOne(id);
		if (null != report) {
			ReportDomain domain = new ReportDomain();
			BeanUtils.copyProperties(report, domain);
			HashMap<Long, AccountDTO> avatarMap = authUtilService
					.getavatarUsersInfo(report.getCreatedBy().toString());
			AccountDTO stu = avatarMap.get(report.getCreatedBy());
			if (null != stu) {
				domain.setCreatorName(stu.getName());
				domain.setCreatorAvatar(stu.getAvatar());
			}
			HashSet<String> idList = new HashSet<String>();
			idList.add(report.getId());
			List<File> fileList = fileService.findAllBySourceIds(idList);
			domain.setFileList(fileList);
			return domain;
		}
		return null;
	}

	/**
	 * 保存报告信息
	 * 
	 * @param domain
	 * @param dto
	 * @return
	 */
	public Report save(ReportDomain domain, AccountDTO dto) {

		Report report = null;
		
		if (StringUtils.isEmpty(domain.getId())) {// 新增
			report = new Report();
			BeanUtils.copyProperties(domain, report);
			String id = UUID.randomUUID().toString();
			report.setId(id);
			report.setCreatedDate(new Date());
			report.setOrgId(dto.getOrgId());
			report.setCreatedBy(dto.getId());
			report.setCreatorName(dto.getName());
			report.setJobNum(dto.getWorkNo());
			StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
			if (null != stuDTO) {
				report.setCounselorId(stuDTO.getCounselorId());
				report.setCounselorName(stuDTO.getCounselorName());
				report.setGroupId(stuDTO.getTrainingGroupId());
				report.setGroupName(stuDTO.getTrainingGroupName());
			}
			if(!StringUtils.isEmpty(domain.getCommitStatus())){
				if(domain.getCommitStatus().equals(ReportStatusCode.REPORT_COMMIT)){
					report.setStatus(ReportStatusCode.REPORT_STATUS_CHECK_PENDING);
					report.setLastModifiedDate(new Date());
				}else{
					report.setStatus(ReportStatusCode.REPORT_STATUS_UNCOMMIT);
				}
			}else{
				report.setStatus(ReportStatusCode.REPORT_STATUS_UNCOMMIT);
			}
			

			// 保存上传附件
			if (null != domain.getFileList() && domain.getFileList().size() > 0) {
				List<File> fileList = new ArrayList<File>();
				for (File f : domain.getFileList()) {
					File file = new File();
					String fileId = UUID.randomUUID().toString();
					file.setId(fileId);
					file.setFileName(f.getFileName());
					file.setSrcUrl(f.getSrcUrl());
					file.setSourceId(id);
					file.setCreatedBy(dto.getId());
					fileList.add(file);
				}
				fileService.saveList(fileList);
			}

		} else {// 修改
			report = findById(domain.getId());
			report.setReportTitle(domain.getReportTitle());
			report.setDescription(domain.getDescription());
			report.setStatus(domain.getStatus());
			report.setLastModifiedDate(new Date());
			if (StringUtils.isEmpty(report.getCreatorName())) {
				report.setCreatorName(dto.getName());
			}
			if(null == report.getCounselorId()){
				StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
				if (null != stuDTO) {
					report.setCounselorId(stuDTO.getCounselorId());
					report.setCounselorName(stuDTO.getCounselorName());
					report.setGroupId(stuDTO.getTrainingGroupId());
					report.setGroupName(stuDTO.getTrainingGroupName());
				}
			}
			if(!StringUtils.isEmpty(domain.getCommitStatus())){
				if(domain.getCommitStatus().equals(ReportStatusCode.REPORT_COMMIT)){
					report.setStatus(ReportStatusCode.REPORT_STATUS_CHECK_PENDING);
					report.setLastModifiedDate(new Date());
				}else{
					report.setStatus(ReportStatusCode.REPORT_STATUS_UNCOMMIT);
				}
			}else{
				report.setStatus(ReportStatusCode.REPORT_STATUS_UNCOMMIT);
			}
			
			fileService.deleteBySourceId(domain.getId());
			if (null != domain.getFileList() && domain.getFileList().size() > 0) {
				List<File> fileList = new ArrayList<File>();
				for (File f : domain.getFileList()) {
					File file = new File();
					String fileId = UUID.randomUUID().toString();
					file.setId(fileId);
					file.setFileName(f.getFileName());
					file.setSrcUrl(f.getSrcUrl());
					file.setSourceId(domain.getId());
					file.setCreatedBy(dto.getId());
					fileList.add(file);
				}
				fileService.saveList(fileList);
			}
		}

		report = save(report);
		if (StringUtils.isEmpty(domain.getId())) {
			PushMessageDTO msg = new PushMessageDTO();
			msg.setReportId(report.getId());
			msg.setCommitUserName(dto.getName());
         	msg.setTaskName(domain.getReportTitle());
         	msg.setContent(MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_STUDENT_ADD_TASK).replace("{1}", dto.getName()));
			msg.setFunction(PushMessageConstants.MODULE_REPORT);
			msg.setModule(PushMessageConstants.MODULE_TASK);
			msg.setTitle(MessageCode.MESSAGE_TITLE_REPORT);
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(report.getCounselorId());
			msg.setUserIds(ids);
			pushService.addPushList(msg);
		}
		
		return report;
	}

	/**
	 * 删除报告
	 * 
	 * @param id
	 */
	public void delete(String id) {

		Report report = findById(id);
		
		if (null != report) {
			if(!ReportStatusCode.REPORT_STATUS_UNCOMMIT.equals(report.getStatus())){
				throw new CommonException(ErrorCode.ID_IS_REQUIRED,
						"实践报告未提交时才能删除！");
			}
			report.setDeleteFlag(DataValidity.INVALID.getIntValue());
			save(report);
		}
	}
	
	public Report cancel(String id) {

		Report report = findById(id);
		if (null != report) {
			report.setStatus(ReportStatusCode.REPORT_STATUS_UNCOMMIT);
			save(report);
		}
		return report;
	}
	
	public Report review(ReportDomain domain) {

		Report report = findById(domain.getId());
		if (null != report) {
				report.setStatus(domain.getStatus());
				report.setAdvice(domain.getAdvice());
				report.setReviewTime(new Date());
				report = save(report);
				
				if (StringUtils.isEmpty(domain.getId())) {
					PushMessageDTO msg = new PushMessageDTO();
					msg.setReportId(report.getId());
		         	msg.setTaskName(report.getReportTitle());
		         	msg.setContent(MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_REPORT_REVIEW));
					msg.setFunction(PushMessageConstants.MODULE_REPORT);
					msg.setModule(PushMessageConstants.MODULE_TASK);
					msg.setTitle(MessageCode.MESSAGE_TITLE_REPORT);
					ArrayList<Long> ids = new ArrayList<Long>();
					ids.add(report.getCreatedBy());
					msg.setUserIds(ids);
					pushService.addPushList(msg);
				}
		}
		return report;
	}

	RowMapper<ReportDomain> rm = new RowMapper<ReportDomain>() {

		@Override
		public ReportDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			ReportDomain domain = new ReportDomain();
			domain.setId(rs.getString("ID"));
			domain.setReportTitle(rs.getString("REPORT_TITLE"));
			domain.setCreatedDate(rs.getTime("LAST_MODIFIED_DATE"));
			domain.setDescription(rs.getString("DESCRIPTION"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStatus(rs.getString("STATUS"));
			domain.setCreatorName(rs.getString("creator_name"));
			return domain;
		}
	};

	
	public Map<String, Object> findPage(QueryReportDomain domain, String token) {

		PageData<ReportDomain> pageData = queryInforPage(domain);
		List<ReportDomain> dataList = pageData.getData();
		
//		if (null != dataList && !dataList.isEmpty()) {
//			HashSet<String> idList = new HashSet<String>();
//			String userIds = "";
//			for (SummaryDomain dto : dataList) {
//				idList.add(dto.getId());
//				if (StringUtils.isEmpty(userIds)) {
//					userIds += dto.getCreatedBy().toString();
//				} else {
//					userIds += "," + dto.getCreatedBy().toString();
//				}
//			}

//			HashMap<Long, AccountDTO> avatarMap = authUtilService
//					.getavatarUsersInfo(userIds);
//			for (SummaryDomain dto : dataList) {
//				AccountDTO stu = avatarMap.get(dto.getCreatedBy());
//				if (null != stu) {
//					dto.setCreatorName(stu.getName());
//					dto.setCreatorAvatar(stu.getAvatar());
//				}
//			}

//			QueryCommentTotalDomain queryCountDTO = new QueryCommentTotalDomain();
//			queryCountDTO.setSourceIds(idList);
//			queryCountDTO.setModule(PushMessageConstants.MODULE_SUMMARY);
//			HashMap<String, Integer> commentTotalMap = authUtilService
//					.getCommentTotalCount(queryCountDTO, token);
//			if (null != commentTotalMap && !commentTotalMap.isEmpty()) {
//				for (SummaryDomain dto : dataList) {
//					if (null != commentTotalMap.get(dto.getId())) {
//						dto.setCommentNum(commentTotalMap.get(dto.getId()));
//					}
//				}
//			}

//			List<File> fileList = fileService.findAllBySourceIds(idList);
//			if (null != fileList && !fileList.isEmpty()) {
//				HashMap<String, ArrayList<File>> fileMap = new HashMap<String, ArrayList<File>>();
//				for (File file : fileList) {
//					if (null != fileMap.get(file.getSourceId())) {
//						ArrayList<File> list = fileMap.get(file.getSourceId());
//						list.add(file);
//						fileMap.put(file.getSourceId(), list);
//					} else {
//						ArrayList<File> list = new ArrayList<File>();
//						list.add(file);
//						fileMap.put(file.getSourceId(), list);
//					}
//				}
//				for (SummaryDomain dto : dataList) {
//					if (null != fileMap.get(dto.getId())) {
//						dto.setFileList(fileMap.get(dto.getId()));
//					}
//				}
//			}

//		}
		
		Map<String, Object> result = new HashMap<String, Object>();

		if (null != domain.getCounselorId() && domain.getCounselorId() > 0L) {
			List<IdNameDomain> countList = countStatusNum(domain.getCounselorId());
			for(IdNameDomain dto : countList){
				if(ReportStatusCode.REPORT_STATUS_BACK_TO.equals(dto.getName())){
					result.put(ReportStatusCode.REPORT_STATUS_BACK_TO, dto.getId());
				}
				if(ReportStatusCode.REPORT_STATUS_FINISH.equals(dto.getName())){
					result.put(ReportStatusCode.REPORT_STATUS_FINISH, dto.getId());
				}
				if(ReportStatusCode.REPORT_STATUS_CHECK_PENDING.equals(dto.getName())){
					result.put(ReportStatusCode.REPORT_STATUS_CHECK_PENDING, dto.getId());
				}
			}
		}
		
		PageDomain p = new PageDomain();
		p.setPageNumber(domain.getPageNumber());
		p.setPageSize(domain.getPageSize());
		p.setTotalElements(pageData.getPage().getTotalElements());
		p.setTotalPages(pageData.getPage().getTotalPages());
		result.put(ApiReturnConstants.PAGE, p);
		result.put(ApiReturnConstants.DATA, dataList);

		return result;
	}

	private PageData<ReportDomain> queryInforPage(QueryReportDomain domain) {
		String querySql = "SELECT sr.ID,sr.creator_name,sr.DESCRIPTION,sr.JOB_NUM,sr.REPORT_TITLE,sr.`STATUS`,sr.LAST_MODIFIED_DATE FROM `sp_report` sr where sr.DELETE_FLAG =0 ";
		String countSql = "SELECT count(1) FROM `sp_report` sr where sr.DELETE_FLAG =0 ";


		if (!StringUtils.isEmpty(domain.getKeyWords())) {
			querySql += " and (sr.creator_name like '%" + domain.getKeyWords()
					+ "%' or sr.JOB_NUM like '%" + domain.getKeyWords() + "%')";
			countSql += " and (sr.creator_name like '%" + domain.getKeyWords()
					+ "%' or sr.JOB_NUM like '%" + domain.getKeyWords() + "%')";
		}

		if (null != domain.getUserId() && domain.getUserId() > 0L) {
			querySql += " and sr.CREATED_BY =" + domain.getUserId();
			countSql += " and sr.CREATED_BY =" + domain.getUserId();
		}
		if (null != domain.getCounselorId() && domain.getCounselorId() > 0L) {
			querySql += " and sr.COUNSELOR_ID =" + domain.getCounselorId();
			countSql += " and sr.COUNSELOR_ID =" + domain.getCounselorId();
		}
		if (!StringUtils.isEmpty(domain.getStatus())) {
			querySql += " and sr.`STATUS` ='" + domain.getStatus()+"'";
			countSql += " and sr.`STATUS` ='" + domain.getStatus()+"'";
		}
		if (null != domain.getGroupId() && domain.getGroupId() > 0L) {
			querySql += " and sr.GROUP_ID =" + domain.getGroupId();
			countSql += " and sr.GROUP_ID =" + domain.getGroupId();
		}

		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setKey("sr.CREATED_DATE");
		dto.setAsc(false);
		sort.add(dto);

		return pageJdbcUtil.getPageData(domain.getPageSize(),
				domain.getPageNumber(), rm, sort, querySql, countSql);
	}

	
	RowMapper<IdNameDomain> countRm = new RowMapper<IdNameDomain>() {

		@Override
		public IdNameDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			IdNameDomain domain = new IdNameDomain();
			domain.setName(rs.getString("STATUS"));
			domain.setId(rs.getLong("count_num"));
			return domain;
		}
	};
	

	public List<IdNameDomain> countStatusNum(Long counselorId) {
		String sql = "SELECT count(1) as count_num,sr.`STATUS` FROM `sp_report` sr where sr.DELETE_FLAG =0 AND sr.`STATUS` in ('pass','backTo','checkPending') AND sr.COUNSELOR_ID ="+counselorId+"  GROUP BY sr.`STATUS`";

		List<IdNameDomain> list = pageJdbcUtil.getInfo(sql,
				countRm);
		return list;
	}
	
	
	RowMapper<CountDomain> countStuReportRm = new RowMapper<CountDomain>() {

		@Override
		public CountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			CountDomain domain = new CountDomain();
			domain.setCount(rs.getLong("report_num"));
			domain.setId(rs.getLong("CREATED_BY"));
			return domain;
		}
	};
	

	public List<CountDomain> countStuReportNum(Long groupId) {
		String sql = "SELECT count(1) as report_num,CREATED_BY FROM `sp_report` where DELETE_FLAG = 0 and `STATUS` in ('checkPending','backTo','finish') and group_id = "+groupId+" GROUP BY CREATED_BY;";

		List<CountDomain> list = pageJdbcUtil.getInfo(sql,
				countStuReportRm);
		return list;
	}
	
	
	public Map<String, Object> reportStatistics(
			QueryStuPageDomain domain, Long orgId) {

		String querySql = "SELECT pcd.COLLEGE_NAME,pcd.PROFESSIONAL_NAME,pcd.GRADE,pcd.CLASS_NAME,pcd.JOB_NUM, pcd.STUDENT_NAME,p.COUNSELOR_NAME,p.REPORT_TITLE,p.ADVICE,p.`STATUS`,p.REVIEW_TIME,pcd.group_name,p.CREATED_DATE FROM `sp_people_count_detail` pcd LEFT JOIN `sp_report` p ON pcd.STUDENT_ID = p.CREATED_BY where p.DELETE_FLAG = 0 and pcd.GROUP_ID =p.GROUP_ID ";
		String countSql = "SELECT count(1) FROM `sp_people_count_detail` pcd LEFT JOIN `sp_report` p ON pcd.STUDENT_ID = p.CREATED_BY where p.DELETE_FLAG = 0 and pcd.GROUP_ID =p.GROUP_ID ";
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
			querySql += " and p.ORG_ID=" + orgId + "";
			countSql += " and p.ORG_ID=" + orgId + "";
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
		sort.add(dto);
		dto = new SortDTO();
		dto.setKey("p.REVIEW_TIME");
		dto.setAsc(true);
		sort.add(dto);

		return pageJdbcUtil.getPageInfor(domain.getPageSize(),
				domain.getPageNumber(), reportRm, sort, querySql, countSql);
	}
	
	
	RowMapper<ReportStatisticsDomain> reportRm = new RowMapper<ReportStatisticsDomain>() {

		@Override
		public ReportStatisticsDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			ReportStatisticsDomain domain = new ReportStatisticsDomain();
			domain.setJobNum(rs.getString("JOB_NUM"));
			domain.setStudentName(rs.getString("STUDENT_NAME"));
			domain.setClassName(rs.getString("CLASS_NAME"));
			domain.setProfessionalName(rs.getString("PROFESSIONAL_NAME"));
			domain.setCollegeName(rs.getString("COLLEGE_NAME"));
			domain.setGrade(rs.getString("GRADE"));
			domain.setCounselorName(rs.getString("COUNSELOR_NAME"));
			domain.setReportTitle(rs.getString("REPORT_TITLE"));
			domain.setAdvice(rs.getString("ADVICE"));
			domain.setStatus(rs.getString("STATUS"));
			domain.setGroupName(rs.getString("GROUP_NAME"));
			if(null != domain.getStatus() && ReportStatusCode.REPORT_STATUS_UNCOMMIT.equals(domain.getStatus())){
				domain.setCommit(false);
			}else{
				domain.setCommit(true);
			}
			domain.setReviewTime(rs.getTimestamp("REVIEW_TIME"));
			domain.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
			return domain;
		}
	};
}
