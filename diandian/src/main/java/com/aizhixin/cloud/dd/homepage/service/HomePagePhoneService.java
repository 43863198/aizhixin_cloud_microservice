package com.aizhixin.cloud.dd.homepage.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.homepage.dto.HomePageDTO;
import com.aizhixin.cloud.dd.orgStructure.entity.ClassesTeacher;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClass;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassStudent;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.ClassesTeacherRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.repository.AttendanceListQuery;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRepository;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRollCallRepository;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RollCallUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class HomePagePhoneService {
    private final static Logger log = LoggerFactory.getLogger(HomePagePhoneService.class);
    @Autowired
    private ScheduleRollCallRepository scheduleRollCallRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private RollCallRepository rollCallRepository;
    @Autowired
    private ClassesTeacherRepository classesTeacherRepository;
    @Autowired
    private TeachingClassStudentRepository teachingClassStudentRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private WeekService weekService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private OrganSetService organSetService;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private AttendanceListQuery attendanceListQuery;
    @Autowired
    private TeachingClassRepository teachingClassRepository;

    public List getTodayCourse(AccountDTO account) {
        String teachTime = DateFormatUtil.formatShort(new Date());
        PageInfo page = getTeacherScheduleList(PageUtil.createNoErrorPageRequestAndSort(0, 10000, new Sort(new Sort.Order(Sort.Direction.ASC, "periodNo"))), account.getId(), account.getOrganId(), teachTime);
        List<HomePageDTO> homePageDTOS = page.getData();
        if (homePageDTOS != null && homePageDTOS.size() > 0) {
            return homePageDTOS.get(0).getCourseList();
        }
        return null;
    }

    public Map<String, Object> getTodayRollCall(AccountDTO account) {
        String teachTime = DateFormatUtil.formatShort(new Date());
        int truancy = 0; // 旷课
        int later = 0;// 迟到
        int askForLeave = 0;// 请假
        int leave = 0;// 早退

        Page<Schedule> page = scheduleRepository.findByTeacherIdAndTeachDateAndDeleteFlag(PageUtil.createNoErrorPageRequestAndSort(0, 10000, new Sort(new Sort.Order(Sort.Direction.ASC, "periodNo"))), account.getId(), teachTime, DataValidity.VALID.getState());
        List<Schedule> sdList = page.getContent();

        for (Schedule schedule : sdList) {
            ScheduleRollCall scheduleRollCall = findBySchedule(schedule.getId());
            if (scheduleRollCall != null) {
                List<RollCall> rollCalls = null;
                // 课堂内的签到数据在redis库中查询
                boolean inClass = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(schedule.getTeachDate()) && CourseUtils.classBeginTime(schedule.getScheduleStartTime()) && CourseUtils.classEndTime(schedule.getScheduleEndTime()));
                if (inClass) {
                    rollCalls = redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()));
                } else {
                    rollCalls = rollCallRepository.findByScheduleRollcallId(scheduleRollCall.getId());
                }
                for (RollCall rollCall : rollCalls) {
                    switch (rollCall.getType()) {
                        case RollCallConstants.TYPE_TRUANCY:
                            truancy++;
                            break;
                        case RollCallConstants.TYPE_LATE:
                            later++;
                            break;
                        case RollCallConstants.TYPE_ASK_FOR_LEAVE:
                            askForLeave++;
                            break;
                        case RollCallConstants.TYPE_LEAVE:
                            leave++;
                            break;
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("truancy", truancy);
        result.put("later", later);
        result.put("askForLeave", askForLeave);
        result.put("leave", leave);
        return result;
    }

    public Map<String, Object> getCounselorTodayRollCall(AccountDTO account) {
        String teachDate = DateFormatUtil.formatShort(new Date());
        int truancy = 0; // 旷课
        int later = 0;// 迟到
        int askForLeave = 0;// 请假
        int leave = 0;// 早退

        List<ClassesTeacher> classesTeacherList = classesTeacherRepository.findByUserId(account.getId());
        if (classesTeacherList != null && classesTeacherList.size() > 0) {
            Set<Long> classIds = new HashSet<>();
            for (ClassesTeacher item : classesTeacherList) {
                classIds.add(item.getClassesId());
            }
            List<UserInfo> userInfoList = userInfoRepository.findByClassesIdInAndUserType(classIds, 70);
            if (userInfoList != null && userInfoList.size() > 0) {
                Map<Long, UserInfo> stuMap = new HashMap<>();
                Set<Long> stuIds = new HashSet<>();
                for (UserInfo userInfo : userInfoList) {
                    stuMap.put(userInfo.getUserId(), userInfo);
                    stuIds.add(userInfo.getUserId());
                }
                List<TeachingClassStudent> teachingClassStudentList = teachingClassStudentRepository.findByStuIdIn(stuIds);
                if (teachingClassStudentList != null && teachingClassStudentList.size() > 0) {
                    Set<Long> teachingClassIds = new HashSet<>();
                    for (TeachingClassStudent ts : teachingClassStudentList) {
                        teachingClassIds.add(ts.getTeachingClassId());
                    }
                    List<Schedule> sdList = scheduleRepository.findByteachingclassIdInAndTeachDateAndDeleteFlag(teachingClassIds, teachDate, DataValidity.VALID.getState(), new Sort(new Sort.Order(Sort.Direction.ASC, "periodNo")));
                    for (Schedule schedule : sdList) {
                        ScheduleRollCall scheduleRollCall = findBySchedule(schedule.getId());
                        if (scheduleRollCall != null) {
                            boolean inClass = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(schedule.getTeachDate()) && CourseUtils.classBeginTime(schedule.getScheduleStartTime()) && CourseUtils.classEndTime(schedule.getScheduleEndTime()));
                            List<RollCall> rollCalls = null;
                            // 课堂内的签到数据在redis库中查询
                            if (inClass) {
                                rollCalls = redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()));
                            } else {
                                rollCalls = rollCallRepository.findByScheduleRollcallId(scheduleRollCall.getId());
                            }
                            if (rollCalls != null && rollCalls.size() > 0) {
                                for (RollCall rollCall : rollCalls) {
                                    if (stuMap.get(rollCall.getStudentId()) != null) {
                                        switch (rollCall.getType()) {
                                            case RollCallConstants.TYPE_TRUANCY:
                                                truancy++;
                                                break;
                                            case RollCallConstants.TYPE_LATE:
                                                later++;
                                                break;
                                            case RollCallConstants.TYPE_ASK_FOR_LEAVE:
                                                askForLeave++;
                                                break;
                                            case RollCallConstants.TYPE_LEAVE:
                                                leave++;
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("truancy", truancy);
        result.put("later", later);
        result.put("askForLeave", askForLeave);
        result.put("leave", leave);
        return result;
    }

    public List<PeriodDTO> getTodaySchedule(AccountDTO account) {
        List<PeriodDTO> list = redisTokenStore.getScheduleStudentToday(account.getId());
        if (list == null) {
            list = getTodayScheduleFormOrg(account);
            redisTokenStore.setScheduleStudentToday(account.getId(), list);
        }
        return list;
    }

    private List<PeriodDTO> getTodayScheduleFormOrg(AccountDTO account) {
        List<PeriodDTO> scheduleList = periodService.findAllByOrganIdAndStatusV2(account.getId(), account.getOrganId(), new Date());
        return scheduleList;
    }

    private PageInfo getTeacherScheduleList(Pageable pageable, Long teacherId, Long organId, String teachTime) {

        if (StringUtils.isBlank(teachTime)) {
            teachTime = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);
        }

        PageInfo pageInfo = PageInfo.getPageInfo(pageable);

        JSONObject currentSemesters = semesterService.getCurrentSemesters(organId, teachTime);
        if (currentSemesters == null) {
            List list = new ArrayList();
            HomePageDTO homePage = new HomePageDTO();
            homePage.setCourseList(new ArrayList());
            homePage.setTeach_time(teachTime);
            homePage.setDayOfWeek(String.valueOf(DateFormatUtil.getWeekOfDate(teachTime)));
            homePage.setWeekName(String.valueOf(0));

            list.add(homePage);
            pageInfo.setTotalCount(0L);
            pageInfo.setPageCount(0);
            pageInfo.setData(list);
            return pageInfo;
        }

        Page<Schedule> page = scheduleRepository.findByTeacherIdAndTeachDateAndDeleteFlag(pageable, teacherId, teachTime, DataValidity.VALID.getState());
        List<Schedule> sdList = page.getContent();
        Set<Long> scheduleRollCallIds = new HashSet<Long>();
        Map<Long, TeacherScheduleDTO> cMap = new HashMap<Long, TeacherScheduleDTO>();

        // 组装业务数据
        List teaSchList = new ArrayList();

        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);

        OrganSet organSet = organSetService.findByOrganId(organId);
        //classNames
        List<TeachingClass> teachingClassList = teachingClassRepository.findByOrgId(organId);
        Map<Long, String> teachingClassMap = new HashMap<>();
        if (teachingClassList != null && teachingClassList.size() > 0) {
            for (TeachingClass item : teachingClassList) {
                if (!StringUtils.isEmpty(item.getClassNames())) {
                    teachingClassMap.put(item.getTeachingClassId(), item.getClassNames());
                }
            }
        }
        for (Schedule schedule : sdList) {
            boolean flag = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(schedule.getTeachDate()) && CourseUtils.classBeginTime(schedule.getScheduleStartTime()) && CourseUtils.classEndTime(schedule.getScheduleEndTime()));
            TeacherScheduleDTO teacherScheduleDTO = new TeacherScheduleDTO();
            // 填充数据
            teacherScheduleDTO.setId(schedule.getId());
            teacherScheduleDTO.setWeekName(schedule.getWeekName());
            teacherScheduleDTO.setDayOfWeek(schedule.getDayOfWeek() + "");
            teacherScheduleDTO.setCourseName(schedule.getCourseName());
            teacherScheduleDTO.setClassRoom(schedule.getClassRoomName());
            teacherScheduleDTO.setClassBeginTime(schedule.getScheduleStartTime());
            teacherScheduleDTO.setClassEndTime(schedule.getScheduleEndTime());
            teacherScheduleDTO.setWhichLesson(CourseUtils.getWhichLesson(schedule.getPeriodNo(), schedule.getPeriodNum()));
            teacherScheduleDTO.setCoureseId(schedule.getCourseId());
            teacherScheduleDTO.setLessonOrderNum(schedule.getPeriodNo());

            ScheduleRollCall scheduleRollCall = findBySchedule(schedule.getId());
            if (null != scheduleRollCall) {
                if (!flag) {
                    flag = scheduleRollCall.getIsInClassroom();
                }
                if (scheduleRollCall.getIsOpenRollcall()) {
                    scheduleRollCall = scheduleRollCallRepository.findOne(scheduleRollCall.getId());
                }
                if (null != scheduleRollCall) {
                    Boolean rollCall = scheduleRollCall.getIsOpenRollcall();
                    teacherScheduleDTO.setLateTime(!rollCall ? 0 : (scheduleRollCall.getCourseLaterTime().intValue() > 500 ? 0 : scheduleRollCall.getCourseLaterTime()));
                    teacherScheduleDTO.setAbsenteeismTime(rollCall && scheduleRollCall.getAbsenteeismTime() != null ? scheduleRollCall.getAbsenteeismTime() : 0);
                    scheduleRollCallIds.add(scheduleRollCall.getId());
                    teacherScheduleDTO.setRollCall(rollCall);
                    teacherScheduleDTO.setRollcallType(scheduleRollCall.getRollCallType());
                    teacherScheduleDTO.setAttendance(scheduleRollCall.getAttendance());
                    cMap.put(scheduleRollCall.getId(), teacherScheduleDTO);
                }
            } else {
                teacherScheduleDTO.setLateTime(0);
                teacherScheduleDTO.setRollCall(Boolean.FALSE);
            }
            teacherScheduleDTO.setTeacher(schedule.getTeacherNname());
            teacherScheduleDTO.setTeach_time(schedule.getTeachDate());

            if (teachingClassMap.get(schedule.getTeachingclassId()) != null) {
                teacherScheduleDTO.setClassNames(teachingClassMap.get(schedule.getTeachingclassId()));
            }

            Map<String, Object> stusMap = orgManagerRemoteClient.listNotIncludeExceptionV2(schedule.getTeachingclassId(), 1, 10000);
            if (stusMap != null && stusMap.get("data") != null) {
                List list = (List) stusMap.get("data");
                if (list != null) {
                    teacherScheduleDTO.setTotalStu(Long.valueOf(list.size()));
                } else {
                    teacherScheduleDTO.setTotalStu(0L);
                }
            } else {
                teacherScheduleDTO.setTotalStu(0L);
            }
            if (flag) {
                teacherScheduleDTO.setClassrommRollCall(Boolean.TRUE);
                if (null != scheduleRollCall) {
                    List<RollCall> rollCalls = null;
                    // 计算该节课的考勤率
                    rollCalls = listRollCallBySRCIdInRedis(scheduleRollCall.getId());
                    if (rollCalls != null && rollCalls.size() > 0) {
                        teacherScheduleDTO.setTotalStu(Long.valueOf(rollCalls.size()));
                    }
                    teacherScheduleDTO.setAttendance(calculateAttendanceRollCall(rollCalls, organSet.getArithmetic() == null ? 10 : organSet.getArithmetic()));
                    teacherScheduleDTO.setRollcallStu(calculateRollCallStu(rollCalls, organSet.getArithmetic() == null ? 10 : organSet.getArithmetic()));
                }
            } else {
                teacherScheduleDTO.setClassrommRollCall(Boolean.FALSE);
                if (scheduleRollCall != null) {
                    List<RollCall> rollCalls = rollCallRepository.findByScheduleRollcallId(scheduleRollCall.getId());
                    if (rollCalls != null && rollCalls.size() > 0) {
                        teacherScheduleDTO.setTotalStu(Long.valueOf(rollCalls.size()));
                    }
                    teacherScheduleDTO.setRollcallStu(calculateRollCallStu(rollCalls, organSet.getArithmetic() == null ? 10 : organSet.getArithmetic()));
                }
            }
            if ("null".equals(teacherScheduleDTO.getAttendance()) || null == teacherScheduleDTO.getAttendance()) {
                teacherScheduleDTO.setAttendance("未点名");
            }
            teaSchList.add(teacherScheduleDTO);
        }

        pageInfo.setTotalCount(page.getTotalElements());
        pageInfo.setPageCount(page.getTotalPages());

        // 添加上过课的班级的考勤率
        List<AttendanceDTO> attendanceList = attendanceListQuery.queryScheduleAttendanceNew(scheduleRollCallIds);

        if (attendanceList != null && attendanceList.size() > 0) {
            for (AttendanceDTO attendanceDTO : attendanceList) {
                TeacherScheduleDTO dto = cMap.get(attendanceDTO.getScheduleRollCallId());
                if (dto != null) {
                    boolean flag = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(dto.getTeach_time()) && CourseUtils.classBeginTime(dto.getClassBeginTime()) && CourseUtils.classEndTime(dto.getClassEndTime()));
                    if (!flag) {
                        dto.setAttendance(RollCallUtils.AttendanceAccount(attendanceDTO.getAllCount(), attendanceDTO.getNormalCount(), attendanceDTO.getLaterCount(),
                                attendanceDTO.getAskForLeaveCount(), attendanceDTO.getLeaveCount(), organSet.getArithmetic() == null ? 10 : organSet.getArithmetic()));
                    }
                }
            }
        }
        // 组装界面数据
        try {

            HomePageDTO homePage = new HomePageDTO();
            List ls = new ArrayList();
            DateTime dateTime = new DateTime();
            if (StringUtils.isNotBlank(teachTime)) {
                dateTime = DateTime.parse(teachTime);
            }

            String weekName = "";
            if (null != sdList && sdList.size() > 0) {
                // 封装数据
                Schedule schedule = (Schedule) sdList.get(0);
                // 缺少当天课程数据
                homePage.setTeach_time(schedule.getTeachDate());
                homePage.setDayOfWeek(String.valueOf(schedule.getDayOfWeek()));
                weekName = schedule.getWeekName();
                if ("null".equals(weekName)) {
                    weekName = "";
                }
                if (StringUtils.isBlank(weekName)) {
                    WeekDTO week = weekService.getWeek(organId, teachTime == DateFormatUtil.formatShort(new Date()) ? null : teachTime);
                    if (null != week) {
                        weekName = week.getName();
                    } else {
                        weekName = "0";
                    }
                }

            } else {
                homePage.setTeach_time(teachTime);
                homePage.setDayOfWeek(String.valueOf(dateTime.getDayOfWeek()));
                WeekDTO week = weekService.getWeek(organId, teachTime == DateFormatUtil.formatShort(new Date()) ? null : teachTime);
                if (null != week) {
                    weekName = week.getName();
                } else {
                    weekName = "0";
                }
            }

            homePage.setWeekName(weekName);
            homePage.setCourseList(teaSchList);
            ls.add(homePage);
            pageInfo.setData(ls);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pageInfo;
    }

    private ScheduleRollCall findBySchedule(Long scheduleId) {
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

    private List<RollCall> listRollCallBySRCIdInRedis(Long scheduleRollCallId) {
        return redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }

    private String calculateAttendanceRollCall(List<RollCall> rollCalls, int type) {
        String result = null;
        if (rollCalls == null || rollCalls.isEmpty()) {
            return result;
        }
        if (null != rollCalls && !rollCalls.isEmpty()) {
            int total = rollCalls.size();
            int normal = 0;
            int later = 0;
            int askForLeave = 0;
            int leave = 0;
            for (RollCall rollCall : rollCalls) {
                switch (rollCall.getType()) {
                    case RollCallConstants.TYPE_NORMA:
                        normal++;
                        break;
                    case RollCallConstants.TYPE_LATE:
                        later++;
                        break;
                    case RollCallConstants.TYPE_ASK_FOR_LEAVE:
                        askForLeave++;
                        break;
                    case RollCallConstants.TYPE_LEAVE:
                        leave++;
                        break;
                }
            }
            result = RollCallUtils.AttendanceAccount(total, normal, later, askForLeave, leave, type);
        }
        return result;
    }

    private Integer calculateRollCallStu(List<RollCall> rollCalls, int type) {
        Integer result = 0;
        if (rollCalls == null || rollCalls.isEmpty()) {
            return result;
        }
        for (RollCall rollCall : rollCalls) {
            switch (rollCall.getType()) {
                case RollCallConstants.TYPE_NORMA:
                    result++;
                    break;
                case RollCallConstants.TYPE_LATE:
                    result++;
                    break;
                case RollCallConstants.TYPE_COMMITTED:
                    result++;
                    break;
            }
        }
        return result;
    }
}
