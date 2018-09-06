package com.aizhixin.cloud.rollcall.monitor.service;

import java.util.Date;

import com.aizhixin.cloud.rollcall.monitor.entity.BeforeClass;
import com.aizhixin.cloud.rollcall.monitor.entity.OutClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.rollcall.monitor.entity.DayBreak;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Service
public class MonitorService {

    @Autowired
    private DaybreakService daybreakService;

    @Autowired
    private BeforeClassService beforeClassService;

    @Autowired
    private OutClassService outClassService;

    public void clearDayBreak(Long orgId, String date) {
        daybreakService.deleteByOrgId(orgId, date);
    }

    public void addDaybreakSchedule(Long orgId, String orgName, Long scheduleId, String teachDate, Long teachingclassId, String teachingclassName, Long courseId, String courseName,
        Long teacherId, String teacherName, Integer periodNo, Integer perioidNum, String startTime, String endTime, Integer successFlag, String messagex, Long useTime, Date date) {
        daybreakService.save(new DayBreak(orgId, orgName, scheduleId, teachDate, teachingclassId, teachingclassName, courseId, courseName, teacherId, teacherName, periodNo,
            perioidNum, startTime, endTime, successFlag, messagex, useTime, date));
    }

    public void addBeforeClass(Long orgId, String orgName, Long scheduleId, String teachDate, Long teachingclassId, String teachingclassName, Long courseId, String courseName,
        Long teacherId, String teacherName, Integer periodNo, Integer perioidNum, String startTime, String endTime, Integer successFlag, String messagex, Long useTime, Date date,
        String status) {
        beforeClassService.save(new BeforeClass(successFlag, messagex, useTime, date, orgId, orgName, scheduleId, teachDate, teachingclassId, teachingclassName, courseId,
            courseName, teacherId, teacherName, periodNo, perioidNum, startTime, endTime, status));
    }

    public void addOutClass(Long orgId, String orgName, Long scheduleId, String teachDate, Long teachingclassId, String teachingclassName, Long courseId, String courseName,
        Long teacherId, String teacherName, Integer periodNo, Integer perioidNum, String startTime, String endTime, Integer successFlag, String messagex, Long useTime, Date date) {
        outClassService.save(new OutClass(successFlag, messagex, useTime, date, orgId, orgName, scheduleId, teachDate, teachingclassId, teachingclassName, courseId, courseName,
            teacherId, teacherName, periodNo, perioidNum, startTime, endTime));
    }

    public void deleteByScheduleId(Long scheduleId) {
        daybreakService.deleteByScheduleId(scheduleId);
    }
}
