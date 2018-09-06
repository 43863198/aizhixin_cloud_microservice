/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.domain.WeekDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Period;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.Week;
import com.aizhixin.cloud.orgmanager.company.repository.WeekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 学周相关操作业务逻辑处理
 * 
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class WeekService {
	@Autowired
	private WeekRepository weekRepository;
	@Autowired
	private SemesterService semesterService;
	@Autowired
	private PeriodService periodService;

	/**
	 * 保存实体
	 * 
	 * @param week
	 * @return
	 */
	public Week save(Week week) {
		return weekRepository.save(week);
	}

	/**
	 * 保存实体集合
	 * 
	 * @param weekList
	 * @return
	 */
	public List<Week> save(List<Week> weekList) {
		return weekRepository.save(weekList);
	}

	/**
	 * 根据ID查询实体
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Week findById(Long id) {
		return weekRepository.findOne(id);
	}

	/**
	 * 统计学期下学周个数
	 * 
	 * @param semester
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countBySemester(Semester semester) {
		return weekRepository.countBySemesterAndDeleteFlag(semester, DataValidity.VALID.getState());
	}

	/**
	 * count指定学校指定学期的所有name的值的正常数据
	 * 
	 * @param semester
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByName(Semester semester, String name) {
		return weekRepository.countBySemesterAndNameAndDeleteFlag(semester, name, DataValidity.VALID.getState());
	}

	/**
	 * count指定学校指定学期的所有no的值的正常数据
	 * 
	 * @param semester
	 * @param no
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByNo(Semester semester, Integer no) {
		return weekRepository.countBySemesterAndNoAndDeleteFlag(semester, no,
				DataValidity.VALID.getState());
	}

	/**
	 * count指定学校指定学期的所有name的值的正常数据，但是排除特定id（修改时用来排除自己）
	 * 
	 * @param semester
	 * @param name
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByNameAndIdNot(Semester semester, String name, Long id) {
		return weekRepository.countBySemesterAndNameAndIdNotAndDeleteFlag( semester, name, id, DataValidity.VALID.getState());
	}

	/**
	 * count指定学校指定学期的所有no的值的正常数据，但是排除特定id（修改时用来排除自己）
	 * 
	 * @param semester
	 * @param no
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByNoAndIdNot(Semester semester, Integer no, Long id) {
		return weekRepository.countBySemesterAndNoAndIdNotAndDeleteFlag(
				semester, no, id, DataValidity.VALID.getState());
	}

	/**
	 * 分页查询指定学校指定学期的学周的ID和no
	 * 
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<WeekDomain> findIdNo(Pageable pageable, Semester semester) {
		return weekRepository.findIdNo(pageable, semester, DataValidity.VALID.getState());
	}

	/**
	 * 根据学校学期和name查询学周列表
	 * 
	 * @param pageable
	 * @param semester
	 * @param no
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<WeekDomain> findNo(Pageable pageable, Semester semester,
			Integer no) {
		return weekRepository.findBySemesterAndNo(pageable, semester, DataValidity.VALID.getState(), no);
	}

	/**
	 * 根据指定学校学期的查询学周列表
	 * 
	 * @param pageable
	 * @param semester
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<WeekDomain> find(Pageable pageable, Semester semester) {
		return weekRepository.findBySemester(pageable, semester, DataValidity.VALID.getState());
	}

	/**
	 * 删除学期下所有学周
	 * 
	 * @param semester
	 * @return
	 */
	public void deleteAllSemesterWeek(Semester semester) {
		weekRepository.deleteAllSemesterWeek(DataValidity.INVALID.getState(), DataValidity.VALID.getState(), semester);
	}

	/**
	 * 查询指定日期是第几周
	 * 
	 * @param orgId
	 * @param date
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<WeekDomain> getByOrgIdAndDate(Long orgId, Date date) {
		return weekRepository.findByOrgIdAndDate(orgId, DataValidity.VALID.getState(), date, date);
	}

	@Transactional(readOnly = true)
	public List<Week> getBySemesterAndDate(Semester semester, Date date) {
		return weekRepository.findBySemesterAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeleteFlag(semester, date, date, DataValidity.VALID.getState());
	}


	@Transactional(readOnly = true)
	public List<Week> getByIdIn(Set<Long> ids) {
		return weekRepository.findByIdIn(ids);
	}

	@Transactional(readOnly = true)
	public List<Week> findAllWeekBySemester(Semester semester) {
		return weekRepository.findBySemesterAndDeleteFlag(semester, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public List<Week> findBySemesterAndNo(Semester semester, Integer no) {
		return weekRepository.findBySemesterAndNoAndDeleteFlag(semester, no, DataValidity.VALID.getState());
	}

	@Transactional
	public List<Week> findBySemesterAndWeekNo(Semester semester, Integer no) {
		List<Week> list = weekRepository.findBySemesterAndNoAndDeleteFlag(semester, no, DataValidity.VALID.getState());
		if(list != null && list.size() > 0){
			for(Week item : list){
				Semester s = item.getSemester();
				s.getId();
			}
		}
		return list;
	}

	@Transactional(readOnly = true)
	public Integer findMaxWeekNoBySemester(Semester semester) {
		return weekRepository.findMaxWeekNoBySemester(semester, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public Week findMaxWeekBySemester(Semester semester) {
		List<Week> list = weekRepository.findMaxWeekBySemester(semester, DataValidity.VALID.getState());
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Week findMinWeekBySemester(Semester semester) {
		List<Week> list = weekRepository.findMinWeekBySemester(semester, DataValidity.VALID.getState());
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	// *************************************************************以下部分处理页面调用逻辑**********************************************************************//
	/**
	 * 保存学周
	 *
	 * @param wd
	 * @return
	 */
	public Week save(WeekDomain wd) {
		Week week = new Week();
		Semester semester = semesterService.findById(wd.getSemesterId());
		if (null == semester) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
					"根据学期ID没有查找到对应的学期信息");
		}
		week.setOrgId(semester.getOrgId());
		week.setSemester(semester);

		Long c = countByNo(semester, wd.getNo());
		if (null != c && c.longValue() > 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION,
					"该【第几周】值已经存在");
		}
		week.setName(wd.getName());
		week.setStartDate(wd.getStartDate());
		week.setEndDate(wd.getEndDate());
		week.setNo(wd.getNo());
		week.setCreatedBy(wd.getUserId());
		week.setLastModifiedBy(wd.getUserId());
		week = save(week);
		return week;
	}

	/**
	 * 修改学期信息
	 *
	 * @param wd
	 * @return
	 */
	public Week update(WeekDomain wd) {
		if (null == wd.getId() || wd.getId() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Semester semester = semesterService.findById(wd.getSemesterId());
		if (null == semester) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
					"根据学期ID没有查找到对应的学期信息");
		}
		Week week = findById(wd.getId());
		if (null == week) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
					"对应ID的对象不存在");
		}
		week.setOrgId(semester.getOrgId());
		week.setSemester(semester);
		// 验证name
		Long c = countByNoAndIdNot(semester, wd.getNo(), wd.getId());
		if (null != c && c.longValue() > 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION,
					"该【第几周】值已经存在");
		}
		week.setName(wd.getName());
		week.setStartDate(wd.getStartDate());
		week.setEndDate(wd.getEndDate());
		week.setNo(wd.getNo());
		week.setLastModifiedBy(wd.getUserId());
		week.setLastModifiedDate(new Date());
		week = save(week);
		return week;
	}

	/**
	 * 删除学期信息
	 * 
	 * @param userId
	 * @param id
	 */
	public Week delete(Long userId, Long id) {
		if (null == id || id <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Week week = findById(id);
		if (null == week) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "对应ID的对象不存在");
		}
		Integer weekNo = findMaxWeekNoBySemester(week.getSemester());
		if (null != weekNo && weekNo.intValue() != week.getNo()) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "只能删除最后一个学周数据");
		}
		week.setDeleteFlag(DataValidity.INVALID.getState());
		week.setLastModifiedBy(userId);
		week.setLastModifiedDate(new Date());
		week = save(week);
		Semester semester = week.getSemester();
		if (null != semester) {
			long c = countBySemester(semester);
			semester.setNumWeek((int)c);
			semesterService.save(semester);
		}
		return week;
	}

	/**
	 * 按照查询条件分页查询指定学校的学周信息列表
	 *
	 * @param pageable
	 * @param semesterId
	 * @param no
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<WeekDomain> queryList(Pageable pageable, Long semesterId, Integer no) {
		PageData<WeekDomain> p = new PageData<>();
		Semester semester = semesterService.findById(semesterId);
		p.getPage().setPageNumber(pageable.getPageNumber() + 1);
		p.getPage().setPageSize(pageable.getPageSize());

		if (null == semester) {
			return p;
		}
		Page<WeekDomain> page = null;
		if (null != no && no.intValue() > 0) {
			page = findNo(pageable, semester, no);
		} else {
			page = find(pageable, semester);
		}
		p.setData(page.getContent());
		p.getPage().setTotalElements(page.getTotalElements());
		p.getPage().setTotalPages(page.getTotalPages());
		return p;
	}

	/**
	 * 分页查询指定学校学期的学周id、name字段 主要用于下拉列表
	 *
	 * @param pageable
	 * @param semesterId
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<WeekDomain> dropList(Pageable pageable, Long semesterId) {
		PageData<WeekDomain> p = new PageData<>();
		p.getPage().setPageNumber(pageable.getPageNumber() + 1);
		p.getPage().setPageSize(pageable.getPageSize());

		Semester semester = semesterService.findById(semesterId);
		if (null == semester) {
			return p;
		}

		Page<WeekDomain> page = findIdNo(pageable, semester);
		p.setData(page.getContent());
		p.getPage().setTotalElements(page.getTotalElements());
		p.getPage().setTotalPages(page.getTotalPages());
		return p;
	}

	/**
	 * 创建指定学期的所有学周
	 *
	 * @param semesterId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Long createAllWeek(Long semesterId, Date startDate, Date endDate) {
		Semester semester = semesterService.findById(semesterId);
		Long result = countBySemester(semester);
		if (null != result && result.longValue() > 0L) {
			throw new CommonException(ErrorCode.PARAMS_CONFLICT, "该学期下已存在学周,请清除已存在学周后重新生成学周");
		}
		if (startDate.before(semester.getStartDate()) || endDate.after(semester.getEndDate())) {
			throw new CommonException(ErrorCode.PARAMS_CONFLICT, "学周的起始及结束时间必须在学期的时间范围之内");
		}
		long c = countBySemester(semester);
		if (c > 0) {
			throw new CommonException(ErrorCode.PARAMS_CONFLICT, "学期对应的学周已经初始化，不能重复操作");
		}
//		Semester semester = null;
		Date startSunDay = DateUtil.getSunday(startDate);
		Date startMonday = startDate;//

		List<Week> weekList = new ArrayList<>();
		int i = 1;
		Week week = null;
		while (startMonday.before(endDate)) {
			week = new Week();
			week.setSemester(semester);
			week.setNo(i);
			week.setName("第" + week.getNo() + "周");
			week.setStartDate(startMonday);
			week.setEndDate(startSunDay);
			week.setOrgId(semester.getOrgId());
			weekList.add(week);
			i++;
			startMonday = DateUtil.getNextMonday(startMonday);
			startSunDay = DateUtil.getSunDayByMonday(startMonday);
		}
		if (null != week) {
			week.setEndDate(endDate);
		}

		save(weekList);
		semester.setNumWeek(weekList.size());
		semesterService.save(semester);
		return new Long(weekList.size());
	}

	
	/**
	 * 根据id获取学周信息
	 * @param id
	 * @return
	 */
	public WeekDomain get(Long id) {

		if (null == id || id <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Week week = findById(id);
		if (null == week) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
					"对应ID的对象不存在");
		}

		WeekDomain wd = new WeekDomain(id, week.getName(), week.getStartDate(),
				week.getEndDate(), week.getNo(), week.getCreatedDate());
		wd.setSemesterId(week.getSemester().getId());
		wd.setSemesterName(week.getSemester().getName());

		return wd;
	}

//	public void createAllWeek(Semester semester) {
//
//		Date startSunDay = DateUtil.getSunday(semester.getStartDate());
//		Date endMonday = DateUtil.getMonday(semester.getEndDate());
//		int weeksNum = DateUtil.getDaysBetweenDate(startSunDay, endMonday) / 7 + 2;
//
//		semester.setNumWeek(weeksNum);
//		semesterService.save(semester);
//
//		if (weeksNum > 0) {
//			// 插入week表
//			List<Week> weekList = new ArrayList<Week>();
//			for (int i = 0; i < weeksNum; i++) {
//				Week week = new Week();
//				week.setSemester(semester);
//				// week.setName(String.valueOf(i + 1));
//				week.setNo(i + 1);
//				if (i == 0) {
//					week.setStartDate(semester.getStartDate());
//					week.setEndDate(startSunDay);
//				} else if (i == weeksNum - 1) {
//					week.setStartDate(endMonday);
//					week.setEndDate(semester.getEndDate());
//				} else {
//					week.setStartDate(DateUtil
//							.afterNDay(startSunDay, 7 * i - 6));
//					week.setEndDate(DateUtil.afterNDay(startSunDay, 7 * i));
//				}
//				week.setOrgId(semester.getOrgId());
//				weekList.add(week);
//			}
//			save(weekList);
//		}
//	}

	/**
	 * 查询指定日期是第几周(周几)
	 *
	 * @param r
	 * @param orgId
	 * @param date
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> getWeekByOrgIdAndDate(Map<String, Object> r, Long orgId, Date date) {
		if (null == date) {
			date = new Date();
		}
		Semester semester = semesterService.getSemesterByDate(orgId, date);
		if (null == semester) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该时间没有对应的学期数据");
		}

		List<WeekDomain> ls = getByOrgIdAndDate(orgId, date);
		if (null != ls && ls.size() > 0) {
			WeekDomain week = ls.get(0);
			week.setSemesterName(semester.getName());
			r.put(ApiReturnConstants.DATA, week);
		} else {
			WeekDomain week = null;
			//判断距离学期的结束时间近还是距离开始时间近
			long end = Math.abs(date.getTime() - semester.getEndDate().getTime());
			long start = Math.abs(date.getTime() - semester.getStartDate().getTime());
			if (end < start) {
				Week maxWeek = findMaxWeekBySemester(semester);
				if (null == maxWeek) {
					throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[查询条件]没有对应的学周数据");
				}
				week = new WeekDomain(maxWeek.getId(), maxWeek.getName(), maxWeek.getStartDate(), maxWeek.getEndDate(), maxWeek.getNo(), maxWeek.getCreatedDate(), semester.getId());
				r.put(ApiReturnConstants.DATA, week);
			} else {
				Week minWeek = findMinWeekBySemester(semester);
				if (null == minWeek) {
					throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[查询条件]没有对应的学周数据");
				}
				week = new WeekDomain(minWeek.getId(), minWeek.getName(), minWeek.getStartDate(), minWeek.getEndDate(), minWeek.getNo(), minWeek.getCreatedDate(), semester.getId());
				r.put(ApiReturnConstants.DATA, week);
			}
		}

		return r;
	}

	public Week getWeekBySemesterAndDate(Semester semester, Date date) {
		List<Week> weeks = getBySemesterAndDate(semester, date);
		if (null != weeks && weeks.size() > 0) {
			return weeks.get(0);
		}
		return null;
	}

//	private void fillOutData(List<WeekDomain> outData, List<Week> list) {
//		if (list.size() > 0) {
//			for (Week week : list) {
//				WeekDomain d = new WeekDomain(week.getId(), week.getName(), week.getStartDate(), week.getEndDate(), week.getNo(), null);
//				outData.add(d);
//				if (null != week.getSemester()) {
//					d.setSemesterId(week.getSemester().getId());
//					d.setSemesterCode(week.getSemester().getCode());
//					d.setSemesterName(week.getSemester().getName());
//				}
//			}
//		}
//	}

//	public List<WeekDomain> saveAll(List<WeekDomain> ds) {
//		Set<String> scodes = new HashSet<>();
//		for (WeekDomain d : ds) {
//			scodes.add(d.getSemesterCode());
//		}
//		Map<String, Semester> sms = new HashMap<>();
//		List<Semester> ss = semesterService.findByCodes(scodes);
//		for (Semester s : ss) {
//			sms.put(s.getCode(), s);
//		}
//		List<Week> list = new ArrayList<>();
//		for(WeekDomain d : ds) {
//			Week week = new Week();
//			week.setName(d.getName());
//			week.setStartDate(d.getStartDate());
//			week.setEndDate(d.getEndDate());
//			week.setNo(d.getNo());
//			week.setCreatedBy(d.getUserId());
//			week.setLastModifiedBy(d.getUserId());
//			if(!StringUtils.isEmpty(d.getSemesterCode())) {
//				Semester s = sms.get(d.getSemesterCode());
//				if (null != s) {
//					week.setSemester(s);
//					week.setOrgId(s.getOrgId());
//				}
//			}
//			list.add(week);
//		}
//		List<WeekDomain> outData = new ArrayList<>();
//		if (list.size() > 0) {
//			list = save(list);
//			fillOutData(outData, list);
//		}
//		return outData;
//	}
//
//	public List<WeekDomain> updateAll(List<WeekDomain> ds) {
//		Set<String> scodes = new HashSet<>();
//		Set<Long> ids = new HashSet<>();
//		Map<Long, WeekDomain> wds = new HashMap<>();
//		for (WeekDomain d : ds) {
//			scodes.add(d.getSemesterCode());
//			ids.add(d.getId());
//			wds.put(d.getId(), d);
//		}
//		Map<String, Semester> sms = new HashMap<>();
//		List<Semester> ss = semesterService.findByCodes(scodes);
//		for (Semester s : ss) {
//			sms.put(s.getCode(), s);
//		}
//		List<Week> list = getByIdIn(ids);
//		for(Week week : list) {
//			WeekDomain d = wds.get(week.getId());
//			if (null != d) {
//				week.setName(d.getName());
//				week.setStartDate(d.getStartDate());
//				week.setEndDate(d.getEndDate());
//				week.setNo(d.getNo());
//				week.setLastModifiedBy(d.getUserId());
//				if (!StringUtils.isEmpty(d.getSemesterCode())) {
//					Semester s = sms.get(d.getSemesterCode());
//					if (null != s) {
//						week.setSemester(s);
//						week.setOrgId(s.getOrgId());
//					}
//				}
//			}
//		}
//		List<WeekDomain> outData = new ArrayList<>();
//		if (list.size() > 0) {
//			list = save(list);
//			fillOutData(outData, list);
//		}
//		return outData;
//	}
//
//	public List<WeekDomain> deleteAll(List<WeekDomain> ds) {
//		List<WeekDomain> outData = new ArrayList<>();
//		Set<Long> ids = new HashSet<>();
//		Long userId = 0L;
//		for (WeekDomain d : ds) {
//			ids.add(d.getId());
//			if(userId <= 0) {
//				userId = d.getUserId();
//			}
//		}
//		List<Week> list = getByIdIn(ids);
//		for(Week week : list) {
//			week.setDeleteFlag(DataValidity.INVALID.getState());
//			week.setLastModifiedBy(userId);
//		}
//		list = save(list);
//		fillOutData(outData, list);
//		return outData;
//	}


	public Map<String, Date> getPeriodStarttimeInWeek(Long orgId, Semester semester, Integer weekNo, Integer dayOfWeek, Integer startPeriodNo, Integer periodNum) {
		Map<String, Date> rs = new HashMap<>();
		List<Week> weeks = findBySemesterAndNo(semester, weekNo);
		Week week = null;
		if (weeks.size() > 0) {
			week = weeks.get(0);
		}
		if (null != week) {
			Date date = null;
			if (1 != weekNo) {
				date = week.getStartDate();
				date = DateUtil.afterNDay(date, dayOfWeek - 1);
			} else {
				Date d = week.getStartDate();
				int dow = DateUtil.getDayOfWeek(d);
				if (dow <= dayOfWeek) {
					date = DateUtil.afterNDay(date, dayOfWeek - dow);
				}
			}
			Period periodStart = null;
			Period periodEnd = null;
			List<Period> periodList = periodService.findByOrgAndNo(orgId, startPeriodNo);
			if (periodList.size() > 0) {
				periodStart = periodList.get(0);
			}
			periodList = periodService.findByOrgAndNo(orgId, startPeriodNo + periodNum - 1);
			if (periodList.size() > 0) {
				periodEnd = periodList.get(0);
			}
			if (null != date && null != periodStart && null != periodEnd) {
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				StringBuilder startDate = new StringBuilder();
				StringBuilder endDate = new StringBuilder();
				startDate.append(format1.format(date)).append(" ").append(periodStart.getStartTime());
				endDate.append(format1.format(date)).append(" ").append(periodEnd.getEndTime());
				try {
					rs.put("start", format2.parse(startDate.toString()));
					rs.put("end", format2.parse(endDate.toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return rs;
	}

	public static void main(String[] args) {
		WeekService weekService = new WeekService();
		weekService.createAllWeek(1L, DateUtil.parse("2018-01-23"), DateUtil.parse("2018-01-27"));
	}
}
