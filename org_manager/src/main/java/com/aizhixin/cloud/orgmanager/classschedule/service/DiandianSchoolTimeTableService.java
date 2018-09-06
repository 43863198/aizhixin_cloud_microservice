package com.aizhixin.cloud.orgmanager.classschedule.service;

import com.aizhixin.cloud.orgmanager.classschedule.core.ClassesOrStudents;
import com.aizhixin.cloud.orgmanager.classschedule.core.SingleOrDouble;
import com.aizhixin.cloud.orgmanager.classschedule.core.StopOrAddTempCourse;
import com.aizhixin.cloud.orgmanager.classschedule.domain.*;
import com.aizhixin.cloud.orgmanager.classschedule.entity.*;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.core.UserType;
import com.aizhixin.cloud.orgmanager.company.domain.StudentDomain;
import com.aizhixin.cloud.orgmanager.company.entity.*;
import com.aizhixin.cloud.orgmanager.company.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 点点课表相关实现逻辑 Created by zhen.pan on 2017/5/10.
 */
@Component
@Transactional
public class DiandianSchoolTimeTableService {
    @Autowired
    private SchoolTimeTableService schoolTimeTableService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private TeachingClassService teachingClassService;
    @Autowired
    private WeekService weekService;
    @Autowired
    private TeachingClassTeacherService teachingClassTeacherService;
    @Autowired
    private TeachingClassStudentsService teachingClassStudentsService;
    @Autowired
    private TeachingClassClassesService teachingClassClassesService;
    @Autowired
    private UserService userService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private ClassesTeacherService classesTeacherService;
    @Autowired
    private TempAdjustCourseScheduleService tempAdjustCourseScheduleService;
    @Autowired
    private TempCourseScheduleService tempCourseScheduleService;
    @Autowired
    private SchoolHolidayService schoolHolidayService;
    @Autowired
    private SchoolUnifyAdjustCourseService schoolUnifyAdjustCourseService;

    public Set<Integer> getSingleOrDoublesCondition(int weekNo) {
        Set<Integer> singleOrDoubles = new HashSet<>();
        singleOrDoubles.add(SingleOrDouble.ALL.getState());// 不区分单双周
        if (0 == weekNo % 2) {// 双周
            singleOrDoubles.add(SingleOrDouble.DOUBLE.getState());
        } else {// 单周
            singleOrDoubles.add(SingleOrDouble.SINGLE.getState());
        }
        return singleOrDoubles;
    }

    /**
     * 特定学校某学期某一天的所有课表
     * 
     * @param orgId
     * @param semesterId
     * @param teachDate
     * @return
     */
    @Transactional(readOnly = true)
    public List<DianDianSchoolTimeDomain> findSchoolTimeDay(Long orgId, Long semesterId, Date teachDate) {
        List<DianDianSchoolTimeDomain> ddss = new ArrayList<>();
        Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(orgId, semesterId, teachDate);
        SchoolUnifyAdjustCourseSchedule schedule = null;
        Week destWeek = null;
        if (null != semester) {
            // 2017-09-29增加整体调课和节假日功能
            // 首先处理节假日（节假日不返回任何课表数据）
            List<SchoolHoliday> holidays = schoolHolidayService.findBySemesterAndDate(semester, DateUtil.format(teachDate));
            if (null != holidays && holidays.size() > 0) {
                for (SchoolHoliday h : holidays) {
                    System.out.println("Match holiday orgId:" + orgId + "\t startday:" + h.getStartDate() + "\t endDay:" + h.getEndDate());
                }
                return ddss;
            }
            // 处理整体调课
            List<SchoolUnifyAdjustCourseSchedule> adjustCourseSchedules = schoolUnifyAdjustCourseService.findBySemesterAndDate(semester, DateUtil.format(teachDate));
            if (null != adjustCourseSchedules && adjustCourseSchedules.size() > 0) {
                schedule = adjustCourseSchedules.get(0);
                destWeek = weekService.getWeekBySemesterAndDate(semester, teachDate);
                teachDate = DateUtil.parse(schedule.getSrcDate());// 将参数日期的时间修改为源时间
            }

            Week week = weekService.getWeekBySemesterAndDate(semester, teachDate);
            if (null != week) {
                if (null != week.getNo() && week.getNo() > 0) {
                    // 填充课程节相关信息
                    Integer dayOfWeek = DateUtil.getDayOfWeek(teachDate);
                    ddss = schoolTimeTableService.findDianDianDaySchoolTime(semester, week.getNo(), dayOfWeek, getSingleOrDoublesCondition(week.getNo()));
                    Map<Long, List<DianDianSchoolTimeDomain>> cache = new HashMap<>();
                    Set<Long> teachingClassIds = new HashSet<>();
                    // 填充周信息
                    for (DianDianSchoolTimeDomain d : ddss) {
                        if (null != d.getTeachingClassId() && d.getTeachingClassId() > 0) {
                            List<DianDianSchoolTimeDomain> sl = cache.get(d.getTeachingClassId());
                            if (null == sl) {
                                cache.put(d.getTeachingClassId(), new ArrayList<>());
                            }
                            cache.get(d.getTeachingClassId()).add(d);
                            teachingClassIds.add(d.getTeachingClassId());
                        }
                        d.setWeekId(week.getId());
                        d.setWeekName(week.getName());
                        d.setWeekNo(week.getNo());
                        d.setDayOfWeek(dayOfWeek);
                    }

                    // 调停课
                    List<Period> aps = periodService.findByOrgId(orgId);
                    Map<Integer, Period> prd = new HashMap<>();
                    for (Period p : aps) {
                        prd.put(p.getNo(), p);
                    }
                    List<DianDianSchoolTimeDomain> add = new ArrayList<>();
                    List<TempCourseSchedule> tempCourseScheduleList = tempCourseScheduleService.findByOrgIdAndWeekNoAndDayOfWeek(orgId, semester, week.getNo(), dayOfWeek);
                    Set<String> deleteSet = new HashSet<>();
                    if (null != tempCourseScheduleList && tempCourseScheduleList.size() > 0) {// 有临时调课数据
                        for (TempCourseSchedule t : tempCourseScheduleList) {
                            TeachingClass teachingClass = t.getTeachingClass();
                            Period p = prd.get(t.getPeriodNo());
                            if (StopOrAddTempCourse.ADD.getState().intValue() == t.getAdjustType() && null != p && null != teachingClass) {
                                DianDianSchoolTimeDomain d = new DianDianSchoolTimeDomain(teachingClass.getId(), p.getId(), p.getNo(), t.getPeriodNum(), t.getClassroom());
                                d.setWeekId(week.getId());
                                d.setWeekName(week.getName());
                                d.setWeekNo(week.getNo());
                                d.setDayOfWeek(dayOfWeek);
                                add.add(d);

                                List<DianDianSchoolTimeDomain> sl = cache.get(d.getTeachingClassId());
                                if (null == sl) {
                                    cache.put(d.getTeachingClassId(), new ArrayList<>());
                                }
                                cache.get(d.getTeachingClassId()).add(d);
                                teachingClassIds.add(d.getTeachingClassId());
                            }
                            if (StopOrAddTempCourse.STOP.getState().intValue() == t.getAdjustType() && null != p && null != teachingClass) {
                                deleteSet.add(teachingClass.getId() + "-" + week.getId() + "-" + t.getDayOfWeek() + "-" + p.getId() + "-" + t.getPeriodNum());
                            }
                        }
                        if (deleteSet.size() > 0) {
                            List<DianDianSchoolTimeDomain> r2 = new ArrayList<>();
                            for (DianDianSchoolTimeDomain d : ddss) {
                                if (!deleteSet.contains(d.getTeachingClassId() + "-" + d.getWeekId() + "-" + d.getDayOfWeek() + "-" + d.getPeriodId() + "-" + d.getPeriodNum())) {
                                    r2.add(d);
                                }
                            }
                            ddss = r2;
                        }
                        if (add.size() > 0) {
                            ddss.addAll(add);
                        }
                    }

                    if (teachingClassIds.size() > 0) {
                        // 填充教学班信息
                        List<TeachingClassDomain> tcs = teachingClassService.findByIds(teachingClassIds);
                        for (TeachingClassDomain t : tcs) {
                            List<DianDianSchoolTimeDomain> ds = cache.get(t.getId());
                            if (null != ds) {
                                for (DianDianSchoolTimeDomain d : ds) {
                                    d.setTeachingClassName(t.getName());
                                    d.setTeachingClassCode(t.getCode());
                                    d.setSemesterId(t.getSemesterId());
                                    // if (null != semester.getYear()) {
                                    // d.setSemesterName((null == semester.getYear().getName() ? "" : semester.getYear().getName() + " ") + t.getSemesterName());
                                    // } else {
                                    // d.setSemesterName(t.getSemesterName());
                                    // }
                                    d.setSemesterName(t.getSemesterName());
                                    d.setCourseId(t.getCourseId());
                                    d.setCourseName(t.getCourseName());
                                    d.setClassOrStudents(t.getClassOrStudents());
                                }
                            }
                        }

                        // 填充老师信息
                        Map<Long, String> tchs = teachingClassTeacherService.getMutilTeachingClassTeachers(teachingClassIds);
                        for (DianDianSchoolTimeDomain d : ddss) {
                            d.setTeachers(tchs.get(d.getTeachingClassId()));
                        }
                    }
                }
            }
        }
        // 处理整体调课（替换学周相关数据）
        if (null != schedule) {
            for (DianDianSchoolTimeDomain d : ddss) {
                d.setDayOfWeek(schedule.getDestDayOfWeek());
                if (null != destWeek) {
                    d.setWeekNo(destWeek.getNo());
                    d.setWeekId(destWeek.getId());
                }
            }
        }
        return ddss;
    }

    private List<DianDianWeekSchoolTimeTableDomain> fillWeekSchoolTimeTable(Set<Long> teachingClassIds, Semester semester, Week week, User teacher) {
        List<DianDianWeekSchoolTimeTableDomain> list = new ArrayList<>();
        List<DianDianWeekSchoolTimeTableDomain> r = new ArrayList<>();

        if (teachingClassIds.size() <= 0) {
            return r;
        }
        list = schoolTimeTableService.findDianDianWeekSchoolTime(teachingClassIds, week.getNo(), getSingleOrDoublesCondition(week.getNo()));
        List<TempCourseSchedule> tempCourseScheduleList = tempCourseScheduleService.findByTeachingclassIdsAndWeekNo(teachingClassIds, week.getNo());
        if ((null == list || list.size() <= 0) && (null == tempCourseScheduleList ||tempCourseScheduleList.size() <= 0)) {
            return r;
        }

        // 课程名称填充
        Map<Long, String> tcCouses = teachingClassService.getCourseNameByIds(teachingClassIds);
        // 课程节信息填充//周内每天时间计算填充
        Map<Integer, Date> wds = DateUtil.getWeekAllDate(week.getStartDate());
        List<Period> aps = periodService.findByOrgId(semester.getOrgId());
        Map<Long, Period> pr = new HashMap<>();
        Map<Integer, Period> prd = new HashMap<>();
        for (Period p : aps) {
            prd.put(p.getNo(), p);
            pr.put(p.getId(), p);
        }

        Set<String> deleteSet = new HashSet<>();
        List<DianDianWeekSchoolTimeTableDomain> addResult = new ArrayList<>();
        if (null != tempCourseScheduleList && tempCourseScheduleList.size() > 0) {
            for (TempCourseSchedule t : tempCourseScheduleList) {
                TeachingClass teachingClass = t.getTeachingClass();
                Period p = prd.get(t.getPeriodNo());
                if (null == p || null == teachingClass) {
                    continue;
                }
                Period pe2 = null;
                if (t.getPeriodNum() > 1) {
                    pe2 = prd.get(t.getPeriodNo() + t.getPeriodNum() - 1);
                } else {
                    pe2 = p;
                }
                if (null == pe2) {
                    continue;
                }
                if (StopOrAddTempCourse.ADD.getState().intValue() == t.getAdjustType()) {
                    DianDianWeekSchoolTimeTableDomain d = new DianDianWeekSchoolTimeTableDomain(teachingClass.getId(), teachingClass.getName(), teachingClass.getCode(),
                            week.getId(), p.getId(), t.getPeriodNum(), t.getDayOfWeek(), t.getClassroom());
                    d.setPeriodNo(p.getNo());
                    d.setClassroom(t.getClassroom());
                    d.setPeriodStarttime(p.getStartTime());
                    d.setPeriodEndtime(pe2.getEndTime());

                    d.setTeachDate(wds.get(d.getDayOfWeek()));
                    d.setCourseName(tcCouses.get(d.getTeachingClassId()));
                    if (null != teacher) {
                        d.setTeacherId(teacher.getId());
                        d.setTeacherName(teacher.getName());
                    }
                    addResult.add(d);
                }
                if (StopOrAddTempCourse.STOP.getState().intValue() == t.getAdjustType()) {
                    deleteSet.add(teachingClass.getId() + "-" + week.getId() + "-" + t.getDayOfWeek() + "-" + p.getId() + "-" + t.getPeriodNum());
                }
            }
            if (addResult.size() > 0) {
                r.addAll(addResult);
            }
        }

        if (null != list && list.size() > 0) {
            // 过滤周时间范围外的数据
            Date startDate = week.getStartDate();
            Date endDate = week.getEndDate();
            int startDayOfWeek = DateUtil.getDayOfWeek(startDate);
            int endDayOfWeek = DateUtil.getDayOfWeek(endDate);

            for (DianDianWeekSchoolTimeTableDomain d : list) {
                if (d.getDayOfWeek() >= startDayOfWeek && d.getDayOfWeek() <= endDayOfWeek) {
                    r.add(d);
                }
            }
            if (r.isEmpty()) {
                return r;
            }
            for (DianDianWeekSchoolTimeTableDomain d : r) {
                d.setCourseName(tcCouses.get(d.getTeachingClassId()));
                d.setWeekId(week.getId());
                if (null != teacher) {
                    d.setTeacherId(teacher.getId());
                    d.setTeacherName(teacher.getName());
                }
            }
            Period p2 = null;
            for (DianDianWeekSchoolTimeTableDomain d : r) {
                Period p = pr.get(d.getPeriodId());
                d.setPeriodNo(p.getNo());
                d.setPeriodStarttime(p.getStartTime());
                p2 = prd.get(d.getPeriodNo() + d.getPeriodNum() - 1);
                if (null != p2) {
                    d.setPeriodEndtime(p2.getEndTime());
                }
                d.setTeachDate(wds.get(d.getDayOfWeek()));
            }
        }

        if (deleteSet.size() > 0) {
            List<DianDianWeekSchoolTimeTableDomain> r2 = new ArrayList<>();
            for (DianDianWeekSchoolTimeTableDomain d : r) {
                if (!deleteSet.contains(d.getTeachingClassId() + "-" + d.getWeekId() + "-" + d.getDayOfWeek() + "-" + d.getPeriodId() + "-" + d.getPeriodNum())) {
                    r2.add(d);
                }
            }
            r = r2;
        }
        return r;
    }

    @Transactional(readOnly = true)
    public List<DianDianWeekSchoolTimeTableDomain> findStudentWeekSchoolTimeTable(Long weekId, Long studentId) {
        List<DianDianWeekSchoolTimeTableDomain> r = new ArrayList<>();
        User student = userService.findById(studentId);
        if (UserType.B_STUDENT.getState().intValue() != student.getUserType()) {
            return r;
        }
        Week week = weekService.findById(weekId);
        Semester semester = null;
        if (null != week) {
            semester = week.getSemester();
        } else {
            return r;
        }
        if (null == semester) {
            return r;
        }
        Set<Long> teachingClassIds = teachingClassStudentsService.getStudentTeachingClassIds(semester, student);
        r = fillWeekSchoolTimeTable(teachingClassIds, semester, week, null);
        if (null != teachingClassIds && teachingClassIds.size() > 0) {
            List<IdIdNameDomain> idIdNameDomainList = teachingClassTeacherService.findTeacherByTeachingClassIds(teachingClassIds);
            Map<Long, IdIdNameDomain> teachingTeacherMap = new HashMap<>();
            for (IdIdNameDomain t : idIdNameDomainList) {
                if (!teachingTeacherMap.containsKey(t.getLogicId())) {
                    teachingTeacherMap.put(t.getLogicId(), t);
                }
            }
            for (DianDianWeekSchoolTimeTableDomain d : r) {
                IdIdNameDomain t = teachingTeacherMap.get(d.getTeachingClassId());
                if (null != t) {
                    d.setTeacherId(t.getId());
                    d.setTeacherName(t.getName());
                }
            }
        }
        return r;
    }

    @Transactional(readOnly = true)
    public List<DianDianWeekSchoolTimeTableDomain> findTeacherWeekSchoolTimeTable(Long weekId, Long teacherId) {
        List<DianDianWeekSchoolTimeTableDomain> r = new ArrayList<>();
        User teacher = userService.findById(teacherId);
        if (UserType.B_TEACHER.getState().intValue() != teacher.getUserType()) {
            return r;
        }
        Week week = weekService.findById(weekId);
        Semester semester = null;
        if (null != week) {
            semester = week.getSemester();
        } else {
            return r;
        }
        if (null == semester) {
            return r;
        }
        Set<Long> teachingClassIds = teachingClassTeacherService.findTeachingClassIdsByTeacher(semester, teacher);
        r = fillWeekSchoolTimeTable(teachingClassIds, semester, week, teacher);

        if (null != r) {
            if (null != teachingClassIds && teachingClassIds.size() > 0) {
                List<IdIdNameDomain> ts = teachingClassTeacherService.findTeacherByTeachingClassIds(teachingClassIds);
                Map<Long, List<IdNameDomain>> tts = new HashMap<>();
                for (IdIdNameDomain d : ts) {
                    List<IdNameDomain> list = tts.get(d.getLogicId());
                    if (null == list) {
                        list = new ArrayList<>();
                        tts.put(d.getLogicId(), list);
                    }
                    IdNameDomain t = new IdNameDomain(d.getId(), d.getName());
                    list.add(t);
                }
                for (DianDianWeekSchoolTimeTableDomain d : r) {
                    List<IdNameDomain> list = tts.get(d.getTeachingClassId());
                    if (null != list) {
                        d.setTeachers(list);
                    }
                }
            }
        }
        return r;
    }

    @Transactional(readOnly = true)
    public List<DianDianDaySchoolTimeTableDomain> findStudentDaySchoolTimeTable(Long studentId, Date date) {
        List<DianDianDaySchoolTimeTableDomain> r = new ArrayList<>();
        User student = userService.findById(studentId);
        if (UserType.B_STUDENT.getState().intValue() != student.getUserType()) {
            return r;
        }
        if (null == date) {
            date = new Date();
        }
        date = DateUtil.getZerotime(date);
        Integer dayOfWeek = DateUtil.getDayOfWeek(date);
        Semester semester = semesterService.getSemesterByDate(student.getOrgId(), date);
        if (null == semester) {
            return r;
        }
        Week week = weekService.getWeekBySemesterAndDate(semester, date);
        if (null == week) {
            return r;
        }
        Set<Long> teachingClassIds = teachingClassStudentsService.getStudentTeachingClassIds(semester, student);
        if (teachingClassIds.size() > 0) {
            r = schoolTimeTableService.findDianDianDaySchoolTime(teachingClassIds, semester, week.getNo(), dayOfWeek, getSingleOrDoublesCondition(week.getNo()));
            // 课程名称填充
            Map<Long, IdNameDomain> tcCouses = teachingClassService.getCourseIdNameByIds(teachingClassIds);// 填充老师信息
            Map<Long, String> tchs = teachingClassTeacherService.getMutilTeachingClassTeachers(teachingClassIds);
            for (DianDianDaySchoolTimeTableDomain d : r) {
                d.setTeachDate(date);
                d.setWeekId(week.getId());
                d.setSemesterId(semester.getId());
                d.setSemesterName(semester.getName());
                IdNameDomain n = tcCouses.get(d.getTeachingClassId());
                if (null != n) {
                    d.setCourseId(n.getId());
                    d.setCourseName(n.getName());
                }
                d.setTeachers(tchs.get(d.getTeachingClassId()));
            }
            // 课程节信息填充//周内每天时间计算填充
            Map<Long, Period> pr = new HashMap<>();
            Map<Integer, Period> prd = new HashMap<>();
            List<Period> aps = periodService.findByOrgId(semester.getOrgId());
            for (Period p : aps) {
                prd.put(p.getNo(), p);
                pr.put(p.getId(), p);
            }
            Period p2 = null;
            for (DianDianDaySchoolTimeTableDomain d : r) {
                Period p = pr.get(d.getPeriodId());
                d.setPeriodId(d.getPeriodId());
                d.setPeriodNo(p.getNo());
                d.setPeriodStarttime(p.getStartTime());
                p2 = prd.get(d.getPeriodNo() + d.getPeriodNum() - 1);
                if (null != p2) {
                    d.setPeriodEndtime(p2.getEndTime());
                }
            }
            // List<TempAdjustCourseSchedule> tempAdjustCourseScheduleList = tempAdjustCourseScheduleService.findByTeachingclassIdsAndWeekNoAndDayOfWeek(teachingClassIds,
            // week.getNo(), dayOfWeek);
            List<DianDianDaySchoolTimeTableDomain> add = new ArrayList<>();
            List<TempCourseSchedule> tempCourseScheduleList = tempCourseScheduleService.findByTeachingclassIdsAndWeekNoAndDayOfWeek(teachingClassIds, week.getNo(), dayOfWeek);
            Set<String> deleteSet = new HashSet<>();
            if (null != tempCourseScheduleList && tempCourseScheduleList.size() > 0) {// 有临时调课数据
                for (TempCourseSchedule t : tempCourseScheduleList) {
                    TeachingClass teachingClass = t.getTeachingClass();
                    Period p = prd.get(t.getPeriodNo());
                    Period pe2 = null;
                    if (t.getPeriodNum() > 1) {
                        pe2 = prd.get(t.getPeriodNo() + t.getPeriodNum() - 1);
                    } else {
                        pe2 = p;
                    }
                    if (StopOrAddTempCourse.ADD.getState().intValue() == t.getAdjustType() && null != p && null != teachingClass) {
                        DianDianDaySchoolTimeTableDomain d
                            = new DianDianDaySchoolTimeTableDomain(teachingClass.getId(), week.getId(), p.getId(), t.getDayOfWeek(), t.getPeriodNum(), t.getClassroom());
                        d.setPeriodNo(p.getNo());
                        d.setPeriodStarttime(p.getStartTime());
                        d.setPeriodEndtime(pe2.getEndTime());
                        d.setTeachDate(date);
                        d.setSemesterId(semester.getId());
                        d.setSemesterName(semester.getName());
                        IdNameDomain n = tcCouses.get(d.getTeachingClassId());
                        if (null != n) {
                            d.setCourseId(n.getId());
                            d.setCourseName(n.getName());
                        }
                        d.setTeachers(tchs.get(d.getTeachingClassId()));
                        r.add(d);
                    }
                    if (StopOrAddTempCourse.STOP.getState().intValue() == t.getAdjustType() && null != p && null != teachingClass) {
                        deleteSet.add(teachingClass.getId() + "-" + week.getId() + "-" + t.getDayOfWeek() + "-" + p.getId() + "-" + t.getPeriodNum());
                    }
                }
                if (deleteSet.size() > 0) {
                    List<DianDianDaySchoolTimeTableDomain> r2 = new ArrayList<>();
                    for (DianDianDaySchoolTimeTableDomain d : r) {
                        if (!deleteSet.contains(d.getTeachingClassId() + "-" + d.getWeekId() + "-" + d.getDayOfWeek() + "-" + d.getPeriodId() + "-" + d.getPeriodNum())) {
                            r2.add(d);
                        }
                    }
                    r = r2;
                }
                if (add.size() > 0) {
                    r.addAll(add);
                }
            }
        }
        return r;
    }

    @Transactional(readOnly = true)
    public Set<Long> findStudentDayTeachingClassId(Long studentId, Date date) {
        Set<Long> r = new HashSet<>();
        User student = userService.findById(studentId);
        if (UserType.B_STUDENT.getState().intValue() != student.getUserType()) {
            return r;
        }
        if (null == date) {
            date = new Date();
        }
        date = DateUtil.getZerotime(date);
        Semester semester = semesterService.getSemesterByDate(student.getOrgId(), date);
        if (null == semester) {
            return r;
        }
        Week week = weekService.getWeekBySemesterAndDate(semester, date);
        if (null == week) {
            return r;
        }
        return teachingClassStudentsService.getStudentTeachingClassIds(semester, student);
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> findAllStudentByTeacher(Long teacherId, Long semesterId, String name) {
        List<StudentDomain> r = new ArrayList<>();
        User teacher = userService.findById(teacherId);
        if (UserType.B_TEACHER.getState().intValue() != teacher.getUserType()) {
            return r;
        }
        Semester semester = null;
        if (null != semesterId && semesterId > 0) {
            semester = semesterService.findById(semesterId);
        } else {
            Date date = new Date();
            date = DateUtil.getZerotime(date);
            semester = semesterService.getSemesterByDate(teacher.getOrgId(), date);
        }
        if (null == semester) {
            return r;
        }
        Set<Long> cids = new HashSet<>();
        List<Classes> cs = classesTeacherService.findClassesByTeacher(teacher);
        if (cs.size() > 0) {// 班主任所带班级的查询
            for (Classes c : cs) {
                cids.add(c.getId());
            }
        }
        Set<TeachingClass> tc = teachingClassTeacherService.findTeachingClassByTeacher(semester, teacher);
        List<TeachingClass> ts = new ArrayList<>();
        Set<Long> tids = new HashSet<>();
        for (TeachingClass t : tc) {// 老师所带课程所有班级的添加，进行去重操作
            if (ClassesOrStudents.CLASSES.getState().intValue() == t.getClassOrStudents()) {
                List<Classes> tts = teachingClassClassesService.findClassesByTeachingClass(t);
                for (Classes c : tts) {
                    if (!cids.contains(c.getId())) {
                        cids.add(c.getId());
                        cs.add(c);
                    }
                }
            } else {
                if (!tids.contains(t.getId())) {
                    tids.add(t.getId());
                    ts.add(t);
                }
            }
        }
        if (cs.size() > 0) {// 班主任所带班级及教学班按照班级组织学生的班级列表(已经过去重操作)
            r = userService.findStudentByClassesInAndName(cs, name);
        }
        if (ts.size() > 0) {// 选修课教学班包含的学生
            List<StudentDomain> t = null;
            if (cs.size() > 0) {
                t = teachingClassStudentsService.findTeachStudentByTeachingClassInAndClassesNotIn(ts, cs, name);
            } else {
                t = teachingClassStudentsService.findTeachStudentByTeachingClassIn(ts, name);
            }
            r.addAll(t);
        }
        return r;
    }
    // public boolean validateTempAdjustCourseSchedule (TempAdjustCourseScheudleDomain tempAdjustCourseScheudleDomain) {
    // if (null == tempAdjustCourseScheudleDomain.getOrgId() || tempAdjustCourseScheudleDomain.getOrgId() <= 0) {
    // throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学校ID是必须的");
    // }
    // if (null == tempAdjustCourseScheudleDomain.getSemesterId() || tempAdjustCourseScheudleDomain.getSemesterId() <= 0) {
    // throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学期ID是必须的");
    // }
    // Semester semester = semesterService.findById(tempAdjustCourseScheudleDomain.getSemesterId());
    // if (null == semester) {
    // throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据学期ID没有查找到对象的信息");
    // }
    // if (semester.getNumWeek() != tempAdjustCourseScheudleDomain.getOrgId().longValue()) {
    // throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据学期信息和学校信息不相符合");
    // }
    // if (null == tempAdjustCourseScheudleDomain.getWeekNo() || tempAdjustCourseScheudleDomain.getWeekNo() <= 0 || tempAdjustCourseScheudleDomain.getWeekNo() > 100000) {
    // throw new CommonException(ErrorCode.ID_IS_REQUIRED, "第几周是必须的有效值");
    // }
    // if (null == tempAdjustCourseScheudleDomain.getDayOfWeek() || tempAdjustCourseScheudleDomain.getDayOfWeek() <= 0 || tempAdjustCourseScheudleDomain.getDayOfWeek() > 7) {
    // throw new CommonException(ErrorCode.ID_IS_REQUIRED, "星期几是必须的有效值");
    // }
    // if (null == tempAdjustCourseScheudleDomain.getPeriodNo() || tempAdjustCourseScheudleDomain.getPeriodNo() <= 0 || tempAdjustCourseScheudleDomain.getPeriodNo() > 50) {
    // throw new CommonException(ErrorCode.ID_IS_REQUIRED, "第几节是必须的有效值");
    // }
    // if (null == tempAdjustCourseScheudleDomain.getPeriodNum() || tempAdjustCourseScheudleDomain.getPeriodNum() <= 0 || tempAdjustCourseScheudleDomain.getPeriodNum() > 50) {
    // throw new CommonException(ErrorCode.ID_IS_REQUIRED, "持续节是必须的有效值");
    // }
    // long c = schoolTimeTableService.countByTempCourseTime(semester, tempAdjustCourseScheudleDomain.getWeekNo(), tempAdjustCourseScheudleDomain.getDayOfWeek(),
    // tempAdjustCourseScheudleDomain.getPeriodNo(), tempAdjustCourseScheudleDomain.getPeriodNo() + tempAdjustCourseScheudleDomain.getPeriodNum() - 1,
    // getSingleOrDoublesCondition(tempAdjustCourseScheudleDomain.getDayOfWeek()));
    // if (c > 0) {
    // return false;
    // }
    // return true;
    // }

    public Long addTempAdjustCourseSchedule(TempAdjustCourseFullDomain tempAdjustCourseFullDomain) {
        TempAdjustCourseSchedule t = tempCourseScheduleService.saveTempAdjustCourseSchedule(tempAdjustCourseFullDomain);
        if (null != t) {
            return t.getId();
        }
        return null;
    }

    public void cancelTempAdjustCourseSchedule(Long adjustCourseId) {
        // tempAdjustCourseScheduleService.cancelTempAdjustCourseSchedule(adjustCourseId);
    }

    @Transactional(readOnly = true)
    public PageData<TempAdjustCourseListDomain> listTempAdjustCourse(Long orgId, Long semesterId, String teacher, Integer adjustType, Date startDate, Date endDate,
        Pageable pageable) {
        return tempAdjustCourseScheduleService.list(orgId, semesterId, teacher, adjustType, startDate, endDate, pageable);
    }
}