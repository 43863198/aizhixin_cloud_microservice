package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.communication.utils.HttpSimpleUtils;
import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.repository.*;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.GDMapUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RollCallUtils;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Transactional
public class RollCallService {

    private final static Logger log = LoggerFactory.getLogger(RollCallService.class);

    @Autowired
    private RollCallRepository rollCallRepository;

    @Autowired
    private RollCallRecordService rollCallRecordService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ScheduleRollCallService scheduleRollCallService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private CourseRollCallService courseRollCallService;

    @Autowired
    private ScheduleQuery scheduleQuery;

    @Autowired
    private OrganSetService organSetService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private StudentLeaveScheduleService studentLeaveScheduleService;

    @Autowired
    private StudentAttendanceQuery studentAttendanceQuery;
    @Autowired
    private InitScheduleService initScheduleService;

    @Autowired
    private AttendanceListQuery attendanceListQuery;

    @Autowired
    private StudentService studentService;

    @Autowired
    private RollCallExportInfoQuery rollCallExportInfoQuery;

    @Autowired
    private PersonalAttendanceQuery personalAttendanceQuery;

    @Autowired
    private AssessService assessService;

    @Autowired
    private CounselorStudentAttendanceQuery counselorStudentAttendanceQuery;

    @Autowired
    private HttpSimpleUtils httpSimpleUtils;

    @Autowired
    private ModifyAttendanceLogService modifyAttendanceLogService;

    @Autowired
    private RollCallStatsService rollCallStatsService;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

    /**
     * 获取老师的点名信息
     *
     * @param teacherId
     * @param scheduleId
     * @param type
     * @param name
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<RollCallClassDTO> getRollCall(Long teacherId, Long scheduleId, String type, String name) {
        Schedule schedule = scheduleService.findOne(scheduleId);
        if (null == schedule) {
            return null;
        }

        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);

        if (null == scheduleRollCall) {
            return null;
        }
        boolean inClass = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(schedule.getTeachDate()) && CourseUtils.classBeginTime(schedule.getScheduleStartTime()) && CourseUtils.classEndTime(schedule.getScheduleEndTime()));

        String authCode = "";
        if (ScheduleConstants.TYPE_ROLL_CALL_DIGITAL.equals(scheduleRollCall.getRollCallType()) && StringUtils.isBlank(scheduleRollCall.getLocaltion())) {
            authCode = String.valueOf(getRandomAuthCode());
            scheduleRollCall.setLocaltion(authCode);
            scheduleRollCallService.save(scheduleRollCall, scheduleId);

        } else {
            authCode = scheduleRollCall.getLocaltion() == null ? "" : scheduleRollCall.getLocaltion();
        }

        List<RollCall> rollCallList = null;
        // 课堂内的签到数据在redis库中查询
        if (inClass || scheduleRollCall.getIsInClassroom()) {
            rollCallList = listRollCallBySRCIdInRedis(scheduleRollCall.getId());

            // 处理初始化未成功
            if (null != rollCallList && rollCallList.size() > 0) {

            } else {
                initRollCall(scheduleRollCall);
                rollCallList = listRollCallBySRCIdInRedis(scheduleRollCall.getId());
                if (null == rollCallList || rollCallList.size() == 0) {
                    log.warn("初始化课程异常...");
                }
            }
        } else {
            rollCallList = rollCallRepository.findByScheduleRollcallId(scheduleRollCall.getId());
        }

        if (null == rollCallList) {
            return null;
        }
        if (StringUtils.isNotBlank(type)) {
            rollCallList = Lists.newArrayList(Collections2.filter(rollCallList, (rollCall) -> {
                return rollCall.getType().equals(type) ? true : false;
            }));
        }

        if (StringUtils.isNotBlank(name)) {
            rollCallList = Lists.newArrayList(Collections2.filter(rollCallList, (rollCall) -> {
                return null == rollCall.getStudentName() ? false : rollCall.getStudentName().contains(name) ? true : false;
            }));
        }

        if (null != rollCallList && rollCallList.size() > 0) {

            Collections.sort(rollCallList, new Comparator<RollCall>() {
                @Override
                public int compare(RollCall o1, RollCall o2) {
                    if (o1.getStudentNum() == null) {
                        o1.setStudentNum("");
                    }
                    return o1.getStudentNum().compareTo(o2.getStudentName());
                }
            });
        }

        int clasR = (null == scheduleRollCall.getClassRoomRollCall() ? CourseRollCallConstants.NOT_OPEN_CLASSROOMROLLCALL : scheduleRollCall.getClassRoomRollCall());
        boolean isClassroomRollCall = false;
        if (ScheduleConstants.TYPE_OPEN_CLASSROOMROLLCALL == clasR) {
            isClassroomRollCall = true;
        }

        String ids = "";
        Map<Long, RollCallClassDTO> map = new TreeMap<Long, RollCallClassDTO>();

        for (RollCall rollCall : rollCallList) {
            RollCallDTO rollCallDTO = new RollCallDTO();
            // 组装数据
            rollCallDTO.setId(rollCall.getId());
            rollCallDTO.setScheduleId(rollCall.getScheduleRollcallId());
            rollCallDTO.setClassId(rollCall.getClassId());
            rollCallDTO.setUserId(rollCall.getStudentId());
            rollCallDTO.setType(rollCall.getType());
            rollCallDTO.setDistance(null == rollCall.getDistance() ? "" : rollCall.getDistance());
            rollCallDTO.setSignTime(DateFormatUtil.format(rollCall.getSignTime(), DateFormatUtil.FORMAT_MINUTE));
            rollCallDTO.setUserName(rollCall.getStudentName());
            String className = rollCall.getClassName(); // "23";
            rollCallDTO.setClassName(className);
            rollCallDTO.setRollCall(scheduleRollCall.getIsOpenRollcall());
            rollCallDTO.setTeacherId(schedule.getTeacherId());
            rollCallDTO.setCourseId(schedule.getCourseId());

            if (null != map.get(rollCall.getClassId() == null ? 0 : rollCall.getClassId())) {
                RollCallClassDTO classDTO = (RollCallClassDTO) map.get(rollCall.getClassId() == null ? 0 : rollCall.getClassId());
                ArrayList<RollCallDTO> ls = classDTO.getRollCallList();
                ls.add(rollCallDTO);
                if (StringUtils.isNotBlank(ids)) {
                    ids += "," + rollCallDTO.getUserId().toString();
                } else {
                    ids += rollCallDTO.getUserId().toString();
                }
                classDTO.setClassSize(classDTO.getClassSize() + 1);
                classDTO.setAuthCode(authCode);
                classDTO.setClassroomRollcall(isClassroomRollCall);
            } else {
                RollCallClassDTO classDTO = new RollCallClassDTO();
                classDTO.setClassName(className);
                ArrayList<RollCallDTO> ls = new ArrayList<RollCallDTO>();
                ls.add(rollCallDTO);
                if (StringUtils.isNotBlank(ids)) {
                    ids += "," + rollCallDTO.getUserId().toString();
                } else {
                    ids += rollCallDTO.getUserId().toString();
                }
                classDTO.setRollCallList(ls);
                classDTO.setClassSize(1);
                classDTO.setAuthCode(authCode);
                classDTO.setClassroomRollcall(isClassroomRollCall);
                map.put(rollCallDTO.getClassId() == null ? 0 : rollCallDTO.getClassId(), classDTO);
            }
        }

        if (StringUtils.isNotBlank(ids)) {
            List<RollCallClassDTO> rsList = new ArrayList<RollCallClassDTO>();
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                RollCallClassDTO classDTO = (RollCallClassDTO) entry.getValue();
                rsList.add(classDTO);
            }
            return rsList;
        }
        return new ArrayList();
    }

    public void initDigit(Long scheduleId) {
        Schedule schedule = scheduleService.findOne(scheduleId);
        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
        if (null == scheduleRollCall) {
            scheduleRollCall = new ScheduleRollCall();
            scheduleRollCall.setSchedule(schedule);
            scheduleRollCall.setRollCallType(ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC);
            scheduleRollCall.setCourseLaterTime(15);
            scheduleRollCall.setAbsenteeismTime(0);
            scheduleRollCall.setClassRoomRollCall(CourseRollCallConstants.NOT_OPEN_CLASSROOMROLLCALL);
            scheduleRollCall.setIsOpenRollcall(Boolean.FALSE);
            scheduleRollCall.setIsInClassroom(Boolean.TRUE);
            scheduleRollCallService.save(scheduleRollCall, scheduleId);
        }
        initRollCall(scheduleRollCall);
    }

    public List<RollCallClassDTO> getRollCallNum(Long teacher_id, Long scheduleId, String type, String name) {
        log.info("获取点名信息:" + scheduleId);
        Schedule schedule = scheduleService.findOne(scheduleId);
        if (null == schedule) {
            return null;
        }

        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);

        if (null == scheduleRollCall) {
            initDigit(scheduleId);
            scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
        }

        boolean inClass = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(schedule.getTeachDate()) && CourseUtils.classBeginTime(schedule.getScheduleStartTime())
                && CourseUtils.classEndTime(schedule.getScheduleEndTime()));

        String authCode = "";

        List<RollCall> rollCallList = null;
        // 课堂内的签到数据在redis库中查询
        if (inClass || scheduleRollCall.getIsInClassroom()) {
            rollCallList = listRollCallBySRCIdInRedis(scheduleRollCall.getId());
            if (null != rollCallList && rollCallList.size() > 0) {
            } else {
                initDigit(scheduleId);
                rollCallList = listRollCallBySRCIdInRedis(scheduleRollCall.getId());
            }
        } else {
            rollCallList = rollCallRepository.findByScheduleRollcallId(scheduleRollCall.getId());
        }

        if (null == rollCallList) {
            return null;
        }
        if (StringUtils.isNotBlank(type)) {
            rollCallList = Lists.newArrayList(Collections2.filter(rollCallList, (rollCall) -> {
                return rollCall.getType().equals(type) ? true : false;
            }));
        }

        if (StringUtils.isNotBlank(name)) {
            rollCallList = Lists.newArrayList(Collections2.filter(rollCallList, (rollCall) -> {
                return null == rollCall.getStudentName() ? false : rollCall.getStudentName().contains(name) ? true : false;
            }));
        }

        String ids = "";
        Map<Long, RollCallClassDTO> map = new TreeMap<Long, RollCallClassDTO>();

        int clasR = (null == scheduleRollCall.getClassRoomRollCall() ? CourseRollCallConstants.NOT_OPEN_CLASSROOMROLLCALL : scheduleRollCall.getClassRoomRollCall());
        boolean isClassroomRollCall = false;
        if (ScheduleConstants.TYPE_OPEN_CLASSROOMROLLCALL == clasR) {
            isClassroomRollCall = true;
        }

        List<Long> slss = null;
        for (RollCall rollCall : rollCallList) {
            if (!RollCallConstants.TYPE_CANCEL_ROLLCALL.equals(rollCall.getType()) && slss != null && slss.contains(rollCall.getStudentId())) {
                String tempType = rollCall.getType();
                rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                if (!RollCallConstants.TYPE_ASK_FOR_LEAVE.equals(tempType)) {
                    if (inClass) {
                        redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCall.getStudentId(), rollCall);
                    } else {
                        rollCallRepository.save(rollCall);
                    }
                }
            }

            RollCallDTO rollCallDTO = new RollCallDTO();
            // 组装数据
            rollCallDTO.setId(rollCall.getId());
            rollCallDTO.setScheduleId(rollCall.getScheduleRollcallId());
            rollCallDTO.setClassId(rollCall.getClassId());
            rollCallDTO.setUserId(rollCall.getStudentId());
            rollCallDTO.setType(rollCall.getType());
            rollCallDTO.setDistance(null == rollCall.getDistance() ? "" : rollCall.getDistance());
            rollCallDTO.setSignTime(DateFormatUtil.format(rollCall.getSignTime(), DateFormatUtil.FORMAT_MINUTE));
            rollCallDTO.setUserName(rollCall.getStudentName());
            String className = rollCallDTO.getClassName(); // "23";
            rollCallDTO.setClassName(className);
            rollCallDTO.setRollCall(scheduleRollCall.getIsOpenRollcall());
            rollCallDTO.setTeacherId(schedule.getTeacherId());
            rollCallDTO.setCourseId(schedule.getCourseId());
            if (null != map.get(rollCall.getClassId() == null ? 0 : rollCall.getClassId())) {
                RollCallClassDTO classDTO = (RollCallClassDTO) map.get(rollCall.getClassId() == null ? 0 : rollCall.getClassId());
                ArrayList<RollCallDTO> ls = classDTO.getRollCallList();
                ls.add(rollCallDTO);
                if (StringUtils.isNotBlank(ids)) {
                    ids += "," + rollCallDTO.getUserId().toString();
                } else {
                    ids += rollCallDTO.getUserId().toString();
                }
                classDTO.setClassSize(classDTO.getClassSize() + 1);
                classDTO.setAuthCode(authCode);
                classDTO.setClassroomRollcall(isClassroomRollCall);
            } else {
                RollCallClassDTO classDTO = new RollCallClassDTO();
                classDTO.setClassName(className);
                ArrayList<RollCallDTO> ls = new ArrayList<RollCallDTO>();
                ls.add(rollCallDTO);
                if (StringUtils.isNotBlank(ids)) {
                    ids += "," + rollCallDTO.getUserId().toString();
                } else {
                    ids += rollCallDTO.getUserId().toString();
                }
                classDTO.setRollCallList(ls);
                classDTO.setClassSize(1);
                classDTO.setAuthCode(authCode);
                classDTO.setClassroomRollcall(isClassroomRollCall);
                map.put(rollCallDTO.getClassId() == null ? 0 : rollCallDTO.getClassId(), classDTO);
            }
        }

        if (StringUtils.isNotBlank(ids)) {
            List<RollCallClassDTO> rsList = new ArrayList<RollCallClassDTO>();
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                RollCallClassDTO classDTO = (RollCallClassDTO) entry.getValue();
                rsList.add(classDTO);
            }
            return rsList;
        }
        List<RollCallClassDTO> nullList = new ArrayList<>();
        RollCallClassDTO d = new RollCallClassDTO();
        d.setClassroomRollcall(false);
        nullList.add(d);
        return nullList;
    }

    // 生产六位不重复的随机数
    public static int getRandomAuthCode() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < 6; i++)
            result = result * 10 + array[i];
        while (true) {
            if (result > 100000 && result < 1000000) {
                break;
            }
            result = getRandomAuthCode();
        }
        return result;
    }

    public Object excuteReport(Long scheduleId, AccountDTO account) {

        Schedule schedule = scheduleService.findOne(scheduleId);
        if (schedule == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "schedule not found.");
            resBody.put("success", Boolean.FALSE);
            return resBody;
        }

        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);

        RollCall rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), account.getId());

        if (rollCall == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "error schedule!");
            resBody.put("success", Boolean.FALSE);
            return new ResponseEntity<Object>(resBody, HttpStatus.UNAUTHORIZED);
        }

        if (!rollCall.getCanRollCall()) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "u can't report!");
            resBody.put("success", Boolean.FALSE);
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }

        rollCall.setType(RollCallConstants.TYPE_NORMA.equals(rollCall.getLastType()) ? RollCallConstants.TYPE_NORMA
                : RollCallConstants.TYPE_TRUANCY.equals(rollCall.getLastType()) ? RollCallConstants.TYPE_LATE : RollCallConstants.TYPE_LEAVE.equals(rollCall.getLastType())
                ? RollCallConstants.TYPE_LEAVE : RollCallConstants.TYPE_LATE.equals(rollCall.getLastType()) ? RollCallConstants.TYPE_LATE : RollCallConstants.TYPE_NORMA);
        rollCall.setCanRollCall(Boolean.FALSE);
        rollCall.setHaveReport(Boolean.TRUE);

        redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), account.getId(), rollCall);

        Map<String, Object> resBody = new HashMap<>();
        resBody.put("message", "success!");
        resBody.put("success", Boolean.TRUE);
        return resBody;
    }

    /**
     * 学生签到
     *
     * @param account
     * @param signInDTO
     * @return
     */
    public Object excuteSignIn(AccountDTO account, SignInDTO signInDTO) {
        Long scheduleId = signInDTO.getScheduleId();
        Schedule schedule = scheduleService.findOne(scheduleId);
        if (null == schedule) {
            return null;
        }
        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
        Long scheduleRollCallId = scheduleRollCall.getId();
        Map<String, Object> resBody = new HashMap<>();
        if (account.getAntiCheating()) {
            if (StringUtils.isNotBlank(signInDTO.getDeviceToken())) {
                if (!signAntiCheating(scheduleRollCallId, account.getId(), signInDTO.getDeviceToken())) {
                    resBody.put(ApiReturnConstants.MESSAGE, RollCallConstants.ROLL_CALL_WARNING_MESSAGE);
                    resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_ANTICHEATING);
                    resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                    return resBody;
                }
            }
        }
        try {
            // 是否开启点名
            if (scheduleRollCall == null) {
                resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_WARNING_TEACHERNOTOPEN_CODE);
                resBody.put(ApiReturnConstants.MESSAGE, RollCallConstants.ROLL_CALL_WARNING_TEACHERNOTOPEN_MESSAGE);
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return resBody;
            }
            // 点名方式是否切换
            if (!scheduleRollCall.getRollCallType().equals(signInDTO.getRollCallType())) {
                resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_HAVING_CHANGE);
                resBody.put(ApiReturnConstants.MESSAGE, RollCallConstants.ROLL_CALL_WARNING_HAVINTCHANGE_MESSAGE);
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return resBody;
            }

            RollCall rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), account.getId());

            // 在学生端查询的时候已经进行了判断。
            List<Long> slss = studentLeaveScheduleService.findStudentIdByScheduleId(schedule.getId());
            // 第一次签到
            if (rollCall == null) {
                return null;
            }
            // 判断之前的状态，如果正常直接返回
            if (RollCallConstants.TYPE_NORMA.equals(rollCall.getType())) {
                // 直接返回
                resBody.put(ApiReturnConstants.MESSAGE, "已签到成功!");
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
                return resBody;
            }

            if (ScheduleConstants.TYPE_CLOSE_CLASSROOMROLLCALL == scheduleRollCall.getClassRoomRollCall().intValue()) {
                resBody.put(ApiReturnConstants.MESSAGE, RollCallConstants.ROLL_CALL_CLOSE_MESSAGE);
                resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_CLOSE);
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return resBody;
            }

            if (!rollCall.getCanRollCall()) {
                resBody.put(ApiReturnConstants.MESSAGE, "老师已修改您的考勤,不能再次签到。请联系老师 !");
                resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_CHANGE);
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return resBody;
            }

            if (slss != null && slss.contains(account.getId())) {
                rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
            }

            if (RollCallConstants.TYPE_ASK_FOR_LEAVE.equals(rollCall.getType())) {
                resBody.put(ApiReturnConstants.MESSAGE, "type is ASK_FOR_LEAVE !");
                resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_ASKFORLEAVE);
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                if (log.isDebugEnabled()) {
                    log.info("this student is ASK_FOR_LEAVE,studentId:" + rollCall.getStudentId());
                }
                return resBody;
            }

            rollCall.setHaveReport(true);
            rollCall.setGpsLocation(signInDTO.getGps());
            rollCall.setGpsDetail(signInDTO.getGpsDetail());
            rollCall.setGpsType(signInDTO.getGpsType());
            rollCall.setDeviceToken(signInDTO.getDeviceToken());
            rollCall.setSemesterId(schedule.getSemesterId());

            if (rollCall.getSignTime() == null) {
                rollCall.setSignTime(new Date(System.currentTimeMillis()));
            }

            // 获取中值 或者 验证码
            String verify = scheduleRollCall.getLocaltion();

            if (0 == rollCall.getStudentId().intValue()) {
                if (log.isDebugEnabled()) {
                    log.warn("数据异常...,排课id为:" + scheduleRollCall.getId() + ",签到数据排课id为:" + scheduleRollCall.getId());
                    log.warn("数据异常...,学生id为:" + account.getId() + ",签到数据学生id为:" + rollCall.getStudentId());
                }
                return null;
            }

            OrganSet organSet = organSetService.findByOrganId(account.getOrganId());

            if (ScheduleConstants.TYPE_ROLL_CALL_DIGITAL.equals(signInDTO.getRollCallType())) {
                if (verify.equals(signInDTO.getAuthCode())) {
                    // 入库签到信息
                    if (null != slss && slss.contains(rollCall.getStudentId())) {
                        rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                    } else {
                        rollCall.setType(CourseUtils.getResultType(scheduleRollCall.getSchedule().getScheduleStartTime(), scheduleRollCall.getCourseLaterTime(), scheduleRollCall.getAbsenteeismTime(), null));
                    }
                    redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), account.getId(), rollCall);
                } else {
                    // 前台已经做了验证码校验
                    resBody.put(ApiReturnConstants.MESSAGE, "authCode is error !");
                    resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                    return resBody;
                }
            } else if (ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC.equals(signInDTO.getRollCallType())) {
                if (StringUtils.isBlank(verify)) {
                    rollCall.setType(RollCallConstants.TYPE_COMMITTED);

                    // verify 中值未计算出来。
                    // 查询当前有多少人签到 ,是否达到计算条件
                    List<RollCall> rollCallList = listRollCallBySRCIdInRedis(scheduleRollCallId);

                    List<RollCall> rollCallReportLit = new ArrayList();
                    for (RollCall r : rollCallList) {
                        if (r.getStudentId().longValue() == rollCall.getStudentId().longValue()) {
                            r = rollCall;
                        }
                        if (r.getHaveReport()) {
                            rollCallReportLit.add(r);
                        }
                    }

                    if ((null == organSet ? 11 : organSet.getCalcount()) <= rollCallReportLit.size()) {
                        count(rollCallReportLit, scheduleRollCallId, scheduleRollCall.getSchedule().getScheduleStartTime(), scheduleRollCall.getCourseLaterTime(), scheduleRollCall.getAbsenteeismTime(),
                                null == organSet.getDeviation() ? 500 : organSet.getDeviation().intValue(), null == organSet.getConfilevel() ? 60 : organSet.getConfilevel().intValue(), true);
                    }
                } else {
                    // verify 中值已计算出来。
                    String gps = signInDTO.getGps();
                    double distance = GDMapUtil.compare(gps, scheduleRollCall.getLocaltion());
                    // gps 与 verify 比较 是否在偏移量范围内
                    int dis = (int) (distance / 10);
                    if (dis < 1) {
                        dis = 1;
                    }
                    dis = Integer.parseInt(String.valueOf(dis) + "0");
                    // 结果
                    if (distance < organSet.getDeviation()) {
                        rollCall.setType(CourseUtils.getResultType(scheduleRollCall.getSchedule().getScheduleStartTime(), scheduleRollCall.getCourseLaterTime(), scheduleRollCall.getAbsenteeismTime(), rollCall.getSignTime()));
                        rollCall.setDistance("  <" + dis + "m");
                    } else {
                        rollCall.setType(RollCallConstants.TYPE_EXCEPTION);
                        rollCall.setDistance("  >" + organSet.getDeviation() + "m");
                    }
                }
                redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), account.getId(), rollCall);
            } else {
                resBody.put(ApiReturnConstants.MESSAGE, "点名类型错误。");
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return resBody;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resBody.put(ApiReturnConstants.MESSAGE, "success!");
        resBody.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return resBody;
    }

    /**
     * 学生签到防作弊校验
     *
     * @param scheduleRollCallId
     * @param studentId
     * @param deviceToken
     * @return
     */
    public boolean signAntiCheating(Long scheduleRollCallId, Long studentId, String deviceToken) {
        boolean isCan = true;
        deviceToken = StringUtils.isNotBlank(deviceToken) ? deviceToken.trim() : null;
        try {
            Object stuId = stringRedisTemplate.opsForHash().get(RedisUtil.getAntiCheatingKey(scheduleRollCallId), deviceToken);
            if (log.isDebugEnabled()) {
                log.info("antiCheating--> scheduleRollCallId:" + scheduleRollCallId + ",stuId:" + stuId + ",studentId:" + studentId + ",deviceToken:" + deviceToken);
            }

            if (null == stuId) {
                stringRedisTemplate.opsForHash().put(RedisUtil.getAntiCheatingKey(scheduleRollCallId), deviceToken, String.valueOf(studentId));
            } else {
                Long stuIdL = Long.valueOf((String) stuId);
                if (log.isDebugEnabled()) {
                    log.info("studentId:" + studentId + ",studIdL" + stuIdL);
                }
                if (!stuIdL.equals(studentId)) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCan;
    }

    public List<RollCall> findAllByscheduleRollCallIdInAndStudentId(Set<Long> ids, Long studentId) {
        return rollCallRepository.findByScheduleRollcallIdInAndStudentId(ids, studentId);
    }

    /**
     * 计算定位点名的中值
     *
     * @param reportCalls
     * @param scheduleRollCallId
     * @param beginTime
     * @param lateTime
     * @param deviation
     * @param confilevel
     * @param isClassTime
     */
    public void count(List<RollCall> reportCalls, Long scheduleRollCallId, String beginTime, int lateTime, int absenteeismTime, int deviation, int confilevel, boolean isClassTime) {
        // 开始计算中值以及置信度
        GDMapUtil gdMap = new GDMapUtil();
        for (RollCall rcs : reportCalls) {
            gdMap.put(rcs.getGpsLocation());
        }

        // 课堂内，需要计算置信度
        int level = 0;
        if (isClassTime) {
            level = gdMap.getConfiLevel(deviation);
            if (level < confilevel) {
                log.info("计算中值，合格距离" + deviation + ",未满足置信度:" + confilevel + ",实际置信度为:" + level + "。其中排课id为:" + scheduleRollCallId);
                return;
            }
        }
        log.info("计算中值，合格距离" + deviation + ",满足置信度:" + confilevel + ",实际置信度为:" + level + "。其中排课id为:" + scheduleRollCallId);

        List<Long> slss = new ArrayList();
        scheduleRollCallService.updateScheduleVerify(scheduleRollCallId, gdMap.getMidDistribution());
        for (RollCall rcs : reportCalls) {
            rcs.setLastType(rcs.getType());
            double distance = gdMap.compareMid(rcs.getGpsLocation());
            int dis = (int) (distance / 10);
            if (dis < 1) {
                dis = 1;
            }
            dis = Integer.parseInt(String.valueOf(dis) + "0");

            // 结果
            if (distance < deviation) {
                rcs.setType(CourseUtils.getResultType(beginTime, lateTime, absenteeismTime, rcs.getSignTime()));
                rcs.setDistance("  <" + dis + "m");
            } else {
                // 是否在课程内
                if (isClassTime) {
                    rcs.setType(RollCallConstants.TYPE_EXCEPTION);
                } else {
                    rcs.setType(RollCallConstants.TYPE_TRUANCY);
                }
                rcs.setDistance("  >" + deviation + "m");
            }
            if (slss.contains(rcs.getStudentId())) {
                rcs.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
            }
            redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), rcs.getStudentId(), rcs);
        }
    }

    /**
     * ]只有已经结束的课程才可以进行该操作,取消正在进行的课程的点名信息
     *
     * @param scheduleId
     * @param teacherId
     */
    public void cancleRollCall(Long scheduleId, Long teacherId) {
        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
        List<RollCall> rollCallList = rollCallRepository.findByScheduleRollcallId(scheduleRollCall.getId());
        // 修改所有学生的状态为取消考勤
        Set<Long> cancleRollCallIds = new HashSet<Long>();
        for (RollCall rollCall : rollCallList) {
            cancleRollCallIds.add(rollCall.getId());
        }
        rollCallRepository.cancleRollCall(cancleRollCallIds, RollCallConstants.TYPE_CANCEL_ROLLCALL, null);
        scheduleRollCall.setLocaltion("");
        scheduleRollCallService.save(scheduleRollCall, scheduleId);
    }

    /**
     * 学生点名结果修改
     *
     * @param rollcallDTO
     */
    public void updateRollCallResult(RollCallDTO rollcallDTO) {

        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findOne(rollcallDTO.getScheduleId());
        Schedule schedule = scheduleService.findOne(scheduleRollCall.getSchedule().getId());
        modifyAttendanceLogService.modifyAttendance(rollcallDTO.getId(), rollcallDTO.getType(), schedule.getTeacherNname(), schedule.getTeacherId());
        RollCall rollCall = null;
        if (scheduleRollCall.getIsInClassroom()) {

            Object obj = redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollcallDTO.getUserId());

            // 课堂内修改
            rollCall = (RollCall) obj;
            rollCall.setLastType(rollCall.getType());
            rollCall.setType(rollcallDTO.getType());
            rollCall.setCanRollCall(Boolean.FALSE);

            redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollcallDTO.getUserId(), rollCall);
        } else {
            // 课堂外修改
            rollCall = rollCallRepository.findOne(rollcallDTO.getId());
            rollCall.setType(rollcallDTO.getType());
            rollCall.setCanRollCall(Boolean.FALSE);
            rollCallRepository.save(rollCall);

            //更新统计
            rollCallStatsService.statsStuAllByStuId(rollCall.getOrgId(), rollCall.getSemesterId(), rollCall.getStudentId());
            rollCallStatsService.statsStuTeachingClassByTeachingClass(rollCall.getOrgId(), rollCall.getSemesterId(), rollCall.getTeachingClassId());
        }

    }

    public boolean updateRollCall(Set<Long> rollCallIds, String type, Long teacherId) {
        // 修改redis库数据
        List<Long> scheduleRollCallIds = scheduleQuery.queryIdByTeacherIdAndCurrentTime(teacherId);

        boolean flag = true;

        if (null != scheduleRollCallIds && scheduleRollCallIds.size() > 0) {
            // 默认当前时间该老师只有一节课
            Long scheduleRollCallId = scheduleRollCallIds.get(0);
            String teacherName = "";
            if (null != scheduleRollCallId) {
                ScheduleRollCall scheduleRollCall = scheduleRollCallService.findOne(scheduleRollCallId);
                if (null != scheduleRollCall) {
                    Schedule schedule = scheduleService.findOne(scheduleRollCall.getSchedule().getId());
                    teacherName = schedule.getTeacherNname();
                }
            }
            if (null != scheduleRollCallId) {
                List<RollCall> rollCalls = listRollCallBySRCIdInRedis(scheduleRollCallId);
                if (null != rollCallIds && rollCallIds.size() > 0) {
                    Map map = new HashedMap();
                    for (RollCall rc : rollCalls) {
                        if (rollCallIds.contains(rc.getId())) {
                            flag = false;
                            rc.setLastType(rc.getType());
                            rc.setType(type);
                            rc.setCanRollCall(Boolean.FALSE);
                            map.put(rc.getStudentId(), rc);
                            modifyAttendanceLogService.modifyAttendance(rc.getId(), type, teacherName, teacherId);
                        }
                        List<RollCall> rollCallRe = (List<RollCall>) redisTemplate.opsForValue().get(RedisUtil.DIANDIAN_ROLLCALL + rc.getStudentId());
                        if (null != rollCallRe) {
                            for (RollCall r : rollCallRe) {
                                if (r.getScheduleRollcallId().equals(rc.getScheduleRollcallId())) {
                                    r.setType(type);
                                    redisTemplate.opsForValue().set(RedisUtil.DIANDIAN_ROLLCALL + r.getStudentId(), rollCallRe, 15, TimeUnit.HOURS);
                                }
                            }
                        }
                    }
                    redisTemplate.opsForHash().putAll(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), map);
                }
            }
        }

        if (flag) {
            List<RollCall> rollCallList = rollCallRepository.findByIdIn(rollCallIds);
            if (null != rollCallList && rollCallList.size() > 0) {
                if (null != rollCallList.get(0)) {
                    ScheduleRollCall scheduleRollCall = scheduleRollCallService.findOne(rollCallList.get(0).getScheduleRollcallId());
                    if (null != scheduleRollCall) {
                        Schedule schedule = scheduleService.findOne(scheduleRollCall.getSchedule().getId());
                        for (RollCall rollCall : rollCallList) {
                            modifyAttendanceLogService.modifyAttendance(rollCall.getId(), type, schedule.getTeacherNname(), teacherId);
                        }
                    }
                }
                // 修改数据库数据
                rollCallRepository.updateRollCall(rollCallIds, type, Boolean.FALSE);
            }
        }

        //更新统计
        rollCallStatsService.initStatsDateByRollCallIds(rollCallIds);

        return true;
    }

    /**
     * 开启随堂点
     */
    public void openClassrommRollcall(Long scheduleId, String rollcallType) {
        Schedule schedule = scheduleService.findOne(scheduleId);
        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
        List<RollCall> rollCallList = null;
        if (scheduleRollCall != null) {
            rollCallList = listRollCallBySRCIdInRedis(scheduleRollCall.getId());
        }
        if (rollCallList == null) {
            //初始化签到
            initScheduleService.initScheduleRollCall(schedule, Boolean.TRUE, null, null, null, null);
        }
        // 判断是否已经在 进行点名签到中
        if (null != rollCallList && rollCallList.size() > 0) {
            redisTemplate.delete(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()));
        }

        scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
        scheduleRollCall.setClassRoomRollCall(CourseRollCallConstants.OPEN_CLASSROOMROLLCALL);
        scheduleRollCall.setRollCallType(rollcallType);
        scheduleRollCall.setLocaltion("");
        scheduleRollCall.setCourseLaterTime(0);
        scheduleRollCall.setAbsenteeismTime(0);
        scheduleRollCall.setIsOpenRollcall(Boolean.TRUE);
        scheduleRollCall.setIsInClassroom(Boolean.TRUE);

        initRollCall(scheduleRollCall);

        if (ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC.equals(rollcallType)) {
            // 将上课的id放入reids,定时任务后续将计算中值
            Set<Long> scheduleRollCallIds = (Set<Long>) redisTemplate.opsForHash().get(RedisUtil.getScheduleOrganId(), schedule.getOrganId().longValue());
            if (null == scheduleRollCallIds) {
                scheduleRollCallIds = new HashSet<>();
            }
            scheduleRollCallIds.add(scheduleRollCall.getId());
            redisTemplate.opsForHash().put(RedisUtil.getScheduleOrganId(), schedule.getOrganId(), scheduleRollCallIds);
            // 临时方案，将数据缓存redis
            redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallDateKey(scheduleRollCall.getId().longValue()), scheduleRollCall.getId(),
                    new ScheduleRollCallIngDTO("1000", scheduleRollCall.getCourseLaterTime(), scheduleRollCall.getAbsenteeismTime()));
            redisTemplate.expire(RedisUtil.getScheduleRollCallDateKey(scheduleRollCall.getId().longValue()), 24, TimeUnit.HOURS);

        }
        scheduleRollCallService.save(scheduleRollCall, scheduleId);
        // 将上课的id放入reids,定时任务后续将计算中值 无用代码
        Set<Long> scheduleRollCallIds = (Set<Long>) redisTemplate.opsForHash().get(RedisUtil.getScheduleOrganId(), schedule.getOrganId().longValue());
        if (null == scheduleRollCallIds) {
            scheduleRollCallIds = new HashSet<>();
        }

    }

    /**
     * 初始化学生签到数据
     *
     * @param scheduleRollCall
     * @return
     */
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
        Date date = DateFormatUtil.parse2(schedule.getTeachDate() + " " + schedule.getScheduleStartTime(), DateFormatUtil.FORMAT_MINUTE);
        List<StudentDTO> studentList = studentService.listStudents2(teachingclassId, date);
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
        scheduleRollCallService.save(scheduleRollCall, schedule.getId());
        schedule.setIsInitRollcall(Boolean.TRUE);
        scheduleService.save(schedule);
        log.info("初始化学生签到信息完毕。" + schedule.getId());
        return true;
    }

    public boolean closeClassrommRollcall(Long scheduleId, ScheduleRollCall scheduleRollCall) {
        try {
            // 1.修改排课表，修改其标志为 关闭随堂点
            scheduleRollCall.setClassRoomRollCall(CourseRollCallConstants.CLOSED_CLASSROOMROLLCALL);
            scheduleRollCallService.save(scheduleRollCall, scheduleId);
            List<RollCall> rollCallList = listRollCallBySRCIdInRedis(scheduleRollCall.getId());
            // 2.将未提交的学生状态改为旷课。
            Map<Long, RollCall> rollCallMap = new HashMap();
            for (RollCall rollCall : rollCallList) {
                String type = rollCall.getType();
                rollCall.setLastType(type);
                if (RollCallConstants.TYPE_UNCOMMITTED.equals(type)) {
                    rollCall.setType(RollCallConstants.TYPE_TRUANCY);
                } else if (RollCallConstants.TYPE_COMMITTED.equals(type)) {
                    rollCall.setType(RollCallConstants.TYPE_NORMA);
                }
                rollCall.setCanRollCall(Boolean.FALSE);
                rollCallMap.put(rollCall.getStudentId(), rollCall);
            }
            redisTemplate.opsForHash().putAll(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCallMap);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public PageData<AttendanceAllDTO> getSchedule(Integer offset, Integer limit, Long organId, Long userId, Long semesterId, String typeId, String courseName, Long courseId) {
        return studentAttendanceQuery.queryScheduleAttendance(offset, limit, organId, userId, semesterId, typeId, courseName, courseId);
    }

    public List<AttendanceCountDTO> getAttendance(Long organId, Long userId, Long semesterId) {
        return studentAttendanceQuery.queryScheduleAttendanceCount(organId, userId, semesterId);
    }

    public List<AttendanceCountDTO> getcourseCount(Integer offset, Integer limit, Long organId, Long userId, String typeId, Long semesterId, String courseName, Long courseId) {
        return studentAttendanceQuery.queryScheduleAttendanceCourse(offset, limit, organId, userId, semesterId, typeId, courseName, courseId);
    }

    public List<RollCall> listRollCallBySRCIdInRedis(Long scheduleRollCallId) {
        return redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }

    public void deleteRollCallBySRCIdInRedis(Long scheduleRollCallId) {
        redisTemplate.delete(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }

    public void updateRollCallByStudentIdAndScheduleRollCall(String type, Long studentId, Set<Long> scheduleRollCallIds) {
        rollCallRepository.updateRollCallByStudentIdAndScheduleRollCall(type, studentId, scheduleRollCallIds);
    }

    public Object getRollCallInfoByTeacherAndWeek(Long weekId, Long teacherId) {
        List<RollCallOfScheduleDTO> resultList = new ArrayList();
        Map<Long, RollCallOfScheduleDTO> t = new HashMap<Long, RollCallOfScheduleDTO>();

        List<RollCallOfSearchDTO> rl = attendanceListQuery.getRollCallInfoByTeacherAndWeek(teacherId, weekId);
        for (RollCallOfSearchDTO dto : rl) {
            Long classId = dto.getClassId();
            if (null == classId || "".equals(classId)) {
                continue;
            }
            if (0 == classId.longValue()) {
                log.warn("排课id:" + dto.getScheduleId());
                continue;
            }
            Long scheduleId = dto.getScheduleId();
            RollCallOfScheduleDTO p = null;
            if (null == t.get(scheduleId)) {
                RollCallOfScheduleDTO ts = new RollCallOfScheduleDTO();
                t.put(scheduleId, ts);
                ts.setCourseId(dto.getCourseId());
                ts.setClassInfo(new ArrayList<RollCallOfClassDTO>());
                ts.setDayOfWeek(dto.getDayOfWeek());
                ts.setPeriodName("第" + dto.getPeriodName() + "节课");
                ts.setScheduleId(dto.getScheduleId());
                ts.setCourseName(dto.getCourseName());
                resultList.add(ts);
            }
            p = t.get(scheduleId);
            p.setAllCount((p.getAllCount() == null ? 0 : p.getAllCount()) + dto.getAllCount());
            p.setnCount((p.getnCount() == null ? 0 : p.getnCount()) + dto.getnCount());
            RollCallOfClassDTO tc = new RollCallOfClassDTO();
            tc.setAllCount(dto.getAllCount());
            tc.setClassId(dto.getClassId());
            tc.setClassName(null == dto.getClassName() ? "" : dto.getClassName());
            tc.setnCount(dto.getnCount());
            p.getClassInfo().add(tc);
        }

        return resultList;
    }

    /**
     * 教师端导出学生考勤
     *
     * @param teacherId
     * @param beginTime
     * @param endTime
     * @param courseId
     * @param isHearTeacher
     * @return
     */
    public List<RollCallExport> exportRollCallExecl(long teacherId, String beginTime, String endTime, Long courseId, boolean isHearTeacher, String studentIds) {
        return rollCallExportInfoQuery.queryRollCall(teacherId, beginTime, endTime, courseId, isHearTeacher, studentIds);
    }

    public PersonalAttendanceDTO getPersonalAttendanceForTeacher(Long teacherId, Long semesterId) {
        List<PersonalAttendanceDTO> lr = personalAttendanceQuery.queryForTeacher(teacherId, semesterId);
        List<PersonalAttendanceDTO> la = personalAttendanceQuery.queryAssess(teacherId, semesterId);
        if (null != lr && lr.size() > 0) {
            PersonalAttendanceDTO pr = lr.get(0);
            float avage = 0.00f;
            if (null != la && la.size() > 0) {
                pr.setAssessTotalCount(la.get(0).getAssessTotalCount());
            }
            return pr;
        }
        return null;
    }

    public PersonalAttendanceDTO getPersonalAttendanceForStudent(Long studentId, Long semesterId) {
        List<PersonalAttendanceDTO> lr = personalAttendanceQuery.query(studentId, semesterId);
        if (null != lr && lr.size() > 0) {
            PersonalAttendanceDTO pr = lr.get(0);
            Long a = assessService.getWaitAssessCount(studentId, semesterId);
            pr.setWaitAssess(a);
            return pr;
        }
        return new PersonalAttendanceDTO();
    }

    // 班级到课率
    public List courselorClassAttendance(Long headTeacherId, Long semesterId) {
        List<IdNameDomain> classs = orgManagerRemoteService.getClassesByTeacher(headTeacherId);
        List<CounselorClassesAddendanceDTO> counselorClassesAddendanceDTOS = null;
        if (null != classs && classs.size() > 0) {
            Set<Long> set = new HashSet<>();
            for (IdNameDomain idNameDomain : classs) {
                set.add(idNameDomain.getId());
            }
            counselorClassesAddendanceDTOS = counselorStudentAttendanceQuery.queryClassesAttendance(set, semesterId);
        }
        return counselorClassesAddendanceDTOS;
    }

    public List<CounselorStudentAddendanceDTO> courselorStudentAttendance(Long classId, Long semesterId, String sort) {
        List<CounselorStudentAddendanceDTO> dtoList = counselorStudentAttendanceQuery.queryStudentsAttendance(classId, semesterId, sort);

        try {
            // 组装头像
            if (null != dtoList) {
                Set studentIds = new HashSet();
                for (CounselorStudentAddendanceDTO dto : dtoList) {
                    studentIds.add(dto.getStudentId());
                }
                Map<String, String> listMap = httpSimpleUtils.HttpGet(StringUtils.join(studentIds.toArray(), ","));
                for (CounselorStudentAddendanceDTO dto : dtoList) {
                    dto.setAvatar(org.springframework.util.StringUtils.isEmpty(listMap.get(dto.getStudentId().intValue() + "avatar")) ? "" : listMap.get(dto.getStudentId().intValue() + "avatar"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dtoList;
    }

    /**
     * 补录考勤
     */
    public void additionaleRollcall(Long scheduleId) {
        Schedule schedule = scheduleService.findOne(scheduleId);
        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
        if (null == scheduleRollCall) {
            scheduleRollCall = new ScheduleRollCall();
            scheduleRollCall.setCreatedDate(DateFormatUtil.parse2(schedule.getTeachDate(), DateFormatUtil.FORMAT_SHORT));
        }
        // 进行该排课点名信息的初始化操作。
        scheduleRollCall.setSchedule(schedule);
        scheduleRollCall.setRollCallType(ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC);
        scheduleRollCall.setCourseLaterTime(0);
        scheduleRollCall.setAbsenteeismTime(0);
        scheduleRollCall.setClassRoomRollCall(CourseRollCallConstants.NOT_OPEN_CLASSROOMROLLCALL);
        scheduleRollCall.setLocaltion("补录考勤");
        scheduleRollCall.setIsOpenRollcall(Boolean.TRUE);
        scheduleRollCall.setIsInClassroom(Boolean.FALSE);
        scheduleRollCallService.save(scheduleRollCall, scheduleId);
        Long teachingclassId = schedule.getTeachingclassId();
        Date startDate = DateFormatUtil.parse2(schedule.getTeachDate() + " " + schedule.getScheduleStartTime(), DateFormatUtil.FORMAT_MINUTE);
        Date endDate = DateFormatUtil.parse2(schedule.getTeachDate() + " " + schedule.getScheduleEndTime(), DateFormatUtil.FORMAT_MINUTE);
        List<StudentDTO> studentList = studentService.listStudents2(teachingclassId, startDate);
        if (null == studentList) {
            log.info("根据教学班id获取学生列表信息为空!");
            return;
        }
        List<Long> studentLeaves = studentLeaveScheduleService.findStudentIdByScheduleId(schedule, startDate, endDate);
        RollCall rollCall = null;
        List list = new ArrayList();
        for (StudentDTO dto : studentList) {
            rollCall = new RollCall();
            rollCall.setId(RedisUtil.getRollCallId());
            rollCall.setScheduleRollcallId(scheduleRollCall.getId());
            rollCall.setTeacherId(schedule.getTeacherId());
            rollCall.setStudentId(dto.getStudentId());
            rollCall.setStudentNum(dto.getSutdentNum());
            rollCall.setClassId(dto.getClassesId());
            rollCall.setClassName(dto.getClassesName());
            rollCall.setTeachingClassId(teachingclassId);
            rollCall.setCourseId(schedule.getCourseId());
            rollCall.setCanRollCall(Boolean.FALSE);
            rollCall.setHaveReport(Boolean.FALSE);
            rollCall.setProfessionalId(dto.getProfessionalId());
            rollCall.setProfessionalName(dto.getProfessionalName());
            rollCall.setCollegeId(dto.getCollegeId());
            rollCall.setCollegeName(dto.getCollegeName());
            rollCall.setOrgId(schedule.getOrganId());
            rollCall.setTeachingYear(dto.getTeachingYear());
            rollCall.setCreatedDate(DateFormatUtil.parse2(schedule.getTeachDate(), DateFormatUtil.FORMAT_SHORT));
            // 判断该学生是否有请假
            if (studentLeaves != null && studentLeaves.contains(dto.getStudentId())) {
                rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
            } else {
                rollCall.setType(RollCallConstants.TYPE_NORMA);
            }
            rollCall.setStudentName(dto.getStudentName());
            rollCall.setSemesterId(schedule.getSemesterId());
            list.add(rollCall);
        }
        rollCallRepository.save(list);
        schedule.setIsInitRollcall(Boolean.TRUE);
        scheduleService.save(schedule);
    }

    /**
     * 补全到课率
     *
     * @param organIds
     */
    @Async
    public void initScheduleRollCallAttendance(Set<Long> organIds) {
        if (null == organIds || organIds.isEmpty()) {
            return;
        }
        for (Long organId : organIds) {
            List<Schedule> scheduleList = scheduleRollCallService.findAllByOrganId(organId);
            if (scheduleList == null || scheduleList.isEmpty()) {
                return;
            }
            for (Schedule schedule : scheduleList) {
                ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule.getId());
                if (scheduleRollCall == null) {
                    continue;
                }
                calculateAttendance(organId, scheduleRollCall.getId());
            }
        }
    }

    public String calculateAttendance(Long orgId, Long scheduleRollCallId) {

        OrganSet organSet = organSetService.getByOrganId(orgId);
        int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();

        List<RollCall> rollCalls = rollCallRepository.findAllByScheduleRollcallIdAndDeleteFlag(scheduleRollCallId, DataValidity.VALID.getState());
        return calculateAttendanceRollCall(rollCalls, type);
    }

    public String calculateAttendanceRollCall(List<RollCall> rollCalls, int type) {
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

    public Integer calculateRollCallStu(List<RollCall> rollCalls, int type) {
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

    /**
     * 补全考勤表组织结构信息
     */
    public void addRollCallOrgInfo(int pageSize) {
        int totalPages;
        int pageNumber = 1;
        do {
            Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
            Page<RollCall> rollCallPage = rollCallRepository.findAllByDeleteFlagAndOrgIdIsNotNull(DataValidity.VALID.getState(), pageable);
            totalPages = rollCallPage.getTotalPages();
            List<RollCall> content = rollCallPage.getContent();
            if (content != null && !content.isEmpty()) {
                conRollcall(content);
            }
            pageNumber++;
        } while (totalPages >= pageNumber);
    }

    /**
     * 补全考勤表组织结构信息
     */
    public void addRollCallOrgInfo(int pageSize, Long orgId) {
        int totalPages;
        int pageNumber = 1;
        do {
            Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
            Page<RollCall> rollCallPage = rollCallRepository.findAllByDeleteFlagAndOrgId(DataValidity.VALID.getState(), orgId, pageable);
            totalPages = rollCallPage.getTotalPages();
            List<RollCall> content = rollCallPage.getContent();
            if (content != null && !content.isEmpty()) {
                conRollcall(content);
            }
            pageNumber++;
        } while (totalPages >= pageNumber);
    }

    public void conRollcall(List<RollCall> content) {

        Set<Long> set = new HashSet<>();
        for (RollCall rollCall : content) {
            set.add(rollCall.getStudentId());
        }
        List<StudentDTO> studentDTOS = studentService.getStudentByIds(set);
        if (studentDTOS == null || studentDTOS.isEmpty()) {
            return;
        }
        Map<Long, StudentDTO> stuMap = new HashMap<>();
        for (StudentDTO studentDTO : studentDTOS) {
            stuMap.put(studentDTO.getStudentId(), studentDTO);
        }
        StudentDTO studentDTO = null;
        List<RollCall> rollCalls = new ArrayList<>();
        for (RollCall rollCall : content) {
            studentDTO = stuMap.get(rollCall.getStudentId());
            if (studentDTO == null) {
                continue;
            }
            if (null == studentDTO.getOrgId()) {
                continue;
            }
            rollCall.setProfessionalId(studentDTO.getProfessionalId());
            rollCall.setProfessionalName(studentDTO.getProfessionalName());
            rollCall.setCollegeId(studentDTO.getCollegeId());
            rollCall.setCollegeName(studentDTO.getCollegeName());
            rollCall.setTeachingYear(studentDTO.getTeachingYear());
            rollCall.setOrgId(studentDTO.getOrgId());
            rollCalls.add(rollCall);
        }
        rollCallRepository.save(rollCalls);
    }
}
