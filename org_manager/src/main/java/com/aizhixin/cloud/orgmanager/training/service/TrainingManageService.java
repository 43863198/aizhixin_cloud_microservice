package com.aizhixin.cloud.orgmanager.training.service;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.domain.CounRollcallGroupDTOV2;
import com.aizhixin.cloud.orgmanager.common.domain.CounRollcallGroupPracticeDTO;
import com.aizhixin.cloud.orgmanager.common.domain.IdDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.MessageDTOV2;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import com.aizhixin.cloud.orgmanager.remote.StudentPracticeService;
import com.aizhixin.cloud.orgmanager.training.core.GroupStatusConstants;
import com.aizhixin.cloud.orgmanager.training.dto.*;
import com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo;
import com.aizhixin.cloud.orgmanager.training.entity.GroupRelation;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroupSet;
import com.aizhixin.cloud.orgmanager.training.util.PageJdbcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@Transactional
@Service
public class TrainingManageService {
	
	private static Logger log = LoggerFactory
			.getLogger(TrainingManageService.class);
	
	@Autowired
	private MentorsTrainingService mentorsTrainingService;
	@Autowired
	private TrainingGroupService trainingGroupService;
	@Autowired
	private UserService userService;
	@Autowired
	private GroupRelationService groupRelationService;
	@Autowired
	private PageJdbcUtil pageJdbcUtil;
	@Autowired
	private TrainingGroupSetService trainingGroupSetService;
	@Autowired
	@Lazy
	private RedisDataService redisDataService;
	@Autowired
	private StudentPracticeService studentPracticeService;

	/**
	 * 创建实训小组
	 * 
	 * @param trainingGropDTO
	 * @return
	 */
	public Map<String, Object> creatGroup(TrainingGropDTO trainingGropDTO) {
		Map<String, Object> result = new HashMap<>();
		try {
			TrainingGroup trainingGroup = new TrainingGroup();
			if (null != trainingGropDTO.getGroupName()) {
				trainingGroup.setGropName(trainingGropDTO.getGroupName());
			}
			if (null != trainingGropDTO.getStartDate()) {
				trainingGroup.setStartDate(trainingGropDTO.getStartDate());
			}
			if (null != trainingGropDTO.getEndDate()) {
				trainingGroup.setEndDate(trainingGropDTO.getEndDate());
			}
			if (null != trainingGropDTO.getOrgId()) {
				trainingGroup.setOrgId(trainingGropDTO.getOrgId());
			}
			if (null != trainingGropDTO.getCorporateMentorsId()) {
				CorporateMentorsInfo corporateMentorsInfo = mentorsTrainingService
						.queryCorporateMentorsInfoById(trainingGropDTO
								.getCorporateMentorsId());
				trainingGroup.setCorporateMentorsInfo(corporateMentorsInfo);
			}
			if (null != trainingGropDTO.getTeacherId()) {
				User teacher = userService.findById(trainingGropDTO
						.getTeacherId());
				trainingGroup.setTeacher(teacher);
				redisDataService.delCounselorGroupInfor(teacher.getId());
			} else {
				Long teacherId = getTeacherId(trainingGropDTO.getStudentIds()
						.get(0));
				if (null != teacherId && teacherId.longValue() > 0L) {
					User teacher = userService.findById(teacherId);
					trainingGroup.setTeacher(teacher);
					redisDataService.delCounselorGroupInfor(teacherId);
				}
			}

			trainingGroupService.save(trainingGroup);
			Long id = trainingGroup.getId();
			HashSet<Long> stuIds = new HashSet<Long>();
			stuIds.addAll(trainingGropDTO.getStudentIds());
			if (null != id) {

				if (null != trainingGropDTO.getSetDTO()) {
					TrainingGropSetDTO setDTO = trainingGropDTO.getSetDTO();
					if (null == setDTO.getSignWeight()
							&& null == setDTO.getSummaryWeight()
							&& null == setDTO.getReportWeight()
							&& null == setDTO.getTaskWeight()) {
					} else {
						TrainingGroupSet set = new TrainingGroupSet();
						BeanUtils.copyProperties(setDTO, set);
						set.setGroupId(id);
						set.setScoreDate(trainingGroup.getEndDate());
						trainingGroupSetService.save(set);
					}
				}

				ArrayList<GroupRelation> groupRelationList = new ArrayList<GroupRelation>();
				for (int i = 0; i < trainingGropDTO.getStudentIds().size(); i++) {
					GroupRelation groupRelation = new GroupRelation();
					groupRelation.setTrainingGroup(trainingGroup);
					groupRelation.setUser(userService.findById(trainingGropDTO
							.getStudentIds().get(i)));
					// groupRelationService.creaRelation(groupRelation);
					groupRelationList.add(groupRelation);
				}
				groupRelationService.creaRelation(groupRelationList);

			}
			if (null != trainingGroup.getTeacher()) {
				addCallGroup(trainingGroup, stuIds);
			}
			
			studentPracticeService.queryTrainGroupInfo(trainingGroup.getId());
			
			try{
				MessageDTOV2 messageDTO = new MessageDTOV2();
				messageDTO.setAudience(new ArrayList<>(stuIds));
				TrainingGroupInfoDTO msgDto = new TrainingGroupInfoDTO(
						trainingGroup.getId(), trainingGroup.getGropName(), trainingGroup.getStartDate(),
						trainingGroup.getEndDate());
				messageDTO.setData(msgDto);
				messageDTO.setType(20);
				userService.pushMsg(messageDTO);
			}catch (Exception e) {
				log.error("下发实训计划创建消息失败！", e);
			}
			
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "实训计划创建失败！");
			log.error("实训计划创建失败！", e);
			return result;
		}
		result.put("success", true);
		result.put("message", "实训计划创建成功！");
		return result;
	}

	public void initCallGroupByGroupId(Long groupId) {

		TrainingGroup group = trainingGroupService.getGroupInfoById(groupId);
		if (null != group && null != group.getTeacher().getId()) {
			List<StudentDTO> studentDTOs = groupRelationService
					.findStudentsByGroupId(groupId);
			HashSet<Long> stuIds = new HashSet<Long>();
			for (StudentDTO dto : studentDTOs) {
				stuIds.add(dto.getId());
			}
			addCallGroup(group, stuIds);
		}
	}

	public void addCallGroup(TrainingGroup group, HashSet<Long> stuIds) {

		AddCallGroupThread thread = new AddCallGroupThread(group, stuIds);
		thread.start();
	}

	class AddCallGroupThread extends Thread {
		private TrainingGroup group;
		private HashSet<Long> stuIds;

		public AddCallGroupThread(TrainingGroup group, HashSet<Long> stuIds) {
			this.group = group;
			this.stuIds = stuIds;
		}

		public void run() {

			CounRollcallGroupDTOV2 dto = new CounRollcallGroupDTOV2();
			dto.setPracticeId(group.getId());
			dto.setTempGroupName(group.getGropName());
			dto.setStudentList(stuIds);
			userService.addCounsellorGroup(group.getOrgId(), group.getTeacher()
					.getId(), group.getTeacher().getName(), dto);

			group = null;
			stuIds.clear();
			stuIds = null;
		}
	}

	public Long getTeacherId(Long stuId) {

		String querySql = "select ct.TEACHER_ID from `t_classes_teacher` ct where CLASSES_ID IN (SELECT t.CLASSES_ID from `t_user` t where ACCOUNT_ID = "
				+ stuId + ") ORDER BY id desc LIMIT 0,1";
		List<IdDomain> teacherList = pageJdbcUtil
				.getInfo(querySql, teacherIdRm);
		if (null != teacherList && !teacherList.isEmpty()) {
			return teacherList.get(0).getId();
		}
		return null;
	}

	RowMapper<IdDomain> teacherIdRm = new RowMapper<IdDomain>() {
		@Override
		public IdDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			IdDomain domain = new IdDomain();
			domain.setId(rs.getLong("TEACHER_ID"));
			return domain;
		}
	};

	/**
	 * 按机构统计指导教师人数
	 * 
	 * @param orgId
	 * @return
	 */
	public Long getClassTeacherTotal(Long orgId) {

		String querySql = "SELECT count(DISTINCT(teacher_id)) as total from `t_classes_teacher` where ORG_ID = "
				+ orgId;
		List<IdDomain> teacherList = pageJdbcUtil.getInfo(querySql, totalRm);
		if (null != teacherList && !teacherList.isEmpty()) {
			return teacherList.get(0).getId();
		}
		return null;
	}

	/**
	 * 按机构统计学生人数
	 * 
	 * @param orgId
	 * @return
	 */
	public Long getStuTotal(Long orgId) {

		String querySql = "SELECT count(1) as total from `t_user` where USER_TYPE = 70 and DELETE_FLAG = 0 and ORG_ID ="
				+ orgId;
		List<IdDomain> teacherList = pageJdbcUtil.getInfo(querySql, totalRm);
		if (null != teacherList && !teacherList.isEmpty()) {
			return teacherList.get(0).getId();
		}
		return null;
	}

	/**
	 * 按机构统计企业导师人数
	 * 
	 * @param orgId
	 * @return
	 */
	public Long getMentorTotal(Long orgId) {

		String querySql = "SELECT count(1) as total from `t_corporate_mentors_info` where  DELETE_FLAG = 0 and ORG_ID ="
				+ orgId;
		List<IdDomain> teacherList = pageJdbcUtil.getInfo(querySql, totalRm);
		if (null != teacherList && !teacherList.isEmpty()) {
			return teacherList.get(0).getId();
		}
		return null;
	}

	/**
	 * 按机构统计参与计划结束数量
	 * 
	 * @param orgId
	 * @return
	 */
	public Long getGroupEndTotal(Long orgId) {

		String querySql = "SELECT count(1) as total from `t_training_group` where DELETE_FLAG = 0 and END_DATE < NOW() and ORG_ID ="
				+ orgId;
		List<IdDomain> teacherList = pageJdbcUtil.getInfo(querySql, totalRm);
		if (null != teacherList && !teacherList.isEmpty()) {
			return teacherList.get(0).getId();
		}
		return null;
	}

	/**
	 * 按机构统计参与计划未结束数量
	 * 
	 * @param orgId
	 * @return
	 */
	public Long getGroupNotOverTotal(Long orgId) {

		String querySql = "SELECT count(1) as total from `t_training_group` where DELETE_FLAG = 0 and END_DATE > NOW() and ORG_ID ="
				+ orgId;
		List<IdDomain> teacherList = pageJdbcUtil.getInfo(querySql, totalRm);
		if (null != teacherList && !teacherList.isEmpty()) {
			return teacherList.get(0).getId();
		}
		return null;
	}

	RowMapper<IdDomain> totalRm = new RowMapper<IdDomain>() {
		@Override
		public IdDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			IdDomain domain = new IdDomain();
			domain.setId(rs.getLong("total"));
			return domain;
		}
	};

	public TrainingCountDTO getTrainingStatistics(Long orgId) {
		TrainingCountDTO dto = new TrainingCountDTO();
		dto.setClassTeacherTotal(getClassTeacherTotal(orgId));
		dto.setMentorTotal(getMentorTotal(orgId));
		dto.setStuTotal(getStuTotal(orgId));
		dto.setGroupEndTotal(getGroupEndTotal(orgId));
		dto.setGroupNotOverTotal(getGroupNotOverTotal(orgId));
		return dto;
	}

	/**
	 * 判断学生是否已加入实训小组
	 * 
	 * @param studentId
	 * @return
	 */
	public Map<String, Object> checkStudent(Long studentId) {
		Map<String, Object> result = new HashMap<>();
		List<Long> trainingGroupId = groupRelationService
				.findTrainingGroupIdByStudentId(studentId);
		if (trainingGroupId.size() > 0) {
			result.put("success", false);
			result.put("message", "该用户已加入了其他实训小组！");
		} else {
			result.put("success", true);
			result.put("message", "该用户未加入其他实训小组！");
		}
		return result;
	}

	public List<StudentDTO> checkStudent(Set<Long> studentIds) {
		List<StudentDTO> stuList = groupRelationService
				.findTrainingGroupIdByStudentIds(studentIds);
		return stuList;
	}

	/**
	 * 判断学校老师是否已加入实训小组
	 * 
	 * @param teacherId
	 * @return
	 */
	public Map<String, Object> checkTeacher(Long teacherId) {
		Map<String, Object> result = new HashMap<>();
		List<Long> trainingGroupId = trainingGroupService
				.findTrainingGroupIdByTeacherId(teacherId);
		if (trainingGroupId.size() > 0) {
			result.put("success", false);
			result.put("message", "该学生已加入了其他实训小组！");
		} else {
			result.put("success", true);
			result.put("message", "该学生未加入其他实训小组！");
		}
		return result;
	}

	/**
	 * 实训小组的查询
	 * 
	 * @param pageable
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<TrainingGroupListInfoDTO> queryGroupList(Pageable pageable,
			String name, Long orgId, String status) {
		PageData<TrainingGroupListInfoDTO> reslultData = new PageData<>();
		List<TrainingGroupListInfoDTO> datas = new ArrayList<>();
		Page<TrainingGroupInfoDTO> trainingGroupInfoDTOs = null;
		if (StringUtils.isEmpty(name)) {
			if (GroupStatusConstants.GROUP_STATUS_ALL.equals(status)) {
				trainingGroupInfoDTOs = trainingGroupService
						.getAllGroupInfoAll(pageable, orgId);
			}
			if (GroupStatusConstants.GROUP_STATUS_END.equals(status)) {
				trainingGroupInfoDTOs = trainingGroupService
						.getAllGroupInfoEnd(pageable, orgId);
			}
			if (GroupStatusConstants.GROUP_STATUS_NOT_OVER.equals(status)) {
				trainingGroupInfoDTOs = trainingGroupService.getAllGroupInfo(
						pageable, orgId);
			}
		} else {
			if (GroupStatusConstants.GROUP_STATUS_ALL.equals(status)) {
				trainingGroupInfoDTOs = trainingGroupService
						.getAllGroupInfoByNameAll(pageable, name, orgId);
			}
			if (GroupStatusConstants.GROUP_STATUS_END.equals(status)) {
				trainingGroupInfoDTOs = trainingGroupService
						.getAllGroupInfoByNameEnd(pageable, name, orgId);
			}
			if (GroupStatusConstants.GROUP_STATUS_NOT_OVER.equals(status)) {
				trainingGroupInfoDTOs = trainingGroupService
						.getAllGroupInfoByName(pageable, name, orgId);
			}
		}
		if (null != trainingGroupInfoDTOs) {
			if (trainingGroupInfoDTOs.getContent().size() > 0) {
				for (TrainingGroupInfoDTO data : trainingGroupInfoDTOs
						.getContent()) {
					TrainingGroupListInfoDTO trainingGroupListInfoDTO = new TrainingGroupListInfoDTO();
					trainingGroupListInfoDTO.setTeacherId(data.getTeacherId());
					trainingGroupListInfoDTO.setTeacherName(data
							.getTeacherName());
					trainingGroupListInfoDTO.setCorporateMentorsId(data
							.getCorporateMentorsInfoId());
					trainingGroupListInfoDTO.setCorporateMentorsName(data
							.getCorporateMentorsInfoName());
					trainingGroupListInfoDTO.setStartDate(data.getStartDate());
					trainingGroupListInfoDTO.setEndDate(data.getEndDate());
					if (data.getEndDate().compareTo(new Date()) > 0) {
						trainingGroupListInfoDTO
								.setStatus(GroupStatusConstants.GROUP_STATUS_NOT_OVER);
					} else {
						trainingGroupListInfoDTO
								.setStatus(GroupStatusConstants.GROUP_STATUS_END);
					}
					trainingGroupListInfoDTO.setGroupName(data.getGropName());
					trainingGroupListInfoDTO.setId(data.getId());
					if (null != data.getId()) {
						Long count = groupRelationService.findStudentCount(data
								.getId());
						trainingGroupListInfoDTO.setStudentCount(count);
					}
					datas.add(trainingGroupListInfoDTO);
				}
				reslultData.setData(datas);
			}
			reslultData.getPage().setTotalElements(
					trainingGroupInfoDTOs.getTotalElements());
			reslultData.getPage().setTotalPages(
					trainingGroupInfoDTOs.getTotalPages());
		}
		reslultData.getPage().setPageNumber(pageable.getPageNumber());
		reslultData.getPage().setPageSize(pageable.getPageSize());
		return reslultData;
	}

	/**
	 * 根据orgId实训小组的查询
	 * 
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<TrainingGroupInfoDTO> queryGroupListByOrgId(Long orgId) {

		List<TrainingGroupInfoDTO> list = trainingGroupService
				.findAllByOrgId(orgId);
		for (TrainingGroupInfoDTO dto : list) {
			if (null != dto.getId()) {
				Long count = groupRelationService.findStudentCount(dto.getId());
				dto.setStudentCount(count);
			}
		}
		return list;
	}

	/**
	 * 实训小组的更新
	 * 
	 * @param trainingGropDTO
	 * @return
	 */
	public Map<String, Object> updateGroup(TrainingGropDTO trainingGropDTO) {
		Map<String, Object> result = new HashMap<>();
		try {
			TrainingGroup trainingGroup = trainingGroupService
					.getGroupInfoById(trainingGropDTO.getId());

			boolean isChange = false;
			if (null != trainingGropDTO.getGroupName()
					&& null != trainingGroup.getGropName()
					&& !trainingGropDTO.getGroupName().equals(
							trainingGroup.getGropName())) {
				isChange = true;
			}

			if (null != trainingGropDTO.getGroupName()) {
				trainingGroup.setGropName(trainingGropDTO.getGroupName());
			}
			if (null != trainingGropDTO.getStartDate()) {
				trainingGroup.setStartDate(trainingGropDTO.getStartDate());
			}
			if (null != trainingGropDTO.getEndDate()) {
				trainingGroup.setEndDate(trainingGropDTO.getEndDate());
			}
			if (null != trainingGropDTO.getOrgId()) {
				trainingGroup.setOrgId(trainingGropDTO.getOrgId());
			}
			if (null != trainingGropDTO.getCorporateMentorsId()) {
				CorporateMentorsInfo corporateMentorsInfo = mentorsTrainingService
						.queryCorporateMentorsInfoById(trainingGropDTO
								.getCorporateMentorsId());
				trainingGroup.setCorporateMentorsInfo(corporateMentorsInfo);
			}

			if (null == trainingGroup.getTeacher().getId()) {
				Long teacherId = getTeacherId(trainingGropDTO.getStudentIds()
						.get(0));
				if (null != teacherId && teacherId.longValue() > 0L) {
					User teacher = userService.findById(teacherId);
					trainingGroup.setTeacher(teacher);
					redisDataService.delCounselorGroupInfor(teacherId);
				}
			} else {
				redisDataService.delCounselorGroupInfor(trainingGroup
						.getTeacher().getId());
			}

			trainingGroupService.save(trainingGroup);
			Long id = trainingGroup.getId();
			if (null != id) {

				if (null != trainingGropDTO.getSetDTO()) {
					TrainingGropSetDTO setDTO = trainingGropDTO.getSetDTO();
					if (null == setDTO.getSignWeight()
							&& null == setDTO.getSummaryWeight()
							&& null == setDTO.getReportWeight()
							&& null == setDTO.getTaskWeight()) {
					} else {

						TrainingGroupSet set = trainingGroupSetService
								.findByGroupId(id);
						if (null != set) {
							Long setId = set.getId();
							BeanUtils.copyProperties(setDTO, set);
							set.setId(setId);
							set.setScoreDate(trainingGroup.getEndDate());
						} else {
							set = new TrainingGroupSet();
							BeanUtils.copyProperties(setDTO, set);
							set.setGroupId(id);
							set.setScoreDate(trainingGroup.getEndDate());
						}
						trainingGroupSetService.save(set);
					}
				}

				groupRelationService.dedeleteByGroupId(id);
				for (int i = 0; i < trainingGropDTO.getStudentIds().size(); i++) {
					GroupRelation groupRelation = new GroupRelation();
					groupRelation.setTrainingGroup(trainingGroup);
					groupRelation.setUser(userService.findById(trainingGropDTO
							.getStudentIds().get(i)));
					groupRelationService.creaRelation(groupRelation);
				}

				if (null != trainingGroup.getTeacher()) {
					HashSet<Long> stuIds = new HashSet<Long>();
					stuIds.addAll(trainingGropDTO.getStudentIds());
					updateCallGroup(trainingGroup, stuIds,isChange);
				}
				
				studentPracticeService.queryTrainGroupInfo(trainingGroup.getId());
				
				try{
					MessageDTOV2 messageDTO = new MessageDTOV2();
					messageDTO.setAudience(trainingGropDTO.getStudentIds());
					TrainingGroupInfoDTO msgDto = new TrainingGroupInfoDTO(
							trainingGroup.getId(), trainingGroup.getGropName(), trainingGroup.getStartDate(),
							trainingGroup.getEndDate());
					messageDTO.setData(msgDto);
					messageDTO.setType(20);
					userService.pushMsg(messageDTO);
				}catch (Exception e) {
					log.error("下发实训计划修改消息失败！", e);
				}
			}
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "实训计划更新失败！");
			log.error("实训计划更新失败！", e);
			return result;
		}
		result.put("success", true);
		result.put("message", "实训小组更新成功！");
		return result;
	}

	public void updateCallGroup(TrainingGroup group, HashSet<Long> stuIds,
			boolean isChange) {

		UpdateCallGroupThread thread = new UpdateCallGroupThread(group, stuIds,
				isChange);
		thread.start();
	}

	class UpdateCallGroupThread extends Thread {
		private boolean isChange;
		private TrainingGroup group;
		private HashSet<Long> stuIds;

		public UpdateCallGroupThread(TrainingGroup group, HashSet<Long> stuIds,
				boolean isChange) {
			this.group = group;
			this.stuIds = stuIds;
			this.isChange = isChange;
		}

		public void run() {

			CounRollcallGroupPracticeDTO dto = new CounRollcallGroupPracticeDTO();
			dto.setPracticeId(group.getId());
			dto.setStudentList(stuIds);
			userService.updateCounsellorGroup(dto);

			if (isChange) {
				IdNameDomain domain = new IdNameDomain();
				domain.setId(group.getId());
				domain.setName(group.getGropName());
				studentPracticeService.synGroupName(domain);
			}

			group = null;
			stuIds.clear();
			stuIds = null;
		}
	}

	/**
	 * 根据orgId实训小组的查询
	 * 
	 * @param ids
	 * @return
	 */
	public List<TrainingRelationInfoDTO> queryGroupInfoListByIds(Set<Long> ids) {
		List<TrainingRelationInfoDTO> trinfoList = new ArrayList<>();
		for (Long id : ids) {
			TrainingRelationInfoDTO trinfo = new TrainingRelationInfoDTO();
			List<GroupRelation> grList = groupRelationService
					.findTrainingGroupIdByGid(id);
			if (null != grList && grList.size() > 0) {
				TrainingGroup tg = grList.get(0).getTrainingGroup();
				if (null != tg) {
					trinfo.setId(tg.getId());
					trinfo.setName(tg.getGropName());
					if (null != tg.getTeacher()) {
						trinfo.setTeacherId(tg.getTeacher().getId());
						trinfo.setTeacherName(tg.getTeacher().getName());
					}
					if (null != tg.getCorporateMentorsInfo()) {
						trinfo.setCorporateMentorsId(tg
								.getCorporateMentorsInfo().getId());
						trinfo.setCorporateMentorsName(tg
								.getCorporateMentorsInfo().getName());
						trinfo.setEnterpriseId(tg.getCorporateMentorsInfo()
								.getEnterpriseId());
						trinfo.setEnterpriseName(tg.getCorporateMentorsInfo()
								.getEnterpriseName());
						trinfo.setAccountId(tg.getCorporateMentorsInfo()
								.getAccountId());

						trinfo.setCorporateMentorsPhone(tg
								.getCorporateMentorsInfo().getPhone());
					}
					trinfo.setOrgId(tg.getOrgId());
					List<CorporateMentorsInfoByStudentDTO> students = new ArrayList<>();
					for (GroupRelation gr : grList) {
						User user = gr.getUser();
						if (null != user) {
							CorporateMentorsInfoByStudentDTO student = new CorporateMentorsInfoByStudentDTO();
							student.setSid(user.getId());
							student.setOrgId(user.getOrgId());
							student.setSname(user.getName());
							student.setSex(user.getSex());
							student.setSjobNumber(user.getJobNumber());
							student.setSphone(user.getPhone());
							if (null != user.getClasses()) {
								student.setClassesName(user.getClasses()
										.getName());
								student.setClassesId(user.getClasses().getId());
							}
							if (null != user.getCollege()) {
								student.setCollegeName(user.getCollege()
										.getName());
								student.setCollegeId(user.getCollege().getId());
							}
							if (null != user.getProfessional()) {
								student.setProfessionalName(user
										.getProfessional().getName());
								student.setProfessionalId(user
										.getProfessional().getId());
							}
							students.add(student);
						}
					}
					trinfo.setStudents(students);
				}
			}
			trinfoList.add(trinfo);
		}
		return trinfoList;
	}

	/**
	 * 根据orgId实训小组的查询
	 * 
	 * @param orgId
	 * @return
	 */
	public void deleteGroup(Long orgId) {
		trainingGroupService.deleteGroup(orgId);
	}

	public PageData<TrainingGroupInfoDTO> getGroupListByTeacherId(
			Pageable pageable, Long teacherId, String flag) {

		PageData<TrainingGroupInfoDTO> page = new PageData<TrainingGroupInfoDTO>();
		Page<TrainingGroupInfoDTO> groupList = null;
		if (GroupStatusConstants.GROUP_STATUS_NOT_OVER.equals(flag)) {
			groupList = trainingGroupService.findGroupInforByTeacherId(
					pageable, teacherId);
		} else {
			groupList = trainingGroupService.findEndGroupInforByTeacherId(
					pageable, teacherId);
		}
		if (null != groupList.getContent() && !groupList.getContent().isEmpty()) {
			Set<Long> ids = new HashSet<Long>();
			for (TrainingGroupInfoDTO group : groupList.getContent()) {
				ids.add(group.getId());
			}
			List<TrainingGroupInfoDTO> countList = groupRelationService
					.findStudentCount(ids);
			if (null != countList && !countList.isEmpty()) {
				HashMap<Long, Long> countMap = new HashMap<Long, Long>();
				//实践计划学生数量查询
				for (TrainingGroupInfoDTO countDto : countList) {
					countMap.put(countDto.getId(), countDto.getStudentCount());
				}
				//实践计划日志周志是否打分查询
//				List<TrainingGroupSet> setList = trainingGroupSetService.findAllByGroupIds(ids);
//				HashMap<Long, Boolean> isGradeMap = new HashMap<Long, Boolean>();
//				if (null != setList && !setList.isEmpty()) {
//					for(TrainingGroupSet set : setList){
//						isGradeMap.put(set.getGroupId(), set.getSummaryIsGrade());
//					}
//				}
				for (TrainingGroupInfoDTO group : groupList.getContent()) {
					if (null != countMap.get(group.getId())) {
						group.setStudentCount(countMap.get(group.getId()));
					}
//					if(null != isGradeMap.get(group.getId())) {
//						group.setSummaryIsGrade(isGradeMap.get(group.getId()));
//					}
				}
			}
		}
		PageDomain pageDomain = new PageDomain();
		pageDomain.setPageNumber(groupList.getNumber());
		pageDomain.setPageSize(groupList.getSize());
		pageDomain.setTotalElements(groupList.getTotalElements());
		pageDomain.setTotalPages(groupList.getTotalPages());
		page.setPage(pageDomain);
		page.setData(groupList.getContent());
		return page;
	}

	public TrainingGroupInfoDTO stuGetGroupInfor(Long id){
		TrainingGroupInfoDTO dto = trainingGroupService.findGroupInforById(id);
		if(null != dto){
			Long stuCount = groupRelationService
					.findStudentCount(id);
			dto.setStudentCount(stuCount);
		}
		return dto;
	}
}
