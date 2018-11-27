package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.approve.core.AdjustCourseScheduleRecordState;
import com.aizhixin.cloud.dd.approve.domain.AdjustCourseScheduleRecordDomain;
import com.aizhixin.cloud.dd.approve.services.AdjustCourseScheduleRecordService;
import com.aizhixin.cloud.dd.approve.task.InsertAdjustCourseScheduleRecordThread;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.common.schedule.MyScheduleService;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRepository;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRollCallRepository;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LIMH on 2017/9/10.
 */
@Service
public class TempAdjustCourseScheduleService {

    private final Logger log = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private ScheduleRollCallRepository scheduleRollCallRepository;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Lazy
    @Autowired
    private InitScheduleService initScheduleService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private WeekService weekService;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private TempAdjustCourseMessageService tempAdjustCourseMessageService;
    @Autowired
    private AdjustCourseScheduleRecordService adjustCourseScheduleRecordService;
    @Autowired
    private StudentService studentService;

    /**
     * 停课
     *
     * @return
     */
    public Map stopSchedule(String accessToken, AccountDTO account, TempAdjustCourseFullDomain domain) {
        Map<String, Object> result = new HashMap<>();

        if (domain.getDeleteTempCourseDomain() == null) {
            log.warn("数据传输异常!");
            result.put(ApiReturnConstants.MESSAGE, "停课失败，联系管理员！");
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return result;
        }
        String currentDate = DateFormatUtil.formatShort(new Date());

        TempAdjustCourseDomain deleteTempCourseDomain = domain.getDeleteTempCourseDomain();

        if (currentDate.equals(deleteTempCourseDomain.getEventDate())) {
            // 当天的课停课，直接物理删除

            // 查询当天排课信息
            List<Schedule> scheduleList = scheduleRepository.findByTeachingclassIdAndTeachDateAndPeriodNoAndDeleteFlag(domain.getTeachingClassId(),
                    deleteTempCourseDomain.getEventDate(), deleteTempCourseDomain.getPeriodNo(), DataValidity.VALID.getState());
            if (scheduleList == null || scheduleList.size() > 1) {
                log.warn("排课异常!");
                result.put(ApiReturnConstants.MESSAGE, "停课失败，联系管理员！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }
            if (scheduleList.size() == 0) {
                // result.put(ApiReturnConstants.MESSAGE, "课程已经进行，不能进行停课！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
                return result;
            }
            Schedule schedule = scheduleList.get(0);
            // 判断原课程是否已经进行
            if (isDesSchedulePassed(schedule.getTeachDate() + " " + schedule.getScheduleStartTime())) {
                // 课程已经进行，不能进行调课。
                result.put(ApiReturnConstants.MESSAGE, "课程已经进行，不能进行停课！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            Map rMap = adjustSchedule(domain);
            if (null != rMap) {
                return rMap;
            }

            if (!deleteDesSchedule(schedule)) {
                result.put(ApiReturnConstants.MESSAGE, "停课失败，联系管理员！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            //删除学生缓存
            List<StudentDTO> studentList = studentService.listStudents(schedule.getTeachingclassId());
            if (studentList != null && studentList.size() > 0) {
                for (StudentDTO item : studentList) {
                    stringRedisTemplate.delete(currentDate + "_" + item.getStudentId());
                    redisTokenStore.delScheduleStudentToday(item.getStudentId());
                }
            }
        } else {
            Map rMap = adjustSchedule(domain);
            if (null != rMap) {
                return rMap;
            }
        }
        // 停课记录 @author xiagen
        AdjustCourseScheduleRecordDomain adjustCourseScheduleRecordDomain = new AdjustCourseScheduleRecordDomain();
        adjustCourseScheduleRecordDomain.setType(AdjustCourseScheduleRecordState.DLE_COURSE_SCHEDUL);
        adjustCourseScheduleRecordDomain.setAgoAttendClassAddress(deleteTempCourseDomain.getClassroom());
        String delDate = deleteTempCourseDomain.getEventDate().replaceAll("-", ".");
        if (deleteTempCourseDomain.getPeriodNum() == 1) {
            adjustCourseScheduleRecordDomain.setAgoAttendClassTime(delDate + " 第" + deleteTempCourseDomain.getPeriodNo() + "节");
        } else {
            Integer endPeriodNo = deleteTempCourseDomain.getPeriodNo() + deleteTempCourseDomain.getPeriodNum() - 1;
            adjustCourseScheduleRecordDomain.setAgoAttendClassTime(delDate + " 第" + deleteTempCourseDomain.getPeriodNo() + "-" + endPeriodNo + "节");
        }
        new Thread(new InsertAdjustCourseScheduleRecordThread(adjustCourseScheduleRecordService, domain.getTeachingClassId(), adjustCourseScheduleRecordDomain, orgManagerRemoteService, account.getId(), initScheduleService)).start();
        tempAdjustCourseMessageService.sendMessage(accessToken, domain.getTeachingClassId(), account.getName());
        result.put(ApiReturnConstants.MESSAGE, "停课成功！");
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;
    }

    /**
     * 加课
     *
     * @param domain
     * @return
     */
    public Map addSchedule(String accessToken, AccountDTO account, TempAdjustCourseFullDomain domain) {
        Map<String, Object> result = new HashMap<>();

        if (domain.getAddTempAdjustCourseDomain() == null) {
            log.warn("数据传输异常!");
            result.put(ApiReturnConstants.MESSAGE, "加课失败，联系管理员！");
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return result;
        }

        String currentDate = DateFormatUtil.formatShort(new Date());
        TempAdjustCourseDomain addTempAdjustCourseDomain = domain.getAddTempAdjustCourseDomain();

        if (currentDate.equals(addTempAdjustCourseDomain.getEventDate())) {

            // 添加当天排课信息
            String str = orgManagerRemoteService.teachingClassGetById(domain.getTeachingClassId());
            JSONObject json = JSONObject.fromObject(str);
            if (json == null) {
                log.warn("获取教学班信息异常!");
                result.put(ApiReturnConstants.MESSAGE, "加课失败，联系管理员！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            WeekDTO week = weekService.getWeek(account.getOrganId(), addTempAdjustCourseDomain.getEventDate());
            addTempAdjustCourseDomain.setWeekNo(Integer.valueOf(week.getName()));
            try {
                addTempAdjustCourseDomain.setDayOfWeek(DateFormatUtil.getWeekOfDate(addTempAdjustCourseDomain.getEventDate()));
            } catch (Exception e) {
            }

            List<PeriodDTO> periodDTOS = periodService.listPeriod(account.getOrganId());
            Map<Integer, PeriodDTO> periodMap = new HashedMap();
            for (PeriodDTO periodDTO : periodDTOS) {
                periodMap.put(periodDTO.getNo(), periodDTO);
            }

            // 新增课程
            DianDianSchoolTimeDomain ddDomain = new DianDianSchoolTimeDomain();
            ddDomain.setPeriodId(periodMap.get(addTempAdjustCourseDomain.getPeriodNo()).getId());
            ddDomain.setPeriodNo(addTempAdjustCourseDomain.getPeriodNo());
            ddDomain.setPeriodNum(addTempAdjustCourseDomain.getPeriodNum());
            ddDomain.setTeachers(account.getId() + "," + account.getName());
            ddDomain.setCourseName(json.getString("courseName"));
            ddDomain.setCourseId(json.getLong("courseId"));
            ddDomain.setTeachingClassId(domain.getTeachingClassId());
            ddDomain.setTeachingClassCode(json.getString("code"));
            ddDomain.setTeachingClassName(json.getString("name"));
            ddDomain.setSemesterName(json.getString("semesterName"));
            ddDomain.setSemesterId(json.getLong("semesterId"));
            ddDomain.setWeekId(week.getId());
            ddDomain.setWeekName(week.getName());
            ddDomain.setWeekNo(Integer.valueOf(week.getName()));
            ddDomain.setDayOfWeek(DateFormatUtil.getWeekOfDate(addTempAdjustCourseDomain.getEventDate()));
            ddDomain.setClassroom(addTempAdjustCourseDomain.getClassroom());

            if (ddDomain.getPeriodId() == null) {
                result.put(ApiReturnConstants.MESSAGE, "未找到课程节信息,加课失败！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            // 判断原课程是否已经进行
            if (isDesSchedulePassed(currentDate + " " + periodMap.get(addTempAdjustCourseDomain.getPeriodNo()).getStartTime())) {
                // 课程已经进行，不能进行调课。
                result.put(ApiReturnConstants.MESSAGE, "课程即将或已经进行，不能进行加课！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            Map rMap = adjustSchedule(domain);
            if (null != rMap) {
                return rMap;
            }

            addSrcSchedule(account.getOrganId(), ddDomain);
        } else {
            Map rMap = adjustSchedule(domain);
            if (null != rMap) {
                return rMap;
            }
        }

        // 添加加课记录 @author xiagen
        AdjustCourseScheduleRecordDomain adjustCourseScheduleRecordDomain = new AdjustCourseScheduleRecordDomain();
        adjustCourseScheduleRecordDomain.setType(AdjustCourseScheduleRecordState.ADD_COURSE_SCHEDUL);
        adjustCourseScheduleRecordDomain.setNewAttendClassAddress(addTempAdjustCourseDomain.getClassroom());
        String date = addTempAdjustCourseDomain.getEventDate().replaceAll("-", ".");
        if (addTempAdjustCourseDomain.getPeriodNum() == 1) {
            adjustCourseScheduleRecordDomain.setNewAttendClassTime(date + " 第" + addTempAdjustCourseDomain.getPeriodNo() + "节");
        } else {
            Integer endPeriodNo = addTempAdjustCourseDomain.getPeriodNo() + addTempAdjustCourseDomain.getPeriodNum() - 1;
            adjustCourseScheduleRecordDomain.setNewAttendClassTime(date + " 第" + addTempAdjustCourseDomain.getPeriodNo() + "-" + endPeriodNo + "节");
        }
        //异步添加加课记录 @author xiagen
        new Thread(new InsertAdjustCourseScheduleRecordThread(adjustCourseScheduleRecordService, domain.getTeachingClassId(), adjustCourseScheduleRecordDomain, orgManagerRemoteService, account.getId(), initScheduleService)).start();
        tempAdjustCourseMessageService.sendMessage(accessToken, domain.getTeachingClassId(), account.getName());
        result.put(ApiReturnConstants.MESSAGE, "加课成功！");
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;
    }

    public Map adjustSchedule(TempAdjustCourseFullDomain domain) {
        Map<String, Object> result = new HashMap<>();
        try {
            orgManagerRemoteService.addtempcourseschedule(domain);
        } catch (Exception e) {
            String resultMessage = "操作失败，请联系管理员";
            String message = e.getCause().getMessage();
            if (message.indexOf("cause") != -1) {
                message = message.substring(message.lastIndexOf("{"), message.length());
                JSONObject jsonObject = JSONObject.fromObject(message);
                resultMessage = jsonObject.getString("cause");
            }
            result.put(ApiReturnConstants.MESSAGE, resultMessage);
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return result;
        }
        return null;
    }

    /**
     * 临时调课
     */
    public Map tempAdjustCourseScheduleService(String accessToken, AccountDTO account, TempAdjustCourseFullDomain domain) {

        Map<String, Object> result = new HashMap<>();

        // 1.判断原课程必须大于当前时间

        TempAdjustCourseDomain deleteTempCourseDomain = domain.getDeleteTempCourseDomain();
        TempAdjustCourseDomain addTempAdjustCourseDomain = domain.getAddTempAdjustCourseDomain();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String currentDate = DateFormatUtil.formatShort(new Date());
        String src = deleteTempCourseDomain.getEventDate();
        String des = addTempAdjustCourseDomain.getEventDate();

        domain.setUserId(account.getId());
        WeekDTO week = weekService.getWeek(account.getOrganId(), addTempAdjustCourseDomain.getEventDate());
        addTempAdjustCourseDomain.setWeekNo(Integer.valueOf(week.getName()));
        try {
            addTempAdjustCourseDomain.setDayOfWeek(DateFormatUtil.getWeekOfDate(addTempAdjustCourseDomain.getEventDate()));
        } catch (Exception e) {
            log.warn("时间转换异常", e);
        }

        List<PeriodDTO> periodDTOS = periodService.listPeriod(account.getOrganId());
        Map<Integer, PeriodDTO> periodMap = new HashedMap();
        for (PeriodDTO periodDTO : periodDTOS) {
            periodMap.put(periodDTO.getNo(), periodDTO);
        }

        if (currentDate.equals(src) && currentDate.equals(des)) {

            // 查询当天排课信息
            List<Schedule> scheduleList = scheduleRepository.findByTeachingclassIdAndTeachDateAndPeriodNoAndDeleteFlag(domain.getTeachingClassId(),
                    deleteTempCourseDomain.getEventDate(), deleteTempCourseDomain.getPeriodNo(), DataValidity.VALID.getState());
            if (scheduleList == null || scheduleList.size() > 1) {
                log.warn("获取教学班信息异常!");
                result.put(ApiReturnConstants.MESSAGE, "调课失败，联系管理员！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            Schedule schedule = scheduleList.get(0);

            // 判断原课程是否已经进行
            if (isDesSchedulePassed(schedule.getTeachDate() + " " + schedule.getScheduleStartTime())) {
                // 课程已经进行，不能进行调课。
                result.put(ApiReturnConstants.MESSAGE, "课程即将或已经进行，不能进行加课！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            // 新增课程
            DianDianSchoolTimeDomain ddDomain = new DianDianSchoolTimeDomain();
            ddDomain.setPeriodId(periodMap.get(addTempAdjustCourseDomain.getPeriodNo()).getId());
            ddDomain.setPeriodNo(addTempAdjustCourseDomain.getPeriodNo());
            ddDomain.setPeriodNum(addTempAdjustCourseDomain.getPeriodNum());
            ddDomain.setTeachers(schedule.getTeacherId() + "," + schedule.getTeacherNname());
            ddDomain.setCourseName(schedule.getCourseName());
            ddDomain.setCourseId(schedule.getCourseId());
            ddDomain.setTeachingClassId(schedule.getTeachingclassId());
            ddDomain.setTeachingClassCode(schedule.getTeachingclassCode());
            ddDomain.setTeachingClassName(schedule.getTeachingclassName());
            ddDomain.setSemesterName(schedule.getSemesterName());
            ddDomain.setSemesterId(schedule.getSemesterId());
            ddDomain.setWeekId(schedule.getWeekId());
            ddDomain.setWeekName(schedule.getWeekName());
            ddDomain.setWeekNo(Integer.valueOf(schedule.getWeekName()));
            ddDomain.setDayOfWeek(schedule.getDayOfWeek());
            ddDomain.setClassroom(addTempAdjustCourseDomain.getClassroom());

            // 计算课程开始时间
            PeriodDTO period = (PeriodDTO) periodMap.get(ddDomain.getPeriodNo());

            if (period == null) {
                result.put(ApiReturnConstants.MESSAGE, "调课课程节异常，请确认课程节信息！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            // 判断目标课程是否进行
            if (isDesSchedulePassed(schedule.getTeachDate() + " " + period.getStartTime())) {
                // 课程已经进行，不能进行调课。
                result.put(ApiReturnConstants.MESSAGE, "课程即将或已经进行，不能进行加课！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            Map rMap = adjustSchedule(domain);
            if (null != rMap) {
                return rMap;
            }

            addSrcSchedule(account.getOrganId(), ddDomain);
            deleteDesSchedule(schedule);

        } else if (currentDate.equals(src) && !currentDate.equals(des)) {
            // 当天调整未来
            // 查询当天排课信息
            List<Schedule> scheduleList = scheduleRepository.findByTeachingclassIdAndTeachDateAndPeriodNoAndDeleteFlag(domain.getTeachingClassId(),
                    deleteTempCourseDomain.getEventDate(), deleteTempCourseDomain.getPeriodNo(), DataValidity.VALID.getState());
            if (scheduleList == null || scheduleList.size() > 1) {
                log.warn("排课异常!");
                result.put(ApiReturnConstants.MESSAGE, "调课失败，联系管理员！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }
            Schedule schedule = scheduleList.get(0);

            // 判断原课程是否已经进行
            if (isDesSchedulePassed(schedule.getTeachDate() + " " + schedule.getScheduleStartTime())) {
                // 课程已经进行，不能进行调课。
                result.put(ApiReturnConstants.MESSAGE, "课程即将或已经进行，不能进行加课！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            Map rMap = adjustSchedule(domain);
            if (null != rMap) {
                return rMap;
            }

            // 删除当天排课
            deleteDesSchedule(schedule);
        } else if (!currentDate.equals(src) && currentDate.equals(des)) {
            // 未来调整到当天

            // 判断当天的课程时候已进行
            if (isDesSchedulePassed(DateFormatUtil.formatShort(new Date()) + " " + periodMap.get(addTempAdjustCourseDomain.getPeriodNo()).getStartTime())) {
                // 课程已经进行，不能进行调课。
                result.put(ApiReturnConstants.MESSAGE, "课程即将或已经进行，不能进行加课！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            // 添加当天排课信息
            String str = orgManagerRemoteService.teachingClassGetById(domain.getTeachingClassId());
            JSONObject json = JSONObject.fromObject(str);
            if (json == null) {
                log.warn("获取教学班信息异常!");
                result.put(ApiReturnConstants.MESSAGE, "调课失败，联系管理员！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            // 新增课程
            DianDianSchoolTimeDomain ddDomain = new DianDianSchoolTimeDomain();
            ddDomain.setPeriodId(periodMap.get(addTempAdjustCourseDomain.getPeriodNo()).getId());
            ddDomain.setPeriodNo(addTempAdjustCourseDomain.getPeriodNo());
            ddDomain.setPeriodNum(addTempAdjustCourseDomain.getPeriodNum());
            ddDomain.setTeachers(account.getId() + "," + account.getName());
            ddDomain.setCourseName(json.getString("courseName"));
            ddDomain.setCourseId(json.getLong("courseId"));
            ddDomain.setTeachingClassId(domain.getTeachingClassId());
            ddDomain.setTeachingClassCode(json.getString("code"));
            ddDomain.setTeachingClassName(json.getString("name"));
            ddDomain.setSemesterName(json.getString("semesterName"));
            ddDomain.setSemesterId(json.getLong("semesterId"));
            ddDomain.setWeekId(week.getId());
            ddDomain.setWeekName(week.getName());
            ddDomain.setWeekNo(Integer.valueOf(week.getName()));
            ddDomain.setDayOfWeek(DateFormatUtil.getWeekOfDate(addTempAdjustCourseDomain.getEventDate()));
            ddDomain.setClassroom(addTempAdjustCourseDomain.getClassroom());

            if (ddDomain.getPeriodId() == null) {
                result.put(ApiReturnConstants.MESSAGE, "未找到课程节信息,加课失败！");
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return result;
            }

            // 通知平台
            Map rMap = adjustSchedule(domain);
            if (null != rMap) {
                return rMap;
            }

            addSrcSchedule(account.getOrganId(), ddDomain);

        } else if (!currentDate.equals(src) && !currentDate.equals(des)) {
            // 未来调整未来
            Map rMap = adjustSchedule(domain);
            if (null != rMap) {
                return rMap;
            }
        }
        // 临时调课记录 @author xiagen
        AdjustCourseScheduleRecordDomain adjustCourseScheduleRecordDomain = new AdjustCourseScheduleRecordDomain();
        adjustCourseScheduleRecordDomain.setType(AdjustCourseScheduleRecordState.ADJUST_COURSE_SCHEDUL);
        adjustCourseScheduleRecordDomain.setAgoAttendClassAddress(deleteTempCourseDomain.getClassroom());
        String delDate = deleteTempCourseDomain.getEventDate().replaceAll("-", ".");
        if (deleteTempCourseDomain.getPeriodNum() == 1) {
            adjustCourseScheduleRecordDomain.setAgoAttendClassTime(delDate + " 第" + deleteTempCourseDomain.getPeriodNo() + "节");
        } else {
            Integer endPeriodNo = deleteTempCourseDomain.getPeriodNo() + deleteTempCourseDomain.getPeriodNum() - 1;
            adjustCourseScheduleRecordDomain.setAgoAttendClassTime(delDate + " 第" + deleteTempCourseDomain.getPeriodNo() + "-" + endPeriodNo + "节");
        }
        adjustCourseScheduleRecordDomain.setNewAttendClassAddress(addTempAdjustCourseDomain.getClassroom());
        String addDate = addTempAdjustCourseDomain.getEventDate().replaceAll("-", ".");
        if (addTempAdjustCourseDomain.getPeriodNum() == 1) {
            adjustCourseScheduleRecordDomain.setNewAttendClassTime(addDate + " 第" + addTempAdjustCourseDomain.getPeriodNo() + "节");
        } else {
            Integer endPeriodNo = addTempAdjustCourseDomain.getPeriodNo() + addTempAdjustCourseDomain.getPeriodNum() - 1;
            adjustCourseScheduleRecordDomain.setNewAttendClassTime(addDate + " 第" + addTempAdjustCourseDomain.getPeriodNo() + "-" + endPeriodNo + "节");
        }
        new Thread(new InsertAdjustCourseScheduleRecordThread(adjustCourseScheduleRecordService, domain.getTeachingClassId(), adjustCourseScheduleRecordDomain, orgManagerRemoteService, account.getId(), initScheduleService)).start();

        tempAdjustCourseMessageService.sendMessage(accessToken, domain.getTeachingClassId(), account.getName());


        result.put(ApiReturnConstants.MESSAGE, "调课成功！");
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;
    }

    // 删除原课程
    public boolean deleteDesSchedule(Schedule schedule) {
        try {
            // 删除缓存数据
            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule);
            if (scheduleRollCall != null) {
                // 删除考勤信息
                stringRedisTemplate.delete(RedisUtil.getScheduleRollCallDateKey(scheduleRollCall.getId().longValue()));
                stringRedisTemplate.delete(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()));
                scheduleRollCallRepository.delete(scheduleRollCall);
            }
            // 删除数据库数据
            scheduleRepository.delete(schedule);
            //删除学生缓存
            List<StudentDTO> studentList = studentService.listStudents(schedule.getTeachingclassId());
            if (studentList != null && studentList.size() > 0) {
                String currentDate = DateFormatUtil.formatShort(new Date());
                for (StudentDTO item : studentList) {
                    stringRedisTemplate.delete(currentDate + "_" + item.getStudentId());
                    redisTokenStore.delScheduleStudentToday(item.getStudentId());
                }
            }
        } catch (Exception e) {
            log.warn("删除原始排课信息失败！", e);
            return false;
        }
        return true;
    }

    /**
     * 当天排课 初始化信息
     *
     * @param orgId
     * @param ddDomain
     * @return
     */
    public boolean addSrcSchedule(Long orgId, DianDianSchoolTimeDomain ddDomain) {
        try {
            if (null == ddDomain) {
                return false;
            }
            List<PeriodDTO> periods = periodService.listPeriod(orgId);
            initScheduleService.addSchedule(orgId, DateFormatUtil.formatShort(new Date()), periods, ddDomain);
        } catch (Exception e) {
            log.warn("初始化目标课程信息失败！", e);
            return false;
        }
        // 点点签到重构优化
        try {
            List<Schedule> schedules = scheduleRepository.findAllByTeachingclassIdAndTeachDateAndPeriodNoAndPeriodNumAndDeleteFlag(ddDomain.getTeachingClassId(),
                    DateFormatUtil.formatShort(new Date()), ddDomain.getPeriodNo(), ddDomain.getPeriodNum(), DataValidity.VALID.getState());
            if (schedules != null) {
                for (Schedule schedule : schedules) {
                    ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule);
                    //TODO 修改
                    if (scheduleRollCall != null) {
                        if (schedule.getTeachDate().equals(DateFormatUtil.formatShort(new Date()))) {
                            scheduleRollCall.setIsOpenRollcall(true);
                            scheduleRollCallService.save(scheduleRollCall, schedule.getId());
                        }
                    }
                    //删除学生缓存
                    List<StudentDTO> studentList = studentService.listStudents(schedule.getTeachingclassId());
                    if (studentList != null && studentList.size() > 0) {
                        String currentDate = DateFormatUtil.formatShort(new Date());
                        for (StudentDTO item : studentList) {
                            stringRedisTemplate.delete(currentDate + "_" + item.getStudentId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return true;
    }

    private boolean isDesSchedulePassed(String desDate) {
        return !compare_date(DateFormatUtil.format(new Date((System.currentTimeMillis() + 30 * 60 * 1000)), "yyyy-MM-dd HH:mm"), desDate);
    }

    private boolean compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() < dt2.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return false;
    }

}
