package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.domain.TempAdjustCourseListDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TempAdjustCourseSchedule;
import com.aizhixin.cloud.orgmanager.classschedule.repository.TempAdjustCourseScheduleRepository;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.service.PeriodService;
import com.aizhixin.cloud.orgmanager.company.service.SemesterService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import com.aizhixin.cloud.orgmanager.company.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional
public class TempAdjustCourseScheduleService {
    @Autowired
    private EntityManager em;
    @Autowired
    private TempAdjustCourseScheduleRepository tempAdjustCourseScheduleRepository;
    @Autowired
    private TeachingClassService teachingClassService;
    @Autowired
    private SchoolTimeTableService schoolTimeTableService;
    @Autowired
    private TeachingClassTeacherService teachingClassTeacherService;
    @Autowired
    private DiandianSchoolTimeTableService diandianSchoolTimeTableService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private WeekService weekService;
    @Autowired
    private UserService userService;
    @Autowired
    private PeriodService periodService;

//    private long adjustCourseMinTimeMinute = 30 * 60 * 1000;//当天调课最小的时间间隔

    /**
     * 保存实体
     *
     * @param tempAdjustCourseSchedule
     * @return
     */
    public TempAdjustCourseSchedule save(TempAdjustCourseSchedule tempAdjustCourseSchedule) {
        return tempAdjustCourseScheduleRepository.save(tempAdjustCourseSchedule);
    }

    public List<TempAdjustCourseSchedule> save(List<TempAdjustCourseSchedule> tempAdjustCourseSchedules) {
        return tempAdjustCourseScheduleRepository.save(tempAdjustCourseSchedules);
    }

    @Transactional(readOnly = true)
    public TempAdjustCourseSchedule findById(Long id) {
        return tempAdjustCourseScheduleRepository.findOne(id);
    }

//    @Transactional(readOnly = true)
//    public List<TempAdjustCourseSchedule> findByAdjustId(Long adjustId) {
//        return tempAdjustCourseScheduleRepository.findByAdjustId(adjustId);
//    }

//    @Transactional(readOnly = true)
//    public List<TempAdjustCourseSchedule> findByTeachingclassIdsAndWeekNo(Set<Long> teachingclassIds, Integer weekNo) {
//        return tempAdjustCourseScheduleRepository.findByTeachingClass_idInAndWeekNoAndValidStatusAndApprovalStatusAndDeleteFlag(teachingclassIds, weekNo, ValidOrCancel.VALID.getState(), ApprovalStatus.PASS.getState(), DataValidity.VALID.getState());
//    }
//
//    @Transactional(readOnly = true)
//    public List<TempAdjustCourseSchedule> findByTeachingclassIdsAndWeekNoAndDayOfWeek(Set<Long> teachingclassIds, Integer weekNo, Integer dayOfWeek) {
//        return tempAdjustCourseScheduleRepository.findByTeachingClass_idInAndWeekNoAndDayOfWeekAndValidStatusAndApprovalStatusAndDeleteFlag(teachingclassIds, weekNo, dayOfWeek, ValidOrCancel.VALID.getState(), ApprovalStatus.PASS.getState(), DataValidity.VALID.getState());
//    }
//    @Transactional(readOnly = true)
//    public List<TempAdjustCourseSchedule> findByTeachingclassAndWeekAndPeriodAndAdjustType(TeachingClass teachingClass, Integer weekNo, Integer dayOfWeek, Integer periodNo, Integer periodNum, Integer adjustType) {
//        return tempAdjustCourseScheduleRepository.findByTeachingClassAndWeekNoAndDayOfWeekAndPeriodNoAndPeriodNumAndAdjustTypeAndDeleteFlag(teachingClass, weekNo, dayOfWeek, periodNo, periodNum, adjustType, DataValidity.VALID.getState());
//    }

    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//

//    private TempAdjustCourseSchedule createAddCopy(TeachingClass teachingClass, TempAdjustCourseDomain tempAdjustCourseDomain, User teacher) {
//        TempAdjustCourseSchedule tempAdjustCourseSchedule = new TempAdjustCourseSchedule();
//        tempAdjustCourseSchedule.setOrgId(teachingClass.getOrgId());
//        tempAdjustCourseSchedule.setSemester(teachingClass.getSemester());
//        tempAdjustCourseSchedule.setTeachingClass(teachingClass);
//
//        tempAdjustCourseSchedule.setValidStatus(ValidOrCancel.VALID.getState());
//        tempAdjustCourseSchedule.setApprovalStatus(ApprovalStatus.PASS.getState());
//
//        tempAdjustCourseSchedule.setWeekNo(tempAdjustCourseDomain.getWeekNo());
//        tempAdjustCourseSchedule.setDayOfWeek(tempAdjustCourseDomain.getDayOfWeek());
//        tempAdjustCourseSchedule.setEventDate(tempAdjustCourseDomain.getEventDate());
//        tempAdjustCourseSchedule.setClassroom(tempAdjustCourseDomain.getClassroom());
//        tempAdjustCourseSchedule.setPeriodNo(tempAdjustCourseDomain.getPeriodNo());
//        tempAdjustCourseSchedule.setPeriodNum(tempAdjustCourseDomain.getPeriodNum());
//
//        tempAdjustCourseSchedule.setLastModifiedBy(teacher.getId());
//        tempAdjustCourseSchedule.setCreatedBy(teacher.getId());
//        tempAdjustCourseSchedule.setCreatorNo(teacher.getJobNumber());
//        tempAdjustCourseSchedule.setCreatorName(teacher.getName());
//        return tempAdjustCourseSchedule;
//    }

//    private Date validateAndGetDate(Map<String, Date> dateMap) {
//        Date startDate = dateMap.get("start");
//        if (null == startDate) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据参数条件计算不了相应的时间");
//        }
////        if (startDate.before(current)) {
////            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "不能在开课后添加排课信息");
////        }
////        Date nextday = DateUtil.getZerotime(DateUtil.afterNDay(current, 1));
////        if (!startDate.after(nextday)) {//今天
////            if (startDate.getTime() - current.getTime() < adjustCourseMinTimeMinute) {//暂时提前半小时
////                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "必须在开课前一段时间才能添加课时");
////            }
////        }
//        return startDate;
//    }


//    public TempAdjustCourseSchedule saveTempAdjustCourseSchedule (TempAdjustCourseFullDomain tac) {
//        if (null == tac.getTeachingClassId() || tac.getTeachingClassId() <= 0) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID是必须的");
//        }
//        if (null == tac.getUserId() || tac.getUserId() <= 0) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师ID是必须的");
//        }
//        User teacher = userService.findById(tac.getUserId());
//        if (null == teacher) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师是必须的");
//        }
//        TeachingClass teachingClass = teachingClassService.findById(tac.getTeachingClassId());
//        if (null == teachingClass) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班是必须的");
//        }
//        Semester semester = teachingClass.getSemester();
//
//        TempAdjustCourseSchedule tempAdjustCourseSchedule = null;
//        TempAdjustCourseSchedule addTempAdjustCourseSchedule = null;
//        TempAdjustCourseSchedule stopTempAdjustCourseSchedule = null;
//        TempAdjustCourseDomain add = tac.getAddTempAdjustCourseDomain();
//        TempAdjustCourseDomain stop = tac.getDeleteTempCourseDomain();
////        Date current = new Date ();
//
//        if (null != add && null != add.getEventDate() && null != stop && null != stop.getEventDate()) {//调课
//            Date destDate = DateUtil.parse(add.getEventDate());
//            if (null == destDate) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "目标日期格式不是yyyy-MM-dd");
//            }
//            if (null == add.getPeriodNo() || add.getPeriodNo() <= 0) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节是必须的");
//            }
//            if (null == add.getPeriodNum() || add.getPeriodNum() <= 0) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "持续节是必须的");
//            }
//            Week week = weekService.getWeekBySemesterAndDate(semester, destDate);
//            if (null == week) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据目标日期没有查找到对应的学周信息");
//            }
//            add.setWeekNo(week.getNo());
//            add.setDayOfWeek(DateUtil.getDayOfWeek(destDate));
//            long pc = periodService.countByNoAndOrgId(add.getPeriodNo(), teachingClass.getOrgId());
//            if (pc <= 0) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节不存在");
//            }
//
//            pc = periodService.countByNoAndOrgId(add.getPeriodNo() + add.getPeriodNum() - 1, teachingClass.getOrgId());
//            if (pc <= 0) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节不存在");
//            }
//
//            Set<TeachingClass> tcs = teachingClassTeacherService.findTeachingClassByTeacher(semester, teacher);
//            if (null != tcs && tcs.size() > 0) {
//                long c = schoolTimeTableService.countByCourseScheduleTime(tcs, add.getWeekNo(), add.getDayOfWeek(), add.getPeriodNo(), add.getPeriodNo() + add.getPeriodNum(), diandianSchoolTimeTableService.getSingleOrDoublesCondition(add.getDayOfWeek()));
//                if (c > 0) {//还需进一步判断冲突的课程是否有被临时调停
//                    throw new CommonException(ErrorCode.ID_IS_REQUIRED, "增加临时课程的数据和其他课程表有冲突");
//                }
//            }
//
//            Map<String, Date> dateMap = weekService.getPeriodStarttimeInWeek(teachingClass.getOrgId(), semester, stop.getWeekNo(), stop.getDayOfWeek(), stop.getPeriodNo(), stop.getPeriodNum());
//            Date startDate = validateAndGetDate(dateMap);
//            long c = schoolTimeTableService.countByTeachingClassAndWeekAndDayOfWeek(teachingClass, stop.getWeekNo(), stop.getDayOfWeek(), diandianSchoolTimeTableService.getSingleOrDoublesCondition(stop.getWeekNo()), stop.getPeriodNo(), stop.getPeriodNum());
//            if (c <= 0) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据停止课程条件查找不到对应的课表数据");
//            }
//            tempAdjustCourseSchedule = createAddCopy(teachingClass, stop, teacher);
//            tempAdjustCourseSchedule.setEventDate(DateUtil.format(startDate));
//
//            tempAdjustCourseSchedule.setAdjustType(StopOrAddTempCourse.CHANGE.getState());
//            tempAdjustCourseSchedule = save(tempAdjustCourseSchedule);
//        }  else if (null != add && null != add.getEventDate()) {//---------------------------增加
//            Date destDate = DateUtil.parse(add.getEventDate());
//            if (null == destDate) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "目标日期格式不是yyyy-MM-dd");
//            }
//            if (null == add.getPeriodNo() || add.getPeriodNo() <= 0) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节是必须的");
//            }
//            if (null == add.getPeriodNum() || add.getPeriodNum() <= 0) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "持续节是必须的");
//            }
//            Week week = weekService.getWeekBySemesterAndDate(semester, destDate);
//            if (null == week) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据目标日期没有查找到对应的学周信息");
//            }
//            add.setWeekNo(week.getNo());
//            add.setDayOfWeek(DateUtil.getDayOfWeek(destDate));
//            long pc = periodService.countByNoAndOrgId(add.getPeriodNo(), teachingClass.getOrgId());
//            if (pc <= 0) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节不存在");
//            }
//
//            pc = periodService.countByNoAndOrgId(add.getPeriodNo() + add.getPeriodNum() - 1, teachingClass.getOrgId());
//            if (pc <= 0) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节不存在");
//            }
//
//            Set<TeachingClass> tcs = teachingClassTeacherService.findTeachingClassByTeacher(semester, teacher);
//            if (null != tcs && tcs.size() > 0) {
//                long c = schoolTimeTableService.countByCourseScheduleTime(tcs, add.getWeekNo(), add.getDayOfWeek(), add.getPeriodNo(), add.getPeriodNo() + add.getPeriodNum(), diandianSchoolTimeTableService.getSingleOrDoublesCondition(add.getDayOfWeek()));
//                if (c > 0) {//还需进一步判断冲突的课程是否有被临时调停
//                    throw new CommonException(ErrorCode.ID_IS_REQUIRED, "增加临时课程的数据和其他课程表有冲突");
////                List<SchoolTimeTable> conflictSchedule = schoolTimeTableService.findBySemesterAndWeekAndDayOfWeek(teachingClass.getSemester(), add.getWeekNo(), add.getDayOfWeek(), add.getPeriodNo(), add.getPeriodNo() + add.getPeriodNum() - 1, diandianSchoolTimeTableService.getSingleOrDoublesCondition(add.getDayOfWeek()));
////                boolean conflict = false;
////                for (SchoolTimeTable s : conflictSchedule) {
////                    //和临时调停课进行比较
////                }
////                if (conflict) {
////                    throw new CommonException(ErrorCode.ID_IS_REQUIRED, "增加临时课程的数据和其他课程表有冲突");
////                }
//                }
//            }
//            addTempAdjustCourseSchedule = createAddCopy(teachingClass, add, teacher);
//            addTempAdjustCourseSchedule.setEventDate(DateUtil.format(destDate));
//            addTempAdjustCourseSchedule.setAdjustType(StopOrAddTempCourse.ADD.getState());
//            tempAdjustCourseSchedule = save(addTempAdjustCourseSchedule);
//        } else if (null != stop && null != stop.getEventDate()) {//-------------------------------------------停课
//            Map<String, Date> dateMap = weekService.getPeriodStarttimeInWeek(teachingClass.getOrgId(), semester, stop.getWeekNo(), stop.getDayOfWeek(), stop.getPeriodNo(), stop.getPeriodNum());
//            Date startDate = validateAndGetDate(dateMap);
//            long c = schoolTimeTableService.countByTeachingClassAndWeekAndDayOfWeek(teachingClass, stop.getWeekNo(), stop.getDayOfWeek(), diandianSchoolTimeTableService.getSingleOrDoublesCondition(stop.getWeekNo()), stop.getPeriodNo(), stop.getPeriodNum());
//            if (c <= 0) {
////                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据停止课程条件查找不到对应的课表数据");
//                //需要先检查新增临时的课程里边是否有数据
//                List<TempAdjustCourseSchedule> addTemps = findByTeachingclassAndWeekAndPeriodAndAdjustType(teachingClass, stop.getWeekNo(), stop.getDayOfWeek(), stop.getPeriodNo(), stop.getPeriodNum(), StopOrAddTempCourse.ADD.getState());
//                if (null == addTemps || addTemps.size() <= 0) {
//                    throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据停止课程条件查找不到对应的课表数据");
//                }
//            }
//            stopTempAdjustCourseSchedule = createAddCopy(teachingClass, stop, teacher);
//            if (!StringUtils.isEmpty(stopTempAdjustCourseSchedule.getEventDate())) {
//                stopTempAdjustCourseSchedule.setEventDate(DateUtil.format(startDate));
//            }
//            stopTempAdjustCourseSchedule.setAdjustType(StopOrAddTempCourse.STOP.getState());
//            stopTempAdjustCourseSchedule.setCreatedBy(tac.getUserId());
//            stopTempAdjustCourseSchedule.setLastModifiedBy(tac.getUserId());
//            tempAdjustCourseSchedule = save(stopTempAdjustCourseSchedule);
//        } else {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "添加或停止课程的信息最少需要有一个");
//        }
//        return tempAdjustCourseSchedule;
//    }


//    public void cancelTempAdjustCourseSchedule(Long id) {
//        if (null == id || id <= 0) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "调停课ID是必须的");
//        }
//
//        TempAdjustCourseSchedule tempAdjustCourseSchedule = findById(id);
//        if (null != tempAdjustCourseSchedule) {
//            tempAdjustCourseSchedule.setValidStatus(ValidOrCancel.CANDEL.getState());
//            tempAdjustCourseSchedule.setDeleteFlag(DataValidity.INVALID.getState());
//            save (tempAdjustCourseSchedule);
//        }
//    }

    @Transactional(readOnly = true)
    public PageData<TempAdjustCourseListDomain> list (Long orgId, Long semesterId, String teacher, Integer adjustType, Date startDate, Date endDate, Pageable pageable) {
        PageData <TempAdjustCourseListDomain> p = new PageData<>();
        p.getPage().setPageNumber(pageable.getPageNumber() + 1);
        p.getPage().setPageSize(pageable.getPageSize());

        Semester semester = null;
        if (null != semesterId && semesterId > 0) {
            semester = semesterService.findById(semesterId);
        }
        Map <String, Object> qryParam = new HashMap <>();
        qryParam.put("deleteFlag", DataValidity.VALID.getState());
        StringBuilder hql = new StringBuilder("SELECT  new com.aizhixin.cloud.orgmanager.classschedule.domain.TempAdjustCourseListDomain(t.id, t.creatorNo, t.creatorName, t.teachingClass.id, t.teachingClass.code, t.teachingClass.name, t.adjustType, t.createdDate, t.eventDate, t.periodNo, t.periodNum, t.classroom) FROM com.aizhixin.cloud.orgmanager.classschedule.entity.TempAdjustCourseSchedule t WHERE t.deleteFlag = :deleteFlag");
        StringBuilder chql = new StringBuilder("SELECT COUNT(t.id) FROM com.aizhixin.cloud.orgmanager.classschedule.entity.TempAdjustCourseSchedule t WHERE t.deleteFlag = :deleteFlag");
        if (null != semester) {
            hql.append(" AND t.semester = :semester");
            chql.append(" AND t.semester = :semester");
            qryParam.put("semester", semester);
        } else {
            if (null == orgId || orgId <= 0) {
                return p;
            }
            qryParam.put("orgId", orgId);
            hql.append(" AND t.orgId=:orgId");
            chql.append(" AND t.orgId=:orgId");

        }
        if (!StringUtils.isEmpty(teacher)) {
            hql.append(" AND (t.creatorNo like :teacher OR t.creatorName like :teacher)");
            chql.append(" AND (t.creatorNo like :teacher OR t.creatorName like :teacher)");
            qryParam.put("teacher", "%" + teacher + "%");
        }
        if (null != adjustType && adjustType > 0) {
            hql.append(" AND t.adjustType = :adjustType");
            chql.append(" AND t.adjustType = :adjustType");
            qryParam.put("adjustType", adjustType);
        }
        if (null != startDate) {
            hql.append(" AND t.createdDate >= :startDate");
            chql.append(" AND t.createdDate >= :startDate");
            qryParam.put("startDate", startDate);
        }
        if (null != endDate) {
            endDate = DateUtil.nextDate(endDate);
            endDate = DateUtil.getZerotime(endDate);
            hql.append(" AND t.createdDate < :endDate");
            chql.append(" AND t.createdDate < :endDate");
            qryParam.put("endDate", endDate);
        }
        hql.append(" order by t.id desc");

        Query q = em.createQuery(chql.toString());
        for (Map.Entry <String, Object> e : qryParam.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long) q.getSingleResult();
        if (count > 0) {
            TypedQuery<TempAdjustCourseListDomain> tq = em.createQuery(hql.toString(), TempAdjustCourseListDomain.class);
            for (Map.Entry<String, Object> e : qryParam.entrySet()) {
                tq.setParameter(e.getKey(), e.getValue());
            }
            tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            tq.setMaxResults(pageable.getPageSize());
            p.setData(tq.getResultList());
        }
        p.getPage().setTotalElements(count);
        p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageable.getPageSize()));
        return p;
    }
}
