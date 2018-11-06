package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.rollcall.entity.ModifyAttendanceLog;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.repository.ModifyAttendanceLogRepository;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-10-16
 */
@Service
@Transactional
public class ModifyAttendanceLogService {
    @Autowired
    private ModifyAttendanceLogRepository modifyAttendanceLogRepository;

    @Autowired
    private RollCallRepository rollCallRepository;

    public void modifyAttendance(Long rollCallId, String type, String operator, Long operatorId) {
        RollCall rollCall = rollCallRepository.findOne(rollCallId);
        if (null != rollCall) {
            ModifyAttendanceLog modifyAttendanceLog = new ModifyAttendanceLog();
            modifyAttendanceLog.setOperatingDate(new Date());
            modifyAttendanceLog.setOperatorId(operatorId);
            modifyAttendanceLog.setOperator(operator);
            modifyAttendanceLog.setRollcallId(rollCallId);
            if (!StringUtils.isBlank(rollCall.getType())) {
                modifyAttendanceLog.setOperatingContent(operator + "将" + rollCall.getStudentName() + "签到状态由" + convert(Integer.valueOf(rollCall.getType())) + "修改为" + convert(Integer.valueOf(type)));
            }
            modifyAttendanceLogRepository.save(modifyAttendanceLog);
        }
    }

    public void modifyAttendances(List<RollCall> rollCalls, String operator, Long operatorId, String type) {
        List<ModifyAttendanceLog> logs = new ArrayList<>();
        for (RollCall rollCall : rollCalls) {
            ModifyAttendanceLog modifyAttendanceLog = new ModifyAttendanceLog();
            modifyAttendanceLog.setOperatingDate(new Date());
            modifyAttendanceLog.setOperatorId(operatorId);
            modifyAttendanceLog.setOperator(operator);
            modifyAttendanceLog.setRollcallId(rollCall.getId());
            if (!StringUtils.isBlank(rollCall.getType())) {
                String logStr = operator + "将" + rollCall.getStudentName() + "签到状态由" + convert(Integer.valueOf(rollCall.getLastType())) + "修改为" + convert(Integer.valueOf(rollCall.getType()));
                if (StringUtils.isNotEmpty(type)) {
                    logStr += "(" + type + ")";
                }
                modifyAttendanceLog.setOperatingContent(logStr);
            }
            logs.add(modifyAttendanceLog);
        }
        if (logs.size() > 0) {
            modifyAttendanceLogRepository.save(logs);
        }
    }

    public String convert(int number) {
        if (number > 0 && number < 10) {
            String[] num = {"已到", "旷课", "迟到", "请假", "早退", "已提交", "未提交", "超出距离", "取消考勤"};
            return num[number - 1];
        } else {
            return "未考勤";
        }
    }
}
