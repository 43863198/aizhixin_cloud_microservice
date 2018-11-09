package com.aizhixin.cloud.dd.rollcall.serviceV2;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.ScheduleRollCallJdbc;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.LocaltionDTO;
import com.aizhixin.cloud.dd.rollcall.dto.ScheduleRollCallIngDTO;
import com.aizhixin.cloud.dd.rollcall.dto.SignInDTO;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.GDMapUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RollCallMapUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by LIMH on 2017/8/10.
 */
@Component
@Transactional
public class RollCallServiceV2 {
    private final static Logger log = LoggerFactory.getLogger(RollCallServiceV2.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private OrganSetService organSetService;
    @Autowired
    private StudentLeaveScheduleService studentLeaveScheduleService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RollCallRecordService rollCallRecordService;
    @Autowired
    private ScheduleRollCallJdbc scheduleRollCallJdbc;

    /**
     * 学生签到
     *
     * @param account
     * @param signInDTO
     * @return
     */
    public Object excuteSignIn(AccountDTO account, SignInDTO signInDTO) {
        Map<String, Object> resBody = new HashMap<>();
        Long scheduleId = signInDTO.getScheduleId();
        if (scheduleId == null) {
            log.info("scheduleId is null ");
            resBody.put(ApiReturnConstants.MESSAGE, "无排课Id信息!");
            resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_SCHEDULE);
            resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return resBody;
        }
        Schedule schedule = scheduleService.findOne(scheduleId);
        if (null == schedule) {
            log.info("schedule is null" + scheduleId);
            resBody.put(ApiReturnConstants.MESSAGE, "无排课信息!");
            resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_SCHEDULE);
            resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return resBody;
        }
        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
        Long scheduleRollCallId = scheduleRollCall.getId();
        //防作弊
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
            // 第一次签到  停止考勤，没有考勤记录
            if (rollCall == null) {
                log.info("rollCall is null" + scheduleId);
                resBody.put(ApiReturnConstants.MESSAGE, "您已停止考勤!");
                resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_PAUSE);
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return resBody;
            }
            if (ScheduleConstants.TYPE_CLOSE_CLASSROOMROLLCALL == scheduleRollCall.getClassRoomRollCall().intValue()) {
                resBody.put(ApiReturnConstants.MESSAGE, RollCallConstants.ROLL_CALL_CLOSE_MESSAGE);
                resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_CLOSE);
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return resBody;
            }
            if (!rollCall.getCanRollCall() || RollCallConstants.TYPE_CANCEL_ROLLCALL.equals(rollCall.getType())) {
                if (rollCall.getIsPublicLeave()) {
                    resBody.put(ApiReturnConstants.MESSAGE, "请公假不能签到!");
                    resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_ASKFORLEAVE);
                } else {
                    resBody.put(ApiReturnConstants.MESSAGE, "老师已修改您的考勤,不能再次签到。请联系老师 !");
                    resBody.put(ApiReturnConstants.CODE, RollCallConstants.ROLL_CALL_CHANGE);
                }
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return resBody;
            }
            // 判断之前的状态，如果正常直接返回
            if (RollCallConstants.TYPE_NORMA.equals(rollCall.getType())) {
                // 直接返回
                resBody.put(ApiReturnConstants.MESSAGE, "已签到成功!");
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
                return resBody;
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
                    log.info("数据异常...,排课id为:" + scheduleRollCall.getId() + ",签到数据排课id为:" + scheduleRollCall.getId());
                    log.info("数据异常...,学生id为:" + account.getId() + ",签到数据学生id为:" + rollCall.getStudentId());
                }
                log.info("rollCall.getStudentId() is error" + rollCall.getStudentId());
                resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                return resBody;
            }
            OrganSet organSet = organSetService.findByOrganId(account.getOrganId());
            if (ScheduleConstants.TYPE_ROLL_CALL_DIGITAL.equals(signInDTO.getRollCallType())) {
                if (verify.equals(signInDTO.getAuthCode())) {
                    rollCall.setType(CourseUtils.getResultType(scheduleRollCall.getSchedule().getScheduleStartTime(), scheduleRollCall.getCourseLaterTime(), scheduleRollCall.getAbsenteeismTime(), rollCall.getSignTime()));
                    redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), account.getId(), rollCall);
                } else {
                    // 前台已经做了验证码校验
                    resBody.put(ApiReturnConstants.MESSAGE, "authCode is error !");
                    resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                    return resBody;
                }
            } else if (ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC.equals(signInDTO.getRollCallType())) {
                verify = (String) redisTemplate.opsForValue().get("l-" + scheduleRollCallId);
                if (StringUtils.isBlank(verify)) {
                    //RollCallMapUtil.setValue(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId), account.getId(), );
                    redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId), account.getId(), new LocaltionDTO(account.getId(), rollCall.getGpsLocation(), rollCall.getSignTime()));
                    rollCall.setType(RollCallConstants.TYPE_COMMITTED);
                } else {
                    // verify 中值已计算出来。
                    String gps = signInDTO.getGps();
                    double distance = GDMapUtil.compare(gps, verify);
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
            log.warn("签到异常" + e.getMessage(), e);
            resBody.put(ApiReturnConstants.MESSAGE, "签到异常!");
            resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return resBody;
        }
        resBody.put(ApiReturnConstants.MESSAGE, "success!");
        resBody.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return resBody;
    }

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

    public void checkRollCallTypeSchedule() {
        //获取中值任务
        Object run = redisTemplate.opsForValue().get(RedisUtil.getScheduleTask());
        if (null == run) {
            redisTemplate.opsForValue().append(RedisUtil.getScheduleTask(), RedisUtil.DIANDIAN_TASK_RUNNING);
        } else {
            String str = String.valueOf(run);
            if (RedisUtil.DIANDIAN_TASK_RUNNING.equals(str)) {
                log.info("计算任务正在执行中，等待其上一次执行结束...");
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
                    log.info("执行计算中值耗时为：" + (end - start) + "ms");
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
        String midValu = gdMap.getMidDistribution();
        log.info("计算中值，合格距离" + deviation + ",满足置信度:" + confilevel + ",实际置信度为:" + level + "。其中排课id为:" + scheduleRollCallId + "中值为：" + midValu);
        scheduleRollCallService.updateScheduleVerify(scheduleRollCallId, gdMap.getMidDistribution());
        scheduleRollCallJdbc.updateScheduleVerify(scheduleRollCallId, midValu);
        redisTemplate.opsForValue().set("l-" + scheduleRollCallId, midValu, 1, TimeUnit.DAYS);
        log.info("中值更新成功，其中排课点名任务id为:" + scheduleRollCallId + "中值为：" + midValu);

        ScheduleRollCallIngDTO dto = (ScheduleRollCallIngDTO) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallDateKey(scheduleRollCallId), scheduleRollCallId);
        updateRollcall(organId, scheduleRollCallId, gdMap, deviation, dto);
    }

    @Async("threadPool1")
    private void updateRollcall(Long organId, Long scheduleRollCallId, GDMapUtil gdMap, int deviation, ScheduleRollCallIngDTO dto) {
        List<LocaltionDTO> list = redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId.longValue()));
        for (LocaltionDTO localtionDTO : list) {
            Long studentId = localtionDTO.getId();
            RollCall rcs = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), studentId);
            rcs.setLastType(rcs.getType());
            double distance = gdMap.compareMid(localtionDTO.getLo());
            int dis = (int) (distance / 10);
            if (dis < 1) {
                dis = 1;
            }
            dis = Integer.parseInt(String.valueOf(dis) + "0");
            // 结果
            if (distance < deviation) {
                rcs.setType(CourseUtils.getResultType(dto.getBeginTime(), dto.getLateTime(), dto.getAbsenteeismTime(), localtionDTO.getSignTime()));
                rcs.setDistance("  <" + dis + "m");
            } else {
                rcs.setType(RollCallConstants.TYPE_EXCEPTION);
                rcs.setDistance("  >" + deviation + "m");
            }
            redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), rcs.getStudentId(), rcs);
        }
        deleteRedisRollCallIng(organId, scheduleRollCallId);
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

    /**
     * 同步签到信息
     */
//    @Scheduled(fixedDelay = 5000)
//    public void synSignInfo() {
//        ConcurrentHashMap<String, Map<Long, LocaltionDTO>> map = RollCallMapUtil.getMap();
//        log.info("需要上传的签到信息数量为:" + map.size());
//        long start = System.currentTimeMillis();
//        if (map.size() > 0) {
//            for (Map.Entry<String, Map<Long, LocaltionDTO>> entry : map.entrySet()) {
//                redisTemplate.opsForHash().putAll(entry.getKey(), entry.getValue());
//            }
//            map.clear();
//            log.info("上传签到耗时:" + (System.currentTimeMillis() - start) / 1000);
//        }
//    }

}