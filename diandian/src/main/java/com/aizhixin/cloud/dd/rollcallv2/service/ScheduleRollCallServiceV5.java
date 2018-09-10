package com.aizhixin.cloud.dd.rollcallv2.service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.homepage.dto.HomePageDTO;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.remote.RollCallRemoteClient;
import com.aizhixin.cloud.dd.rollcall.domain.CurrentScheduleRulerDomain;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcallv2.domain.RollcallRedisDomain;
import com.aizhixin.cloud.dd.rollcallv2.domain.ScheduleRedisDomain;
import com.aizhixin.cloud.dd.rollcallv2.domain.ScheduleRollCallRedisDomain;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LIMH
 * @date 2017/12/26
 */
@Service
public class ScheduleRollCallServiceV5 {

    private final Logger log = LoggerFactory.getLogger(ScheduleRollCallServiceV5.class);

    @Autowired
    private RollCallRemoteClient rollCallRemoteClient;

    @Autowired
    private ScheduleRollCallService scheduleRollCallService;

    @Autowired
    private WeekService weekService;

    @Lazy
    @Autowired
    private RollCallService rollCallService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private OrganSetService organSetService;

    /**
     * 学生端 获取某一天的课程列表
     */
    public PageInfo getStudentCourseListDay(Pageable pageable, AccountDTO account, String teachDate) throws Exception {
        if (StringUtils.isBlank(teachDate)) {
            teachDate = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);
        }
        Long orgId = account.getOrganId();
        PageInfo pageInfo = PageInfo.getPageInfo(pageable);
        pageInfo.setPageCount(1);
        List<ScheduleRedisDomain> list = new ArrayList();
        Map<String, List<StudentScheduleDTO>> spc = new HashMap<String, List<StudentScheduleDTO>>();

        OrganSet organSet = organSetService.findByOrganId(account.getOrganId());

        if (DateFormatUtil.formatShort(new Date()).equals(teachDate)) {
            // 查询当天
            list = rollCallRemoteClient.currentDayStudentCourseList(orgId, account.getId());

            StudentScheduleDTO studentScheduleDTO = null;
            List stuSchlist = new ArrayList();
            if (null != list) {
                for (ScheduleRedisDomain scheduleRedisDomain : list) {
                    if (scheduleRedisDomain == null) {
                        continue;
                    }
                    ScheduleRollCallRedisDomain scheduleRollCallRedisDomain = rollCallRemoteClient.getScheduleRollCall(orgId, scheduleRedisDomain.getScheduleId());
                    if (null == scheduleRollCallRedisDomain) {
                        scheduleRollCallRedisDomain = new ScheduleRollCallRedisDomain();
                        scheduleRollCallRedisDomain.setRollCallType(ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC);
                    }
                    // 组装数据
                    studentScheduleDTO = new StudentScheduleDTO();
                    copyScheduleDto(studentScheduleDTO, scheduleRedisDomain, scheduleRollCallRedisDomain);
                    stuSchlist.add(studentScheduleDTO);
                    spc.put(studentScheduleDTO.getScheduleRollCallId().toString(), Lists.newArrayList(studentScheduleDTO));
                }

                Map<String, RollcallRedisDomain> map = rollCallRemoteClient.currentDayStudentSignType(account.getOrganId(), account.getId());
                if (map != null) {
                    for (Map.Entry<String, RollcallRedisDomain> entry : map.entrySet()) {
                        List<StudentScheduleDTO> tlist = spc.get(String.valueOf(entry.getKey()));
                        if (null != tlist && tlist.size() > 0) {
                            for (StudentScheduleDTO sc : tlist) {
                                RollcallRedisDomain rollcallRedisDomain = entry.getValue();
                                if (rollcallRedisDomain == null) {
                                    continue;
                                }
                                sc.setType(rollcallRedisDomain.getType());
                                if (RollCallConstants.TYPE_EXCEPTION.equals(rollcallRedisDomain.getType())) {
                                    sc.setDeviation(organSet.getDeviation());
                                }
                            }
                        }
                    }
                }
            }

            Collections.sort(stuSchlist, new Comparator<StudentScheduleDTO>() {
                @Override
                public int compare(StudentScheduleDTO o1, StudentScheduleDTO o2) {
                    return o1.getLessonOrderNum().compareTo(o2.getLessonOrderNum());
                }
            });

            // 组织返回页面数据
            HomePageDTO homePage = new HomePageDTO();
            DateTime dateTime = new DateTime();
            if (StringUtils.isNotBlank(teachDate)) {
                dateTime = DateTime.parse(teachDate);
            }

            String weekName = "";
            homePage.setTeach_time(teachDate);
            if (null != stuSchlist && stuSchlist.size() > 0) {
                ScheduleRedisDomain ScheduleRedisDomain = list.get(0);
                homePage.setDayOfWeek(String.valueOf(ScheduleRedisDomain.getDayOfWeek()));
                weekName = ScheduleRedisDomain.getWeekName();
                homePage.setCourseList(stuSchlist);
            } else {
                homePage.setDayOfWeek(String.valueOf(dateTime.getDayOfWeek()));
                WeekDTO week = weekService.getWeek(account.getOrganId(), teachDate == null ? DateFormatUtil.formatShort(new Date()) : teachDate);
                if (null != week) {
                    weekName = week.getName();
                }
                homePage.setCourseList(new ArrayList());
            }
            pageInfo.setTotalCount(Long.valueOf(homePage.getCourseList().size()));
            homePage.setWeekName(weekName);
            List ls = new ArrayList();
            ls.add(homePage);
            pageInfo.setData(ls);

        } else {
            return scheduleRollCallService.getStudentCourseListDay(pageable, account, teachDate);
        }

        return pageInfo;
    }

    // public Integer subDeviation(String deviation) {
    // if (StringUtils.isNotBlank(deviation)) {
    //
    // }
    // return 0;
    // }

    /**
     * 学生端 获取某一天的课程列表
     *
     * @param pageable
     * @param account
     * @param teachDate
     * @return
     * @throws ParseException
     */
    public PageInfo getStudentCourseListDay_VS2(Pageable pageable, AccountDTO account, String teachDate) throws ParseException {
        if (StringUtils.isBlank(teachDate)) {
            teachDate = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);
        }

        PageInfo pageInfo = PageInfo.getPageInfo(pageable);

        String teachingClassIdsStr = stringRedisTemplate.opsForValue().get(DateFormatUtil.formatShort(new Date()) + "_" + account.getId());

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
                new Sort(new Sort.Order(Sort.Direction.ASC, "periodNo")));

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
                Set<ScheduleRollCall> set = schedule.getScheduleRollCall();// scheduleRollCallRepository.findBySchedule(schedule);
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

                studentScheduleDTO.setCanReport(false);
                studentScheduleDTO.setInClass(flag);
                if (null != scheduleRollCall) {
                    if (flag) {
                        currenScheduleRollCallId = scheduleRollCall.getId();
                    }
                    studentScheduleDTO.setType(RollCallConstants.TYPE_UNCOMMITTED);
                    studentScheduleDTO.setRollcallType(scheduleRollCall.getRollCallType());
                    studentScheduleDTO.setRollCall(scheduleRollCall.getIsOpenRollcall());
                    studentScheduleDTO.setLocaltion(scheduleRollCall.getLocaltion());
                    if (scheduleRollCall.getIsOpenRollcall() && flag) {
                        studentScheduleDTO.setCanReport(true);
                    }
                }
                tlist.add(studentScheduleDTO);
                stuSchlist.add(studentScheduleDTO);
            }

            if (DateFormatUtil.formatShort(new Date()).equals(teachDate)) {
                Map<String, RollcallRedisDomain> map = rollCallRemoteClient.currentDayStudentSignType(account.getOrganId(), account.getId());
                if (map != null) {
                    for (Map.Entry<String, RollcallRedisDomain> entry : map.entrySet()) {
                        List<StudentScheduleDTO> tlist = spc.get(entry.getKey());
                        if (null != tlist && tlist.size() > 0) {
                            for (StudentScheduleDTO sc : tlist) {
                                sc.setType(entry.getValue().getType());
                            }
                        }
                    }
                }
            } else {
                List<RollCall> rlist = rollCallService.findAllByscheduleRollCallIdInAndStudentId(scheduleRollCallIds, account.getId());
                if (null != rlist && !rlist.isEmpty()) {
                    for (RollCall r : rlist) {
                        List<StudentScheduleDTO> tlist = spc.get(r.getScheduleRollcallId());
                        if (null != tlist && tlist.size() > 0) {
                            for (StudentScheduleDTO sc : tlist) {
                                sc.setType(r.getType());
                                sc.setHaveReport(r.getHaveReport());
                                sc.setRollcallType(sc.getRollcallType());
                                sc.setSignTime(r.getSignTime() == null ? "" : sdf.format(r.getSignTime()));
                                sc.setDeviation(organSet.getDeviation());
                            }
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
     * 属性拷贝
     *
     * @param studentScheduleDTO
     * @param scheduleRedisDomain
     * @param scheduleRollCallRedisDomain
     */
    public void copyScheduleDto(StudentScheduleDTO studentScheduleDTO, ScheduleRedisDomain scheduleRedisDomain, ScheduleRollCallRedisDomain scheduleRollCallRedisDomain) {
        studentScheduleDTO.setId(scheduleRedisDomain.getScheduleId());
        studentScheduleDTO.setScheduleId(scheduleRedisDomain.getScheduleId());
        Boolean flag = CourseRollCallConstants.COURSE_IN.equals(scheduleRollCallRedisDomain.getIsInClassroom()) ? true : false;
        studentScheduleDTO.setCanReport(flag);
        studentScheduleDTO.setCourseName(scheduleRedisDomain.getCourseName());
        studentScheduleDTO.setClassRoom(scheduleRedisDomain.getClassRoomName());
        studentScheduleDTO.setClassBeginTime(scheduleRedisDomain.getScheduleStartTime());
        studentScheduleDTO.setClassEndTime(scheduleRedisDomain.getScheduleEndTime());
        // 课程节名称
        studentScheduleDTO.setWhichLesson(CourseUtils.getWhichLesson(scheduleRedisDomain.getPeriodNo(), scheduleRedisDomain.getPeriodNum()));
        studentScheduleDTO.setLateTime(scheduleRollCallRedisDomain.getCourseLaterTime());
        studentScheduleDTO.setWeekName(scheduleRedisDomain.getWeekName());
        studentScheduleDTO.setDayOfWeek(scheduleRedisDomain.getDayOfWeek() + "");
        studentScheduleDTO.setTeacher(scheduleRedisDomain.getTeacherName());
        studentScheduleDTO.setTeach_time(scheduleRedisDomain.getTeachDate());
        studentScheduleDTO.setHaveReport(Boolean.FALSE);
        studentScheduleDTO.setLessonOrderNum(scheduleRedisDomain.getPeriodNo());
        studentScheduleDTO.setAssessScore("");
        studentScheduleDTO.setType("30".equals(scheduleRollCallRedisDomain.getIsInClassroom()) ? RollCallConstants.TYPE_TRUANCY : RollCallConstants.TYPE_UNCOMMITTED);
        studentScheduleDTO.setRollcallType(scheduleRollCallRedisDomain.getRollCallType());
        studentScheduleDTO.setRollCall(scheduleRollCallRedisDomain.getIsOpenRollcall());
        studentScheduleDTO.setLocaltion(scheduleRollCallRedisDomain.getLocaltion());
        studentScheduleDTO.setInClass(flag);
        studentScheduleDTO.setScheduleRollCallId(scheduleRedisDomain.getScheduleRollCallId());
        // studentScheduleDTO.setCanReport();
    }

    /**
     * 获取学生当前签到列表
     *
     * @param orgId
     * @param studentId
     * @return
     */
    public List<StudentScheduleDTO> getStudentSignCourse(Long orgId, Long studentId) {
        // 数据转换
        List<StudentScheduleDTO> studentSignCourse = new ArrayList<>();
        try {
            List<CurrentScheduleRulerDomain> scheduleRulerDomainList = rollCallRemoteClient.getSignCourseRuler(orgId, studentId);
            if (null != scheduleRulerDomainList && !scheduleRulerDomainList.isEmpty()) {
                for (CurrentScheduleRulerDomain curSchedule : scheduleRulerDomainList) {
                    StudentScheduleDTO item = new StudentScheduleDTO();
                    item.setId(curSchedule.getScheduleId());
                    item.setScheduleId(curSchedule.getScheduleId());
                    item.setScheduleRollCallId(curSchedule.getRulerId());
                    item.setCourseName(curSchedule.getCourseName());
                    item.setTeachingClassName(curSchedule.getTeachingclassName());
                    item.setLocaltion(curSchedule.getLocaltion());
                    item.setRollcallType(curSchedule.getRollCallType());
                    item.setLateTime(curSchedule.getCourseLaterTime());
                    item.setRollCall(Boolean.TRUE);
                    item.setInClass(Boolean.TRUE);
                    item.setHaveReport(Boolean.FALSE);
                    item.setDeviation(0);
                    item.setType("1");
                    Schedule schedule = scheduleService.findOne(curSchedule.getScheduleId());
                    item.setClassRoom(schedule.getClassRoomName());
                    item.setClassBeginTime(schedule.getScheduleStartTime());
                    item.setClassEndTime(schedule.getScheduleEndTime());
                    studentSignCourse.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("", e.getMessage());
        }

        return studentSignCourse;
    }

    /**
     * 签到
     *
     * @param account
     * @param signInDTO
     * @return
     */
    public Object excuteSignIn(AccountDTO account, SignInDTO signInDTO) {
        Map<String, Object> resBody = new HashMap<>();
        if (account.getAntiCheating()) {
            if (StringUtils.isNotBlank(signInDTO.getDeviceToken())) {
                if (!rollCallService.signAntiCheating(signInDTO.getScheduleId(), account.getId(), signInDTO.getDeviceToken())) {
                    resBody.put(ApiReturnConstants.MESSAGE, RollCallConstants.ROLL_CALL_WARNING_MESSAGE);
                    resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_ANTICHEATING);
                    resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                    return resBody;
                }
            }
        }
        return rollCallRemoteClient.signin(account.getOrganId(), account.getId(), signInDTO);
    }
}
