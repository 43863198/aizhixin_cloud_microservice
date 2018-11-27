package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.monitor.service.PushMonitor;
import com.aizhixin.cloud.dd.monitor.utils.ScheduelStatusEnum;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClass;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.*;
import com.aizhixin.cloud.dd.rollcall.repository.CourseRollCallRepository;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleQuery;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRepository;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Autowired
    private ScheduleQuery scheduleQuery;
    @Lazy
    @Autowired
    private InitScheduleService initScheduleService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private RollCallRepository rollCallRepository;
    @Autowired
    private CourseRollCallRepository courseRollCallRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private WeekService weekService;
    @Autowired
    private ClaimService claimService;
    @Autowired
    public PushMonitor pushMonitor;
    @Autowired
    private TeachingClassRepository teachingClassRepository;
    @Autowired
    private RollCallStatsService rollCallStatsService;

    /**
     * 目前直接从平台查询学生课表
     *
     * @param account
     * @param weekId
     * @return
     */
    public List<CourseListDTO> getStudentScheduleCourseWeek(AccountDTO account, Long weekId) {

        WeekDTO currentWeek = weekService.getWeek(account.getOrganId());
        //
        WeekDTO weekById = weekService.getWeekById(account.getOrganId(), weekId);

        if (null == currentWeek || null == weekById) {
            return null;
        }

        int currentNo = Integer.valueOf(currentWeek.getName());
        int findNo = Integer.valueOf(weekById.getName());

        List<CourseListDTO> resultList = new ArrayList<CourseListDTO>();
        Map<Integer, CourseListDTO> map = new HashMap<Integer, CourseListDTO>();

        // 进行拼接查询
        List<DianDianWeekSchoolTimeTableDomain> studentScheduleCourse = orgManagerRemoteService.getStudentWeekSchoolTimeTable(weekId, account.getId());
        Set<Long> teachingClassesIds = new HashSet<>();
        Set<Integer> dayWeekSet = new HashSet<>();

        List<Map<String, Object>> teachingClassList = orgManagerRemoteService.findTeachingclassByStudentAndSemester(account.getId(), null);
        if (teachingClassList != null && teachingClassList.size() > 0) {
            for (Map<String, Object> item : teachingClassList) {
                if (item != null && item.get("id") != null) {
                    teachingClassesIds.add(Long.parseLong(item.get("id").toString()));
                }
            }
        } else {
            for (DianDianWeekSchoolTimeTableDomain ddd : studentScheduleCourse) {
                teachingClassesIds.add(ddd.getTeachingClassId());
            }
        }

        List<WeekDTO> weekDTOS = weekService.listWeek(account.getOrganId());
        Map<Long, String> weekMap = new HashMap();
        if (null != weekDTOS) {
            for (WeekDTO weekDTO : weekDTOS) {
                weekMap.put(weekDTO.getId(), weekDTO.getName());
            }
        }
        if (currentNo == findNo) {
            naticateQueryStudent(weekId, teachingClassesIds, resultList, map, dayWeekSet);
            remoteQueryStudent(studentScheduleCourse, account, weekId, resultList, map, dayWeekSet, weekMap);

        } else if (findNo > currentNo) {
            // 平台获取
            remoteQueryStudent(studentScheduleCourse, account, weekId, resultList, map, dayWeekSet, weekMap);
        } else {
            // 本地库获取
            naticateQueryStudent(weekId, teachingClassesIds, resultList, map, dayWeekSet);
        }
        return resultList;
    }

    // 平台查询
    public void remoteQueryStudent(List<DianDianWeekSchoolTimeTableDomain> studentScheduleCourse, AccountDTO account, Long weekId, List<CourseListDTO> resultList,
                                   Map<Integer, CourseListDTO> map, Set<Integer> dayWeekSet, Map weekMap) {
        if (null == studentScheduleCourse) {
            studentScheduleCourse = orgManagerRemoteService.getStudentWeekSchoolTimeTable(weekId, account.getId());
        }
        for (DianDianWeekSchoolTimeTableDomain ddt : studentScheduleCourse) {
            if (dayWeekSet.contains(ddt.getDayOfWeek())) {
                continue;
            }
            CourseListDTO cdto = map.get(ddt.getDayOfWeek());
            if (null == cdto) {
                cdto = new CourseListDTO();
                cdto.setDayOfWeek(ddt.getDayOfWeek() + "");
                cdto.setCourseList(new ArrayList<CourseDTO>());
                map.put(ddt.getDayOfWeek(), cdto);
                resultList.add(cdto);
            }
            CourseDTO courseDTO = new CourseDTO();
            dianDianWeekSchoolTimeTableDomainToCourseDTO(null, ddt, courseDTO, weekMap, Boolean.FALSE);
            cdto.getCourseList().add(courseDTO);
        }
    }

    // 本地库查询
    public void naticateQueryStudent(Long weekId, Set<Long> teachingClassesIds, List<CourseListDTO> resultList, Map<Integer, CourseListDTO> map, Set<Integer> dayWeekSet) {
        Sort sort = new Sort(Sort.Direction.ASC, "dayOfWeek", "periodNo");
        List<Schedule> allByweekId = scheduleRepository.findAllByweekIdAndDeleteFlagAndTeachingclassIdIn(weekId, DataValidity.VALID.getState(), teachingClassesIds, sort);
        for (Schedule ddt : allByweekId) {
            dayWeekSet.add(ddt.getDayOfWeek());
            CourseListDTO cdto = map.get(ddt.getDayOfWeek());
            if (null == cdto) {
                cdto = new CourseListDTO();
                cdto.setDayOfWeek(String.valueOf(ddt.getDayOfWeek()));
                cdto.setCourseList(new ArrayList<CourseDTO>());
                map.put(ddt.getDayOfWeek(), cdto);
                resultList.add(cdto);
            }

            CourseDTO courseDTO = new CourseDTO();
            scheduleToCourse(ddt, courseDTO);
            cdto.getCourseList().add(courseDTO);
        }
    }

    /**
     * 查询教师某周课表
     *
     * @param teacherId
     * @param weekId
     * @param organId
     * @return
     * @throws DlEduException
     */

    public List<CourseListDTO> getTeacherCourseWeek(Long teacherId, Long weekId, Long organId) {
        WeekDTO currentWeek = weekService.getWeek(organId);
        if (null == currentWeek) {
            return null;
        }

        WeekDTO weekById = weekService.getWeekById(organId, weekId);
        if (null == weekById) {
            return null;
        }

        List<WeekDTO> weekDTOS = weekService.listWeek(organId);
        Map<Long, String> weekMap = new HashMap();
        if (null != weekDTOS) {
            for (WeekDTO weekDTO : weekDTOS) {
                weekMap.put(weekDTO.getId(), weekDTO.getName());
            }
        }

        int currentNo = Integer.valueOf(currentWeek.getName());
        int findNo = Integer.valueOf(weekById.getName());

        List<CourseListDTO> rs = new ArrayList<CourseListDTO>();
        HashMap<Integer, CourseListDTO> map = new HashMap<Integer, CourseListDTO>();
        Set<Integer> dayWeekSet = new HashSet<>();

        Map<String, CourseDTO> courseMapToday = new HashedMap();

        List<TeachingClass> teachingClassList = teachingClassRepository.findByOrgId(organId);
        Map<Long, String> teachingClassMap = new HashMap<>();
        if (teachingClassList != null && teachingClassList.size() > 0) {
            for (TeachingClass item : teachingClassList) {
                if (!StringUtils.isEmpty(item.getClassNames())) {
                    teachingClassMap.put(item.getTeachingClassId(), item.getClassNames());
                }
            }
        }

        if (findNo > currentNo) {
            remoteQueryTeacher(teachingClassMap, teacherId, weekId, rs, map, dayWeekSet, weekMap, courseMapToday);
        } else {
            // 进行拼接查询
            naticateQueryTeacher(teachingClassMap, teacherId, weekId, rs, map, dayWeekSet, courseMapToday);
            remoteQueryTeacher(teachingClassMap, teacherId, weekId, rs, map, dayWeekSet, weekMap, courseMapToday);
        }
        return rs;
    }

    public void remoteQueryTeacher(Map<Long, String> teachingClassMap, Long teacherId, Long weekId, List<CourseListDTO> rs, HashMap<Integer, CourseListDTO> map, Set<Integer> dayWeekSet, Map weekMap, Map<String, CourseDTO> courseMapToday) {
        // 平台获取
        List<DianDianWeekSchoolTimeTableDomain> teacherScheduleCourse = orgManagerRemoteService.getTeacherWeekSchoolTimeTable(weekId, teacherId);
        for (DianDianWeekSchoolTimeTableDomain course : teacherScheduleCourse) {
            Boolean isClaim = Boolean.FALSE;
            if (dayWeekSet.contains(course.getDayOfWeek()) || course.getTeachDate().getTime() < System.currentTimeMillis()) {
                String courseKey = course.getTeachingClassId() + DateFormatUtil.formatShort(course.getTeachDate()) + course.getPeriodNo() + course.getPeriodNum();
                CourseDTO courseDTO = courseMapToday.get(courseKey);
                if (courseDTO != null) {
                    continue;
                } else {
                    if (course.getTeachers().size() > 1) {
                        isClaim = Boolean.TRUE;
                    }
                }
            }

            CourseListDTO cdto = map.get(course.getDayOfWeek());
            if (null == cdto) {
                cdto = new CourseListDTO();
                cdto.setDayOfWeek(String.valueOf(course.getDayOfWeek()));
                cdto.setCourseList(new ArrayList<CourseDTO>());
                map.put(course.getDayOfWeek(), cdto);
                rs.add(cdto);
            }

            CourseDTO courseDTO = new CourseDTO();
            dianDianWeekSchoolTimeTableDomainToCourseDTO(teacherId, course, courseDTO, weekMap, isClaim);
            String classNames = teachingClassMap.get(courseDTO.getTeachingClassId());
            if (!StringUtils.isEmpty(classNames)) {
                courseDTO.setClassNames(classNames);
            }
            cdto.getCourseList().add(courseDTO);
        }
    }

    public void naticateQueryTeacher(Map<Long, String> teachingClassMap, Long teacherId, Long weekId, List<CourseListDTO> rs, HashMap<Integer, CourseListDTO> map, Set<Integer> dayWeekSet,
                                     Map<String, CourseDTO> courseMapToday) {
        // 本地库获取
        Sort sort = new Sort(Sort.Direction.ASC, "dayOfWeek", "periodNo");
        List<Schedule> allByTeacherIdAAndWeekId = scheduleRepository.findAllByTeacherIdAndWeekId(teacherId, weekId, sort);
        for (Schedule schedule : allByTeacherIdAAndWeekId) {
            dayWeekSet.add(schedule.getDayOfWeek());
            CourseListDTO cdto = map.get(schedule.getDayOfWeek());
            if (null == cdto) {
                cdto = new CourseListDTO();
                cdto.setDayOfWeek(schedule.getDayOfWeek() + "");
                cdto.setCourseList(new ArrayList<CourseDTO>());
                map.put(schedule.getDayOfWeek(), cdto);
                rs.add(cdto);
            }
            CourseDTO courseDTO = new CourseDTO();
            scheduleToCourse(schedule, courseDTO);
            String classNames = teachingClassMap.get(courseDTO.getTeachingClassId());
            if (!StringUtils.isEmpty(classNames)) {
                courseDTO.setClassNames(classNames);
            }
            cdto.getCourseList().add(courseDTO);

            String key = courseDTO.getTeachingClassId() + courseDTO.getTeach_time() + courseDTO.getPeriodNo() + courseDTO.getPeriodNum();
            courseMapToday.put(key, courseDTO);
        }
    }

    public List<Schedule> listScheduleByTeacherIdAndCourseIdAndTeachDate(Long teacherId, Long courseId, String teachDate) {
        return scheduleRepository.findByTeacherIdAndCourseIdAndTeachDateAndDeleteFlag(teacherId, courseId, teachDate, DataValidity.VALID.getState());
    }

    /**
     * 结构体转换
     *
     * @param course
     * @param dto
     */
    public void dianDianWeekSchoolTimeTableDomainToCourseDTO(Long teacherId, DianDianWeekSchoolTimeTableDomain course, CourseDTO dto, Map<Long, String> weekMap, Boolean isClaim) {
        dto.setId(0L);
        dto.setTeachingClassId(course.getTeachingClassId());
        dto.setTeachingClassCode(course.getTeachingClassCode());
        dto.setDayOfWeek(null == course.getDayOfWeek() ? "" : String.valueOf(course.getDayOfWeek()));
        dto.setClassRoom(course.getClassroom());
        dto.setCourseName(course.getCourseName());
        dto.setClassBeginTime(course.getPeriodStarttime());
        dto.setClassEndTime(course.getPeriodEndtime());
        dto.setTeach_time(DateFormatUtil.formatShort(course.getTeachDate()));
        dto.setWeekName(weekMap.get(course.getWeekId()));
        dto.setWhichLesson(CourseUtils.getWhichLesson(course.getPeriodNo(), course.getPeriodNum()));
        dto.setLessonOrderNum(course.getPeriodNo());
        dto.setPeriodType(course.getPeriodNum().intValue() + "");
        dto.setTeacherId(course.getTeacherId());
        dto.setTeacher(course.getTeacherName());
        dto.setPeriodId(course.getPeriodId());
        dto.setPeriodNo(course.getPeriodNo());
        dto.setPeriodNum(course.getPeriodNum());

        // 共建课程处理
        if (isClaim) {
            dto.setIsCanClaim(Boolean.TRUE);
            List<Schedule> schedules = scheduleRepository.findAllByTeachingclassIdAndTeachDateAndPeriodNoAndPeriodNumAndDeleteFlag(course.getTeachingClassId(),
                    DateFormatUtil.formatShort(course.getTeachDate()), course.getPeriodNo(), course.getPeriodNum(), DataValidity.VALID.getState());
            if (null != schedules && schedules.size() > 0) {
                dto.setTeacherId(schedules.get(0).getTeacherId());
                dto.setTeacher(schedules.get(0).getTeacherNname());
            }
            return;
        }
        if (null != teacherId) {
            List<IdNameDomain> teachers = course.getTeachers();
            if (teachers != null && teachers.size() > 0) {

                IdNameDomain teacher = null;
                Claim claim = claimService.findBy(course.getTeachingClassId(), DateFormatUtil.formatShort(course.getTeachDate()), course.getPeriodNo(), course.getPeriodNum());
                if (null == claim) {
                    Collections.sort(teachers, new Comparator<IdNameDomain>() {
                        @Override
                        public int compare(IdNameDomain o1, IdNameDomain o2) {
                            return o1.getId().compareTo(o2.getId());
                        }
                    });
                    // 默认认领老师
                    teacher = teachers.get(0);
                } else {
                    teacher = new IdNameDomain(claim.getTeacherId(), claim.getTeacherName());
                }

                if (teacher.getId().longValue() == teacherId.longValue()) {
                    dto.setIsCanClaim(Boolean.FALSE);
                } else {
                    dto.setIsCanClaim(Boolean.TRUE);
                    dto.setTeacherId(teacher.getId());
                    dto.setTeacher(teacher.getName());
                }
            }
        }
    }

    public static IdNameDomain parseTeacher(String teachers) {
        IdNameDomain teacherDo = new IdNameDomain();
        // 22,李群;33,李丽
        if (StringUtils.isBlank(teachers)) {
            return teacherDo;
        }
        String[] teacher = teachers.split(";");
        if (teacher.length > 0) {
            String[] teacherPro = teacher[0].split(",");
            teacherDo.setId(Long.parseLong(teacherPro[0] == null ? "0" : teacherPro[0]));
            teacherDo.setName(teacherPro[1] == null ? "" : teacherPro[1]);
        }
        return teacherDo;
    }

    /**
     * 结构体转换
     *
     * @param schedule
     * @param dto
     */
    public void scheduleToCourse(Schedule schedule, CourseDTO dto) {
        dto.setId(schedule.getId());
        dto.setTeachingClassId(schedule.getTeachingclassId());
        dto.setDayOfWeek(schedule.getDayOfWeek() + "");
        dto.setClassRoom(schedule.getClassRoomName());
        dto.setCourseName(schedule.getTeachingclassName());
        dto.setClassBeginTime(schedule.getScheduleStartTime());
        dto.setClassEndTime(schedule.getScheduleEndTime());
        dto.setTeach_time(schedule.getTeachDate());
        dto.setWeekName(schedule.getWeekName());
        dto.setWhichLesson(CourseUtils.getWhichLesson(schedule.getPeriodNo(), schedule.getPeriodNum()));
        dto.setLessonOrderNum(schedule.getPeriodNo());
        dto.setPeriodId(schedule.getPeriodId());
        dto.setPeriodNo(schedule.getPeriodNo());
        dto.setPeriodNum(schedule.getPeriodNum());
        dto.setPeriodType(schedule.getPeriodNum().intValue() + "");
        dto.setTeacherId(schedule.getTeacherId());
        dto.setTeacher(schedule.getTeacherNname());
    }

    public void deleteByTeacherIdAndCourseIdAndTeachDateAndPeriodId(Long teacherId, Long courseId, String teachDate, Long periodId) {
        scheduleRepository.deleteByTeacherIdAndCourseIdAndTeachDateAndPeriodId(teacherId, courseId, teachDate, periodId);
    }

    public void deleteOne(Long id) {
        scheduleRepository.delete(id);
    }

    public List<Schedule> findByTeacherIdAndCourseIdAndTeachDateAndPeriodId(Long teacherId, Long courseId, String teachDate, Long periodId) {
        return scheduleRepository.findByTeacherIdAndCourseIdAndTeachDateAndPeriodIdAndDeleteFlag(teacherId, courseId, teachDate, periodId, DataValidity.VALID.getState());
    }

    public List<Schedule> findByTeacherIdAndCourseIdAndTeachDateAndPeriodIdAndPeriodNum(Long teacherId, Long courseId, String teachDate, Long periodId, Integer periodNum) {
        return scheduleRepository.findByTeacherIdAndCourseIdAndTeachDateAndPeriodIdAndPeriodNumAndDeleteFlag(teacherId, courseId, teachDate, periodId, periodNum,
                DataValidity.VALID.getState());
    }

    public boolean initCurrentDateSchedule() {

        return false;
    }

    public Schedule save(Schedule schedule) {
        schedule = scheduleRepository.save(schedule);
        redisTemplate.opsForValue().set(RedisUtil.getSchduleDominKey(schedule.getId()), schedule, 1, TimeUnit.DAYS);
        return schedule;
    }

    /**
     * 获取即将上课或者下课的排课
     *
     * @param time
     */
    public void executePerTenMinutes(String time) {
        // 获取相关排课信息
        long now = StringUtils.isBlank(time) ? System.currentTimeMillis() : java.sql.Date.valueOf(time).getTime();
        long tenNow = now + 10 * 60 * 1000;
        String begin = DateFormatUtil.format(new Date(now), DateFormatUtil.FORMAT_LONG);
        String end = DateFormatUtil.format(new Date(tenNow), DateFormatUtil.FORMAT_LONG);
        try {

            // 扫描上课时间
            List<CourseScheduleDTO> list = scheduleQuery.queryScheduleByStartAndEndTime(begin, end, time, Boolean.TRUE);

            Map<Long, List> beginMap = new HashMap();
            Map<Long, Boolean> scheduleStartMap = new HashMap<Long, Boolean>();
            StringBuffer sb = new StringBuffer();
            for (CourseScheduleDTO dto : list) {
                if (scheduleStartMap.get(dto.getScheduleId()) == null ? false : scheduleStartMap.get(dto.getScheduleId())) {
                    continue;
                }
                String classbeginBeforeTime = dto.getTeachBeginBeforeTime();
                Date beginBeforTime = DateFormatUtil.parse(classbeginBeforeTime, "yyyy-MM-dd HH:mm");
                Long key = (beginBeforTime.getTime() - now);
                if (key <= 0) {
                    key = 5000l;
                    System.out.println(now + "课程前时间(beginBeforTime):" + beginBeforTime + "," + dto.toString());
                }
                if (!beginMap.containsKey(key)) {
                    List countList = new ArrayList();
                    beginMap.put(key, countList);
                }
                scheduleStartMap.put(dto.getScheduleId(), true);
                beginMap.get(key).add(dto);
                sb.append(dto.getScheduleId());
                sb.append(",");
            }
            log.info("上课需要初始化的课程信息:" + sb.toString());
            sb = null;

            log.info("课前需要启动课程初始化线程数为: " + beginMap.size());

            beginMap.forEach((sleepTime, listDto) -> {
                Thread t = new Thread(() -> {
                    executeBeginClassPerTask(sleepTime, listDto);
                });
                t.setName("course_start_" + sleepTime);
                t.start();
            });

            // 扫描下课时间
            log.info("扫描下课!!!任务:" + begin + " , " + end);
            list = scheduleQuery.queryScheduleByStartAndEndTime(begin, end, time, Boolean.FALSE);

            Map<Long, List> endMap = new HashMap();
            sb = new StringBuffer();
            for (CourseScheduleDTO dto : list) {
                String classEndTime = dto.getTeachEndTime();
                Date classEndDate = DateFormatUtil.parse(classEndTime, "yyyy-MM-dd HH:mm");
                Long key = (classEndDate.getTime() - now);

                if (key <= 0) {
                    // 当前时间超过课程结束时间,5S后执行
                    key = 5000l;
                    System.out.println(now + "课程结束时间(endTime):" + classEndTime + "," + dto.toString());
                }

                if (!endMap.containsKey(key)) {
                    List countList = new ArrayList();
                    endMap.put(key, countList);
                }
                endMap.get(key).add(dto);
                sb.append(dto.getScheduleId());
                sb.append(",");
            }
            log.info("下课需要初始化的课程信息:" + sb.toString());
            sb = null;
            endMap.forEach((sleepTime, listDto) -> {
                Thread t = new Thread(() -> {
                    executeAfterClassPerTask(sleepTime, listDto);
                });
                t.setName("course_end_" + sleepTime);
                t.start();
            });
            endMap = null;
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    /**
     * 课前初始化
     *
     * @param sleepTime
     * @param list
     */
    public void executeBeginClassPerTask(Long sleepTime, List<CourseScheduleDTO> list) {
        long beginTime = System.currentTimeMillis();
        try {
            log.info("等待上课...," + sleepTime / 1000 + "秒," + "需要初始化的数据科目数" + list.size());
            sleepTime = sleepTime - 5 * 1000;// 提前5秒开始计算学生状态
            Thread.sleep(sleepTime);

            log.info("上课了开始初始化学生的状态...");
            for (CourseScheduleDTO dto : list) {

                log.info("上课了开始初始化学生的状态..." + dto.getScheduleId());
                if (null == dto) {
                    continue;
                }
                Schedule schedule = scheduleRepository.findOne(dto.getScheduleId());
                if (null == schedule) {
                    log.info("上课初始化课程信息...，无排课信息" + schedule.getId());
                    continue;
                }
                beforeClassDoAnyThings(schedule);
            }
            log.info("上课了开始初始化学生的状态完毕...");
        } catch (Exception e) {
            log.warn("上课了开始初始化学生的状态失败，", e);
            log.warn("Exception", e);
        }
        log.info("课前初始化数据耗时： " + (System.currentTimeMillis() - beginTime) + "ms");
    }

    public Map<String, Object> beforeClassDoAnyThings(Schedule schedule) {
        Long schedueUseTime = System.currentTimeMillis();
        Boolean scheduleFlag = true;
        String status = "";
        String message = "";
        try {
            CourseRollCall courseRollCall = courseRollCallRepository.findByCourseIdAndTeacherIdAndDeleteFlag(schedule.getCourseId(), schedule.getTeacherId(), DataValidity.VALID.getState());
            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule);
            if (scheduleRollCall == null) {
                log.info("上课初始化课程信息...，scheduleRollCall is null" + schedule.getId());
                message = "scheduleRollCall is null,老师未开启该课程打卡机。";
                pushMonitor.addBeforeClass(schedule, (System.currentTimeMillis() - schedueUseTime), scheduleFlag, message, ScheduelStatusEnum.ScheduleStatuClose.getStatus());
                return ApiReturn.message(Boolean.FALSE, "scheduleRollcall is null", null);
            }
            Long scheduleRollcallId = scheduleRollCall.getId();
            if (null == courseRollCall) {
                log.info("上课初始化课程信息...，打卡机为关闭状态。" + schedule.getId());
                message = "打卡机为关闭状态";
                pushMonitor.addBeforeClass(schedule, (System.currentTimeMillis() - schedueUseTime), scheduleFlag, message, ScheduelStatusEnum.ScheduleStatuClose.getStatus());
                return ApiReturn.message(Boolean.FALSE, message, null);
            }
            if (CourseRollCallConstants.OPEN_ROLLCALL.equals(courseRollCall.getIsOpen())) {
                // 满足条件，进行初始化点名
                initScheduleService.initScheduleRollCall(schedule, Boolean.TRUE, null, null, null, null);
                status = ScheduelStatusEnum.ScheduleStatusOpen.getStatus();
            } else {
                status = ScheduelStatusEnum.ScheduleStatuClose.getStatus();
                message = "打卡机为关闭状态";
                log.info("未开启打卡机，清除缓存数据。-->" + schedule.getId());
                // 删除redis中缓存的数据
                List<RollCall> rollCallList = listRollCallBySRCIdInRedis(scheduleRollcallId);
                // 判断是否已经在 进行点名签到中
                if (null != rollCallList && rollCallList.size() > 0) {
                    redisTemplate.delete(RedisUtil.getScheduleRollCallKey(scheduleRollcallId));
                }
            }
            log.info("上课了开始初始化学生的状态成功..." + schedule.getId());
        } catch (Exception e) {
            log.warn("上课了开始初始化学生的状态失败", e);
            message = e.getMessage();
            scheduleFlag = Boolean.FALSE;
        }
        pushMonitor.addBeforeClass(schedule, (System.currentTimeMillis() - schedueUseTime), scheduleFlag, message, status);
        return ApiReturn.message(scheduleFlag, message, null);
    }

    private List<RollCall> listRollCallBySRCIdInRedis(Long scheduleRollCallId) {
        return redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }

    /**
     * 课后处理
     *
     * @param sleepTime
     * @param list
     */

    public void executeAfterClassPerTask(Long sleepTime, List<CourseScheduleDTO> list) {
        long beginTime = System.currentTimeMillis();
        try {
            log.info("等待下课!!!...," + sleepTime / 1000 + "秒," + "需要修改的数据科目数" + list.size());
            sleepTime = sleepTime - 5 * 1000;// 提前5秒开始计算学生状态
            Thread.sleep(sleepTime);
            updateStudentStatusAfterClass(list);
        } catch (Exception e) {
            log.warn("下课!!!了修改学生状态计算异常，" + e);
            log.warn("Exception", e);
        }
        log.info("课后写入数据库耗时： " + (System.currentTimeMillis() - beginTime) + "ms");

    }

    public void updateStudentStatusAfterClass(List<CourseScheduleDTO> list) {
        log.info("下课!!!修改学生的状态=================================start=================================");
        for (CourseScheduleDTO dto : list) {
            Schedule schedule = scheduleRepository.findOne(dto.getScheduleId());
            log.info("修改下课!!!排课ID为" + schedule.getId());
            if (null == schedule) {
                pushMonitor.addOutClass(schedule, 0L, Boolean.TRUE, "schedule为空,Id:" + dto.getScheduleId());
                continue;
            }
            outClassDoAnything(schedule, Boolean.FALSE);
        }
        log.info("下课!!!修改学生的状态=================================end=================================");
        list = null;
    }

    public Map<String, Object> outClassDoAnything(Schedule schedule, Boolean repaireFlag) {
        Long schedueUseTime = System.currentTimeMillis();
        Boolean scheduleFlag = true;
        String message = "";
        try {
            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule.getId());
            if (null == scheduleRollCall) {
                message = "scheduleRollcall为空，不计算入考勤。排课id为:" + schedule.getId();
                pushMonitor.addOutClass(schedule, (System.currentTimeMillis() - schedueUseTime), scheduleFlag, message);
                return ApiReturn.message(Boolean.TRUE, message, null);
            }
            if (scheduleRollCall != null) {
                if (scheduleRollCall.getIsOpenRollcall() != null && scheduleRollCall.getIsOpenRollcall()) {
                    log.warn("打卡机开启, 排课id为:" + schedule.getId());
                } else {
                    log.warn("打卡机没有开启, 排课id为:" + schedule.getId());
                }
//                List<Long> slss = studentLeaveScheduleService.findStudentIdByScheduleId(schedule.getId());
                Date startDate = DateFormatUtil.parse2(schedule.getTeachDate() + " " + schedule.getScheduleStartTime(), DateFormatUtil.FORMAT_MINUTE);
                Date endDate = DateFormatUtil.parse2(schedule.getTeachDate() + " " + schedule.getScheduleEndTime(), DateFormatUtil.FORMAT_MINUTE);
                List<StudentDTO> studentList = studentService.listStudents2(schedule.getTeachingclassId(), startDate, endDate);
                Map<Long, StudentDTO> studentDTOMap = new HashMap<>();
                if (studentList != null && studentList.size() > 0) {
                    for (StudentDTO studentDTO : studentList) {
                        if (studentDTO.getIsPrivateLeave() || studentDTO.getIsPublicLeave()) {
                            studentDTOMap.put(studentDTO.getStudentId(), studentDTO);
                        }
                    }
                }
                // 查询所有学生的签到信息
                List<RollCall> rollCallList = listRollCallBySRCIdInRedis(scheduleRollCall.getId());
                if (null != rollCallList && rollCallList.size() > 0) {
                    // 对没有学生进行签到处理的课，默认其为关闭该节课考勤，数据不处理。
                    int execptionlCount = 0;
                    for (RollCall rollCall : rollCallList) {
                        if (RollCallConstants.TYPE_UNCOMMITTED.equals(rollCall.getType()) || RollCallConstants.TYPE_ASK_FOR_LEAVE.equals(rollCall.getType()) || RollCallConstants.TYPE_CANCEL_ROLLCALL.equals(rollCall.getType())) {
                            execptionlCount++;
                        }
                    }
                    if (execptionlCount == rollCallList.size()) {
                        log.info("该节课，学生未进行签到操作，不计算入考勤。排课id为:" + schedule.getId() + ",execptionCount:" + execptionlCount);
                        scheduleRollCall.setIsInClassroom(Boolean.FALSE);
                        scheduleRollCall.setIsOpenRollcall(Boolean.FALSE);
                        scheduleRollCallService.save(scheduleRollCall, schedule.getId());
                        message = "该节课，学生未进行签到操作，不计算入考勤";
                        pushMonitor.addOutClass(schedule, (System.currentTimeMillis() - schedueUseTime), scheduleFlag, message);
                        return ApiReturn.message(Boolean.FALSE, message, null);
                    }
                    log.info("需要进行课后处理的课程：" + schedule.getId() + ",需要处理的学生数量为:" + rollCallList.size() + ",其类型为:" + scheduleRollCall.getRollCallType());
                    if (ScheduleConstants.TYPE_ROLL_CALL_DIGITAL.equals(scheduleRollCall.getRollCallType())) {
                        for (RollCall rollCall : rollCallList) {
                            String type = rollCall.getType();
                            if (!type.equals(RollCallConstants.TYPE_CANCEL_ROLLCALL)) {
                                rollCall.setLastType(type);
                                rollCall.setType(type.equals(RollCallConstants.TYPE_COMMITTED) ? RollCallConstants.TYPE_NORMA : type.equals(RollCallConstants.TYPE_UNCOMMITTED) ? RollCallConstants.TYPE_TRUANCY : type);
                                if (studentDTOMap.get(rollCall.getStudentId()) != null) {
                                    if (studentDTOMap.get(rollCall.getStudentId()).getIsPublicLeave()) {
                                        rollCall.setType(RollCallConstants.TYPE_CANCEL_ROLLCALL);
                                        rollCall.setIsPublicLeave(true);
                                    } else if (studentDTOMap.get(rollCall.getStudentId()).getIsPrivateLeave()) {
                                        rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                                    }
                                }
                            }
                        }
                    } else if (ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC.equals(scheduleRollCall.getRollCallType())) {
                        List<RollCall> reportCall = new ArrayList();
                        for (RollCall rollCall : rollCallList) {
                            if (rollCall.getHaveReport() && RollCallConstants.TYPE_COMMITTED.equals(rollCall.getType())) {
                                reportCall.add(rollCall);
                            } else {
                                if (studentDTOMap.get(rollCall.getStudentId()) != null) {
                                    if (studentDTOMap.get(rollCall.getStudentId()).getIsPublicLeave()) {
                                        rollCall.setType(RollCallConstants.TYPE_CANCEL_ROLLCALL);
                                        rollCall.setIsPublicLeave(true);
                                    } else if (studentDTOMap.get(rollCall.getStudentId()).getIsPrivateLeave()) {
                                        rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                                    }
                                } else {
                                    if (RollCallConstants.TYPE_UNCOMMITTED.equals(rollCall.getType()) || RollCallConstants.TYPE_EXCEPTION.equals(rollCall.getType())) {
                                        rollCall.setType(RollCallConstants.TYPE_TRUANCY);
                                    }
                                }
                            }
                        }
                        for (RollCall rollCall : reportCall) {
                            String type = rollCall.getType();
                            if (type.equals(RollCallConstants.TYPE_UNCOMMITTED)) {
                                rollCall.setType(RollCallConstants.TYPE_TRUANCY);
                            } else if (type.equals(RollCallConstants.TYPE_COMMITTED)) {
                                rollCall.setType(CourseUtils.getResultType(schedule.getScheduleStartTime(), scheduleRollCall.getCourseLaterTime() == null ? 15 : scheduleRollCall.getCourseLaterTime(), scheduleRollCall.getAbsenteeismTime(), rollCall.getSignTime()));
                            } else if (type.equals(RollCallConstants.TYPE_EXCEPTION)) {
                                rollCall.setType(RollCallConstants.TYPE_TRUANCY);
                            }
                            if (studentDTOMap.get(rollCall.getStudentId()) != null) {
                                if (studentDTOMap.get(rollCall.getStudentId()).getIsPublicLeave()) {
                                    rollCall.setType(RollCallConstants.TYPE_CANCEL_ROLLCALL);
                                    rollCall.setIsPublicLeave(true);
                                } else if (studentDTOMap.get(rollCall.getStudentId()).getIsPrivateLeave()) {
                                    rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                                }
                            }
                        }
                    }
                    if (null != rollCallList && rollCallList.size() > 0) {
                        for (RollCall rollCall : rollCallList) {
                            rollCall.setSemesterId(schedule.getSemesterId());
                            rollCall.setScheduleRollcallId(scheduleRollCall.getId());
                            List<RollCall> rollCallRe = (List<RollCall>) redisTemplate.opsForValue().get(RedisUtil.DIANDIAN_ROLLCALL + rollCall.getStudentId());
                            if (rollCallRe == null) {
                                rollCallRe = new ArrayList<>();
                            }
                            rollCallRe.add(rollCall);
                            redisTemplate.opsForValue().set(RedisUtil.DIANDIAN_ROLLCALL + rollCall.getStudentId(), rollCallRe, 15, TimeUnit.HOURS);
                        }
                    }
                    rollCallRepository.save(rollCallList);
                } else {
                    log.warn("无学生的签到信息, 排课id为:" + schedule.getId());
                }
                scheduleRollCall.setIsInClassroom(Boolean.FALSE);
                scheduleRollCallService.save(scheduleRollCall, schedule.getId());
                clearRedisInfo(scheduleRollCall.getId());
            } else {
                log.warn("无排课签到任务信息, 排课id为:" + schedule.getId());
            }
            log.info("下课!!!修改学生的状态成功..." + schedule.getId());
            // ===============redisRollCall===================
            deleteRedisRollCallIng(schedule.getOrganId(), scheduleRollCall.getId());
            //统计学生累计考勤
            log.info("统计学生累计考勤...", schedule.getOrganId(), schedule.getSemesterId(), schedule.getTeachingclassId());
            rollCallStatsService.statsStuTeachingClassByTeachingClass(schedule.getOrganId(), schedule.getSemesterId(), schedule.getTeachingclassId());
            rollCallStatsService.statsStuAllByScheduleRollCallId(scheduleRollCall.getId());
        } catch (Exception e) {
            log.warn("Exception", e);
            log.warn("课后处理数据异常", e);
            message = e.getMessage();
            scheduleFlag = false;
        }
        pushMonitor.addOutClass(schedule, (System.currentTimeMillis() - schedueUseTime), scheduleFlag, message);
        return ApiReturn.message(scheduleFlag, message, null);
    }

    private void deleteRedisRollCallIng(Long organId, Long scheduleRollCallId) {
        try {
            Set<Long> scheduleIds = (Set<Long>) redisTemplate.opsForHash().get(RedisUtil.getScheduleOrganId(), organId.longValue());
            if (null != scheduleIds && scheduleIds.size() > 0) {
                scheduleIds.remove(scheduleRollCallId);
                redisTemplate.opsForHash().put(RedisUtil.getScheduleOrganId(), organId.longValue(), scheduleIds);
            }

            stringRedisTemplate.expire(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId.longValue()), 5, TimeUnit.SECONDS);
            stringRedisTemplate.expire(RedisUtil.getScheduleRollCallDateKey(scheduleRollCallId.longValue()), 5, TimeUnit.SECONDS);

            stringRedisTemplate.delete(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId.longValue()));
            stringRedisTemplate.delete(RedisUtil.getScheduleRollCallDateKey(scheduleRollCallId.longValue()));

        } catch (Exception e) {
            log.warn("清除reids数据异常。" + e, e);
        }
    }

    public void clearRedisInfo(Long scheduleRollCallId) {
        stringRedisTemplate.expire(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), 5, TimeUnit.SECONDS);
        stringRedisTemplate.delete(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }

    public boolean isNormal(String beginTime, int lateTime, Date timestamp) {
        if (0 == lateTime) {
            lateTime = 1000;
        }
        try {
            String classBeginTime = DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT) + " " + beginTime;
            Date classBT = DateFormatUtil.parse(classBeginTime, "yyyy-MM-dd HH:mm");
            Long currentTime = 0l;
            if (timestamp == null) {
                currentTime = System.currentTimeMillis();
            } else {
                currentTime = timestamp.getTime();
            }
            long laterTime = (lateTime + 1) * 60 * 1000;
            Date date = new Date(currentTime - laterTime);
            if (classBT.before(date)) {
                // 迟到
                return false;
            } else {
                // 正常
                return true;
            }
        } catch (Exception e) {
            log.warn("Exception", e);
            log.warn("计算学生是否迟到异常，默认学生为未迟到!", e);
        }
        return true;
    }

    @Transactional(readOnly = false)
    public Schedule findOne(Long scheduleId) {
        Schedule schedule = null;
        try {
            schedule = (Schedule) redisTemplate.opsForValue().get(RedisUtil.getSchduleDominKey(scheduleId));
            if (schedule == null) {
                schedule = scheduleRepository.findOne(scheduleId);
                redisTemplate.opsForValue().set(RedisUtil.getSchduleDominKey(scheduleId), schedule, 1, TimeUnit.DAYS);
            }
        } catch (Exception e) {
            log.warn("获取排课信息异常，注意请检查数据。", e);
        }
        return schedule;
    }

    public List<Schedule> findAllByTeacherIdAndStatusAndSemesterId(Long teacherId, Long semesterId, Integer deleteFlag) {
        return scheduleRepository.findDistinctCourseIdByTeacherIdAndSemesterIdAndDeleteFlag(teacherId, semesterId, deleteFlag);
    }

    public List<Schedule> findAllByTeacherId(Long teacherId, Integer deleteFlag) {
        return scheduleRepository.findAllByTeacherIdAndDeleteFlag(teacherId, deleteFlag);
    }

    @Cacheable(value = "CACHE.SCHEDULEINFO", key = "#teachDate + #ids")
    public List<Schedule> findByteachingclassIdInAndTeachDateAndDeleteFlag(String ids, Set teachingclassIds, String teachDate, Integer deleteFlag, Sort sort) {
        return scheduleRepository.findByteachingclassIdInAndTeachDateAndDeleteFlag(teachingclassIds, teachDate, deleteFlag, sort);
    }

    public Map claim(Long teacherId, String teacherName, Long teachingclassId, String teachDate, Integer periodNo, Integer periodNum) {
        List<Schedule> schedules = scheduleRepository.findAllByTeachingclassIdAndTeachDateAndPeriodNoAndPeriodNumAndDeleteFlag(teachingclassId, teachDate, periodNo, periodNum,
                DataValidity.VALID.getState());
        Map result = new HashedMap();
        if (schedules != null && schedules.size() > 0) {
            long srcTeacher = 0;
            long orgId = 0;
            for (Schedule schedule : schedules) {
                ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule.getId());
                srcTeacher = schedule.getTeacherId();
                orgId = schedule.getOrganId();
                if (null == scheduleRollCall) {
                    // 未上过的课认领
                    schedule.setTeacherId(teacherId);
                    schedule.setTeacherNname(teacherName);
                } else if (scheduleRollCall != null && !scheduleRollCall.getIsInClassroom()) {
                    // 已上过的课认领
                    List<RollCall> rollCallList = rollCallRepository.findByScheduleRollcallId(scheduleRollCall.getId());
                    if (null != rollCallList && rollCallList.size() > 0) {
                        for (RollCall rollCall : rollCallList) {
                            rollCall.setTeacherId(teacherId);
                        }
                        rollCallRepository.save(rollCallList);
                    }
                    schedule.setTeacherId(teacherId);
                    schedule.setTeacherNname(teacherName);
                } else if (scheduleRollCall != null && scheduleRollCall.getIsInClassroom()) {
                    // 正在上的课认领
                    // 1.认领课程必须要开启打开机
                    CourseRollCall courseRollCall
                            = courseRollCallRepository.findByCourseIdAndTeacherIdAndDeleteFlag(schedule.getCourseId(), teacherId, DataValidity.VALID.getState());
                    if (courseRollCall == null) {
                        result.put(ApiReturnConstants.MESSAGE, "课程正在进行，需要先开启该课程的打卡机...");
                        result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                        return result;
                    }
                    if (CourseRollCallConstants.CLOSE_ROLLCALL.equals(courseRollCall.getIsOpen())) {
                        result.put(ApiReturnConstants.MESSAGE, "课程正在进行，需要先开启该课程的打卡机...");
                        result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                        return result;
                    }
                    initScheduleService.initScheduleRollCall(schedule, Boolean.TRUE, null, null, null, null);
                    schedule.setTeacherId(teacherId);
                    schedule.setTeacherNname(teacherName);
                }
                save(schedule);
            }
        }

        Claim temp = claimService.findBy(teachingclassId, teachDate, periodNo, periodNum);
        if (null != temp) {
            temp.setDeleteFlag(DataValidity.INVALID.getState());
            claimService.save(temp);
        }
        Claim claim = new Claim();
        claim.setTeacherId(teacherId);
        claim.setTeacherName(teacherName);
        claim.setTeachingclassId(teachingclassId);
        claim.setTeachDate(teachDate);
        claim.setPeriodNo(periodNo);
        claim.setPeriodNum(periodNum);
        claimService.save(claim);
        result.put(ApiReturnConstants.MESSAGE, "认领成功!");
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;
    }

    public List<Schedule> findAllByOrganIdAndTeachDateAndDeleteFlag(Long orgId, String teachDate) {
        return scheduleRepository.findAllByOrganIdAndTeachDateAndDeleteFlag(orgId, teachDate, DataValidity.VALID.getState());
    }
}
