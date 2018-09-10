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

    /**
     * 保存考勤日志
     *
     * @param modifyAttendanceLog
     * @return
     */
    public Map<String, Object> saveLog(ModifyAttendanceLog modifyAttendanceLog) {
        Map<String, Object> result = new HashMap<>();
        ModifyAttendanceLog data = new ModifyAttendanceLog();
        if (null != modifyAttendanceLog.getRollcallId()) {
            data.setRollcallId(modifyAttendanceLog.getRollcallId());
        }
        if (null != modifyAttendanceLog.getOperator()) {
            data.setOperator(modifyAttendanceLog.getOperator());
        }
        if (null != modifyAttendanceLog.getOperatorId()) {
            data.setOperatorId(modifyAttendanceLog.getOperatorId());
        }
        if (null != modifyAttendanceLog.getOperatingContent()) {
            data.setOperatingContent(modifyAttendanceLog.getOperatingContent());
        }
        if (null != modifyAttendanceLog.getOperatingDate()) {
            data.setOperatingDate(modifyAttendanceLog.getOperatingDate());
        }
        try {
            modifyAttendanceLogRepository.save(data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "修改考勤操作日志保存失败！");
            return result;
        }
        result.put("success", true);
        result.put("message", "修改考勤操作日志保存成功！");
        return result;
    }

    public List<ModifyAttendanceLog> getModifyAttendanceLogByRollcallId(Long rollcallId) {
        return modifyAttendanceLogRepository.findAllByRollcallIdOrderByOperatingDate(rollcallId);
    }

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


    public String convert(int number) {
        if (0 < number && number < 6) {
            String[] num = {"已到", "旷课", "迟到", "请假", "早退"};
            return num[number - 1];
        } else {
            return "";
        }
    }


}
