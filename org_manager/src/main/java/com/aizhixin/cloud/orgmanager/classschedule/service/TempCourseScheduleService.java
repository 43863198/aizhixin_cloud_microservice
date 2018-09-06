package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.core.ApprovalStatus;
import com.aizhixin.cloud.orgmanager.classschedule.core.StopOrAddTempCourse;
import com.aizhixin.cloud.orgmanager.classschedule.core.ValidOrCancel;
import com.aizhixin.cloud.orgmanager.classschedule.domain.DianDianDaySchoolTimeTableDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TempAdjustCourseDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TempAdjustCourseFullDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TempAdjustCourseSchedule;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TempCourseSchedule;
import com.aizhixin.cloud.orgmanager.classschedule.repository.TempCourseScheduleRepository;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.entity.Week;
import com.aizhixin.cloud.orgmanager.company.service.PeriodService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import com.aizhixin.cloud.orgmanager.company.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Transactional
public class TempCourseScheduleService {
    @Autowired
    private TempCourseScheduleRepository tempCourseScheduleRepository;
    @Autowired
    private TempAdjustCourseScheduleService tempAdjustCourseScheduleService;
    @Autowired
    private TeachingClassService teachingClassService;
    @Autowired
    private SchoolTimeTableService schoolTimeTableService;
    @Autowired
    private TeachingClassTeacherService teachingClassTeacherService;
    @Autowired
    private DiandianSchoolTimeTableService diandianSchoolTimeTableService;
    @Autowired
    private WeekService weekService;
    @Autowired
    private UserService userService;
    @Autowired
    private PeriodService periodService;

    /**
     * 保存实体
     *
     * @param tempCourseSchedule
     * @return
     */
    public TempCourseSchedule save(TempCourseSchedule tempCourseSchedule) {
        return tempCourseScheduleRepository.save(tempCourseSchedule);
    }

    public List<TempCourseSchedule> save(List<TempCourseSchedule> tempCourseSchedule) {
        return tempCourseScheduleRepository.save(tempCourseSchedule);
    }

    @Transactional(readOnly = true)
    public TempCourseSchedule findById(Long id) {
        return tempCourseScheduleRepository.findOne(id);
    }

//    @Transactional(readOnly = true)
//    public List<TempCourseSchedule> findByAdjustId(Long adjustId) {
//        return tempCourseScheduleRepository.findByAdjustId(adjustId);
//    }

    @Transactional(readOnly = true)
    public List<TempCourseSchedule> findByTeachingclassesAndDateAndAdjustType(Set<TeachingClass> teachingclasses, String eventDate, Integer adjustType) {
        return tempCourseScheduleRepository.findByTeachingClassInAndEventDateAndAdjustTypeAndDeleteFlag(teachingclasses, eventDate, adjustType, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<TempCourseSchedule> findByTeachingclassIdsAndWeekNo(Set<Long> teachingclassIds, Integer weekNo) {
        return tempCourseScheduleRepository.findByTeachingClass_idInAndWeekNoAndDeleteFlag(teachingclassIds, weekNo, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<TempCourseSchedule> findByTeachingclassIdsAndWeekNoAndDayOfWeek(Set<Long> teachingclassIds, Integer weekNo, Integer dayOfWeek) {
        return tempCourseScheduleRepository.findByTeachingClass_idInAndWeekNoAndDayOfWeekAndDeleteFlag(teachingclassIds, weekNo, dayOfWeek, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<TempCourseSchedule> findByOrgIdAndWeekNoAndDayOfWeek(Long orgId, Semester semester, Integer weekNo, Integer dayOfWeek) {
        return tempCourseScheduleRepository.findByOrgIdAndSemesterAndWeekNoAndDayOfWeekAndDeleteFlag(orgId, semester, weekNo, dayOfWeek, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Set<Long> findTempAddCourseScheduleByTeachingclassIds(Set<Long> teachingclassIds) {
        return tempCourseScheduleRepository.findTeachingclassIdByTeachingClassIdsAndAdjustType(teachingclassIds, StopOrAddTempCourse.ADD.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public long countByTeachingclassesAndDateAndAdjustTypeAndPeriodNo(Set<TeachingClass> teachingclasses, String eventDate, Integer adjustType, Integer minPeriodNo, Integer maxPeriodNo) {
        return tempCourseScheduleRepository.countByTeachingClassInAndEventDateAndAdjustTypeAndDeleteFlag(teachingclasses, eventDate, adjustType, minPeriodNo, maxPeriodNo, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public long countByTeachingclassesAndDateAndAdjustTypeAndPeriod(Long teachingclassId, String eventDate, Integer adjustType, Integer periodNo, Integer periodNum) {
        return tempCourseScheduleRepository.countByTeachingClass_idAndEventDateAndAdjustTypeAndPeriodNoAndPeriodNumAndDeleteFlag(teachingclassId, eventDate, adjustType, periodNo, periodNum, DataValidity.VALID.getState());
    }
    @Transactional(readOnly = true)
    public List<TempCourseSchedule> findAddOrStopRecord(TeachingClass teachingclass, String eventDate, Integer adjustType, Integer periodNo, Integer periodNum) {
        return tempCourseScheduleRepository.findByTeachingClassAndEventDateAndAdjustTypeAndPeriodNoAndPeriodNumAndDeleteFlag(teachingclass, eventDate, adjustType, periodNo, periodNum, DataValidity.VALID.getState());
    }
    @Transactional(readOnly = true)
    public List<TempCourseSchedule> findAllTeachingclassAddOrStopRecord(Set<TeachingClass> teachingclasses, String eventDate, Integer adjustType, Integer periodNo, Integer periodNum) {
        return tempCourseScheduleRepository.findByAllTeachingClassAndEventDateAndAdjustTypeAndPeriodNoAndPeriodNum(teachingclasses, eventDate, adjustType, periodNo, periodNum, DataValidity.VALID.getState());
    }
    @Transactional(readOnly = true)
    public List<TempCourseSchedule> findByTeachingclass(List<TeachingClass> teachingclasses) {
        return tempCourseScheduleRepository.findByTeachingClassInAndDeleteFlag(teachingclasses, DataValidity.VALID.getState());
    }
    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//
    private TempAdjustCourseSchedule createAddCopy(TeachingClass teachingClass, TempAdjustCourseDomain tempAdjustCourseDomain, User teacher) {
        TempAdjustCourseSchedule tempAdjustCourseSchedule = new TempAdjustCourseSchedule();
        tempAdjustCourseSchedule.setOrgId(teachingClass.getOrgId());
        tempAdjustCourseSchedule.setSemester(teachingClass.getSemester());
        tempAdjustCourseSchedule.setTeachingClass(teachingClass);

        tempAdjustCourseSchedule.setValidStatus(ValidOrCancel.VALID.getState());
        tempAdjustCourseSchedule.setApprovalStatus(ApprovalStatus.PASS.getState());

        tempAdjustCourseSchedule.setWeekNo(tempAdjustCourseDomain.getWeekNo());
        tempAdjustCourseSchedule.setDayOfWeek(tempAdjustCourseDomain.getDayOfWeek());
        tempAdjustCourseSchedule.setEventDate(tempAdjustCourseDomain.getEventDate());
        tempAdjustCourseSchedule.setClassroom(tempAdjustCourseDomain.getClassroom());
        tempAdjustCourseSchedule.setPeriodNo(tempAdjustCourseDomain.getPeriodNo());
        tempAdjustCourseSchedule.setPeriodNum(tempAdjustCourseDomain.getPeriodNum());

        tempAdjustCourseSchedule.setLastModifiedBy(teacher.getId());
        tempAdjustCourseSchedule.setCreatedBy(teacher.getId());
        tempAdjustCourseSchedule.setCreatorNo(teacher.getJobNumber());
        tempAdjustCourseSchedule.setCreatorName(teacher.getName());
        return tempAdjustCourseSchedule;
    }

    private TempCourseSchedule createAddCopyTempCourseSchedule (TeachingClass teachingClass, TempAdjustCourseDomain tempAdjustCourseDomain, User teacher) {
        TempCourseSchedule tempCourseSchedule = new TempCourseSchedule();

        tempCourseSchedule.setOrgId(teachingClass.getOrgId());
        tempCourseSchedule.setSemester(teachingClass.getSemester());
        tempCourseSchedule.setTeachingClass(teachingClass);

        tempCourseSchedule.setWeekNo(tempAdjustCourseDomain.getWeekNo());
        tempCourseSchedule.setDayOfWeek(tempAdjustCourseDomain.getDayOfWeek());
        tempCourseSchedule.setEventDate(tempAdjustCourseDomain.getEventDate());
        tempCourseSchedule.setClassroom(tempAdjustCourseDomain.getClassroom());
        tempCourseSchedule.setPeriodNo(tempAdjustCourseDomain.getPeriodNo());
        tempCourseSchedule.setPeriodNum(tempAdjustCourseDomain.getPeriodNum());

        tempCourseSchedule.setLastModifiedBy(teacher.getId());
        tempCourseSchedule.setCreatedBy(teacher.getId());

        return tempCourseSchedule;
    }

    private TempAdjustCourseSchedule add(TempAdjustCourseDomain add, TeachingClass teachingClass, User teacher) {
        Date destDate = DateUtil.parse(add.getEventDate());
        if (null == destDate) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "目标日期格式不是yyyy-MM-dd");
        }
        if (null == add.getPeriodNo() || add.getPeriodNo() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节是必须的");
        }
        if (null == add.getPeriodNum() || add.getPeriodNum() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "持续节是必须的");
        }
        Week week = weekService.getWeekBySemesterAndDate(teachingClass.getSemester(), destDate);
        if (null == week) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有查找到对应的学周信息");
        }
        add.setWeekNo(week.getNo());
        add.setDayOfWeek(DateUtil.getDayOfWeek(destDate));
        long pc = periodService.countByNoAndOrgId(add.getPeriodNo(), teachingClass.getOrgId());
        if (pc <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节不存在");
        }
        if (add.getPeriodNum() > 1) {
            pc = periodService.countByNoAndOrgId(add.getPeriodNo() + add.getPeriodNum() - 1, teachingClass.getOrgId());
            if (pc <= 0) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节不存在");
            }
        }
        Set<TeachingClass> tcs = teachingClassTeacherService.findTeachingClassByTeacher(teachingClass.getSemester(), teacher);
        if (null != tcs && tcs.size() > 0) {
            //查找特定天的老师的课节范围内的所有教学班，还需进一步优化，查到具体的节信息
            List<DianDianDaySchoolTimeTableDomain>  tcs2 = schoolTimeTableService.findCourseScheduleInDayAndPeriodRange(tcs, add.getWeekNo(), add.getDayOfWeek(), add.getPeriodNo(), add.getPeriodNo() + add.getPeriodNum() - 1, diandianSchoolTimeTableService.getSingleOrDoublesCondition(add.getDayOfWeek()));
            if (tcs2.size() > 0) {//还需进一步判断冲突的课程是否有被临时调停
                /******************************总课程表相应位置的课程是否被停止，按照小节判断******************************/
                //逐个教学班课程节判断冲突的课表是否停掉
                long stopCount = 0;
                for (DianDianDaySchoolTimeTableDomain d : tcs2) {
                    stopCount = countByTeachingclassesAndDateAndAdjustTypeAndPeriod(d.getTeachingClassId(), add.getEventDate(), StopOrAddTempCourse.STOP.getState(), d.getPeriodNo(), d.getPeriodNum());
                    if (stopCount <= 0) {
                        throw new CommonException(ErrorCode.ID_IS_REQUIRED, "和总课程表数据有冲突");
                    }
                }


                /******************************临时课表相应的位置是否添加了新的数据，按照小节判断，范围匹配******************************/
                long addCount = countByTeachingclassesAndDateAndAdjustTypeAndPeriodNo(tcs, add.getEventDate(), StopOrAddTempCourse.ADD.getState(), add.getPeriodNo(), add.getPeriodNo() + add.getPeriodNum() - 1);
                if (addCount > 0) {
                    throw new CommonException(ErrorCode.ID_IS_REQUIRED, "和临时课程表数据有冲突");
                }
            } else {
                /******************************临时课表相应的位置是否添加了数据，按照小节判断，范围匹配******************************/
                long addCount = countByTeachingclassesAndDateAndAdjustTypeAndPeriodNo(tcs, add.getEventDate(), StopOrAddTempCourse.ADD.getState(), add.getPeriodNo(), add.getPeriodNo() + add.getPeriodNum() - 1);
                if (addCount > 0) {
                    throw new CommonException(ErrorCode.ID_IS_REQUIRED, "和临时课程表数据有冲突");
                }
            }
        }
        TempCourseSchedule tempCourseSchedule = createAddCopyTempCourseSchedule (teachingClass, add, teacher);
        tempCourseSchedule.setAdjustType(StopOrAddTempCourse.ADD.getState());
        TempAdjustCourseSchedule addTempAdjustCourseSchedule = createAddCopy(teachingClass, add, teacher);
        addTempAdjustCourseSchedule.setAdjustType(StopOrAddTempCourse.ADD.getState());
        addTempAdjustCourseSchedule.setTempCourseSchedule(tempCourseSchedule);
        return addTempAdjustCourseSchedule;
    }

    private TempAdjustCourseSchedule stop(TempAdjustCourseDomain stop, TeachingClass teachingClass, User teacher) {
        TempCourseSchedule tempCourseSchedule = null;
//        if (null != stop.getTempCourseScheduleId() && stop.getTempCourseScheduleId() > 0) {//暂时无用
//            tempCourseSchedule = findById(stop.getTempCourseScheduleId());
//            if (null == tempCourseSchedule) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据临时程表ID没有查找到对应的数据");
//            }
//            if (StopOrAddTempCourse.ADD.getState().intValue() != tempCourseSchedule.getAdjustType()) {
//                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "只能停止临时添加的课程");
//            }
//            tempCourseSchedule.setDeleteFlag(DataValidity.INVALID.getState());//精确停止临时添加的课表
//        } else {
        long c = schoolTimeTableService.countByTeachingClassAndWeekAndDayOfWeek(teachingClass, stop.getWeekNo(), stop.getDayOfWeek(), diandianSchoolTimeTableService.getSingleOrDoublesCondition(stop.getWeekNo()), stop.getPeriodNo(), stop.getPeriodNum());
        if (c <= 0) {//停止临时添加的课表，但需要查找临时添加的课表数据，临时课表是具体的，毕业完全匹配(教学班、时间、课程节、类型)
            List<TempCourseSchedule> tlist = findAddOrStopRecord(teachingClass, stop.getEventDate(), StopOrAddTempCourse.ADD.getState(), stop.getPeriodNo(), stop.getPeriodNum());
            if (null == tlist || tlist.size() <= 0) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "查找不到临时课表数据");
            }
            if (tlist.size() > 1) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "查找临时课表的数量多于传入的数据");
            }
            tempCourseSchedule = tlist.get(0);
            tempCourseSchedule.setDeleteFlag(DataValidity.INVALID.getState());
        } else {//停止总课表，总课表是抽象的，直接添加停止记录
            //如果临时课表有数据，优先停止临时课表
            List<TempCourseSchedule> tlist = findAddOrStopRecord(teachingClass, stop.getEventDate(), StopOrAddTempCourse.ADD.getState(), stop.getPeriodNo(), stop.getPeriodNum());
            if (null != tlist && tlist.size() > 0) {
                tempCourseSchedule = tlist.get(0);
                tempCourseSchedule.setDeleteFlag(DataValidity.INVALID.getState());
            } else {
                tempCourseSchedule = createAddCopyTempCourseSchedule(teachingClass, stop, teacher);
                tempCourseSchedule.setAdjustType(StopOrAddTempCourse.STOP.getState());
            }
        }
        TempAdjustCourseSchedule stopTempAdjustCourseSchedule = createAddCopy(teachingClass, stop, teacher);
        stopTempAdjustCourseSchedule.setAdjustType(StopOrAddTempCourse.STOP.getState());
        stopTempAdjustCourseSchedule.setTempCourseSchedule(tempCourseSchedule);
        return stopTempAdjustCourseSchedule;
    }

    /**
     * 新增临时调课记录
     * @param tac
     * @return
     */
    public TempAdjustCourseSchedule saveTempAdjustCourseSchedule (TempAdjustCourseFullDomain tac) {
        if (null == tac.getTeachingClassId() || tac.getTeachingClassId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID是必须的");
        }
        TeachingClass teachingClass = teachingClassService.findById(tac.getTeachingClassId());
        if (null == teachingClass) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班是必须的");
        }
        if (null == tac.getUserId() || tac.getUserId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "操作人ID是必须的");
        }
        User teacher = userService.findById(tac.getUserId());
        if (null == teacher) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "操作人是必须的");
        }

        TempAdjustCourseSchedule tempAdjustCourseSchedule = null;
        TempAdjustCourseSchedule addTempAdjustCourseSchedule = null;
        TempAdjustCourseSchedule stopTempAdjustCourseSchedule = null;

        TempAdjustCourseDomain add = tac.getAddTempAdjustCourseDomain();
        TempAdjustCourseDomain stop = tac.getDeleteTempCourseDomain();

        if (null != add && null != add.getEventDate() && null != stop && null != stop.getEventDate()) {//调课
            addTempAdjustCourseSchedule = add(add, teachingClass, teacher);
            TempCourseSchedule addTemp = null;
            TempCourseSchedule stopTemp = null;
            if (null != addTempAdjustCourseSchedule.getTempCourseSchedule()) {
                addTemp = save(addTempAdjustCourseSchedule.getTempCourseSchedule());
            }
            tempAdjustCourseScheduleService.save(addTempAdjustCourseSchedule);
            stopTempAdjustCourseSchedule = stop(stop, teachingClass, teacher);
            if (null != stopTempAdjustCourseSchedule.getTempCourseSchedule()) {
                if (null != addTemp) {
                    stopTempAdjustCourseSchedule.getTempCourseSchedule().setAdjustId(addTemp.getId());
                }
                stopTemp = save(stopTempAdjustCourseSchedule.getTempCourseSchedule());
            }
            if (null != addTemp && null != stopTemp) {
                addTemp.setAdjustId(stopTemp.getId());
                save(addTemp);
            }
            tempAdjustCourseSchedule = tempAdjustCourseScheduleService.save(stopTempAdjustCourseSchedule);
        }  else if (null != add && null != add.getEventDate()) {//---------------------------------------增加
            addTempAdjustCourseSchedule = add(add, teachingClass, teacher);
            if (null != addTempAdjustCourseSchedule.getTempCourseSchedule()) {
                save(addTempAdjustCourseSchedule.getTempCourseSchedule());
            }
            tempAdjustCourseSchedule = tempAdjustCourseScheduleService.save(addTempAdjustCourseSchedule);
        } else if (null != stop && null != stop.getEventDate()) {//--------------------------------------停课
            stopTempAdjustCourseSchedule = stop(stop, teachingClass, teacher);
            if (null != stopTempAdjustCourseSchedule.getTempCourseSchedule()) {
                save(stopTempAdjustCourseSchedule.getTempCourseSchedule());
            }
            tempAdjustCourseSchedule = tempAdjustCourseScheduleService.save(stopTempAdjustCourseSchedule);
        } else {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "添加或停止课程的信息最少需要有一个");
        }
        return tempAdjustCourseSchedule;
    }

    public void deleteByTeacherClass(List<TeachingClass> teachingclasses) {
        List<TempCourseSchedule> list = findByTeachingclass(teachingclasses);
        if (null != list && !list.isEmpty()) {
            Date current = new Date ();
            for (TempCourseSchedule tempCourseSchedule : list) {
                tempCourseSchedule.setDeleteFlag(DataValidity.INVALID.getState());
                tempCourseSchedule.setLastModifiedDate(current);
            }
            save(list);
        }
    }
}