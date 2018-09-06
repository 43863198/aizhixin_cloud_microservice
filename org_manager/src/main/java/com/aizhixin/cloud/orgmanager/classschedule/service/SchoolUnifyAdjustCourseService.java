/**
 *
 */
package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.domain.SchoolUnifyAdjustCourseDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.SchoolUnifyAdjustCourseSchedule;
import com.aizhixin.cloud.orgmanager.classschedule.repository.SchoolUnifyAdjustCourseRepository;
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

import java.util.Date;
import java.util.List;

/**
 * 整体调课业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class SchoolUnifyAdjustCourseService {
    @Autowired
    private SchoolUnifyAdjustCourseRepository schoolUnifyAdjustCourseRepository;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private WeekService weekService;

    /**
     * 保存实体
     *
     * @param schoolUnifyAdjustCourseSchedule
     * @return
     */
    public SchoolUnifyAdjustCourseSchedule save(SchoolUnifyAdjustCourseSchedule schoolUnifyAdjustCourseSchedule) {
        return schoolUnifyAdjustCourseRepository.save(schoolUnifyAdjustCourseSchedule);
    }

    @Transactional(readOnly = true)
    public SchoolUnifyAdjustCourseSchedule findById(Long id) {
        return schoolUnifyAdjustCourseRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Page<SchoolUnifyAdjustCourseDomain> findByOrgId(Pageable pageable, Long orgId) {
        return schoolUnifyAdjustCourseRepository.findByOrgId(pageable, orgId, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<SchoolUnifyAdjustCourseSchedule> findBySemesterAndDate(Semester semester, String date) {
        return schoolUnifyAdjustCourseRepository.findBySemesterAndDestDateAndDeleteFlag(semester, date, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public long countByOrgIdAndSrcDate(Long orgId, String date) {
        return schoolUnifyAdjustCourseRepository.countByOrgIdAndSrcDateAndDeleteFlag(orgId, date, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public long countByOrgIdAndDestDate(Long orgId, String date) {
        return schoolUnifyAdjustCourseRepository.countByOrgIdAndDestDateAndDeleteFlag(orgId, date, DataValidity.VALID.getState());
    }

    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//
    public SchoolUnifyAdjustCourseSchedule save(SchoolUnifyAdjustCourseDomain d) {
        if (null == d) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "输入数据是必须的");
        }
        if (null == d.getOrgId() || d.getOrgId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学校Id是必须的");
        }
        if (null == d.getSrcDate()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "原日期是必须的");
        }
        if (null == d.getDestDate()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "目标日期是必须的");
        }
        Semester semester = null;
        if (null != d.getSemesterId() && d.getSemesterId() > 0) {
            semester = semesterService.findById(d.getSemesterId());
        }
        if (null == semester) {
            semester = semesterService.getSemesterByDate(d.getOrgId(), d.getSrcDate());
        }
        if (null == semester) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学期是必须的");
        }
        if (d.getSrcDate().before(semester.getStartDate()) || d.getSrcDate().after(semester.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "原日期必须在学期内");
        }
        if (d.getDestDate().before(semester.getStartDate()) || d.getDestDate().after(semester.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "目标日期必须在学期内");
        }

        Date current = new Date();
        if (current.after(d.getSrcDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前日期已经晚于源日期");
        }
        if (current.after(d.getDestDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前日期已经晚于目标日期");
        }
        Integer srcWeekNo = null;
        Integer destNo = null;
        Week startWeek = weekService.getWeekBySemesterAndDate(semester, d.getSrcDate());
        if (null != startWeek) {
            srcWeekNo = startWeek.getNo();
        }
        Week destWeek = weekService.getWeekBySemesterAndDate(semester, d.getDestDate());
        if (null != destWeek) {
            destNo = destWeek.getNo();
        }
        if (null == srcWeekNo || null == destNo) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有找到对应的学周");
        }

        SchoolUnifyAdjustCourseSchedule schoolUnifyAdjustCourseSchedule = new SchoolUnifyAdjustCourseSchedule();
        copyProperty(schoolUnifyAdjustCourseSchedule, d, semester, srcWeekNo, destNo);
        long c = countByOrgIdAndDestDate(d.getOrgId(), schoolUnifyAdjustCourseSchedule.getDestDate());
        if (c > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "目标日期已经存在整体调课信息");
        }
        c = countByOrgIdAndSrcDate(d.getOrgId(), schoolUnifyAdjustCourseSchedule.getSrcDate());
        if (c > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "源日期已经被整体调整过");
        }
        return save(schoolUnifyAdjustCourseSchedule);
    }

    private void copyProperty(SchoolUnifyAdjustCourseSchedule entity, SchoolUnifyAdjustCourseDomain d, Semester semester, Integer srcWeekNo, Integer destWeekNo) {
        entity.setSrcDate(DateUtil.format(d.getSrcDate()));
        entity.setDestDate(DateUtil.format(d.getDestDate()));
        entity.setName(d.getName());
        entity.setSemester(semester);
        entity.setOrgId(d.getOrgId());
        entity.setSrcWeekNo(srcWeekNo);
        entity.setDestWeekNo(destWeekNo);
        entity.setLastModifiedBy(d.getUserId());
        entity.setCreatedBy(d.getUserId());
        entity.setSrcDayOfWeek(DateUtil.getDayOfWeek(d.getSrcDate()));
        entity.setDestDayOfWeek(DateUtil.getDayOfWeek(d.getDestDate()));
    }

    public SchoolUnifyAdjustCourseSchedule update(SchoolUnifyAdjustCourseDomain d) {
        SchoolUnifyAdjustCourseSchedule schoolUnifyAdjustCourseSchedule = null;
        if (null == d) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "输入数据是必须的");
        }
        if (null != d.getId() && d.getId() > 0) {
            schoolUnifyAdjustCourseSchedule = findById(d.getId());
        }
        if (null == schoolUnifyAdjustCourseSchedule) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有查找到需要修改的数据");
        }
        if (null == d.getSrcDate()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "原日期是必须的");
        }
        if (null == d.getDestDate()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "目标日期是必须的");
        }
        Semester semester = null;
        if (null != d.getSemesterId() && d.getSemesterId() > 0) {
            semester = semesterService.findById(d.getSemesterId());
        }
        if (null == semester) {
            semester = semesterService.getSemesterByDate(d.getOrgId(), d.getSrcDate());
        }
        if (null == semester) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学期是必须的");
        }
        if (d.getSrcDate().before(semester.getStartDate()) || d.getSrcDate().after(semester.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "开始日期必须在学期内");
        }
        if (d.getDestDate().before(semester.getStartDate()) || d.getDestDate().after(semester.getEndDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "结束日期必须在学期内");
        }

        Date current = new Date();
        if (current.after(d.getSrcDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前日期已经晚于源日期");
        }
        if (current.after(d.getDestDate())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前日期已经晚于目标日期");
        }

        Integer srcWeekNo = null;
        Integer destNo = null;
        Week srcWeek = weekService.getWeekBySemesterAndDate(semester, d.getSrcDate());
        if (null != srcWeek) {
            srcWeekNo = srcWeek.getNo();
        }
        Week destWeek = weekService.getWeekBySemesterAndDate(semester, d.getDestDate());
        if (null != destWeek) {
            destNo = destWeek.getNo();
        }
        if (null == srcWeekNo || null == destNo) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有找到对应的学周");
        }

        copyProperty(schoolUnifyAdjustCourseSchedule, d, semester, srcWeekNo, destNo);
        long c = countByOrgIdAndDestDate(d.getOrgId(), schoolUnifyAdjustCourseSchedule.getDestDate());
        if (c > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "目标日期已经存在整体调课信息");
        }
        c = countByOrgIdAndSrcDate(d.getOrgId(), schoolUnifyAdjustCourseSchedule.getSrcDate());
        if (c > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "源日期已经被整体调整过");
        }
        return save(schoolUnifyAdjustCourseSchedule);
    }

    public SchoolUnifyAdjustCourseSchedule delete(Long id, Long userId) {
        SchoolUnifyAdjustCourseSchedule schoolHoliday = null;
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

    public SchoolUnifyAdjustCourseDomain get(Long id) {
        SchoolUnifyAdjustCourseSchedule schoolHoliday = null;
        SchoolUnifyAdjustCourseDomain d = new SchoolUnifyAdjustCourseDomain ();
        if (null != id && id > 0) {
            schoolHoliday = findById(id);
        }
        if (null != schoolHoliday) {
            d.setId(schoolHoliday.getId());
            d.setName(schoolHoliday.getName());
            d.setSrcDate(DateUtil.parse(schoolHoliday.getSrcDate()));
            d.setDestDate(DateUtil.parse(schoolHoliday.getDestDate()));
            if (null != schoolHoliday.getSemester()) {
                d.setSemesterId(schoolHoliday.getId());
                d.setSemesterName(schoolHoliday.getSemester().getName());
            }
        }
        return d;
    }

    public PageData<SchoolUnifyAdjustCourseDomain> list(Pageable pageable, Long orgId) {
        PageData<SchoolUnifyAdjustCourseDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        Page<SchoolUnifyAdjustCourseDomain> page = findByOrgId(pageable, orgId);
        pageData.getPage().setTotalPages(page.getTotalPages());
        pageData.getPage().setTotalElements(page.getTotalElements());
        pageData.setData(page.getContent());
        return pageData;
    }
}