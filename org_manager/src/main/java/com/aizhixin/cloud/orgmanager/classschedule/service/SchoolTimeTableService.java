/**
 *
 */
package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.core.SingleOrDouble;
import com.aizhixin.cloud.orgmanager.classschedule.domain.*;
import com.aizhixin.cloud.orgmanager.classschedule.entity.SchoolTimeTable;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.repository.SchoolTimeTableRepository;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.orgmanager.company.entity.Period;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.entity.Week;
import com.aizhixin.cloud.orgmanager.company.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 排课表相关操作业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class SchoolTimeTableService {
    @Autowired
    private SchoolTimeTableRepository schoolTimeTableRepository;
    @Autowired
    private TeachingClassService teachingClassService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private WeekService weekService;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private TeachingClassTeacherService teachingClassTeacherService;


    /**
     * 保存实体
     *
     * @param schoolTimeTable
     * @return
     */
    public SchoolTimeTable save(SchoolTimeTable schoolTimeTable) {
        return schoolTimeTableRepository.save(schoolTimeTable);
    }

    public List<SchoolTimeTable> save(List<SchoolTimeTable> schoolTimeTables) {
        return schoolTimeTableRepository.save(schoolTimeTables);
    }

    @Transactional
    public List<SchoolTimeTable> saveSchoolTimeTableList(List<SchoolTimeTable> schoolTimeTables) {
        return schoolTimeTableRepository.save(schoolTimeTables);
    }

    public void delete(TeachingClass teachingClass) {
        schoolTimeTableRepository.deleteByTeachingClass(teachingClass);
    }

    @Transactional(readOnly = true)
    public SchoolTimeTable findById(Long id) {
        return schoolTimeTableRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public List<SchoolTimeTable> findByTeachingClass(TeachingClass teachingClass) {
        return schoolTimeTableRepository.findByTeachingClassOrderByDayOfWeek(teachingClass);
    }

    @Transactional(readOnly = true)
    public List<DianDianSchoolTimeDomain> findDianDianDaySchoolTime(Semester semester, Integer weekNo, Integer dayOfWeek, Set<Integer> singleOrDoubles) {
        return schoolTimeTableRepository.findBySemesterAndWeekAndDayOfWeek(semester, weekNo, dayOfWeek, singleOrDoubles);
    }

    @Transactional(readOnly = true)
    public List<DianDianWeekSchoolTimeTableDomain> findDianDianWeekSchoolTime(Set<Long> teachingClassIds, Integer weekNo, Set<Integer> singleOrDoubles) {
        return schoolTimeTableRepository.findBySemesterAndWeek(teachingClassIds, weekNo, singleOrDoubles);
    }

    @Transactional(readOnly = true)
    public List<DianDianDaySchoolTimeTableDomain> findDianDianDaySchoolTime(Set<Long> teachingClassIds, Semester semester, Integer weekNo, Integer dayOfWeek, Set<Integer> singleOrDoubles) {
        return schoolTimeTableRepository.findBySemesterAndDayOfWeek(teachingClassIds, semester, weekNo, dayOfWeek, singleOrDoubles);
    }
    public void delete(Long orgId, Semester semester) {
        schoolTimeTableRepository.deleteByOrgIdAndSemester(orgId, semester);
    }
    @Transactional(readOnly = true)
    public long countByTeachingClass(TeachingClass teachingClass) {
        return schoolTimeTableRepository.countByTeachingClass(teachingClass);
    }
    public void deleteByTeachingClassList(List<TeachingClass> teachingClassList) {
        schoolTimeTableRepository.deleteByTeachingClassIn(teachingClassList);
    }

    @Transactional(readOnly = true)
    public List<Long> findScheduleTeachignclassIds(Set<Long> teachingClassIds) {
        return schoolTimeTableRepository.findByTeachingclassIds(teachingClassIds);
    }

//    @Transactional(readOnly = true)
//    public long countByCourseScheduleTime(Set<TeachingClass> teachingClassList, Integer weekNo, Integer dayOfWeek, Integer startPeroidNo, Integer endPeroidNo, Set<Integer> signOrDoubleWeek) {
//        return schoolTimeTableRepository.countByTeachingClassAndWeekAndDayOfWeek(teachingClassList, weekNo, dayOfWeek, signOrDoubleWeek, startPeroidNo, endPeroidNo);
//    }

    @Transactional(readOnly = true)
    public List<SchoolTimeTable> findBySemesterAndWeekAndDayOfWeek(Semester semester, Integer weekNo, Integer dayOfWeek, Integer startPeroidNo, Integer endPeroidNo, Set<Integer> signOrDoubleWeek) {
        return schoolTimeTableRepository.findBySemesterAndWeekAndDayOfWeek(semester, weekNo, dayOfWeek, signOrDoubleWeek, startPeroidNo, endPeroidNo);
    }

    @Transactional(readOnly = true)
    public long countByTeachingClassAndWeekAndDayOfWeek(TeachingClass teachingClass, Integer weekNo, Integer dayOfWeek, Set<Integer> signOrDoubleWeek, Integer periodNo, Integer periodNum) {
        return schoolTimeTableRepository.countByTeachingClassAndWeekAndDayOfWeek(teachingClass, weekNo, dayOfWeek, signOrDoubleWeek, periodNo, periodNum);
    }

    @Transactional(readOnly = true)
    public List<DianDianDaySchoolTimeTableDomain> findCourseScheduleInDayAndPeriodRange(Set<TeachingClass> teachingClassList, Integer weekNo, Integer dayOfWeek, Integer startPeroidNo, Integer endPeroidNo, Set<Integer> signOrDoubleWeek) {
        return schoolTimeTableRepository.findTeachingClassByTeachingclassAndDayAndPeriodRanage(teachingClassList, weekNo, dayOfWeek, signOrDoubleWeek, startPeroidNo, endPeroidNo);
    }

    @Transactional(readOnly = true)
    public List<TeacherCourseScheduleDomain> findCourseScheduleByTeachingClassSet(Set<TeachingClass> teachingClassSet) {
        return schoolTimeTableRepository.findByTeachingClassIn(teachingClassSet);
    }
    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//
    public void save(TeachingClassSchoolTimeTableDomain tct) {
        TeachingClass teachingClass = teachingClassService.findById(tct.getTeachingClassId());
        if (null == teachingClass) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据教学班ID[" + tct.getTeachingClassId() + "]查找不到对应的数据");
        }
        List<CourseTimePeriodDomain> timePeriod = tct.getTimePeriod();
        if (null == timePeriod || timePeriod.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "排课信息是必须的");
        }
        List<SchoolTimeTable> data = new ArrayList<>();
        for (CourseTimePeriodDomain pt : timePeriod) {
            if (null == pt.getDayOfWeek()) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "周几[" + pt.getDayOfWeek() + "]是必须的");
            }
            if (pt.getDayOfWeek() < 1 || pt.getDayOfWeek() > 7) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "周几[" + pt.getDayOfWeek() + "]不是一个表示星期几的数据");
            }
            if (null == pt.getPeriodId() || pt.getPeriodId() <= 0) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程节是必须的");
            }
            Period p = periodService.findById(pt.getPeriodId());
            if (null == p) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据课程节ID[" + pt.getPeriodId() + "]查找不到对应的数据");
            }
            if (null == pt.getPeriodNum() || pt.getPeriodNum() < 0) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "一趟课包含几小节是必须的");
            }
            if (pt.getPeriodNum() > 20) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "一趟课包含几小节[" + pt.getPeriodNum() + "]是异常数据");
            }
            if (null == pt.getStartWeekId() || pt.getStartWeekId() <= 0) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "起始周是必须的");
            }
            Week startWeek = weekService.findById(pt.getStartWeekId());
            if (null == startWeek) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据起始周ID[" + pt.getStartWeekId() + "]查找不到对应的数据");
            }
            if (null == pt.getEndWeekId() || pt.getEndWeekId() <= 0) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "起始周是必须的");
            }
            Week endWeek = weekService.findById(pt.getEndWeekId());
            if (null == endWeek) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据结束周ID[" + pt.getEndWeekId() + "]查找不到对应的数据");
            }
            if (null == pt.getSingleOrDouble()) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "单双周是必须的");
            }
            if (SingleOrDouble.ALL.getState() != pt.getSingleOrDouble() && SingleOrDouble.SINGLE.getState() != pt.getSingleOrDouble() && SingleOrDouble.DOUBLE.getState() != pt.getSingleOrDouble()) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "单双周[" + pt.getSingleOrDouble() + "]是异常数据");
            }

            SchoolTimeTable schoolTimeTable = new SchoolTimeTable();
            schoolTimeTable.setOrgId(teachingClass.getOrgId());
            schoolTimeTable.setSemester(teachingClass.getSemester());
            schoolTimeTable.setTeachingClass(teachingClass);
            schoolTimeTable.setDayOfWeek(pt.getDayOfWeek());
            schoolTimeTable.setPeriod(p);
            schoolTimeTable.setPeriodNo(p.getNo());
            schoolTimeTable.setPeriodNum(pt.getPeriodNum());
            schoolTimeTable.setStartWeek(startWeek);
            schoolTimeTable.setEndWeek(endWeek);
            schoolTimeTable.setStartWeekNo(startWeek.getNo());
            schoolTimeTable.setEndWeekNo(endWeek.getNo());
            schoolTimeTable.setSingleOrDouble(pt.getSingleOrDouble());
            schoolTimeTable.setClassroom(pt.getClassroom());
            schoolTimeTable.setRemark(pt.getRemark());
            schoolTimeTable.setColor(pt.getColor());

            data.add(schoolTimeTable);
        }
        save(data);
    }

    public void update(TeachingClassSchoolTimeTableDomain tct) {
        delete(tct.getTeachingClassId());
        if (null == tct.getTimePeriod() || tct.getTimePeriod().size() <= 0) {
            return;
        }
        save(tct);
    }

    public void updateBatch(List<TeachingClassSchoolTimeTableDomain> tcts) {
        if (null == tcts || tcts.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID是必须的");
        }
        Set<Long> ts = new HashSet<>();
        for (TeachingClassSchoolTimeTableDomain t : tcts) {
            if (null == t.getUserId() || t.getUserId() <= 0) {
                throw new NoAuthenticationException();
            }
            if (ts.contains(t.getTeachingClassId())) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID是必须的");
            } else {
                ts.add(t.getTeachingClassId());
                update(t);
            }
        }
    }

    public void delete(Long teachingClassId) {
        TeachingClass teachingClass = teachingClassService.findById(teachingClassId);
        if (null == teachingClass) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据教学班ID[" + teachingClassId + "]查找不到对应的数据");
        }
        delete(teachingClass);
    }

    public TeachingClassSchoolTimeTableDomain get(Long teachingClassId) {
        TeachingClassSchoolTimeTableDomain tc = new TeachingClassSchoolTimeTableDomain();
        TeachingClass teachingClass = teachingClassService.findById(teachingClassId);
        if (null == teachingClass) {
            return tc;
        }
        tc.setTeachingClassId(teachingClass.getId());
        tc.setTeachingClassName(teachingClass.getName());

        List<SchoolTimeTable> ts = findByTeachingClass(teachingClass);
        List<CourseTimePeriodDomain> tp = new ArrayList<>();
        tc.setTimePeriod(tp);
        for (SchoolTimeTable s : ts) {
            CourseTimePeriodDomain cp = new CourseTimePeriodDomain();
            cp.setClassroom(s.getClassroom());
            cp.setColor(s.getColor());
            cp.setRemark(s.getRemark());
            if(null != s.getEndWeek()) {
                cp.setEndWeekId(s.getEndWeek().getId());
            }
            cp.setEndWeekNo(s.getEndWeekNo());
            if (null != s.getPeriod()) {
                cp.setPeriodId(s.getPeriod().getId());
            }
            cp.setPeriodMo(s.getPeriodNo());
            cp.setPeriodNum(s.getPeriodNum());
            cp.setSingleOrDouble(s.getSingleOrDouble());
            if (null != s.getStartWeek()) {
                cp.setStartWeekId(s.getStartWeek().getId());
            }
            cp.setStartWeekNo(s.getStartWeekNo());
            if (null != s.getDayOfWeek()) {
                cp.setDayOfWeek(s.getDayOfWeek());
            }
            tp.add(cp);
        }
        return tc;
    }

    public List<TeachingClassSchoolTimeTableDomain> get(Set<Long> teachingClassIds) {
        List<TeachingClassSchoolTimeTableDomain> r = new ArrayList<>();
        for (Long teachingClassId : teachingClassIds) {
            TeachingClassSchoolTimeTableDomain d = get(teachingClassId);
            if (null != d.getTeachingClassId() && d.getTeachingClassId() > 0) {
                r.add(d);
            }
        }
        return r;
    }

//    public void saveAll(List<SchoolTimeTableDomain> schoolTimeTableDomains) {
//        Long orgId = null;
//        Long userId = null;
//        String semesterCode = null;
//        Set<String> teachingClassCodes = new HashSet<>();
//        Set<String> courseCodes = new HashSet<>();
//        Set<String> classesCodes = new HashSet<>();//需要咨询选修、必修课是否按照班级挂学生
//        Set<String> teacherCodes = new HashSet<>();
////        Set<String> collegeCodes = new HashSet<>();
//
//        for(SchoolTimeTableDomain d : schoolTimeTableDomains) {
//            if (null == orgId || orgId <= 0) {
//                orgId = d.getOrgId();
//            }
//            if (null == userId || userId <= 0) {
//                userId = d.getUserId();
//            }
//            if (StringUtils.isEmpty(semesterCode)) {
//                semesterCode = d.getSemesterCode();
//            }
//            teachingClassCodes.add(d.getTeachingClassCode());
//            courseCodes.add(d.getCourseCode());
//            teacherCodes.add(d.getTeacherJobNumber());
////            collegeCodes.add(d.getCollegeCode());
//        }
//        Map<String, TeachingClass> teachingClassMap = new HashMap<>();
//        Map<String, Course> courseMap = new HashMap<>();
//        Map<String, User> teacherMap = new HashMap<>();
////        Map<String, College> collegeMap = new HashMap<>();
//        Map<Integer, Week> weekMap = new HashMap<>();
//        Map<Integer, Period> periodMap = new HashMap<>();
//        Map<Long, TeachingClassTeacher> teachingClassTeacherHashMap = new HashMap<>();
//
//        //查询相关数据及对应的学期、学周、课程节数据
//        List<TeachingClass> teachingClassList = teachingClassService.findByCodeIn(orgId, teachingClassCodes);
//        for (TeachingClass t : teachingClassList) {
//            teachingClassMap.put(t.getCode(), t);
//        }
//        List<Course> courseList = courseService.findByOrgIdAndCodes(orgId, courseCodes);
//        for (Course c : courseList) {
//            courseMap.put(c.getCode(), c);
//        }
//        List<User> teacherList = userService.findTeachersByCodeInAndOrg(orgId, teacherCodes);
//        for (User teacher : teacherList) {
//            teacherMap.put(teacher.getJobNumber(), teacher);
//        }
////        List<College> collegeList = collegeService.findByCodeIn(orgId, collegeCodes);
////        for (College c : collegeList) {
////            collegeMap.put(c.getCode(), c);
////        }
//        Semester semester = semesterService.findByOrgIdAndCode(orgId, semesterCode);
//        if (null == semester) {
//            return;
//        }
//        delete(orgId, semester);//删除该学期的所有老排课数据
//
//        List<Week> weekList = weekService.findAllWeekBySemester(semester);
//        for (Week w : weekList) {
//            weekMap.put(w.getNo(), w);
//        }
//        List<Period> periodList = periodService.findByOrgId(orgId);
//        for (Period p : periodList) {
//            periodMap.put(p.getNo(), p);
//        }
//        List<TeachingClassTeacher> teachingClassTeacherList = teachingClassTeacherService.findByTeachingClassesIn(teachingClassList);
//        for (TeachingClassTeacher teachingClassTeacher : teachingClassTeacherList) {
//            teachingClassTeacherHashMap.put(teachingClassTeacher.getTeachingClass().getId(), teachingClassTeacher);
//        }
//        List<TeachingClass> teachingClassListForUpdate = new ArrayList<>();
//        List<SchoolTimeTable> schoolTimeTableList = new ArrayList<>();
//        Set<Long> teachingClassIds = new HashSet<>();
//        for(SchoolTimeTableDomain d : schoolTimeTableDomains) {
//            SchoolTimeTable schoolTimeTable = new SchoolTimeTable();
//            schoolTimeTable.setOrgId(orgId);
//            schoolTimeTable.setSemester(semester);
//            TeachingClass teachingClass = teachingClassMap.get(d.getTeachingClassCode());
//            schoolTimeTable.setTeachingClass(teachingClass);
//
//            if (null != teachingClass) {
//                if (!teachingClassIds.contains(teachingClass.getId())) {
//                    teachingClassListForUpdate.add(teachingClass);
//                }
//                Course course = courseMap.get(d.getCourseCode());
//                teachingClass.setCourse(course);
//                teachingClass.setStudentsCount(d.getStudentsNum());
//
//                TeachingClassTeacher teachingClassTeacher = teachingClassTeacherHashMap.get(teachingClass.getId());
//                if (null != teachingClassTeacher) {//暂只能处理一个老师的情况
//                } else {
//                    teachingClassTeacher = new TeachingClassTeacher();
//                    teachingClassTeacher.setSemester(teachingClass.getSemester());
//                    teachingClassTeacher.setOrgId(teachingClass.getOrgId());
//                    teachingClassTeacherList.add(teachingClassTeacher);
//                }
//                User teacher = teacherMap.get(d.getTeacherJobNumber());
//                if (null != teacher) {
//                    teachingClassTeacher.setTeacher(teacher);
//                }
//            }
//
//            schoolTimeTable.setDayOfWeek(d.getDayOfWeek());
//            Period p = periodMap.get(d.getPeriodMo());
//            schoolTimeTable.setPeriod(p);
//            if (null != p) {
//                schoolTimeTable.setPeriodNo(p.getNo());
//            }
//            schoolTimeTable.setPeriodNum(d.getPeriodNum());
//
//            Week startWeek = weekMap.get(d.getStartWeekNo());
//            schoolTimeTable.setStartWeek(startWeek);
//            if (null != startWeek) {
//                schoolTimeTable.setStartWeekNo(startWeek.getNo());
//            }
//            Week endWeek = weekMap.get(d.getEndWeekNo());
//            schoolTimeTable.setEndWeek(endWeek);
//            if (null != endWeek) {
//                schoolTimeTable.setEndWeekNo(endWeek.getNo());
//            }
//            if (StringUtils.isEmpty(d.getSingleOrDouble())) {
//                schoolTimeTable.setSingleOrDouble(SingleOrDouble.ALL.getState());
//            } else if ("单".equals(d.getSingleOrDouble())) {
//                schoolTimeTable.setSingleOrDouble(SingleOrDouble.SINGLE.getState());
//            } else if ("双".equals(d.getSingleOrDouble())) {
//                schoolTimeTable.setSingleOrDouble(SingleOrDouble.DOUBLE.getState());
//            } else {
//                schoolTimeTable.setSingleOrDouble(SingleOrDouble.ALL.getState());
//            }
//            schoolTimeTable.setClassroom(d.getClassroom());
//
//            schoolTimeTableList.add(schoolTimeTable);
//        }
//        save(schoolTimeTableList);
//        teachingClassTeacherService.save(teachingClassTeacherList);
//        teachingClassService.save(teachingClassListForUpdate);
//    }

    /**
     * 查询老师学期的总课表
     * @param semesterId    学期ID
     * @param teacherId     老师ID
     * @return  课表
     */
    public List<TeacherCourseScheduleAllDomain> findTeacherSemesterCourseSchedule(Long semesterId, Long teacherId) {
        List<TeacherCourseScheduleAllDomain> r = new ArrayList<>();
        Map<Integer, TeacherCourseScheduleAllDomain> dayDatas = new HashMap();
        Map<String, TeacherCourseSchdulePeroidDomain> peroidDatas = new HashMap();
        User teacher = userService.findById(teacherId);
        if (null == teacher) {
            return r;
        }
        Semester semester = semesterService.findById(semesterId);
        if (null == semester) {
            return r;
        }
        List<TeachingClass> teachingClassList = teachingClassTeacherService.findAllTeachingClassByTeacherAndSemester(semester, teacher);
        Set<TeachingClass> teachingClassSet = new HashSet<>();
        if (null != teachingClassList && !teachingClassList.isEmpty()) {
            teachingClassSet.addAll(teachingClassList);
            List<TeacherCourseScheduleDomain> datas = findCourseScheduleByTeachingClassSet(teachingClassSet);
            for (TeacherCourseScheduleDomain d : datas) {
                TeacherCourseScheduleAllDomain dw = dayDatas.get(d.getDayOfWeek());
                if (null == dw) {
                    dw = new TeacherCourseScheduleAllDomain ();
                    dw.setDayOfWeek(d.getDayOfWeek());
                    dw.setPeroidList(new ArrayList<>());
                    dayDatas.put(d.getDayOfWeek(), dw);
                    r.add(dw);
                }
                String key = d.getDayOfWeek() + "-" + d.getPeriodNo();
                TeacherCourseSchdulePeroidDomain p = peroidDatas.get(key);
                if (null == p) {
                    p = new TeacherCourseSchdulePeroidDomain();
                    p.setPeriodNo(d.getPeriodNo());
                    p.setDetails(new ArrayList<>());
                    peroidDatas.put(key, p);
                    dw.getPeroidList().add(p);
                }

                p.getDetails().add(new TeacherCourseSchduleDetailDomain(d));
            }
        }
        return r;
    }
}