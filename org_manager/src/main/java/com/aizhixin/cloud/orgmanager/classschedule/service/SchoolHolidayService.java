/**
 *
 */
package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.domain.SchoolHolidayDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.SchoolHoliday;
import com.aizhixin.cloud.orgmanager.classschedule.repository.SchoolHolidayRepository;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.Week;
import com.aizhixin.cloud.orgmanager.company.service.SemesterService;
import com.aizhixin.cloud.orgmanager.company.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 排课表相关操作业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class SchoolHolidayService {
    @Autowired
    private SchoolHolidayRepository schoolHolidayRepository;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private WeekService weekService;

    /**
     * 保存实体
     *
     * @param schoolTimeTable
     * @return
     */
    public SchoolHoliday save(SchoolHoliday schoolTimeTable) {
        return schoolHolidayRepository.save(schoolTimeTable);
    }

    @Transactional(readOnly = true)
    public SchoolHoliday findById(Long id) {
        return schoolHolidayRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Page<SchoolHolidayDomain> findByOrgId(Pageable pageable, Long orgId) {
        return schoolHolidayRepository.findByOrgId(pageable, orgId, DataValidity.VALID.getState());
    }
    @Transactional(readOnly = true)
    public List<SchoolHoliday> findBySemesterAndDate(Semester semester, String date) {
        return schoolHolidayRepository.findBySemesterAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeleteFlag(semester, date, date, DataValidity.VALID.getState());
    }

    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//
    public SchoolHoliday save(SchoolHolidayDomain d) {
        if (null == d) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "输入数据是必须的");
        }
        if (null == d.getOrgId() || d.getOrgId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学校Id是必须的");
        }
        Semester semester = null;
        if (null != d.getSemesterId() && d.getSemesterId() > 0) {
            semester = semesterService.findById(d.getSemesterId());
        }
        if (null == semester) {
            semester = semesterService.getSemesterByDate(d.getOrgId(), d.getStartDate());
        }
        if (null == semester) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学期是必须的");
        }
        if (null == d.getStartDate()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "开始日期是必须的");
        }
        if (null == d.getEndDate()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "结束日期是必须的");
        }
        if (d.getStartDate().before(semester.getStartDate()) || d.getStartDate().after(semester.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "开始日期必须在学期内");
        }
        if (d.getEndDate().before(semester.getStartDate()) || d.getEndDate().after(semester.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "结束日期必须在学期内");
        }
        if (d.getStartDate().after(d.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "开始日期必须小于等于结束日期");
        }
//        Date current = new Date();
//        if (current.after(d.getStartDate())) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前日期已经晚于要设定的日期");
//        }
        Integer startWeekNo = null;
        Integer endWeekNo = null;
        Week startWeek = weekService.getWeekBySemesterAndDate(semester, d.getStartDate());
        if (null != startWeek) {
            startWeekNo = startWeek.getNo();
        }
        Week endWeek = weekService.getWeekBySemesterAndDate(semester, d.getEndDate());
        if (null != endWeek) {
            endWeekNo = endWeek.getNo();
        }
        if (null == startWeekNo || null == endWeekNo) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有找到对应的学周");
        }

        SchoolHoliday schoolHoliday = new SchoolHoliday();
        copyProperty(schoolHoliday, d, semester, startWeekNo, endWeekNo);
        return save(schoolHoliday);
    }

    private void copyProperty(SchoolHoliday schoolHoliday, SchoolHolidayDomain d, Semester semester, Integer startWeekNo, Integer endWeekNo) {
        schoolHoliday.setEndDate(DateUtil.format(d.getEndDate()));
        schoolHoliday.setStartDate(DateUtil.format(d.getStartDate()));
        schoolHoliday.setName(d.getName());
        schoolHoliday.setSemester(semester);
        schoolHoliday.setOrgId(d.getOrgId());
        schoolHoliday.setStartWeekNo(startWeekNo);
        schoolHoliday.setEndWeekNo(endWeekNo);
        schoolHoliday.setLastModifiedBy(d.getUserId());
        schoolHoliday.setCreatedBy(d.getUserId());
        schoolHoliday.setStartDayOfWeek(DateUtil.getDayOfWeek(d.getStartDate()));
        schoolHoliday.setEndDayOfWeek(DateUtil.getDayOfWeek(d.getEndDate()));
    }

    public SchoolHoliday update(SchoolHolidayDomain d) {
        SchoolHoliday schoolHoliday = null;
        if (null == d) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "输入数据是必须的");
        }
        if (null != d.getId() && d.getId() > 0) {
            schoolHoliday = findById(d.getId());
        }
        if (null == schoolHoliday) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有查找到需要修改的数据");
        }
        Semester semester = null;
        if (null != d.getSemesterId() && d.getSemesterId() > 0) {
            semester = semesterService.findById(d.getSemesterId());
        }
        if (null == semester) {
            semester = semesterService.getSemesterByDate(d.getOrgId(), d.getStartDate());
        }
        if (null == semester) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学期是必须的");
        }
        if (null == d.getStartDate()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "开始日期是必须的");
        }
        if (null == d.getEndDate()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "结束日期是必须的");
        }
        if (d.getStartDate().before(semester.getStartDate()) || d.getStartDate().after(semester.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "开始日期必须在学期内");
        }
        if (d.getEndDate().before(semester.getStartDate()) || d.getEndDate().after(semester.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "结束日期必须在学期内");
        }
        if (d.getStartDate().after(d.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "开始日期必须小于等于结束日期");
        }
//        Date current = new Date();
//        if (current.after(d.getEndDate())) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前日期已经早于要设定的日期");
//        }
        Integer startWeekNo = null;
        Integer endWeekNo = null;
        Week startWeek = weekService.getWeekBySemesterAndDate(semester, d.getStartDate());
        if (null != startWeek) {
            startWeekNo = startWeek.getNo();
        }
        Week endWeek = weekService.getWeekBySemesterAndDate(semester, d.getEndDate());
        if (null != endWeek) {
            endWeekNo = endWeek.getNo();
        }
        if (null == startWeekNo || null == endWeekNo) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有找到对应的学周");
        }

        copyProperty(schoolHoliday, d, semester, startWeekNo, endWeekNo);
        return save(schoolHoliday);
    }

    public SchoolHoliday delete(Long id, Long userId) {
        SchoolHoliday schoolHoliday = null;
        if (null != id && id > 0) {
            schoolHoliday = findById(id);
        }
        if (null == schoolHoliday) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有查找到需要修改的数据");
        }
//        Date current = new Date();
//        if (current.after(DateUtil.parse(schoolHoliday.getEndDate()))) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前日期已经晚于设定的日期");
//        }
        schoolHoliday.setLastModifiedBy(userId);
        schoolHoliday.setDeleteFlag(DataValidity.INVALID.getState());
        return save(schoolHoliday);
    }

    @Transactional(readOnly = true)
    public SchoolHolidayDomain get(Long id) {
        SchoolHoliday schoolHoliday = null;
        SchoolHolidayDomain d = new SchoolHolidayDomain ();
        if (null != id && id > 0) {
            schoolHoliday = findById(id);
        }
        if (null != schoolHoliday) {
            d.setId(schoolHoliday.getId());
            d.setName(schoolHoliday.getName());
            d.setStartDate(DateUtil.parse(schoolHoliday.getStartDate()));
            d.setEndDate(DateUtil.parse(schoolHoliday.getEndDate()));
            if (null != schoolHoliday.getSemester()) {
                d.setSemesterId(schoolHoliday.getSemester().getId());
                d.setSemesterName(schoolHoliday.getSemester().getName());
            }
        }
        return d;
    }

    @Transactional(readOnly = true)
    public PageData<SchoolHolidayDomain> list(Pageable pageable, Long orgId) {
        PageData<SchoolHolidayDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        Page<SchoolHolidayDomain> page = findByOrgId(pageable, orgId);
        pageData.getPage().setTotalPages(page.getTotalPages());
        pageData.getPage().setTotalElements(page.getTotalElements());
        pageData.setData(page.getContent());
        return pageData;
    }
}