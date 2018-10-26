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
import com.aizhixin.cloud.studentpractice.common.core.PushMessageConstants;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageDTO;
import com.aizhixin.cloud.studentpractice.common.domain.QueryCommentTotalDomain;
import com.aizhixin.cloud.studentpractice.common.domain.SortDTO;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.PushService;
import com.aizhixin.cloud.studentpractice.common.service.QueryService;
import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.summary.core.SummaryCode;
import com.aizhixin.cloud.studentpractice.summary.domain.QuerySummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryNumCountDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryReplyCountDomain;
import com.aizhixin.cloud.studentpractice.summary.entity.Summary;
import com.aizhixin.cloud.studentpractice.summary.entity.SummaryReplyCount;
import com.aizhixin.cloud.studentpractice.summary.repository.SummaryRepository;
import com.aizhixin.cloud.studentpractice.task.core.MessageCode;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
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
public class SummaryService {

	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	@Lazy
	private AuthUtilService authUtilService;
	@Autowired
	private SummaryRepository summaryRepository;
	@Autowired
	private FileService fileService;
	@Autowired
	private PeopleCountDetailService peopleCountDetailService;
	@Autowired
	@Lazy
	private QueryService queryService;
	@Autowired
	@Lazy
	private PushService pushService;

	public Summary save(Summary summary) {
		return summaryRepository.save(summary);
	}
	
	public void saveList(List<Summary> summaryList) {
		 summaryRepository.save(summaryList);
	}

	public Summary findById(String id) {
		return summaryRepository.findOne(id);
	}

	public SummaryDomain findDetail(String id) {

		Summary summary = summaryRepository.findOne(id);
		if (null != summary) {
			SummaryDomain domain = new SummaryDomain();
			BeanUtils.copyProperties(summary, domain);
			HashMap<Long, AccountDTO> avatarMap = authUtilService
					.getavatarUsersInfo(summary.getCreatedBy().toString());
			AccountDTO stu = avatarMap.get(summary.getCreatedBy());
			if (null != stu) {
				domain.setCreatorName(stu.getName());
				domain.setCreatorAvatar(stu.getAvatar());
			}
			HashSet<String> idList = new HashSet<String>();
			idList.add(summary.getId());
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
	public Summary save(SummaryDomain domain, AccountDTO dto) {

		Summary summary = null;
		
		if (StringUtils.isEmpty(domain.getId())) {// 新增
			summary = new Summary();
			BeanUtils.copyProperties(domain, summary);
			String id = UUID.randomUUID().toString();
			summary.setId(id);
			summary.setCreatedDate(new Date());
			summary.setOrgId(dto.getOrgId());
			summary.setOrgName(dto.getOrgName());
			summary.setCreatedBy(dto.getId());
			summary.setCreatorName(dto.getName());
			summary.setJobNum(dto.getWorkNo());
			StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
			if (null != stuDTO) {
				summary.setMentorId(stuDTO.getMentorId());
				summary.setMentorName(stuDTO.getMentorName());
				summary.setCounselorId(stuDTO.getCounselorId());
				summary.setCounselorName(stuDTO.getCounselorName());
				summary.setGroupId(stuDTO.getTrainingGroupId());
				summary.setGroupName(stuDTO.getTrainingGroupName());
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
			summary = findById(domain.getId());
			summary.setSummaryTitle(domain.getSummaryTitle());
			summary.setSummaryType(domain.getSummaryType());
			summary.setDescription(domain.getDescription());
			summary.setPublishStatus(domain.getPublishStatus());
			if (StringUtils.isEmpty(summary.getCreatorName())) {
				summary.setCreatorName(dto.getName());
			}
			if(null == summary.getCounselorId()){
				StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
				if (null != stuDTO) {
					summary.setMentorId(stuDTO.getMentorId());
					summary.setMentorName(stuDTO.getMentorName());
					summary.setCounselorId(stuDTO.getCounselorId());
					summary.setCounselorName(stuDTO.getCounselorName());
					summary.setGroupId(stuDTO.getTrainingGroupId());
					summary.setGroupName(stuDTO.getTrainingGroupName());
				}
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

		summary = save(summary);
		if (StringUtils.isEmpty(domain.getId())) {
			PushMessageDTO msg = new PushMessageDTO();
			msg.setSummaryId(summary.getId());
         	msg.setTaskName(domain.getSummaryTitle());
         	msg.setContent(MessageCode.MESSAGE_HEAD.concat(MessageCode.MESSAGE_SUMMARY_SAVE).replace("{1}", dto.getName()));
			msg.setFunction(PushMessageConstants.MODULE_TASK);
			msg.setModule(PushMessageConstants.MODULE_TASK);
			msg.setTitle(MessageCode.MESSAGE_TITLE_SUMMARY);
			msg.setGroupId(summary.getGroupId());
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(summary.getCounselorId());
			msg.setUserIds(ids);
			pushService.addPushList(msg);
		}
		
		return summary;
	}

	/**
	 * 删除报告
	 * 
	 * @param id
	 */
	public Summary delete(String id) {

		Summary summary = findById(id);
		if (null != summary) {
			summary.setDeleteFlag(DataValidity.INVALID.getIntValue());
			summary = save(summary);
		}
		return summary;
	}

	RowMapper<SummaryDomain> rm = new RowMapper<SummaryDomain>() {

		@Override
		public SummaryDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			SummaryDomain domain = new SummaryDomain();
			domain.setId(rs.getString("ID"));
			domain.setSummaryType(rs.getString("SUMMARY_TYPE"));
			domain.setSummaryTitle(rs.getString("SUMMARY_TITLE"));
			domain.setCreatedDate(rs.getDate("CREATED_DATE"));
			domain.setCreatedBy(rs.getLong("CREATED_BY"));
//			domain.setDescription(rs.getString("DESCRIPTION"));
			domain.setCounselorId(rs.getLong("COUNSELOR_ID"));
			domain.setPublishStatus(rs.getString("PUBLISH_STATUS"));
			domain.setCommentNum(rs.getInt("COMMENT_NUM"));
			domain.setBrowseNum(rs.getInt("BROWSE_NUM"));
			domain.setOrgName(rs.getString("org_name"));
			domain.setJobNum(rs.getString("JOB_NUM"));
			return domain;
		}
	};

	/**
	 * 分页查询报告表
	 * 
	 * @param pageSize
	 *            每页数量
	 * @param offset
	 *            第几页
	 * @param domain
	 * @param userId
	 *            创建者id
	 * @param sortField
	 *            排序字段
	 * @param sortFlag
	 *            排序类型
	 * @return
	 */
	public Map<String, Object> findPage(QuerySummaryDomain domain, String token) {

		PageData<SummaryDomain> pageData = queryInforPage(domain);
		List<SummaryDomain> dataList = pageData.getData();
		if (null != dataList && !dataList.isEmpty()) {
			HashSet<String> idList = new HashSet<String>();
			String userIds = "";
			for (SummaryDomain dto : dataList) {
				idList.add(dto.getId());
				if (StringUtils.isEmpty(userIds)) {
					userIds += dto.getCreatedBy().toString();
				} else {
					userIds += "," + dto.getCreatedBy().toString();
				}
			}

			HashMap<Long, AccountDTO> avatarMap = authUtilService
					.getavatarUsersInfo(userIds);
			for (SummaryDomain dto : dataList) {
				AccountDTO stu = avatarMap.get(dto.getCreatedBy());
				if (null != stu) {
					dto.setCreatorName(stu.getName());
					dto.setCreatorAvatar(stu.getAvatar());
				}
			}

			QueryCommentTotalDomain queryCountDTO = new QueryCommentTotalDomain();
			queryCountDTO.setSourceIds(idList);
			queryCountDTO.setModule(PushMessageConstants.MODULE_TASK);

			HashMap<String, Integer> commentTotalMap = authUtilService
					.getCommentTotalCount(queryCountDTO, token);
			if (null != commentTotalMap && !commentTotalMap.isEmpty()) {
				for (SummaryDomain dto : dataList) {
					if (null != commentTotalMap.get(dto.getId())) {
						dto.setCommentNum(commentTotalMap.get(dto.getId()));
					}
				}
			}

			// 日报，月报等附件信息列表
			List<File> fileList = fileService.findAllBySourceIds(idList);
			if (null != fileList && !fileList.isEmpty()) {
				HashMap<String, ArrayList<File>> fileMap = new HashMap<String, ArrayList<File>>();
				for (File file : fileList) {
					if (null != fileMap.get(file.getSourceId())) {
						ArrayList<File> list = fileMap.get(file.getSourceId());
						list.add(file);
						fileMap.put(file.getSourceId(), list);
					} else {
						ArrayList<File> list = new ArrayList<File>();
						list.add(file);
						fileMap.put(file.getSourceId(), list);
					}
				}
				for (SummaryDomain dto : dataList) {
					if (null != fileMap.get(dto.getId())) {
						dto.setFileList(fileMap.get(dto.getId()));
					}
				}
			}

		}

		Map<String, Object> result = new HashMap<String, Object>();

		List<IdNameDomain> countList = countSummaryNum(domain);
		if (null != countList && !countList.isEmpty()) {
			for (IdNameDomain dto : countList) {
				if (SummaryCode.SUMMARY_TYPE_DAILY.equals(dto.getName())) {
					result.put("dailyNum", dto.getId());
				}
				if (SummaryCode.SUMMARY_TYPE_WEEKLY.equals(dto.getName())) {
					result.put("weeklyNum", dto.getId());
				}
				if (SummaryCode.SUMMARY_TYPE_MONTHLY.equals(dto.getName())) {
					result.put("monthlyNum", dto.getId());
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

	private PageData<SummaryDomain> queryInforPage(QuerySummaryDomain domain) {
		String querySql = "SELECT ID,JOB_NUM,SUMMARY_TYPE,SUMMARY_TITLE,CREATED_DATE,CREATED_BY,COUNSELOR_ID,PUBLISH_STATUS,COMMENT_NUM,BROWSE_NUM,org_name from `SP_SUMMARY` s where DELETE_FLAG = 0 ";
		String countSql = "SELECT count(1) FROM `SP_SUMMARY` s where DELETE_FLAG = 0 ";

		if (!StringUtils.isEmpty(domain.getSummaryTitle())) {
			querySql += " and s.SUMMARY_TITLE like '%"
					+ domain.getSummaryTitle() + "%'";
			countSql += " and s.SUMMARY_TITLE like '%"
					+ domain.getSummaryTitle() + "%'";
		}

		if (null != domain.getDayNum() && domain.getDayNum().intValue() > 0) {
			// querySql +=
			// " and (s.CREATED_DATE <= CURDATE() and s.CREATED_DATE >= date_sub(curdate(),interval "+domain.getDayNum().intValue()+" day))";
			// countSql +=
			// " and (s.CREATED_DATE <= CURDATE() and s.CREATED_DATE >= date_sub(curdate(),interval "+domain.getDayNum().intValue()+" day))";
			if (1 == domain.getDayNum()) {
				querySql += " and date(s.CREATED_DATE) = curdate() ";
				countSql += " and date(s.CREATED_DATE) = curdate() ";
			} else if (7 == domain.getDayNum()) {
				querySql += " and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(s.CREATED_DATE) ";
				countSql += " and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(s.CREATED_DATE) ";
			} else {
				querySql += " and DATE_SUB(CURDATE(), INTERVAL  3 MONTH) <= date(s.CREATED_DATE) ";
				countSql += " and DATE_SUB(CURDATE(), INTERVAL  3 MONTH) <= date(s.CREATED_DATE) ";
			}
		}
		if (!StringUtils.isEmpty(domain.getSummaryType())) {
			querySql += " and s.SUMMARY_TYPE = '" + domain.getSummaryType()
					+ "'";
			countSql += " and s.SUMMARY_TYPE = '" + domain.getSummaryType()
					+ "'";
		}
		if (!StringUtils.isEmpty(domain.getPublishStatus())) {
			querySql += " and s.PUBLISH_STATUS = '" + domain.getPublishStatus()
					+ "'";
			countSql += " and s.PUBLISH_STATUS = '" + domain.getPublishStatus()
					+ "'";
		}
		if (!StringUtils.isEmpty(domain.getCreatorName())) {
			querySql += " and s.creator_name like '%" + domain.getCreatorName()
					+ "%'";
			countSql += " and s.creator_name like '%" + domain.getCreatorName()
					+ "%'";
		}

		if (null != domain.getUserId() && domain.getUserId() > 0L) {
			querySql += " and s.CREATED_BY =" + domain.getUserId();
			countSql += " and s.CREATED_BY =" + domain.getUserId();
		}
		if (null != domain.getCounselorId() && domain.getCounselorId() > 0L) {
			querySql += " and s.COUNSELOR_ID =" + domain.getCounselorId();
			countSql += " and s.COUNSELOR_ID =" + domain.getCounselorId();
		}
		if (null != domain.getMentorId() && domain.getMentorId() > 0L) {
			querySql += " and s.MENTOR_ID =" + domain.getMentorId();
			countSql += " and s.MENTOR_ID =" + domain.getMentorId();
		}
		if (null != domain.getGroupId() && domain.getGroupId() > 0L) {
			querySql += " and s.GROUP_ID =" + domain.getGroupId();
			countSql += " and s.GROUP_ID =" + domain.getGroupId();
		}

		List<SortDTO> sort = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		if (StringUtils.isBlank(domain.getSortField())) {
			dto.setKey("s.CREATED_DATE");
		} else {
			dto.setKey(domain.getSortField());
		}
		if ("asc".equals(domain.getSortFlag())) {
			dto.setAsc(true);
		} else {
			dto.setAsc(false);
		}
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
			domain.setId(rs.getLong("count_num"));
			domain.setName(rs.getString("SUMMARY_TYPE"));
			return domain;
		}
	};

	/**
	 * 根据条件统计日报周报月报数量
	 * 
	 * @param domain
	 * @return
	 */
	public List<IdNameDomain> countSummaryNum(QuerySummaryDomain domain) {
		String countSql = "SELECT count(1) as count_num,s.SUMMARY_TYPE FROM `sp_summary` s where s.DELETE_FLAG = 0 ";

		if (!StringUtils.isEmpty(domain.getCreatorName())) {
			countSql += " and s.creator_name like '%" + domain.getCreatorName()
					+ "%'";
		}
		
		if (null != domain.getDayNum() && domain.getDayNum().intValue() > 0) {
			if (1 == domain.getDayNum()) {
				countSql += " and date(s.CREATED_DATE) = curdate() ";
			} else if (7 == domain.getDayNum()) {
				countSql += " and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(s.CREATED_DATE) ";
			} else {
				countSql += " and DATE_SUB(CURDATE(), INTERVAL  3 MONTH) <= date(s.CREATED_DATE) ";
			}
		}
		
		if (null != domain.getUserId() && domain.getUserId() > 0L) {
			countSql += " and s.CREATED_BY =" + domain.getUserId();
		}
		if (null != domain.getCounselorId() && domain.getCounselorId() > 0L) {
			countSql += " and s.COUNSELOR_ID =" + domain.getCounselorId();
		}
		if (null != domain.getMentorId() && domain.getMentorId() > 0L) {
			countSql += " and s.MENTOR_ID =" + domain.getMentorId();
		}
		if (null != domain.getGroupId() && domain.getGroupId() > 0L) {
			countSql += " and s.GROUP_ID =" + domain.getGroupId();
		}
		countSql += " GROUP BY s.SUMMARY_TYPE";
		List<IdNameDomain> list = pageJdbcUtil.getInfo(countSql, countRm);
		return list;
	}

	RowMapper<SummaryNumCountDomain> countSummaryNumRm = new RowMapper<SummaryNumCountDomain>() {

		@Override
		public SummaryNumCountDomain mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			// TODO Auto-generated method stub
			SummaryNumCountDomain domain = new SummaryNumCountDomain();
			domain.setCountNum(rs.getInt("count_num"));
			domain.setSummaryType(rs.getString("SUMMARY_TYPE"));
			domain.setCreatorBy(rs.getLong("CREATED_BY"));
			return domain;
		}
	};

	public void countSummaryNumTask() {
		
		List<IdNameDomain> groupIdList =  queryService.getAllGroupValid();
		for(IdNameDomain groupDTO :groupIdList){
		String countSql = "SELECT count(1) as count_num,s.SUMMARY_TYPE,s.CREATED_BY FROM `sp_summary` s where s.DELETE_FLAG = 0 and group_id = "+groupDTO.getId()+" GROUP BY s.SUMMARY_TYPE,s.CREATED_BY";

		List<SummaryNumCountDomain> list = pageJdbcUtil.getInfo(countSql,
				countSummaryNumRm);
		if (null != list && !list.isEmpty()) {

			HashMap<Long, SummaryNumCountDomain> countMap = new HashMap<Long, SummaryNumCountDomain>();
			Set<Long> stuIds = new HashSet<Long>();

			for (SummaryNumCountDomain domain : list) {
				if (null == countMap.get(domain.getCreatorBy())) {
					if (SummaryCode.SUMMARY_TYPE_DAILY.equals(domain
							.getSummaryType())) {
						domain.setDailyNum(domain.getCountNum());
					}
					if (SummaryCode.SUMMARY_TYPE_WEEKLY.equals(domain
							.getSummaryType())) {
						domain.setWeeklyNum(domain.getCountNum());
					}
					if (SummaryCode.SUMMARY_TYPE_MONTHLY.equals(domain
							.getSummaryType())) {
						domain.setMonthlyNum(domain.getCountNum());
					}
					countMap.put(domain.getCreatorBy(), domain);
				} else {
					SummaryNumCountDomain temp = countMap.get(domain
							.getCreatorBy());
					if (SummaryCode.SUMMARY_TYPE_DAILY.equals(domain
							.getSummaryType())) {
						temp.setDailyNum(domain.getCountNum());
					}
					if (SummaryCode.SUMMARY_TYPE_WEEKLY.equals(domain
							.getSummaryType())) {
						temp.setWeeklyNum(domain.getCountNum());
					}
					if (SummaryCode.SUMMARY_TYPE_MONTHLY.equals(domain
							.getSummaryType())) {
						temp.setMonthlyNum(domain.getCountNum());
					}

				}
				stuIds.add(domain.getCreatorBy());
			}
			List<PeopleCountDetail> detailList = peopleCountDetailService
					.findAllByStuIdsAndGroupId(stuIds,groupDTO.getId());

			if (null != detailList && !detailList.isEmpty()) {
				for (PeopleCountDetail detail : detailList) {
					if (null != countMap.get(detail.getStudentId())) {
						SummaryNumCountDomain domain = countMap.get(detail
								.getStudentId());
						detail.setDailyNum(domain.getDailyNum());
						detail.setWeeklyNum(domain.getWeeklyNum());
						detail.setMonthlyNum(domain.getMonthlyNum());
						Integer totalNum = 0;
						if(null != domain.getDailyNum()){
							totalNum += domain.getDailyNum().intValue();
						}
						if(null != domain.getWeeklyNum()){
							totalNum += domain.getWeeklyNum().intValue();
						}
						if(null != domain.getMonthlyNum()){
							totalNum += domain.getMonthlyNum().intValue();
						}
						detail.setSummaryTotalNum(totalNum);
						if(totalNum.intValue() > 0){
							detail.setWhetherCommit(TaskStatusCode.PRACTICE_COMMIT);
						}
					}
				}
			}
			
			peopleCountDetailService.saveList(detailList);
		}
		}
	}

	public void initJobNum(){
		List<Summary> list = summaryRepository.findAllByGroupIdIsNotNullAndJobNumIsNullAndDeleteFlag(DataValidity.VALID.getIntValue());
		if(null != list && !list.isEmpty()){
			for(Summary summary : list){
				HashSet<Long> ids = new HashSet<Long>();
				List<PeopleCountDetail> detailList = peopleCountDetailService.findAllByStuIds(ids);
				if(null !=detailList && !detailList.isEmpty()){
					PeopleCountDetail detail =detailList.get(0);
					if(null != detail && StringUtils.isNotEmpty(detail.getJobNum())){
						summary.setJobNum(detail.getJobNum());
					}
				}else{
					continue;
				}
			}
			saveList(list);
		}
	}
	
//	public Summary review(SummaryDomain domain){
//		Summary summary = findById(domain.getId());
//		if(null != summary){
//			summary.setIsReview(true);
//			summary.setSummaryScore(domain.getSummaryScore());
//			summary.setLastModifiedDate(new Date());
//			summary = save(summary);
//		}
//		return summary;
//	}
	
}
