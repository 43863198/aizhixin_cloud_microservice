package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.monitor.service.PushMonitor;
import com.aizhixin.cloud.dd.rollcall.dto.StudentDTO;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ClassOutService {

    private RollCallStatsService rollCallStatsService;
    @Autowired
    private RollCallLogService rollCallLogService;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Autowired
    private PushMonitor pushMonitor;
    @Autowired
    private StudentService studentService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RollCallRepository rollCallRepository;


    @Async
    @Transactional
    public Map<String, Object> outClassDoAnything(Schedule schedule) {
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
                log.warn("scheduleRollCall:{}", scheduleRollCall);
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
                    log.info("处理的后学生数据：{}", rollCallList);
                    rollCallRepository.save(rollCallList);
                    rollCallLogService.saveSignInLog(schedule.getId(), scheduleRollCall.getId(), rollCallList);
                } else {
                    scheduleRollCall.setIsOpenRollcall(Boolean.FALSE);
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

            redisTemplate.expire(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId.longValue()), 5, TimeUnit.SECONDS);
            redisTemplate.expire(RedisUtil.getScheduleRollCallDateKey(scheduleRollCallId.longValue()), 5, TimeUnit.SECONDS);

            redisTemplate.delete(RedisUtil.getScheduleRollCallIngKey(scheduleRollCallId.longValue()));
            redisTemplate.delete(RedisUtil.getScheduleRollCallDateKey(scheduleRollCallId.longValue()));

        } catch (Exception e) {
            log.warn("清除reids数据异常。" + e, e);
        }
    }

    private List<RollCall> listRollCallBySRCIdInRedis(Long scheduleRollCallId) {
        return redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }

    private void clearRedisInfo(Long scheduleRollCallId) {
        redisTemplate.expire(RedisUtil.getScheduleRollCallKey(scheduleRollCallId), 5, TimeUnit.SECONDS);
        redisTemplate.delete(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }
}
