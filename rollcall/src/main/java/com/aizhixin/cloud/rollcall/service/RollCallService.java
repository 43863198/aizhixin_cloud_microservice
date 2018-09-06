package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.core.ApiReturnConstants;
import com.aizhixin.cloud.rollcall.common.util.GDMapUtil;
import com.aizhixin.cloud.rollcall.core.CourseRollCallConstants;
import com.aizhixin.cloud.rollcall.core.RollCallConstants;
import com.aizhixin.cloud.rollcall.domain.*;
import com.aizhixin.cloud.rollcall.entity.OrganSet;
import com.aizhixin.cloud.rollcall.entity.RollCall;
import com.aizhixin.cloud.rollcall.repository.RollCallRepository;
import com.google.common.collect.Maps;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RollCallService {

    private final static Logger LOG = LoggerFactory.getLogger(RollCallService.class);

    @Autowired
    private RollCallRepository rollCallRepository;

    @Autowired
    private OrganSetService organSetService;

    @Autowired
    private ClassOutTaskPreprocessService classOutTaskPreprocessService;

    @Autowired
    private RedisDataService redisDataService;

    // 用于切换map
    private static AtomicBoolean status = new AtomicBoolean(Boolean.TRUE);

    private static ConcurrentHashMap<String, Map<String, LocaltionDomain>> map_1 = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, Map<String, LocaltionDomain>> map_2 = new ConcurrentHashMap();

    @Transactional
    public List<RollCall> save(List<RollCall> list) {
        return rollCallRepository.save(list);
    }

    public ScheduleRollCallRedisDomain getScheduleRollcall(Long orgId, Long scheduleId) {
        ScheduleRedisDomain scheduleRedisDomain = redisDataService.getSchedule(orgId, scheduleId);
        if (scheduleRedisDomain == null) {
            return null;
        }
        return redisDataService.getRuler(orgId, scheduleRedisDomain.getScheduleRollCallId());
    }

    /**
     * 签到
     *
     * @param orgId
     * @param studentId
     * @param signInDomain
     * @return
     */
    public Map signIn(Long orgId, Long studentId, SignInDomain signInDomain) {
        Map result = new HashMap();
        long scheduleId = signInDomain.getScheduleId();

        ScheduleRedisDomain scheduleRedisDomain = redisDataService.getSchedule(orgId, scheduleId);
        Long scheduleRollCallId = scheduleRedisDomain.getScheduleRollCallId();

        ScheduleRollCallRedisDomain scheduleRollCallRedisDomain = redisDataService.getRuler(orgId, scheduleRollCallId);
        if (null == scheduleRollCallRedisDomain) {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return result;
        }

        RollcallRedisDomain studentScheduleRollcall = redisDataService.getStudentScheduleRollcall(orgId, scheduleRollCallId, studentId);
        if (null == studentScheduleRollcall) {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return result;
        }

        if (!studentScheduleRollcall.getCanRollCall()) {
            result.put(ApiReturnConstants.MESSAGE, "老师已修改您的考勤,不能再次签到。请联系老师 !");
            result.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_CHANGE);
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return result;
        }

        // 判断之前的状态，如果正常直接返回
        if (RollCallConstants.TYPE_NORMA.equals(studentScheduleRollcall.getType())) {
            // 直接返回
            result.put(ApiReturnConstants.MESSAGE, "已签到成功!");
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
            return result;
        }

        // if (slss != null && slss.contains(account.getId())) {
        // rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
        // }

        if (RollCallConstants.TYPE_ASK_FOR_LEAVE.equals(studentScheduleRollcall.getType())) {
            result.put(ApiReturnConstants.MESSAGE, "type is ASK_FOR_LEAVE !");
            result.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_ASKFORLEAVE);
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return result;
        }

        studentScheduleRollcall.setHaveReport(Boolean.TRUE);
        studentScheduleRollcall.setGpsLocation(signInDomain.getGps());
        studentScheduleRollcall.setGpsDetail(signInDomain.getGpsDetail());
        studentScheduleRollcall.setGpsType(signInDomain.getGpsType());
        studentScheduleRollcall.setDeviceToken(signInDomain.getDeviceToken());
        studentScheduleRollcall.setStudentId(studentId);
        if (studentScheduleRollcall.getSignTime() == null) {
            studentScheduleRollcall.setSignTime(new Date(System.currentTimeMillis()));
        }

        boolean isNormal
            = classOutTaskPreprocessService.isNormal(scheduleRedisDomain.getStartDate(), scheduleRollCallRedisDomain.getCourseLaterTime(), studentScheduleRollcall.getSignTime());

        // 获取中值 或者 验证码
        String verify = scheduleRollCallRedisDomain.getLocaltion();

        // 数字点名
        switch (signInDomain.getRollCallType()) {
            case CourseRollCallConstants.TYPE_ROLL_CALL_DIGITAL:
                if (verify.equals(signInDomain.getAuthCode())) {
                    if (isNormal) {
                        studentScheduleRollcall.setType(RollCallConstants.TYPE_NORMA);
                    } else {
                        studentScheduleRollcall.setType(RollCallConstants.TYPE_LATE);
                    }
                } else {
                    // 前台已经做了验证码校验
                    result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                    return result;
                }
                break;
            case CourseRollCallConstants.TYPE_ROLL_CALL_AUTOMATIC:
                if (StringUtils.isBlank(verify)) {
                    studentScheduleRollcall.setType(RollCallConstants.TYPE_COMMITTED);
                    setLocaltionValue(orgId + ":" + scheduleRollCallId, studentId, new LocaltionDomain(studentId, signInDomain.getGps(), new Date()));
                } else {
                    OrganSet organSet = organSetService.getByOrganId(orgId);
                    double distance = GDMapUtil.compare(signInDomain.getGps(), verify);
                    if (distance < organSet.getDeviation()) {
                        if (isNormal) {
                            studentScheduleRollcall.setType(RollCallConstants.TYPE_NORMA);
                        } else {
                            studentScheduleRollcall.setType(RollCallConstants.TYPE_LATE);
                        }
                        int dis = (int)(distance / 10);
                        dis = Integer.parseInt(String.valueOf(dis < 1 ? 1 : dis) + "0");
                        studentScheduleRollcall.setDistance("  <" + dis + "m");
                    } else {
                        studentScheduleRollcall.setType(RollCallConstants.TYPE_EXCEPTION);
                        studentScheduleRollcall.setDistance("  >" + organSet.getDeviation() + "m");
                    }
                }
        }

        Map map = new HashMap();
        map.put(studentScheduleRollcall.getStudentId().toString(), studentScheduleRollcall);
        redisDataService.cacheScheduleRollcall(orgId, scheduleRollCallId, map);
        result.put(ApiReturnConstants.MESSAGE, "success!");
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;
    }

    public void objCopy(StudentDomain studentDomain, RollcallRedisDomain rollcallRedisDomain, ScheduleRedisDomain schedule) {
        rollcallRedisDomain.setScheduleRollcallId(schedule.getScheduleRollCallId());
        rollcallRedisDomain.setTeacherId(schedule.getTeacherId());
        rollcallRedisDomain.setStudentId(studentDomain.getStudentId());
        rollcallRedisDomain.setStudentNum(studentDomain.getSutdentNum());
        rollcallRedisDomain.setStudentName(studentDomain.getStudentName());
        rollcallRedisDomain.setClassId(studentDomain.getClassesId());
        rollcallRedisDomain.setClassName(studentDomain.getClassesName());
        rollcallRedisDomain.setTeachingClassId(schedule.getTeachingclassId());
        rollcallRedisDomain.setCourseId(schedule.getCourseId());
        rollcallRedisDomain.setCanRollCall(Boolean.TRUE);
        rollcallRedisDomain.setHaveReport(Boolean.FALSE);
        rollcallRedisDomain.setProfessionalId(studentDomain.getProfessionalId());
        rollcallRedisDomain.setProfessionalName(studentDomain.getProfessionalName());
        rollcallRedisDomain.setCollegeId(studentDomain.getCollegeId());
        rollcallRedisDomain.setCollegeName(studentDomain.getCollegeName());
        rollcallRedisDomain.setTeachingYear(studentDomain.getTeachingYear());
        rollcallRedisDomain.setOrgId(schedule.getOrgId());
        rollcallRedisDomain.setType(RollCallConstants.TYPE_UNCOMMITTED);
        rollcallRedisDomain.setSemesterId(schedule.getSemesterId());
    }

    public RollCall createRollCallByCache(RollcallRedisDomain d, ScheduleRedisDomain schedule) {
        RollCall r = new RollCall();
        r.setCanRollCall(d.getCanRollCall());
        r.setClassId(d.getClassId());
        r.setClassName(d.getClassName());
        r.setCollegeId(d.getCollegeId());
        r.setCollegeName(d.getCollegeName());
        r.setCourseId(d.getCourseId());
        r.setDeviceToken(d.getDeviceToken());
        r.setDistance(d.getDistance());
        r.setGpsDetail(d.getGpsDetail());
        r.setGpsLocation(d.getGpsLocation());
        r.setGpsType(d.getGpsType());
        r.setLastType(d.getLastType());
        r.setHaveReport(d.getHaveReport());
        r.setProfessionalId(d.getProfessionalId());
        r.setProfessionalName(d.getProfessionalName());
        r.setScheduleRollcallId(d.getScheduleRollcallId());
        r.setSemesterId(d.getSemesterId());
        r.setSignTime(d.getSignTime());
        r.setStudentId(d.getStudentId());
        r.setStudentName(d.getStudentName());
        r.setStudentNum(d.getStudentNum());
        r.setTeacherId(schedule.getTeacherId());
        r.setTeachingClassId(d.getTeachingClassId());
        r.setTeachingYear(d.getTeachingYear());
        r.setType(d.getType());
        return r;
    }

    /**
     * 同步签到信息
     */
    @Scheduled(fixedDelay = 10000)
    public void synSignInfo() {
        ConcurrentHashMap<String, Map<String, LocaltionDomain>> map = getRollCallMap();

        long start = System.currentTimeMillis();
        if (map.size() > 0) {
            for (Map.Entry<String, Map<String, LocaltionDomain>> entry : map.entrySet()) {
                redisDataService.cacheOrgInClassScheduleMedian(entry.getKey(), entry.getValue(), 120000);
            }
            map.clear();
        }
        LOG.info("需要上传的签到信息数量为:({}),耗时为:({})", map.size(), (System.currentTimeMillis() - start) / 1000);
    }

    public static void setLocaltionValue(String scheduleKey, Long studentId, LocaltionDomain dto) {
        Map<String, Map<String, LocaltionDomain>> map = null;
        if (status.get()) {
            map = map_1;
        } else {
            map = map_2;
        }
        Map<String, LocaltionDomain> tempMap = map.get(scheduleKey);
        if (tempMap == null) {
            tempMap = new HashedMap();
        }
        tempMap.put(studentId.toString(), dto);
        map.put(scheduleKey, tempMap);
    }

    public static ConcurrentHashMap getRollCallMap() {
        if (status.get()) {
            status.set(Boolean.FALSE);
            return map_1;
        } else {
            status.set(Boolean.TRUE);
            return map_2;
        }
    }

    public String calculateAttendanceRollCall(List<RollcallRedisDomain> rollCalls, Long orgId) {

        String result = null;
        if (rollCalls == null || rollCalls.isEmpty()) {
            return result;
        }
        OrganSet organSet = organSetService.getByOrganId(orgId);
        int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();
        if (null != rollCalls && !rollCalls.isEmpty()) {
            int total = rollCalls.size();
            int normal = 0;
            int later = 0;
            int askForLeave = 0;
            int leave = 0;
            for (RollcallRedisDomain rollCall : rollCalls) {
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
            result = attendanceAccount(total, normal, later, askForLeave, leave, type);
        }
        return result;
    }

    public static String attendanceAccount(int total, int normal, int later, int askForleave, int leave, int type) {
        if (type == 0) {
            type = 10;
        }
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);
        int element = 0;
        switch (type) {
            case 10:
                element = normal;
                break;
            case 20:
                element = normal + askForleave;
                break;
            case 30:
                element = normal + later;
                break;
            case 40:
                element = normal + leave;
                break;
            case 50:
                element = normal + later + leave;
                break;
            case 60:
                element = normal + later + leave + askForleave;
                break;
        }
        float temp = (float)element / (total == 0 ? 1 : total);
        return nt.format(temp);
    }

    public List<RollCall> listRollCall(Long orgId, Long scheduleId) {
        List<RollCall> rollCallList = new ArrayList<>();
        ScheduleRedisDomain s = redisDataService.getSchedule(orgId, scheduleId);
        // 获取课堂的学生数据
        Set<Long> ss = redisDataService.getScheduleStudent(orgId, scheduleId);
        if (null != ss && !ss.isEmpty()) {
            Set<String> studentIds = new HashSet<>();
            for (Long sid : ss) {
                studentIds.add(sid.toString());
            }
            // 获取学生的签到列表
            List<RollcallRedisDomain> studentRollcallList = redisDataService.getScheduleRollcall(orgId, s.getScheduleRollCallId(), studentIds);
            if (null == studentRollcallList || studentRollcallList.isEmpty()) {
                LOG.warn("退出课堂ID({})时，根据学生ID列表，没有找到学生签到列表数据", s.getScheduleId(), s.getScheduleRollCallId());
                return rollCallList;
            }
            for (RollcallRedisDomain r : studentRollcallList) {
                if (null != r) {
                    rollCallList.add(createRollCallByCache(r, s));
                }
            }
        }
        return rollCallList;
    }
}
