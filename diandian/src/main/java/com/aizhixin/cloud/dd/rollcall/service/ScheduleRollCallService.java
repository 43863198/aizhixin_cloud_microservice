package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.CountDomain;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.homepage.dto.HomePageDTO;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClass;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.*;
import com.aizhixin.cloud.dd.rollcall.repository.*;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RollCallUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Transactional
public class ScheduleRollCallService {
    private final static Logger log = LoggerFactory.getLogger(ScheduleRollCallService.class);
    @Autowired
    private ScheduleRollCallRepository scheduleRollCallRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AttendanceListQuery attendanceListQuery;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AssessRepository assessRepository;
    @Autowired
    private WeekService weekService;
    @Autowired
    private OrganSetService organSetService;
    @Autowired
    private ScheduleQuery scheduleQuery;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private TeachingClassRepository teachingClassRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RollCallRepository rollCallRepository;

    /**
     * 教师端获取某一天的课程列表
     *
     * @param pageable
     * @param teacherId
     * @param organId
     * @param teachTime
     * @return
     */
    public PageInfo getTeacherScheduleList(Pageable pageable, Long teacherId, Long organId, String teachTime) {

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

            ScheduleRollCall scheduleRollCall = findBySchedule(schedule);
            if (null != scheduleRollCall) {
                if (!flag) {
                    flag = scheduleRollCall.getIsInClassroom();
                }
                if (scheduleRollCall.getIsOpenRollcall()) {
                    scheduleRollCall = findOne(scheduleRollCall.getId());
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

            Map<String, Object> stusMap = orgManagerRemoteService.listNotIncludeExceptionV2(schedule.getTeachingclassId(), 1, 10000);
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
                    teacherScheduleDTO.setAttendance(calculateAttendanceRollCall(rollCalls, organSet.getArithmetic() == null ? 10 : organSet.getArithmetic()));
                    teacherScheduleDTO.setRollcallStu(calculateRollCallStu(rollCalls, organSet.getArithmetic() == null ? 10 : organSet.getArithmetic()));
                }
            } else {
                teacherScheduleDTO.setClassrommRollCall(Boolean.FALSE);
                if (scheduleRollCall != null) {
                    List<RollCall> rollCalls = rollCallRepository.findByScheduleRollcallId(scheduleRollCall.getId());
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
            log.warn("Exception", e);
        }

        return pageInfo;
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
            List<RollCall> datas = new ArrayList<>();
            for (RollCall rollCall : rollCalls) {
                if (!rollCall.getType().equals(RollCallConstants.TYPE_CANCEL_ROLLCALL)) {
                    datas.add(rollCall);
                }
            }
            int total = datas.size();
            int normal = 0;
            int later = 0;
            int askForLeave = 0;
            int leave = 0;
            for (RollCall rollCall : datas) {
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
        List<RollCall> datas = new ArrayList<>();
        for (RollCall rollCall : rollCalls) {
            if (!rollCall.getType().equals(RollCallConstants.TYPE_CANCEL_ROLLCALL)) {
                datas.add(rollCall);
            }
        }
        for (RollCall rollCall : datas) {
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

    public List<StudentScheduleDTO> getStudentSignCourse(Long studentId) {
        // 数据转换
        List<StudentScheduleDTO> studentSignCourse = null;
        try {
            String currentDate = DateFormatUtil.formatShort(new Date());
            String teachingClassIdsStr = stringRedisTemplate.opsForValue().get(currentDate + "_" + studentId);
            // 防止没有加入缓存
            if (StringUtils.isBlank(teachingClassIdsStr)) {
                List<DianDianDaySchoolTimeTableDomain> scheduleList = orgManagerRemoteService.getStudentDaySchoolTimeTable(studentId, currentDate);
                Set set = new HashSet();
                for (DianDianDaySchoolTimeTableDomain dianDianDaySchoolTimeTableDomain : scheduleList) {
                    set.add(dianDianDaySchoolTimeTableDomain.getTeachingClassId());
                }
                teachingClassIdsStr = StringUtils.join(set.toArray(), ",");
                stringRedisTemplate.opsForValue().set(currentDate + "_" + studentId, teachingClassIdsStr, 1, TimeUnit.DAYS);
            }
            if (StringUtils.isNotBlank(teachingClassIdsStr)) {
                Set<Long> teachingClassIds = new HashSet();
                String[] split = teachingClassIdsStr.split(",");
                for (String str : split) {
                    teachingClassIds.add(Long.valueOf(str));
                }
                studentSignCourse = scheduleQuery.getStudentSignCourse(teachingClassIds, teachingClassIdsStr);
                if (studentSignCourse != null && studentSignCourse.size() > 0) {
                    for (StudentScheduleDTO dto : studentSignCourse) {
                        RollCall rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(dto.getScheduleRollCallId()), studentId);
                        if (rollCall != null) {
                            dto.setIsPublicLeave(rollCall.getIsPublicLeave());
                        } else {
                            dto.setCanReport(false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取签到列表失败", e);
        }
        return studentSignCourse;
    }

    /**
     * 学生端 获取某一天的课程列表
     *
     * @param pageable
     * @param account
     * @param teachDate
     * @return
     * @throws ParseException
     */
    public PageInfo getStudentCourseListDay(Pageable pageable, AccountDTO account, String teachDate) throws ParseException {
        if (StringUtils.isBlank(teachDate)) {
            teachDate = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);
        }

        PageInfo pageInfo = PageInfo.getPageInfo(pageable);

        String teachingClassIdsStr = stringRedisTemplate.opsForValue().get(teachDate + "_" + account.getId());

        // 防止没有加入缓存
        if (StringUtils.isBlank(teachingClassIdsStr)) {
            List<DianDianDaySchoolTimeTableDomain> scheduleList = orgManagerRemoteService.getStudentDaySchoolTimeTable(account.getId(), teachDate);

            Set set = new HashSet();
            for (DianDianDaySchoolTimeTableDomain dianDianDaySchoolTimeTableDomain : scheduleList) {
                set.add(dianDianDaySchoolTimeTableDomain.getTeachingClassId());
            }
            teachingClassIdsStr = StringUtils.join(set.toArray(), ",");
        }

        // 数据转换
        List<StudentScheduleDTO> stuSchlist = null;
        List<Schedule> pageList = null;
        if (StringUtils.isNotBlank(teachingClassIdsStr)) {
            Set<Long> teachingClassIds = new HashSet();
            StringBuffer ids = new StringBuffer("teachingClassIds");
            ids.append(teachingClassIdsStr);

            String[] split = teachingClassIdsStr.split(",");
            for (String str : split) {
                teachingClassIds.add(Long.valueOf(str));
            }

            // 查询某一天的排课，并按照课程节排序
            pageList = scheduleService.findByteachingclassIdInAndTeachDateAndDeleteFlag(teachingClassIdsStr, teachingClassIds, teachDate, DataValidity.VALID.getState(),
                    new Sort(new Order(Direction.ASC, "periodNo")));

            pageInfo.setTotalCount(1L);
            pageInfo.setPageCount(null == pageList ? 0 : pageList.size());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<Long, List<StudentScheduleDTO>> spc = new HashMap<Long, List<StudentScheduleDTO>>();
            Set<Long> scheduleRollCallIds = new HashSet<Long>();

            Set<Long> scheduleIds = new HashSet<Long>();
            Map<Long, List<StudentScheduleDTO>> scheduleMap = new HashMap<Long, List<StudentScheduleDTO>>();
            stuSchlist = new ArrayList();
            StudentScheduleDTO studentScheduleDTO = null;

            OrganSet organSet = organSetService.findByOrganId(account.getOrganId());

            long currenScheduleRollCallId = 0;
            for (Schedule schedule : pageList) {
                Set<ScheduleRollCall> set = schedule.getScheduleRollCall();
                ScheduleRollCall scheduleRollCall = null;
                if (null != set && set.size() > 0) {
                    scheduleRollCall = set.iterator().next();
                }
                if (null == scheduleRollCall) {
                    continue;
                }
                Long scheduleIdRollCallId = scheduleRollCall.getId();
                scheduleRollCallIds.add(scheduleIdRollCallId);
                scheduleIds.add(schedule.getId());

                List<StudentScheduleDTO> tlist = spc.get(scheduleIdRollCallId);
                if (null == tlist) {
                    tlist = new ArrayList<StudentScheduleDTO>();
                    spc.put(scheduleIdRollCallId, tlist);
                    scheduleMap.put(schedule.getId(), tlist);
                }

                // 组装数据
                studentScheduleDTO = new StudentScheduleDTO();
                studentScheduleDTO.setId(schedule.getId());
                studentScheduleDTO.setScheduleId(schedule.getId());
                studentScheduleDTO.setCanReport(scheduleRollCall.getIsInClassroom());
                studentScheduleDTO.setCourseName(schedule.getCourseName());
                studentScheduleDTO.setClassRoom(schedule.getClassRoomName());
                studentScheduleDTO.setClassBeginTime(schedule.getScheduleStartTime());
                studentScheduleDTO.setClassEndTime(schedule.getScheduleEndTime());
                // 课程节名称
                studentScheduleDTO.setWhichLesson(CourseUtils.getWhichLesson(schedule.getPeriodNo(), schedule.getPeriodNum()));
                studentScheduleDTO.setLateTime(scheduleRollCall.getCourseLaterTime().longValue() > 120 ? 0 : scheduleRollCall.getCourseLaterTime());
                studentScheduleDTO.setAbsenteeismTime(scheduleRollCall.getAbsenteeismTime() == null ? 0 : scheduleRollCall.getAbsenteeismTime());
                studentScheduleDTO.setWeekName(schedule.getWeekName());
                studentScheduleDTO.setDayOfWeek(schedule.getDayOfWeek() + "");
                studentScheduleDTO.setTeacher(schedule.getTeacherNname());
                studentScheduleDTO.setTeach_time(schedule.getTeachDate());
                studentScheduleDTO.setHaveReport(Boolean.FALSE);
                studentScheduleDTO.setLessonOrderNum(schedule.getPeriodNo());
                studentScheduleDTO.setAssessScore("");

                // 是否在签到时间内 课前10分钟，截至上课时间
                boolean flag = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(schedule.getTeachDate()) && CourseUtils.classBeginTime(schedule.getScheduleStartTime())
                        && CourseUtils.classEndTime(schedule.getScheduleEndTime()));
                if (!flag && scheduleRollCall.getIsInClassroom()) {
                    flag = true;
                }
                studentScheduleDTO.setInClass(flag);
                if (null != scheduleRollCall) {
                    if (flag) {
                        currenScheduleRollCallId = scheduleRollCall.getId();
                    }
                    if (scheduleRollCall.getIsOpenRollcall() && flag) {
                        studentScheduleDTO.setCanReport(true);
                    }
                    RollCall rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(currenScheduleRollCallId), account.getId());
                    if (rollCall != null) {
                        studentScheduleDTO.setType(rollCall.getType());
                        studentScheduleDTO.setRollcallType(scheduleRollCall.getRollCallType());
                        studentScheduleDTO.setRollCall(scheduleRollCall.getIsOpenRollcall());
                        studentScheduleDTO.setLocaltion(scheduleRollCall.getLocaltion());
                        studentScheduleDTO.setHaveReport(rollCall.getHaveReport());
                        studentScheduleDTO.setSignTime(rollCall.getSignTime() == null ? "" : sdf.format(rollCall.getSignTime()));
                        studentScheduleDTO.setIsPublicLeave(rollCall.getIsPublicLeave());
                    } else {
                        studentScheduleDTO.setType(RollCallConstants.TYPE_UNCOMMITTED);
                        studentScheduleDTO.setRollcallType(scheduleRollCall.getRollCallType());
                        studentScheduleDTO.setRollCall(scheduleRollCall.getIsOpenRollcall());
                        studentScheduleDTO.setLocaltion(scheduleRollCall.getLocaltion());
                        studentScheduleDTO.setCanReport(false);
                    }
                }
                tlist.add(studentScheduleDTO);
                stuSchlist.add(studentScheduleDTO);
            }

            List<RollCall> rlist = rollCallRepository.findByScheduleRollcallIdInAndStudentId(scheduleRollCallIds, account.getId());
            // 添加签到信息
            if (null != rlist && rlist.size() > 0) {
                for (RollCall r : rlist) {
                    List<StudentScheduleDTO> tlist = spc.get(r.getScheduleRollcallId());
                    if (null != tlist && tlist.size() > 0) {
                        for (StudentScheduleDTO sc : tlist) {
                            sc.setType(r.getType());
                            sc.setHaveReport(r.getHaveReport());
                            sc.setRollcallType(sc.getRollcallType());
                            sc.setSignTime(r.getSignTime() == null ? "" : sdf.format(r.getSignTime()));
                            sc.setDeviation(organSet.getDeviation());
                            sc.setIsPublicLeave(r.getIsPublicLeave());
                        }
                    }
                }
            }

            if (0 != currenScheduleRollCallId) {
                // 当前正在上的课程需要从redis中获取签到信息
                RollCall r = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(currenScheduleRollCallId), account.getId());
                if (r != null) {
                    List<StudentScheduleDTO> tlist = spc.get(currenScheduleRollCallId);
                    if (null != tlist && tlist.size() > 0) {
                        for (StudentScheduleDTO sc : tlist) {
                            sc.setType(r.getType());
                            sc.setHaveReport(r.getHaveReport());
                            sc.setRollcallType(sc.getRollcallType());
                            sc.setSignTime(r.getSignTime() == null ? "" : sdf.format(r.getSignTime()));
                            sc.setDeviation(organSet.getDeviation());
                            sc.setIsPublicLeave(r.getIsPublicLeave());
                        }
                    }
                }
            }
        }

        // 组织返回页面数据
        HomePageDTO homePage = new HomePageDTO();
        DateTime dateTime = new DateTime();
        if (StringUtils.isNotBlank(teachDate)) {
            dateTime = DateTime.parse(teachDate);
        }

        String weekName = "";
        homePage.setTeach_time(teachDate);
        if (null != stuSchlist && stuSchlist.size() > 0) {
            Schedule schedule = pageList.get(0);
            homePage.setDayOfWeek(String.valueOf(schedule.getDayOfWeek()));
            weekName = schedule.getWeekName();
            homePage.setCourseList(stuSchlist);
        } else {
            homePage.setDayOfWeek(String.valueOf(dateTime.getDayOfWeek()));
            WeekDTO week = weekService.getWeek(account.getOrganId(), teachDate == null ? DateFormatUtil.formatShort(new Date()) : teachDate);
            if (null != week) {
                weekName = week.getName();
            } else {
                weekName = "0";
            }
            homePage.setCourseList(new ArrayList());
        }
        homePage.setWeekName(weekName);
        List ls = new ArrayList();
        ls.add(homePage);
        pageInfo.setData(ls);

        return pageInfo;
    }

    /**
     * 学生端查询课程详情
     *
     * @param account
     * @param scheduleId
     * @return
     */
    @SuppressWarnings("rawtypes")
    public StudentScheduleDTO getStudentCourseInfo(AccountDTO account, Long scheduleId) {

        Schedule schedule = scheduleRepository.findOne(scheduleId);
        if (null != schedule) {
            StudentScheduleDTO studentScheduleDTO = new StudentScheduleDTO();
            List<Assess> al = assessRepository.findByStudentIdAndScheduleId(account.getId(), scheduleId);
            if (null != al && 0 < al.size()) {
                studentScheduleDTO.setInAssess(true);
            } else {
                studentScheduleDTO.setInAssess(false);
            }
            studentScheduleDTO.setId(scheduleId);
            studentScheduleDTO.setScheduleId(scheduleId);
            studentScheduleDTO.setCourseName(schedule.getCourseName());
            studentScheduleDTO.setTeacher(schedule.getTeacherNname());
            studentScheduleDTO.setClassRoom(schedule.getClassRoomName());
            studentScheduleDTO.setTeach_time(schedule.getTeachDate());
            studentScheduleDTO.setDayOfWeek(String.valueOf(schedule.getDayOfWeek()));
            studentScheduleDTO.setWeekName(schedule.getWeekName());
            studentScheduleDTO.setClassBeginTime(schedule.getScheduleStartTime());
            studentScheduleDTO.setClassEndTime(schedule.getScheduleEndTime());
            studentScheduleDTO.setWhichLesson(CourseUtils.getWhichLesson(schedule.getPeriodNo(), schedule.getPeriodNum()));
            //获取头像
            UserInfo user = userInfoRepository.findByUserId(schedule.getTeacherId());
            studentScheduleDTO.setAvatar(user.getAvatar());

            ScheduleRollCall scheduleRollCall = findBySchedule(schedule);
            if (null != scheduleRollCall) {
                studentScheduleDTO.setLateTime(scheduleRollCall.getCourseLaterTime());
                studentScheduleDTO.setAbsenteeismTime(scheduleRollCall.getAbsenteeismTime());
                RollCall rollCall = null;
                if (scheduleRollCall.getIsInClassroom()) {
                    // 默认学生查询的为当前课程的信息，从redis查询，未查询到则从数据库查询。
                    rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), account.getId());
                } else {
                    Set ids = new HashSet();
                    ids.add(scheduleRollCall.getId());
                    List<RollCall> scheduleList = rollCallRepository.findByScheduleRollcallIdInAndStudentId(ids, account.getId());
                    if (null != scheduleList && scheduleList.size() > 0) {
                        rollCall = scheduleList.get(0);
                    }
                }

                if (null != rollCall) {
                    studentScheduleDTO.setType(rollCall.getType());
                    studentScheduleDTO.setSignTime(rollCall.getSignTime() == null ? "" : DateFormatUtil.format(rollCall.getSignTime(), DateFormatUtil.FORMAT_MINUTE));
                    studentScheduleDTO.setHaveReport(rollCall.getHaveReport());
                    studentScheduleDTO.setAddress(rollCall.getGpsDetail());
                    studentScheduleDTO.setRollcallType(rollCall.getType());
                    studentScheduleDTO.setIsPublicLeave(rollCall.getIsPublicLeave());
                } else {
                    studentScheduleDTO.setType(RollCallConstants.TYPE_UNCOMMITTED);
                    studentScheduleDTO.setRollcallType(RollCallConstants.TYPE_UNCOMMITTED);
                }
                studentScheduleDTO.setRollCall(scheduleRollCall.getIsOpenRollcall());
                studentScheduleDTO.setInClass(scheduleRollCall.getIsInClassroom());
                scheduleRollCall.setLocaltion(scheduleRollCall.getLocaltion());
            }

            return studentScheduleDTO;
        } else {
            return null;
        }
    }

    /**
     * 获取当前上课课程的详细信息
     *
     * @param scheduleId
     * @return
     */
    public CourseInforDTO getTeacherCourseInfo(Long scheduleId) {

        Schedule schedule = scheduleService.findOne(scheduleId);
        if (null != schedule) {
            ScheduleRollCall scheduleRollCall = findBySchedule(schedule);
            CourseInforDTO inforDTO = new CourseInforDTO();
            inforDTO.setId(scheduleId);
            inforDTO.setDayOfWeek(String.valueOf(schedule.getDayOfWeek()));
            inforDTO.setClassRoom(schedule.getClassRoomName());
            inforDTO.setCourseName(schedule.getCourseName());
            inforDTO.setClassBeginTime(schedule.getScheduleStartTime());
            inforDTO.setClassEndTime(schedule.getScheduleEndTime());
            inforDTO.setTeach_time(schedule.getTeachDate());
            inforDTO.setWeekName(schedule.getWeekName());
            inforDTO.setWhichLesson(CourseUtils.getWhichLesson(schedule.getPeriodNo(), schedule.getPeriodNum()));
            inforDTO.setLateTime(scheduleRollCall == null ? 15 : scheduleRollCall.getCourseLaterTime());
            inforDTO.setAssessPeopelNum(scheduleRollCall == null ? 0 : scheduleRollCall.getAbsenteeismTime());
            inforDTO.setTeachingClassId(schedule.getTeachingclassId());
            inforDTO.setPeriodId(schedule.getPeriodId());
            inforDTO.setLessonOrderNum(schedule.getPeriodNo());
            inforDTO.setPeriodType(schedule.getPeriodNum() + "");
            inforDTO.setIsRollCall(scheduleRollCall == null ? false : scheduleRollCall.getIsOpenRollcall());
            inforDTO.setRollCall(scheduleRollCall == null ? false : scheduleRollCall.getIsOpenRollcall());
            //classNames
            TeachingClass teachingClass = teachingClassRepository.findByTeachingClassId(schedule.getTeachingclassId());
            if (teachingClass != null && !StringUtils.isEmpty(teachingClass.getClassNames())) {
                inforDTO.setClassNames(teachingClass.getClassNames());
            }
            List<ClassStatsDTO> ls = null;
            int totalCount = 0;
            // 潘震：只计算教学班班的学生数量
            try {
                ls = this.getClassInfor(schedule.getTeachingclassId());
                if (ls != null) {
                    for (ClassStatsDTO statsDTO : ls) {
                        if (null != statsDTO.getStudentCount()) {
                            totalCount += statsDTO.getStudentCount();
                        }
                    }
                } else {
                    log.info("根据该排课ID:" + scheduleId + ",未找到班级信息！");
                }
            } catch (Exception e) {
                log.warn("Exception", e);
            }
            Long tt = orgManagerRemoteService.countStudentsByTeachingClassId(schedule.getTeachingclassId());
            if (null != tt) {
                totalCount = tt.intValue();
            }
            inforDTO.setClassInfor(ls);
            Long countAssess = assessRepository.countByScheduleId(scheduleId);// 计算评教
            inforDTO.setAssessPeopelNum(countAssess == null ? 0 : countAssess.intValue());
            inforDTO.setTotalPeopelNum(totalCount);
            return inforDTO;
        } else {
            return null;
        }
    }

    public List<ClassStatsDTO> getClassInfor(Long teachingClassId) {
        List<ClassStatsDTO> ls = new ArrayList<ClassStatsDTO>();
        try {
            List<IdNameDomain> classList = orgManagerRemoteService.listClass(teachingClassId);
            if (null != classList) {
                Set<Long> classIdSet = new HashSet();
                for (IdNameDomain d : classList) {
                    ClassStatsDTO dto = new ClassStatsDTO();
                    dto.setClassId(d.getId());
                    dto.setClassName(d.getName());
                    classIdSet.add(d.getId());
                    List<CountDomain> cd = orgManagerRemoteService.countbyclassesids(classIdSet);
                    if (null != cd && cd.size() > 0) {
                        dto.setStudentCount(cd.get(0).getCount());
                    }
                    classIdSet.clear();
                    ls.add(dto);
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return ls;
    }

    public boolean isCanUpdateRollCall(Long teacherId, Long courseId) {
        boolean flag = true;
        try {
            List<Schedule> scheduleList = scheduleRepository.findByTeacherIdAndCourseIdAndTeachDateAndDeleteFlag(teacherId, courseId,
                    DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT), DataValidity.VALID.getState());
            if (scheduleList != null && scheduleList.size() > 0) {
                for (Schedule schedule : scheduleList) {
                    ScheduleRollCall scheduleRollCall = findBySchedule(schedule);
                    if (null != scheduleRollCall && scheduleRollCall.getIsInClassroom()) {
                        flag = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return flag;
    }

    public List<Map<String, Object>> getClassBySchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findOne(scheduleId);
        List<IdNameDomain> classList = orgManagerRemoteService.listClass(schedule.getTeachingclassId());
        List list = new ArrayList();
        Map map = null;

        Map classMap = new HashedMap();
        List<Assess> assesses = assessRepository.findAllByScheduleId(scheduleId);
        if (null != assesses && assesses.size() > 0) {
            for (Assess a : assesses) {
                classMap.put(a.getClassId(), true);
            }
        } else {
            return null;
        }
        for (IdNameDomain idNameDomain : classList) {
            map = new HashMap();
            Long classId = idNameDomain.getId();
            if (!classMap.containsKey(classId)) {
                continue;
            }
            map.put("classId", classId);
            map.put("className", idNameDomain.getName());
            list.add(map);
        }
        return list;
    }

    public boolean initCurrentDateSchedule() {

        return false;
    }

    public ScheduleRollCall save(ScheduleRollCall scheduleRollCall, Long scheduleId) {
        scheduleRollCall = scheduleRollCallRepository.save(scheduleRollCall);
        redisTemplate.opsForValue().set(RedisUtil.getSchduleRollCallDominKey(scheduleId), scheduleRollCall, 1, TimeUnit.DAYS);
        return scheduleRollCall;
    }

    public ScheduleRollCall findOne(Long id) {
        ScheduleRollCall scheduleRollCall = null;
        try {
            scheduleRollCall = scheduleRollCallRepository.findOne(id);
            redisTemplate.opsForValue().set(RedisUtil.getSchduleRollCallDominKey(scheduleRollCall.getSchedule().getId()), scheduleRollCall, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Exception", e);
            log.warn("获取排课信息的排课规则失败，请检查排课数据", e);
        }
        return scheduleRollCall;
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

    public ScheduleRollCall findBySchedule(Schedule schedule) {
        return scheduleRollCallRepository.findBySchedule_Id(schedule.getId());
    }

    public void updateScheduleVerify(Long scheduleRollCallId, String verify) {
        ScheduleRollCall scheduleRollCall = findOne(scheduleRollCallId);
        scheduleRollCall.setLocaltion(verify);
        save(scheduleRollCall, scheduleRollCall.getSchedule().getId());
    }

    public void deleteByScheduleId(Long scheduleId) {
        scheduleRollCallRepository.deleteByScheduleId(scheduleId);
    }

    public List<Schedule> findAllByOrganId(Long organId) {
        return scheduleRepository.findAllByOrganIdAndDeleteFlag(organId, DataValidity.VALID.getState());
    }

}
