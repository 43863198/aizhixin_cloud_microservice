package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.RollCallInitLog;
import com.aizhixin.cloud.dd.rollcall.entity.RollCallSignInLog;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallInitLogRepository;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallSignInLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RollCallLogService {
    @Autowired
    private RollCallInitLogRepository initLogRepository;
    @Autowired
    private RollCallSignInLogRepository signInLogRepository;

    public void saveInitLog(List<RollCall> datas) {
        try {
            if (datas != null && datas.size() > 0) {
                List<RollCallInitLog> logs = new ArrayList<>();
                for (RollCall rollCall : datas) {
                    RollCallInitLog rollCallInitLog = new RollCallInitLog();
                    BeanUtils.copyProperties(rollCall, rollCallInitLog);
                    logs.add(rollCallInitLog);
                }
                initLogRepository.save(logs);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    public void saveSignInLog(List<RollCall> datas) {
        try {
            if (datas != null && datas.size() > 0) {
                List<RollCallSignInLog> logs = new ArrayList<>();
                for (RollCall rollCall : datas) {
                    RollCallSignInLog rollCallSignInLog = new RollCallSignInLog();
                    BeanUtils.copyProperties(rollCall, rollCallSignInLog);
                    logs.add(rollCallSignInLog);
                }
                signInLogRepository.save(logs);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }
}
