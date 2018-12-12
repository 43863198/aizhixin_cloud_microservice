package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.RollCallLogJdbc;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.RollCallInitLog;
import com.aizhixin.cloud.dd.rollcall.entity.RollCallLog;
import com.aizhixin.cloud.dd.rollcall.entity.RollCallSignInLog;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallInitLogRepository;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallSignInLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RollCallLogService {
    @Autowired
    private RollCallInitLogRepository initLogRepository;
    @Autowired
    private RollCallSignInLogRepository signInLogRepository;
    @Autowired
    private RollCallLogJdbc rollCallLogJdbc;
    @Autowired
    private RollCallRepository rollCallRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    public void saveInitLog(Long scheduleId, Long scheduleRollCallId, List<RollCall> datas) {
        try {
            if (datas != null && datas.size() > 0) {
                RollCallInitLog initLog = new RollCallInitLog();
                initLog.setScheduleId(scheduleId);
                initLog.setScheduleRollCallId(scheduleRollCallId);
                List<RollCallLog> logs = new ArrayList<>();
                for (RollCall rollCall : datas) {
                    RollCallLog rollCallLog = new RollCallLog();
                    BeanUtils.copyProperties(rollCall, rollCallLog);
                    logs.add(rollCallLog);
                }
                initLog.setRollCallList(logs);
                initLogRepository.save(initLog);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    public void saveSignInLog(Long scheduleId, Long scheduleRollCallId, List<RollCall> datas) {
        try {
            if (datas != null && datas.size() > 0) {
                RollCallSignInLog signInLog = new RollCallSignInLog();
                signInLog.setScheduleId(scheduleId);
                signInLog.setScheduleRollCallId(scheduleRollCallId);
                List<RollCallLog> logs = new ArrayList<>();
                for (RollCall rollCall : datas) {
                    RollCallLog rollCallLog = new RollCallLog();
                    BeanUtils.copyProperties(rollCall, rollCallLog);
                    logs.add(rollCallLog);
                }
                signInLog.setRollCallList(logs);
                signInLogRepository.save(signInLog);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    public void checkRollCall() {
        try {
            String logStr = "";
            String date = DateFormatUtil.formatShort(new Date());
            log.info("MongoDB考勤数据检查开始: {}", date);
            List<Map<String, Object>> rollCallCountList = rollCallLogJdbc.getRollCallCount(date);
            if (rollCallCountList != null && rollCallCountList.size() > 0) {
                for (Map<String, Object> rollCallCountMap : rollCallCountList) {
                    if (rollCallCountMap.get("SCHEDULE_ROLLCALL_ID") != null && rollCallCountMap.get("stucount") != null) {
                        int stuCount = Integer.parseInt(rollCallCountMap.get("stucount").toString());
                        if (stuCount > 0) {
                            Long scheduleRollCallId = Long.parseLong(rollCallCountMap.get("SCHEDULE_ROLLCALL_ID").toString());
                            List<RollCallSignInLog> signInLogs = signInLogRepository.findByScheduleRollCallId(scheduleRollCallId);
                            if (signInLogs != null && signInLogs.size() > 0) {
                                RollCallSignInLog signInLog = signInLogs.get(0);
                                if (signInLog != null && signInLog.getRollCallList() != null && signInLog.getRollCallList().size() > stuCount) {
                                    int mc = signInLog.getRollCallList().size() - stuCount;
                                    log.info("{} MongoDB中考勤记录比MySql中多: {}", scheduleRollCallId, mc);
                                    if (!StringUtils.isEmpty(logStr)) {
                                        logStr += ";\n";
                                    }
                                    logStr += scheduleRollCallId + "MongoDB中考勤记录比MySql中多:" + mc;
                                    List<RollCall> rollCallList = rollCallRepository.findByScheduleRollcallId(scheduleRollCallId);
                                    if (rollCallList != null && rollCallList.size() > 0) {
                                        Map<Long, RollCall> rollCallMap = new HashMap<>();
                                        for (RollCall rollCall : rollCallList) {
                                            rollCallMap.put(rollCall.getStudentId(), rollCall);
                                        }
                                        List<RollCall> addList = new ArrayList<>();
                                        for (RollCallLog rollCallLog : signInLog.getRollCallList()) {
                                            if (rollCallMap.get(rollCallLog.getStudentId()) == null) {
                                                RollCall rollCall = new RollCall();
                                                BeanUtils.copyProperties(rollCallLog, rollCall);
                                                addList.add(rollCall);
                                            }
                                        }
                                        if (addList.size() > 0) {
                                            rollCallRepository.save(addList);
                                            log.info("MySql补录考勤记录: {}", addList);
                                        }
                                    } else {
                                        log.info("MySql中没有考勤记录: {}", scheduleRollCallId);
                                    }
                                }
                            } else {
                                log.info("MongoDB中没有考勤记录: {}", scheduleRollCallId);
                            }
                        } else {
                            log.info("MySql中没有考勤记录: {}", rollCallCountMap);
                        }
                    }
                }
                redisTemplate.opsForValue().set("CheckRollCall:" + date, logStr, 1, TimeUnit.DAYS);
            } else {
                log.info("MySql中没有考勤记录: {}", date);
            }
            log.info("MongoDB考勤数据检查结束: {}", date);
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    public Map<String, Object> getCheckRollCallResult() {
        String date = DateFormatUtil.formatShort(new Date());
        String logStr = (String) redisTemplate.opsForValue().get("CheckRollCall:" + date);
        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("log", logStr);
        return result;
    }
}
