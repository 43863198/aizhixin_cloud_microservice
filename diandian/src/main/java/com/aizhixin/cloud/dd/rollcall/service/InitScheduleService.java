package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.monitor.service.PushMonitor;
import com.aizhixin.cloud.dd.monitor.v1.controller.MonitorController;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClass;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassStudent;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.*;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRepository;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRollCallRepository;
import com.aizhixin.cloud.dd.rollcall.repository.StudentLeaveScheduleRepository;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.GDMapUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 进行当天课程信息调度
 *
 * @author meihua.li
 */
@Component
public class InitScheduleService {

    private final static Logger log = LoggerFactory.getLogger(InitScheduleService.class);

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private CourseRollCallService courseRollCallService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ScheduleRollCallRepository scheduleRollCallRepository;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private ClaimService claimService;
    @Autowired
    private StudentLeaveScheduleRepository studentLeaveScheduleRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private TeachingClassRepository teachingClassRepository;
    @Autowired
    private TeachingClassStudentRepository teachingClassStudentRepository;
    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentLeaveScheduleService studentLeaveScheduleService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private OrganSetService organSetService;
    @Lazy
    @Autowired
    private PushMonitor pushMonitor;

    public Boolean checkDayDataTask() {
        Boolean status = redisTokenStore.getInitScheduleStatus(DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT));
        return status;
    }

    public void initSchedule() {
        log.info("开始初始化当天的排课信息...");
        Long start = System.currentTimeMillis();
        // 查询系统中所有学校
        List<IdNameDomain> orgAll = orgManagerRemoteService.findAllOrg();
        // 该处可以优化，进行多线程处理 ==========================================
        if (orgAll != null && orgAll.size() > 0) {
            for (IdNameDomain idNameDomain : orgAll) {
                try {
                    executeTask(idNameDomain.getId(), idNameDomain.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("初始化数据失败，组织机构id:({})", idNameDomain.getId());
                }
            }
        }
        log.info("当天的排课信息初始化结束,总用时:" + (System.currentTimeMillis() - start) + "ms");
        redisTokenStore.setInitScheduleStatus(DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT), true);
    }

    public void executeTask(Long orgId, String name) {
        pushMonitor.clearDaybreak(orgId, DateFormatUtil.formatShort(new Date()));
        String currentDate = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);
        Long semesterId = null;
        try {
            semesterId = semesterService.getSemesterId(orgId);
        } catch (Exception e) {
            log.warn("获取学期信息异。", e);
            return;
        }
        if (null == semesterId) {
            log.info("该学校没有设置学期信息，不需要进行初始化排课操作。学校id为：" + orgId + ",名称为：" + name);
            return;
        }
        // 清除学校中值信息
        Set<Long> tempSet = new HashSet<>();
        redisTemplate.opsForHash().put(RedisUtil.getScheduleOrganId(), orgId, tempSet);
        log.debug("开始执行初始化操作.....................................,组织机构Id:" + orgId);
        List<DianDianSchoolTimeDomain> ddList = orgManagerRemoteService.findSchoolTimeDay(orgId, semesterId, currentDate);
        List<PeriodDTO> periods = periodService.listPeriod(orgId);
        // 将数据直接入库。
        if (ddList != null && ddList.size() > 0) {
            if (log.isInfoEnabled()) {
                log.info("需要初始化排课信息的组织机构:" + orgId + " , " + name);
            }
            Schedule schedule = null;
            for (DianDianSchoolTimeDomain ddDomain : ddList) {
                try {
                    if (ddDomain.getSemesterId().intValue() == semesterId.intValue()) {
                        addSchedule(orgId, currentDate, periods, ddDomain);
                    }
                } catch (Exception e) {
                    log.warn("排课失败，请检查基础数据" + JSONArray.fromObject(ddList).toString(), e);
                }
            }
        }

        refStuTodaySchedule(orgId);
    }

    public void refStuTodaySchedule(Long orgId) {
        List<UserInfo> stus = userInfoRepository.findByOrgIdAndUserType(orgId, 70);
        if (stus != null && stus.size() > 0) {
            for (UserInfo stu : stus) {
                List<PeriodDTO> scheduleList = periodService.findAllByOrganIdAndStatusV2(stu.getUserId(), orgId, new Date());
                redisTokenStore.setScheduleStudentToday(stu.getUserId(), scheduleList);
                //TODO: 修改成消息模式发送
            }
        }
    }

    public void refStuTodayScheduleByTeachingClass(Long teachingClassId) {
        TeachingClass teachingClass = teachingClassRepository.findByTeachingClassId(teachingClassId);
        if (teachingClass != null) {
            List<TeachingClassStudent> students = teachingClassStudentRepository.findByTeachingClassId(teachingClassId);
            if (students != null && students.size() > 0) {
                for (TeachingClassStudent stu : students) {
                    List<PeriodDTO> scheduleList = periodService.findAllByOrganIdAndStatusV2(stu.getStuId(), teachingClass.getOrgId(), new Date());
                    redisTokenStore.setScheduleStudentToday(stu.getStuId(), scheduleList);
                    //TODO: 修改成消息模式发送
                }
            }
        }

    }

    public boolean addSchedule(Long orgId, String currentDate, List<PeriodDTO> periods, DianDianSchoolTimeDomain ddDomain) {

        Long beginTime = System.currentTimeMillis();
        String message = "";
        Schedule schedule = new Schedule();
        try {
            schedule.setOrganId(orgId);
            schedule.setCourseId(ddDomain.getCourseId());
            schedule.setCourseName(ddDomain.getCourseName());
            IdNameDomain teacher = null;
            Claim claim = claimService.findByDb(ddDomain.getTeachingClassId(), currentDate, ddDomain.getPeriodNo(), ddDomain.getPeriodNum());
            if (null != claim) {
                teacher = new IdNameDomain(claim.getTeacherId(), claim.getTeacherName());
            } else {
                teacher = parseTeacher(ddDomain.getTeachers());
            }
            schedule.setTeacherId(teacher.getId());
            schedule.setTeacherNname(teacher.getName());
            schedule.setSemesterId(ddDomain.getSemesterId());
            schedule.setSemesterName(ddDomain.getSemesterName());
            schedule.setWeekId(ddDomain.getWeekId());
            schedule.setWeekName(ddDomain.getWeekNo() + "");
            schedule.setDayOfWeek(ddDomain.getDayOfWeek());
            schedule.setPeriodId(ddDomain.getPeriodId());
            schedule.setPeriodNo(ddDomain.getPeriodNo());
            schedule.setPeriodNum(ddDomain.getPeriodNum());

            // 计算课程开始时间
            PeriodDTO period = (PeriodDTO) periods.get(ddDomain.getPeriodNo() - 1);
            if (null == period) {
                throw new Exception();
            } else if (!ddDomain.getPeriodId().equals(period.getId())) {
                System.out.println("课程节处理异常!");
                throw new Exception();
            }
            System.out.println(ddDomain.getPeriodNo() + " " + period.getStartTime() + " " + period.getEndTime() + " " + ddDomain.getPeriodNum());

            schedule.setScheduleStartTime(period.getStartTime());
            schedule.setScheduleEndTime((periods.get(ddDomain.getPeriodNo() + ddDomain.getPeriodNum() - 2).getEndTime()));
            schedule.setClassRoomName(ddDomain.getClassroom());
            schedule.setTeachDate(currentDate);
            schedule.setTeachingclassId(ddDomain.getTeachingClassId());
            schedule.setTeachingclassCode(ddDomain.getTeachingClassCode());
            schedule.setTeachingclassName(ddDomain.getTeachingClassName());
            schedule.setIsInitRollcall(Boolean.FALSE);
            try {
                List<Schedule> scheduleList = findByTeacherIdAndCourseIdAndTeachDateAndPeriodIdAndPeriodNum(schedule.getTeacherId(), schedule.getCourseId(),
                        schedule.getTeachDate(), schedule.getPeriodId(), schedule.getPeriodNum());
                if (null != scheduleList && !scheduleList.isEmpty()) {
                    for (Schedule sch : scheduleList) {
                        scheduleRollCallRepository.deleteByScheduleId(sch.getId());
                        scheduleRepository.delete(sch.getId());
                        pushMonitor.deleteByScheduleId(sch.getId());
                    }
                }
            } catch (Exception e) {
                log.warn("删除重复数据异常");
            }

            schedule = scheduleRepository.save(schedule);

            //学生请假更新排课id
            Date d = new Date();
            String str = formatDate(d);
            d = formatDate(str);
            if (d != null) {
                List<StudentLeaveSchedule> leaveList = studentLeaveScheduleRepository.findByTeacherIdAndCourseIdAndRequesDateAndRequestPeriodIdAndDeleteFlag(teacher.getId(), schedule.getCourseId(), d, period.getId(), DataValidity.VALID.getState());
                if (leaveList != null && leaveList.size() > 0) {
                    for (StudentLeaveSchedule item : leaveList) {
                        item.setScheduleId(schedule.getId());
                    }
                    studentLeaveScheduleRepository.save(leaveList);
                }
            }

            // 根据教师对该课程的设置是否进行该课程的初始化点名操作。
            initScheduleRollCall(schedule, Boolean.FALSE, null, null, null, null);
        } catch (Exception e) {
            log.warn("排课异常.", e);
            e.printStackTrace();
            message = e.getMessage();
        }
        pushMonitor.pushMonitor(schedule, (System.currentTimeMillis() - beginTime), StringUtils.isBlank(message), message);
        return false;
    }

    private List<Schedule> findByTeacherIdAndCourseIdAndTeachDateAndPeriodIdAndPeriodNum(Long teacherId, Long courseId, String teachDate, Long periodId, Integer periodNum) {
        return scheduleRepository.findByTeacherIdAndCourseIdAndTeachDateAndPeriodIdAndPeriodNumAndDeleteFlag(teacherId, courseId, teachDate, periodId, periodNum,
                DataValidity.VALID.getState());
    }

    private String formatDate(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return f.format(date);
    }

    private Date formatDate(String str) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        try {
            return f.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IdNameDomain parseTeacher(String teachers) {
        IdNameDomain teacherDo = new IdNameDomain();
        // 22,李群;33,李丽
        if (StringUtils.isBlank(teachers)) {
            return teacherDo;
        }

        String[] teacher = teachers.split(";");
        if (teacher.length > 0) {
            long teacherId = 0l;
            String teacherName = "";
            for (int i = 0, len = teacher.length; i < len; i++) {
                String[] teacherPro = teacher[i].split(",");
                long teacherIdTemp = Long.parseLong(teacherPro[0] == null ? "0" : teacherPro[0]);
                String teacherNameTemp = teacherPro[1] == null ? "" : teacherPro[1];
                if (i == 0) {
                    teacherId = teacherIdTemp;
                    teacherName = teacherNameTemp;
                }
                if (teacherId > teacherIdTemp) {
                    teacherId = teacherIdTemp;
                    teacherName = teacherNameTemp;
                    continue;
                }
            }
            teacherDo.setId(teacherId);
            teacherDo.setName(teacherName);
        }
        return teacherDo;
    }

    public static List<IdNameDomain> parseTeacherList(String teachers) {
        List list = new ArrayList();
        String[] teacher = teachers.split(";");
        if (teacher.length > 0) {
            for (int i = 0, len = teacher.length; i < len; i++) {
                String[] teacherPro = teacher[i].split(",");
                long teacherIdTemp = Long.parseLong(teacherPro[0] == null ? "0" : teacherPro[0]);
                String teacherNameTemp = teacherPro[1] == null ? "" : teacherPro[1];
                list.add(new IdNameDomain(teacherIdTemp, teacherNameTemp));
            }
        }
        return list;
    }

    public static IdNameDomain parseTeacherZhangJM(String teachers, String execpId) {
        IdNameDomain teacherDo = new IdNameDomain();
        if (StringUtils.isBlank(teachers)) {
            return teacherDo;
        }

        String[] teacher = teachers.split(";");
        if (teacher.length > 0) {
            for (int i = 0; i < teacher.length; i++) {
                String[] teacherPro = teacher[i].split(",");
                if (execpId.equals(teacherPro[0])) {
                    teacherDo.setId(Long.parseLong(teacherPro[0] == null ? "0" : teacherPro[0]));
                    teacherDo.setName(teacherPro[1] == null ? "" : teacherPro[1]);
                    break;
                }
            }
        }
        return teacherDo;
    }

    /**
     * 初始化排课的点名信息
     *
     * @param schedule
     * @return
     */
    public Boolean initScheduleRollCall(Schedule schedule, Boolean isInClass, Integer lateTime, Integer absenteeismTime, String rollCallType, String isOpen) {
        try {
            if (null == schedule) {
                return true;
            }
            //基础点名规则
            CourseRollCall courseRollCall = courseRollCallService.get(schedule.getCourseId(), schedule.getTeacherId());
            if (null == courseRollCall) {
                if (log.isDebugEnabled()) {
                    log.debug("老师未开启点名，不需要初始化。课程id为:" + schedule.getId());
                }
                return Boolean.TRUE;
            }

            if (null == isOpen) {
                if (!CourseRollCallConstants.OPEN_ROLLCALL.equals(courseRollCall.getIsOpen())) {
                    if (log.isDebugEnabled()) {
                        log.debug("老师已关闭点名，不需要初始化!-->" + schedule.getId());
                    }
                    return Boolean.TRUE;
                }
            }
            //每节课的点名规则
            ScheduleRollCall scheduleRollCall = scheduleRollCallRepository.findBySchedule_Id(schedule.getId());
            redisTemplate.opsForValue().set(RedisUtil.getSchduleRollCallDominKey(schedule.getId()), scheduleRollCall, 1, TimeUnit.DAYS);
            if (null == scheduleRollCall) {
                scheduleRollCall = new ScheduleRollCall();
            }

            if (scheduleRollCall.getIsInClassroom() != null && scheduleRollCall.getIsInClassroom() && scheduleRollCall.getClassRoomRollCall() != null && (scheduleRollCall.getClassRoomRollCall() == CourseRollCallConstants.OPEN_CLASSROOMROLLCALL || scheduleRollCall.getClassRoomRollCall() == CourseRollCallConstants.CLOSED_CLASSROOMROLLCALL)) {
                if (log.isDebugEnabled()) {
                    log.debug("已开启过点名组，不需要初始化!-->" + schedule.getId());
                }
                return Boolean.TRUE;
            }

            // 进行该排课点名信息的初始化操作。
            scheduleRollCall.setSchedule(schedule);
            scheduleRollCall.setRollCallType(null == rollCallType ? courseRollCall.getRollCallType() : rollCallType);
            scheduleRollCall.setCourseLaterTime(null == lateTime ? courseRollCall.getLateTime() : lateTime);
            scheduleRollCall.setAbsenteeismTime(null == absenteeismTime ? courseRollCall.getAbsenteeismTime() : absenteeismTime);
            scheduleRollCall.setClassRoomRollCall(CourseRollCallConstants.NOT_OPEN_CLASSROOMROLLCALL);
            scheduleRollCall.setIsOpenRollcall(Boolean.FALSE);
            scheduleRollCall.setIsInClassroom(Boolean.FALSE);
            scheduleRollCall.setLocaltion("");
            if (isInClass) {
                scheduleRollCall.setIsOpenRollcall(isInClass);
                scheduleRollCall.setIsInClassroom(isInClass);
            }

            scheduleRollCallRepository.save(scheduleRollCall);

            // ==========================reidsRollCall==============================
            // 将上课的id放入reids,定时任务后续将计算中值 排课点名id
            Set<Long> scheduleRollCallIds = (Set<Long>) redisTemplate.opsForHash().get(RedisUtil.getScheduleOrganId(), schedule.getOrganId().longValue());
            if (null == scheduleRollCallIds) {
                scheduleRollCallIds = new HashSet<>();
            }
            scheduleRollCallIds.add(scheduleRollCall.getId());
            redisTemplate.opsForHash().put(RedisUtil.getScheduleOrganId(), schedule.getOrganId(), scheduleRollCallIds);
            // 临时方案，将数据缓存redis
            redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallDateKey(scheduleRollCall.getId().longValue()),
                    scheduleRollCall.getId(),
                    new ScheduleRollCallIngDTO(schedule.getScheduleStartTime(), scheduleRollCall.getCourseLaterTime(), scheduleRollCall.getAbsenteeismTime()));
            redisTemplate.expire(RedisUtil.getScheduleRollCallDateKey(scheduleRollCall.getId().longValue()), 24, TimeUnit.HOURS);

            if (initRollCall(scheduleRollCall)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("初始化排课的点名信息失败," + schedule.getId() + "," + e.getMessage());
        }
        return false;
    }

    public ScheduleRollCall findBySchedule(Long scheduleId) {
        ScheduleRollCall scheduleRollCall = null;
        try {
            scheduleRollCall = (ScheduleRollCall) redisTemplate.opsForValue().get(RedisUtil.getSchduleRollCallDominKey(scheduleId));
            if (scheduleRollCall == null) {
                scheduleRollCall = scheduleRollCallRepository.findBySchedule_Id(scheduleId);
                redisTemplate.opsForValue().set(RedisUtil.getSchduleRollCallDominKey(scheduleId), scheduleRollCall, 1, TimeUnit.DAYS);
            }
        } catch (Exception e) {
            log.warn("获取排课信息的点名规则信息异常，请注意检查数据。", e);
        }
        return scheduleRollCall;
    }

    public boolean initRollCall(ScheduleRollCall scheduleRollCall) {
        if (null == scheduleRollCall) {
            throw new NullPointerException();
        }
        List<RollCall> rollCallList = listRollCallBySRCIdInRedis(scheduleRollCall.getId());
        // 判断是否已经在 进行点名签到中
        if (null != rollCallList && rollCallList.size() > 0) {
            redisTemplate.delete(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()));
        }
        Schedule schedule = scheduleRollCall.getSchedule();
        Long teachingclassId = schedule.getTeachingclassId();
        List<StudentDTO> studentList = studentService.listStudents2(teachingclassId);
        if (null == studentList) {
            log.info("根据教学班id获取学生列表信息为空!" + schedule.getId());
            return false;
        }
        List<Long> studentLeaves = studentLeaveScheduleService.findStudentIdByScheduleId(schedule.getId());
        RollCall rollCall = null;
        Map<Long, RollCall> rollCallMap = new HashMap();
        for (StudentDTO dto : studentList) {
            rollCall = new RollCall();
            rollCall.setId(RedisUtil.getRollCallId());
            rollCall.setScheduleRollcallId(scheduleRollCall.getId());
            rollCall.setTeacherId(schedule.getTeacherId());
            long studentId = dto.getStudentId();
            rollCall.setStudentId(studentId);
            rollCall.setStudentNum(dto.getSutdentNum());
            rollCall.setClassId(dto.getClassesId());
            rollCall.setClassName(dto.getClassesName());
            rollCall.setTeachingClassId(teachingclassId);
            rollCall.setCourseId(schedule.getCourseId());
            rollCall.setCanRollCall(Boolean.TRUE);
            rollCall.setHaveReport(Boolean.FALSE);
            rollCall.setProfessionalId(dto.getProfessionalId());
            rollCall.setProfessionalName(dto.getProfessionalName());
            rollCall.setCollegeId(dto.getCollegeId());
            rollCall.setCollegeName(dto.getCollegeName());
            rollCall.setTeachingYear(dto.getTeachingYear());
            rollCall.setOrgId(schedule.getOrganId());
            // 判断该学生是否有请假
            if (studentLeaves != null && studentLeaves.contains(dto.getStudentId())) {
                rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
            } else {
                rollCall.setType(RollCallConstants.TYPE_UNCOMMITTED);
            }
            rollCall.setStudentName(dto.getStudentName());
            rollCall.setSemesterId(schedule.getSemesterId());
            rollCallMap.put(studentId, rollCall);
        }
        redisTemplate.opsForHash().putAll(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCallMap);
        redisTemplate.expire(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), 24, TimeUnit.HOURS);
        scheduleRollCallRepository.save(scheduleRollCall);
        schedule.setIsInitRollcall(Boolean.TRUE);
        scheduleRepository.save(schedule);
        log.info("初始化学生签到信息完毕。" + schedule.getId());
        return true;
    }

    private List<RollCall> listRollCallBySRCIdInRedis(Long scheduleRollCallId) {
        return redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }

    public void checkRollCallTypeSchedule() {
        //获取中值任务
        Object run = redisTemplate.opsForValue().get(RedisUtil.getScheduleTask());
        if (null == run) {
            redisTemplate.opsForValue().append(RedisUtil.getScheduleTask(), RedisUtil.DIANDIAN_TASK_RUNNING);
        } else {
            String str = String.valueOf(run);
            if (RedisUtil.DIANDIAN_TASK_RUNNING.equals(str)) {
                log.debug("计算任务正在执行中，等待其上一次执行结束...");
                return;
            }
        }

        Set<Long> members = redisTemplate.opsForHash().keys(RedisUtil.getScheduleOrganId());
        if (null != members && members.size() > 0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Long start = System.currentTimeMillis();
                    try {
                        Iterator<Long> iterator = members.iterator();
                        while (iterator.hasNext()) {
                            Long orgId = iterator.next();

                            OrganSet organSet = organSetService.findByOrganId(orgId);
                            int countNum = 11;
                            if (null != organSet) {
                                countNum = organSet.getCalcount();
                            }
                            Set<Long> scheduleIds = (Set<Long>) redisTemplate.opsForHash().get(RedisUtil.getScheduleOrganId(), orgId.longValue());
                            if (null != scheduleIds && scheduleIds.size() > 0) {
                                log.info("中值计算列表:" + StringUtils.join(scheduleIds.toArray(), ","));
                                Iterator<Long> schedueleIterator = scheduleIds.iterator();
                                while (schedueleIterator.hasNext()) {
                                    Long scheduleRollCallId = schedueleIterator.next().longValue();
                                    Set keys = redisTemplate.opsForHash().keys(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId.longValue()));
                                    if (null != keys && keys.size() >= countNum) {
                                        List<LocaltionDTO> values = redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId.longValue()));
                                        count(values, orgId, scheduleRollCallId);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.info("计算中值异常", e);
                    } finally {
                        redisTemplate.opsForValue().getAndSet(RedisUtil.getScheduleTask(), RedisUtil.DIANDIAN_TASK_UNRUNNING);
                    }
                    Long end = System.currentTimeMillis();
                    log.debug("执行计算中值耗时为：" + (end - start) + "ms");
                }
            });
            t.start();
        } else {
            redisTemplate.opsForValue().getAndSet(RedisUtil.getScheduleTask(), RedisUtil.DIANDIAN_TASK_UNRUNNING);
        }
    }

    public void count(List<LocaltionDTO> list, Long organId, Long scheduleRollCallId) {
        // 开始计算中值以及置信度
        GDMapUtil gdMap = new GDMapUtil();
        for (LocaltionDTO localtionDTO : list) {
            gdMap.put(localtionDTO.getLo());
        }
        OrganSet organSet = organSetService.findByOrganId(organId);
        int deviation = 500;
        int confilevel = 60;
        if (null != organSet) {
            deviation = organSet.getDeviation();
            confilevel = organSet.getConfilevel();
        }
        int level = gdMap.getConfiLevel(deviation);
        if (level < confilevel) {
            log.info("计算中值，合格距离" + deviation + ",未满足置信度:" + confilevel + ",实际置信度为:" + level + "。其中排课id为:" + scheduleRollCallId);
            return;
        }

        String midValu = gdMap.getMidValue();
        log.info("计算中值，合格距离" + deviation + ",满足置信度:" + confilevel + ",实际置信度为:" + level + "。其中排课id为:" + scheduleRollCallId + "中值为：" + midValu);

        scheduleRollCallRepository.updateScheduleVerify(scheduleRollCallId, gdMap.getMidDistribution());
        ScheduleRollCallIngDTO dto = (ScheduleRollCallIngDTO) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallDateKey(scheduleRollCallId), scheduleRollCallId);

        updateRollcall(organId, scheduleRollCallId, gdMap, deviation, dto);
    }


    private void updateRollcall(Long organId, Long scheduleRollCallId, GDMapUtil gdMap, int deviation, ScheduleRollCallIngDTO dto) {
//        List<LocaltionDTO> list = redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId.longValue()));
        List<RollCall> list = redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
        for (RollCall localtionDTO : list) {
            updateStuRollcall(localtionDTO, scheduleRollCallId, gdMap, deviation, dto);
        }
        deleteRedisRollCallIng(organId, scheduleRollCallId);
    }

    @Async("threadPool1")
    private void updateStuRollcall(RollCall rcs, Long scheduleRollCallId, GDMapUtil gdMap, int deviation, ScheduleRollCallIngDTO dto) {
        Long studentId = rcs.getStudentId();
//        RollCall rcs = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), studentId);
        rcs.setLastType(rcs.getType());
        double distance = gdMap.compareMid(rcs.getGpsLocation());
        int dis = (int) (distance / 10);
        if (dis < 1) {
            dis = 1;
        }
        dis = Integer.parseInt(String.valueOf(dis) + "0");
        // 结果
        if (distance < deviation) {
            rcs.setType(CourseUtils.getResultType(dto.getBeginTime(), dto.getLateTime(), dto.getAbsenteeismTime(), rcs.getSignTime()));
            rcs.setDistance("  <" + dis + "m");
        } else {
            rcs.setType(RollCallConstants.TYPE_EXCEPTION);
            rcs.setDistance("  >" + deviation + "m");
        }
        redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), rcs.getStudentId(), rcs);
    }

    public void deleteRedisRollCallIng(Long organId, Long scheduleRollCallId) {
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
            log.warn("清除reids数据异常。" + e.getMessage(), e);
        }
    }
}
