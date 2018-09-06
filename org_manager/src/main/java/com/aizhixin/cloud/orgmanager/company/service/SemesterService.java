/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.service;


import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassService;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.domain.SemesterDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.Week;
import com.aizhixin.cloud.orgmanager.company.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 学期相关操作业务逻辑处理
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class SemesterService {
	@Autowired
	private SemesterRepository semesterRepository;
//	@Autowired
//	private OrganizationService organizationService;
	@Autowired
	private WeekService weekService;
	@Autowired
	private TeachingClassService teachingClassService;
	/**
	 * 保存实体
	 * @param semester
	 * @return
	 */
	public Semester save(Semester semester) {
		return semesterRepository.save(semester);
	}
	public List<Semester> save(List<Semester> semesterList) {
		return semesterRepository.save(semesterList);
	}
	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Semester findById(Long id) {
		return semesterRepository.findOne(id);
	}
	/**
	 * count指定学校的所有name的值的正常数据
	 * @param orgId
	 * @param name
	 * @returnf
	 */
	@Transactional(readOnly = true)
	public Long countByName(Long orgId, String name) {
		return semesterRepository.countByOrgIdAndNameAndDeleteFlag(orgId, name, DataValidity.VALID.getState());
	}
	@Transactional(readOnly = true)
	public Long countByCode(Long orgId, String code) {
		return semesterRepository.countByOrgIdAndCodeAndDeleteFlag(orgId, code, DataValidity.VALID.getState());
	}
	@Transactional(readOnly = true)
	public Long countByCodeAndIdNot(Long orgId, String code,Long id) {
		return semesterRepository.countByOrgIdAndCodeAndIdNotAndDeleteFlag(orgId, code, id, DataValidity.VALID.getState());
	}
	
//	@Transactional(readOnly = true)
//	public Long countByYear(Year year) {
//		return semesterRepository.countByYearAndDeleteFlag(year, DataValidity.VALID.getState());
//	}
	/**
	 * count指定学校的所有name的值的正常数据，但是排除特定id（修改时用来排除自己）
	 * @param name
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByNameAndIdNot(Long orgId, String name, Long id) {
		return semesterRepository.countByOrgIdAndNameAndIdNotAndDeleteFlag(orgId, name, id, DataValidity.VALID.getState());
	}
	/**
	 * 分页查询指定学校的学期的ID和name
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<IdNameDomain> findIdName(Pageable pageable, Long orgId) {
		return semesterRepository.findIdName(pageable, orgId, DataValidity.VALID.getState());
	}
	/**
	 * 根据学校和name查询学期列表
	 * @param pageable
	 * @param orgId
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<SemesterDomain> findName(Pageable pageable, Long orgId, String name) {
		return semesterRepository.findByOrgIdAndName(pageable, orgId, DataValidity.VALID.getState(), name);
	}
	/**
	 * 根据指定学校的查询学期列表
	 * @param pageable
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<SemesterDomain> find(Pageable pageable, Long orgId) {
		return semesterRepository.findByOrgId(pageable, orgId, DataValidity.VALID.getState());
	}
	
//	/**
//	 * 按学年查询学期列表
//	 * @param year
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public List<SemesterDomain> findByYear(Year year) {
//		return semesterRepository.findByYearAndDeleteFlag(year, DataValidity.VALID.getState());
//	}
	
//	/**
//	 * 按学年查询学期id和name列表
//	 * @param year
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public List<IdNameDomain> findIdNameByYear(Year year) {
//		return semesterRepository.findIdNameByYearAndDeleteFlag(year, DataValidity.VALID.getState());
//	}

	/**
	 * 查询已存在的学期开始时间是否有在新学期的区间内
	 * @param orgId
	 * @param startDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countBetweenStartDate(Long orgId,Date startDate){
		return semesterRepository.countByOrgIdAndDeleteFlagAndStartDateLessThanEqualAndEndDateGreaterThanEqual(orgId, DataValidity.VALID.getState(), startDate, startDate);
	}
	
	/**
	 * 查询已存在的学期开始时间是否有在新学期的区间内
	 * @param orgId
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countBetweenEndDate(Long orgId,Date endDate){
		return semesterRepository.countByOrgIdAndDeleteFlagAndStartDateLessThanEqualAndEndDateGreaterThanEqual(orgId, DataValidity.VALID.getState(), endDate, endDate);
	}
	
	/**
	 * 查询已存在的学期开始时间是否有在新学期的区间内(修改，不包括当前学期)
	 * @param id
	 * @param orgId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countBetweenStartDateNotId(Long id,Long orgId,Date startDate,Date endDate){
		return semesterRepository.countByOrgIdAndIdNotAndDeleteFlagAndStartDateBetween(orgId, id, DataValidity.VALID.getState(), startDate, endDate);
	}
	
	/**
	 * 查询已存在的学期开始时间是否有在新学期的区间内(修改，不包括当前学期)
	 * @param id
	 * @param orgId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countBetweenEndDateNotId(Long id,Long orgId,Date startDate,Date endDate){
		return semesterRepository.countByOrgIdAndIdNotAndDeleteFlagAndEndDateBetween(orgId, id, DataValidity.VALID.getState(), startDate, endDate);
	}
	
	/**
	 * 获取指定日期是所属机构下那个学期
	 * @param orgId
	 * @param date
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SemesterDomain> getByOrgIdAndDate(Long orgId, Date date){
		return semesterRepository.findByOrgIdAndDate(orgId, DataValidity.VALID.getState(), date, date);
	}
	@Transactional(readOnly = true)
	public List<SemesterDomain> getByOrgIdsAndDate(List<Long> orgIds, Date date){
		return semesterRepository.findByOrgIdsAndDate(orgIds, DataValidity.VALID.getState(), date, date);
	}

	@Transactional(readOnly = true)
	public List<Semester> findByOrgIdAndDate(Long orgId, Date date){
		return semesterRepository.findByOrgIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeleteFlag(orgId, date, date, DataValidity.VALID.getState());
	}
//	public List<Semester> saveAll(List<Semester> semesters) {
//		return semesterRepository.save(semesters);
//	}
	@Transactional(readOnly = true)
	public List<Semester> findByIds(Set<Long> ids) {
		return semesterRepository.findByIdIn(ids);
	}
	@Transactional(readOnly = true)
	public List<Semester> findByCodes(Set<String> codes) {
		return semesterRepository.findByCodeIn(codes);
	}
	@Transactional(readOnly = true)
	public Semester findByOrgIdAndCode(Long orgId, String code) {
		List<Semester> list =  semesterRepository.findByOrgIdAndCodeAndDeleteFlag(orgId, code, DataValidity.VALID.getState());
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	@Transactional(readOnly = true)
	public List<Semester> findByOrgIdAndCodeIn(Long orgId, Set<String> codes) {
		return semesterRepository.findByOrgIdAndCodeInAndDeleteFlag(orgId, codes, DataValidity.VALID.getState());
	}
	//*************************************************************以下部分处理页面调用逻辑**********************************************************************//	
	/**
	 * 保存学期
	 * @param sd
	 * @return
	 */
	public Semester save(SemesterDomain sd) {
		
		Semester semester = new Semester();
		Long c = countByName(sd.getOrgId(), sd.getName());
		if (null !=c && c.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[学期名称]值已经存在");
		}
		if (!StringUtils.isEmpty(sd.getCode())) {
			c = countByCode(sd.getOrgId(), sd.getCode());
			if (c.longValue() > 0) {
				throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学期编码[" +  sd.getCode() + "]已经存在");
			}
			semester.setCode(sd.getCode());
		}
		Long startCount = countBetweenStartDate(sd.getOrgId(), sd.getStartDate());
		Long endCount = countBetweenEndDate(sd.getOrgId(), sd.getEndDate());
		if (null !=startCount && startCount.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[学期时间区间]与其他学期起始时间冲突");
		}
		if (null !=endCount && endCount.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[学期时间区间]与其他学期截至时间冲突");
		}
		
		semester.setOrgId(sd.getOrgId());
		semester.setName(sd.getName());
		semester.setStartDate(sd.getStartDate());
		semester.setEndDate(sd.getEndDate());
		semester.setCreatedBy(sd.getUserId());
		semester.setLastModifiedBy(sd.getUserId());
		return save(semester);
	}
	/**
	 * 修改学期信息
	 * @param sd
	 * @return
	 */
	public Semester update(SemesterDomain sd) {
		if(null == sd.getId() || sd.getId() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
//		boolean changeFlag = false;
		Semester semester = findById(sd.getId());
		if(null == semester) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "对应ID的对象不存在");
		}
		semester.setOrgId(sd.getOrgId());
		//验证name
		Long c = countByNameAndIdNot(sd.getOrgId(), sd.getName(), sd.getId());
		if (null !=c && c.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[学期名称]值已经存在");
		}
		if (!StringUtils.isEmpty(sd.getCode())) {
			c = countByCodeAndIdNot(sd.getOrgId(), sd.getCode(), sd.getId());
			if (c.longValue() > 0) {
				throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学期编码[" +  sd.getCode() + "]已经存在");
			}
			semester.setCode(sd.getCode());
		}
		
		Long startCount = countBetweenStartDateNotId(semester.getId(),sd.getOrgId(), sd.getStartDate(), sd.getEndDate());
		Long endCount = countBetweenEndDateNotId(semester.getId(),sd.getOrgId(), sd.getStartDate(), sd.getEndDate());
		if (null !=startCount && startCount.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[学期时间区间]与其他学期起始时间冲突");
		}
		if (null !=endCount && endCount.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[学期时间区间]与其他学期截至时间冲突");
		}
		
		if(semester.getStartDate().compareTo(sd.getStartDate()) != 0){
			Week minWeek = weekService.findMinWeekBySemester(semester);
			if (null != minWeek) {
				if (sd.getStartDate().after(minWeek.getStartDate())) {
					throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[学期开始日期]晚于学周的开始日期");
				}
			}
		}
		if(semester.getEndDate().compareTo(sd.getEndDate()) != 0){
			Week maxWeek = weekService.findMaxWeekBySemester(semester);
			if (null != maxWeek) {
				if (sd.getEndDate().before(maxWeek.getEndDate())) {
					throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[学期结束日期]早于学周的结束日期");
				}
			}
		}
//		if(changeFlag){
//			weekService.deleteAllSemesterWeek(semester);
//			semester.setNumWeek(null);
//		}
		semester.setName(sd.getName());
		semester.setStartDate(sd.getStartDate());
		semester.setEndDate(sd.getEndDate());
		semester.setLastModifiedBy(sd.getUserId());
		semester.setLastModifiedDate(new Date());
		return save(semester);
	}
	/**
	 * 删除学期信息
	 * @param userId
	 * @param id
	 */
	public Semester delete(Long userId, Long id) {
		if(null == id || id <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Semester semester = findById(id);
		if(null == semester) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "对应ID的对象不存在");
		}
		Long result = weekService.countBySemester(semester);
		if(null != result && result.longValue() > 0L){
			throw new CommonException(ErrorCode.DELETE_CONFLICT, "该学期存在学周不能删除");
		}
		long useCount = teachingClassService.countBySemester(semester);
		if (useCount > 0) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "对应学期数据已经被使用，不能删除");
		}

		semester.setDeleteFlag(DataValidity.INVALID.getState());
		semester.setLastModifiedBy(userId);
		semester.setLastModifiedDate(new Date());
		semester = save(semester);
		return semester;
	}
	/**
	 * 按照查询条件分页查询指定学校的学期信息列表
	 * @param pageable
	 * @param orgId
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<SemesterDomain> queryList(Pageable pageable, Long orgId, String name) {
		PageData<SemesterDomain> p = new PageData<>();
		p.getPage().setPageNumber(pageable.getPageNumber() + 1);
		p.getPage().setPageSize(pageable.getPageSize());
		Page<SemesterDomain> page;
		if(!StringUtils.isEmpty(name)) {
			page = findName(pageable, orgId, name);
		} else {
			page = find(pageable, orgId);
		}
		p.setData(page.getContent());
		p.getPage().setTotalElements(page.getTotalElements());
		p.getPage().setTotalPages(page.getTotalPages());
		return p;
	}
	/**
	 * 分页查询指定学校的学期的id、name字段
	 * 主要用于下拉列表
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<IdNameDomain> dropList(Pageable pageable, Long orgId) {
		PageData<IdNameDomain> p = new PageData<>();
		p.getPage().setPageNumber(pageable.getPageNumber());
		p.getPage().setPageSize(pageable.getPageSize());
		
		Page<IdNameDomain> page = findIdName(pageable, orgId);
		p.setData(page.getContent());
		p.getPage().setTotalElements(page.getTotalElements());
		p.getPage().setTotalPages(page.getTotalPages());
		return p;
	}

	public SemesterDomain get(Long id) {
		if(null == id || id <= 0) {
			return new SemesterDomain();
		}
		Semester semester = findById(id);
		if(null == semester) {
			return new SemesterDomain();
		}
		SemesterDomain c = new SemesterDomain(semester.getId(), semester.getName(), semester.getStartDate(), semester.getEndDate(), semester.getNumWeek(), semester.getCreatedDate());
		c.setCode(semester.getCode());
		return c;
	}
	
	/**
	 * 获取指定日期是所属机构下那个学期
	 * @param r
	 * @param orgId
	 * @param date
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> getSemesterByOrgIdAndDate(Map<String, Object> r,Long orgId,Date date) {
		
		if(null == date){
			date = new Date();
		}
		List<SemesterDomain> ls = getByOrgIdAndDate(orgId, date);
		if(null != ls && ls.size() > 0){
			r.put(ApiReturnConstants.DATA, ls.get(0));
		}else{
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该【查询条件】没有对应的学期数据");
		}
		
		return r;
	}

	/**
	 * 获取指定日期是所属机构下那个学期
	 * @param orgId
	 * @param date
	 * @return
	 */
	@Transactional(readOnly = true)
	public SemesterDomain getSemesterByOrgIdAndDateForOut(Long orgId,Date date) {
		if(null == date){
			date = new Date();
		}
		List<SemesterDomain> ls = getByOrgIdAndDate(orgId, date);
		if(null != ls && ls.size() > 0){
			return ls.get(0);
		}

		return null;
	}

	/**
	 * 获取指定日期是所属机构下那个学期
	 * @param orgIds
	 * @param date
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SemesterDomain> getSemesterListByOrgIdsAndDate(List<Long> orgIds,Date date) {
		if(null == date){
			date = new Date();
		}
		return   getByOrgIdsAndDate(orgIds, date);
	}


	public Semester getSemesterByDate(Long orgId, Date date) {
		if (null != orgId && orgId > 0 && null != date) {
			List<Semester> semesters = findByOrgIdAndDate(orgId, date);
			if (semesters.size() > 0) {
				return semesters.get(0);
			}
		}
		return null;
	}

	public Semester getSemesterByOrgIdAndIdAndDate(Long orgId, Long id, Date date) {
		Semester semester = null;
		if (null != id && id > 0) {
			semester = findById(id);
		} else {
			if (null == date) {
				date = new Date();
				date = DateUtil.getZerotime(date);
			}
			semester = getSemesterByDate(orgId, date);
		}
		return semester;
	}

	private void fillOutData(List<SemesterDomain> outData, List<Semester> list) {
		if (list.size() > 0) {
			for (Semester semester : list) {
				SemesterDomain d = new SemesterDomain(semester.getId(), semester.getName(), semester.getStartDate(), semester.getEndDate(), 0, null);
				d.setCode(semester.getCode());
				outData.add(d);
			}
		}
	}

//	/**
//	 * 批量数据导入
//	 * @param sds
//	 * @return
//	 */
//	public List<SemesterDomain> save(List<SemesterDomain> sds) {
//		List<Semester> list = new ArrayList<>();
//		List<SemesterDomain> outData = new ArrayList<>();
//		for(SemesterDomain sd : sds) {
//			Semester semester = new Semester();
//			semester.setOrgId(sd.getOrgId());
//			semester.setName(sd.getName());
//			semester.setStartDate(sd.getStartDate());
//			semester.setEndDate(sd.getEndDate());
//			semester.setCode(sd.getCode());
//			semester.setCreatedBy(sd.getUserId());
//			semester.setLastModifiedBy(sd.getUserId());
//			list.add(semester);
//		}
//		list = saveAll(list);
//		fillOutData(outData, list);
//		return outData;
//	}
//
//	public List<SemesterDomain> update(List<SemesterDomain> sds) {
//		List<SemesterDomain> outData = new ArrayList<>();
//		Set<Long> ids = new HashSet<>();
//		Map<Long, SemesterDomain> ccs = new HashMap<>();
//		for(SemesterDomain sd : sds) {
//			ids.add(sd.getId());
//			ccs.put(sd.getId(), sd);
//		}
//		List<Semester> list = findByIds(ids);
//		if (list.size() > 0) {
//			for (Semester semester : list) {
//				SemesterDomain d = ccs.get(semester.getId());
//				if (null != d) {
//					semester.setCode(d.getCode());
//					semester.setName(d.getName());
//					semester.setStartDate(d.getStartDate());
//					semester.setEndDate(d.getEndDate());
//				}
//			}
//			list = saveAll(list);
//			fillOutData(outData, list);
//		}
//		return outData;
//	}
//
//	public List<SemesterDomain> delete(List<SemesterDomain> sds) {
//		Set<Long> ids = new HashSet<>();
//		List<SemesterDomain> outData = new ArrayList<>();
//		Long userId = 0L;
//		for(SemesterDomain sd : sds) {
//			ids.add(sd.getId());
//			if(userId <= 0) {
//				userId = sd.getUserId();
//			}
//		}
//		List<Semester> list = findByIds(ids);
//		if (list.size() > 0) {
//			for (Semester semester : list) {
//				semester.setDeleteFlag(DataValidity.INVALID.getState());
//				semester.setLastModifiedBy(userId);
//			}
//			list = saveAll(list);
//			fillOutData(outData, list);
//		}
//		return outData;
//	}
}
