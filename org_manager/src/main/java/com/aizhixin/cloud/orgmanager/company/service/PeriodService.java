/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.domain.PeriodDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Period;
import com.aizhixin.cloud.orgmanager.company.repository.PeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 学周相关操作业务逻辑处理
 * 
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class PeriodService {

	@Value("${period.mycj.orgId}")
	private Long myOrgId;
	@Value("${period.mycj.week}")
	private Integer week;
	@Autowired
	private PeriodRepository periodRepository;

	/**
	 * 保存实体
	 * 
	 * @param period
	 * @return
	 */
	public Period save(Period period) {
		return periodRepository.save(period);
	}


	/**
	 * 根据ID查询实体
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Period findById(Long id) {
		return periodRepository.findOne(id);
	}

	/**
	 * 机构id和课程节序号统计已存在个数
	 * @param no
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByNoAndOrgId(Integer no,Long orgId) {
		return periodRepository.countByNoAndOrgIdAndDeleteFlag(no, orgId, DataValidity.VALID.getState());
	}

	/**
	 * 机构id和课程节序号和课程节主键统计已存在个数
	 * @param no
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByNoAndOrgIdAndIdNot(Integer no,Long orgId,Long id) {
		return periodRepository.countByNoAndOrgIdAndIdNotAndDeleteFlag(no, orgId, id, DataValidity.VALID.getState());
	}
	
	/**
	 * 根据学校查询课程节列表
	 * 
	 * @param pageable
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<PeriodDomain> findByPageAndNo(Pageable pageable,Long orgId,Integer no) {
		return periodRepository.findByPageAndNo(pageable, orgId,
				DataValidity.VALID.getState(),no);
	}
	
	@Transactional(readOnly = true)
	public Page<PeriodDomain> findByPage(Pageable pageable,Long orgId) {
		return periodRepository.findByPage(pageable, orgId,
				DataValidity.VALID.getState());
	}
	
	/**
	 * 根据学校查询课程节id和no
	 * 
	 * @param pageable
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<PeriodDomain> findByMap(Pageable pageable,Long orgId) {
		return periodRepository.findByMap(pageable, orgId,
				DataValidity.VALID.getState());
	}
	
	/**
	 * 查询已存在的课程节开始时间是否有在新学期的区间内
	 * @param orgId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countBetweenStartDate(Long orgId,String startTime,String endTime){
		return periodRepository.countByOrgIdAndDeleteFlagAndStartTimeBetween(orgId, DataValidity.VALID.getState(), startTime, endTime);
	}
	
	/**
	 * 查询已存在的课程节开始时间是否有在新学期的区间内
	 * @param orgId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countBetweenEndDate(Long orgId,String startTime,String endTime){
		return periodRepository.countByOrgIdAndDeleteFlagAndEndTimeBetween(orgId, DataValidity.VALID.getState(), startTime, endTime);
	}
	
	/**
	 * 查询已存在的课程节开始时间是否有在新学期的区间内(修改，不包括当前课程节)
	 * @param id
	 * @param orgId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countBetweenStartDateNotId(Long id,Long orgId,String startTime,String endTime){
		return periodRepository.countByOrgIdAndIdNotAndDeleteFlagAndStartTimeBetween(orgId, id, DataValidity.VALID.getState(), startTime, endTime);
	}
	
	/**
	 * 查询已存在的课程节开始时间是否有在新学期的区间内(修改，不包括当前课程节)
	 * @param id
	 * @param orgId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countBetweenEndDateNotId(Long id,Long orgId,String startTime,String endTime){
		return periodRepository.countByOrgIdAndIdNotAndDeleteFlagAndEndTimeBetween(orgId, id, DataValidity.VALID.getState(), startTime, endTime);
	}

	/**
	 * 查询已存在的课程节截至时间大于新建的课程节开始时间
	 * @param orgId
	 * @param no
	 * @param endTime
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countAfterEndTime(Long orgId,Integer no,String endTime){
		return periodRepository.countByOrgIdAndDeleteFlagAndNoLessThanAndEndTimeAfter(orgId, DataValidity.VALID.getState(), no, endTime);
	}
	@Transactional(readOnly = true)
	public List<Period> findByIds(Set<Long> ids) {
		return periodRepository.findByIdIn(ids);
	}
	@Transactional(readOnly = true)
	public List<Period> findByOrgId(Long orgId) {
		return periodRepository.findByOrgIdAndDeleteFlag(orgId, DataValidity.VALID.getState());
	}

	public List<Period> save(List<Period> periods) {
		return  periodRepository.save(periods);
	}

	@Transactional(readOnly = true)
	public List<Period> findByOrgAndNo(Long orgId, Integer no) {
		return periodRepository.findByOrgIdAndNoAndDeleteFlag(orgId, no, DataValidity.VALID.getState());
	}

	@Transactional
	public List<Period> findByOrgAndPeriodNo(Long orgId, Integer no) {
		return periodRepository.findByOrgIdAndNoAndDeleteFlag(orgId, no, DataValidity.VALID.getState());
	}

	// *************************************************************以下部分处理页面调用逻辑**********************************************************************//
	/**
	 * 保存课程节
	 *
	 * @param pd
	 * @return
	 */
	public Period save(PeriodDomain pd) {
		
		Long c = countByNoAndOrgId(pd.getNo(), pd.getOrgId());
		if (null !=c && c.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION,
					"该【第几节】值已经存在");
		}
		
		if(pd.getStartTime().compareTo(pd.getEndTime()) > 0){
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION,
					"该【起始时间】大于【结束时间】");
		}
		
		Long startCount = countBetweenStartDate(pd.getOrgId(), pd.getStartTime(), pd.getEndTime());
		Long endCount = countBetweenEndDate(pd.getOrgId(), pd.getStartTime(), pd.getEndTime());
		if (null !=startCount && startCount.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该【课节时间区间】与其他课节起始时间冲突");
		}
		if (null !=endCount && endCount.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该【课节时间区间】与其他课节截至时间冲突");
		}
		
		Long countAfterEndTime = countAfterEndTime(pd.getOrgId(), pd.getNo(), pd.getStartTime());
		if (null !=countAfterEndTime && countAfterEndTime.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该【课节起始时间】应大于之前课节的截至时间");
		}
		
		Period period = new Period();
		period.setOrgId(pd.getOrgId());
		period.setStartTime(pd.getStartTime());
		period.setEndTime(pd.getEndTime());
		period.setNo(pd.getNo());
		period.setCreatedBy(pd.getUserId());
		period.setLastModifiedBy(pd.getUserId());
		return save(period);
	}

	/**
	 * 修改课程节信息
	 * 
	 * @param pd
	 * @return
	 */
	public Period update(PeriodDomain pd) {
		if (null == pd.getId() || pd.getId() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		
		if(pd.getStartTime().compareTo(pd.getEndTime()) > 0){
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION,
					"该【起始时间】大于【结束时间】");
		}
		
		Period period = findById(pd.getId());
		if (null == period) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
					"对应ID的对象不存在");
		}
		
		// 验证name
		Long c = countByNoAndOrgIdAndIdNot(pd.getNo(), pd.getOrgId(), pd.getId());
		if (null !=c && c.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION,
					"该【第几节】值已经存在");
		}
		
		Long startCount = countBetweenStartDateNotId(period.getId(),pd.getOrgId(), pd.getStartTime(), pd.getEndTime());
		Long endCount = countBetweenEndDateNotId(period.getId(),pd.getOrgId(), pd.getStartTime(), pd.getEndTime());
		if (null !=startCount && startCount.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该【课程节时间区间】与其他课程节起始时间冲突");
		}
		if (null !=endCount && endCount.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该【课程节时间区间】与其他课程节截至时间冲突");
		}
		
		Long countAfterEndTime = countAfterEndTime(pd.getOrgId(), pd.getNo(), pd.getEndTime());
		if (null !=countAfterEndTime && countAfterEndTime.longValue()> 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该【课节起始时间】应小于之前课节的截至时间");
		}
		
		period.setOrgId(pd.getOrgId());
		period.setStartTime(pd.getStartTime());
		period.setEndTime(pd.getEndTime());
		period.setNo(pd.getNo());
		period.setLastModifiedBy(pd.getUserId());
		period.setLastModifiedDate(new Date());
		return save(period);
	}

	/**
	 * 删除第几节信息
	 * 
	 * @param userId
	 * @param id
	 */
	public void delete(Long userId, Long id) {
		if (null == id || id <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Period period = findById(id);
		if (null == period) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
					"对应ID的对象不存在");
		}
		period.setDeleteFlag(DataValidity.INVALID.getState());
		period.setLastModifiedBy(userId);
		period.setLastModifiedDate(new Date());
		save(period);
	}
	
	/**
	 * 按id查询课程节
	 * @param id
	 * @return
	 */
	public Period getOne(Long id) {
		if (null == id || id <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Period period = findById(id);
		if (null == period) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
					"对应ID的对象不存在");
		}
		return period;
	}

	/**
	 * 按照查询条件分页查询指定学校的学周信息列表
	 * 
	 * @param r
	 * @param pageable
	 * @param orgId
	 * @param no
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> queryList(Map<String, Object> r,
			Pageable pageable,Long orgId,Integer no) {
		
		Page<PeriodDomain> page = null;
		PageDomain p = new PageDomain();
		p.setPageNumber(pageable.getPageNumber());
		p.setPageSize(pageable.getPageSize());
		r.put(ApiReturnConstants.PAGE, p);
		
		if(null != no && no.intValue() > 0){
			page = findByPageAndNo(pageable, orgId,no);
		}else{
			page = findByPage(pageable, orgId);
		}
		//绵阳财经周五下午课程延迟一小时定制
		if (null != myOrgId && null != week && null != orgId && null != page && orgId.longValue() == myOrgId) {
			if(DateUtil.isFriday(week)) {
				List<PeriodDomain> list = page.getContent();
				for (PeriodDomain d : list) {
					String temp = AddOneHourAfter12(d.getStartTime());
					d.setStartTime(temp);
					temp = AddOneHourAfter12(d.getEndTime());
					d.setEndTime(temp);
				}
			}
		}
		
		p.setTotalElements(page.getTotalElements());
		p.setTotalPages(page.getTotalPages());
		r.put(ApiReturnConstants.DATA, page.getContent());
		return r;
	}

	private String AddOneHourAfter12(String temp) {
		int hour = -1;
		if (null != temp) {
			int pt = temp.indexOf(":");
			if (pt > 0) {
				hour = new Integer(temp.substring(0, pt));
				if (hour > 12) {
					hour--;
					if (hour >= 12) {
						temp = hour + temp.substring(pt);
					} else {
						temp = "0" + hour + temp.substring(pt);
					}
				}
			}
		}
		return temp;
	}

	/**
	 * 分页查询指定学校学期的学周id、name字段 主要用于下拉列表
	 * 
	 * @param r
	 * @param pageable
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> dropList(Map<String, Object> r,
			Pageable pageable, Long orgId) {
		
		PageDomain p = new PageDomain();
		r.put(ApiReturnConstants.PAGE, p);
		p.setPageNumber(pageable.getPageNumber());
		p.setPageSize(pageable.getPageSize());
		Page<PeriodDomain> page = findByMap(pageable, orgId);
		p.setTotalElements(page.getTotalElements());
		p.setTotalPages(page.getTotalPages());
		r.put(ApiReturnConstants.DATA, page.getContent());
		return r;
	}

	@Transactional(readOnly = true)
	public Map<Long, Period> getByIds(Set<Long> ids) {
		Map<Long, Period>  r = new HashMap<>();
		List<Period> list = findByIds(ids);
		for (Period p : list) {
			r.put(p.getId(), p);
		}
		return r;
	}


	private void fillOutData(List<PeriodDomain> outData, List<Period> list) {
		if (list.size() > 0) {
			for (Period period : list) {
				PeriodDomain d = new PeriodDomain(period.getId(), period.getStartTime(), period.getEndTime(), period.getNo(), null, period.getOrgId());
				outData.add(d);
			}
		}
	}

//	public List<PeriodDomain> saveAll(List<PeriodDomain> ds) {
//		List<Period> list = new ArrayList<>();
//		for(PeriodDomain d : ds) {
//			Period period = new Period();
//			period.setStartTime(d.getStartTime());
//			period.setEndTime(d.getEndTime());
//			period.setNo(d.getNo());
//			period.setCreatedBy(d.getUserId());
//			period.setLastModifiedBy(d.getUserId());
//			period.setOrgId(d.getOrgId());
//
//			list.add(period);
//		}
//		List<PeriodDomain> outData = new ArrayList<>();
//		if (list.size() > 0) {
//			list = save(list);
//			fillOutData(outData, list);
//		}
//		return outData;
//	}
//
//	public List<PeriodDomain> updateAll(List<PeriodDomain> ds) {
//		Set<Long> ids = new HashSet<>();
//		Map<Long, PeriodDomain> wds = new HashMap<>();
//		for (PeriodDomain d : ds) {
//			ids.add(d.getId());
//			wds.put(d.getId(), d);
//		}
//
//		List<Period> list = findByIds(ids);
//		for(Period period : list) {
//			PeriodDomain d = wds.get(period.getId());
//			if (null != d) {
//				period.setStartTime(d.getStartTime());
//				period.setEndTime(d.getEndTime());
//				period.setNo(d.getNo());
//				period.setCreatedBy(d.getUserId());
//				period.setLastModifiedBy(d.getUserId());
//			}
//		}
//		List<PeriodDomain> outData = new ArrayList<>();
//		if (list.size() > 0) {
//			list = save(list);
//			fillOutData(outData, list);
//		}
//		return outData;
//	}
//
//	public List<PeriodDomain> deleteAll(List<PeriodDomain> ds) {
//		List<PeriodDomain> outData = new ArrayList<>();
//		Set<Long> ids = new HashSet<>();
//		Long userId = 0L;
//		for (PeriodDomain d : ds) {
//			ids.add(d.getId());
//			if(userId <= 0) {
//				userId = d.getUserId();
//			}
//		}
//		List<Period> list = findByIds(ids);
//		for(Period period : list) {
//			period.setDeleteFlag(DataValidity.INVALID.getState());
//			period.setLastModifiedBy(userId);
//		}
//		list = save(list);
//		fillOutData(outData, list);
//		return outData;
//	}
}
