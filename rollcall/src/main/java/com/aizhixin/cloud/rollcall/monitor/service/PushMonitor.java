package com.aizhixin.cloud.rollcall.monitor.service;

import java.util.Date;

import com.aizhixin.cloud.rollcall.domain.ScheduleRedisDomain;
import com.aizhixin.cloud.rollcall.entity.Schedule;
import com.aizhixin.cloud.rollcall.remote.OrgManagerService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Component
public class PushMonitor {

    private final Logger LOG = LoggerFactory.getLogger(PushMonitor.class);

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private OrgManagerService orgManagerService;

    @Async
    public void clearDaybreak(Long orgId, String date) {
        LOG.info("清除学校()数据", orgId);
        monitorService.clearDayBreak(orgId, date);
    }

    public String getOrgName(Long id) {
        String orgInfo = orgManagerService.getOrgInfo(id);
        if (StringUtils.isBlank(orgInfo)) {
            return "";
        }
        JSONObject jsonObject = JSONObject.fromObject(orgInfo);
        return jsonObject.getString("name");
    }

    @Async
    public void pushMonitor(Schedule schedule, Long useTime, Boolean flag, String message) {
        LOG.info("添加数据({}),耗时:({})", schedule.getCourseName(), useTime);
        monitorService.addDaybreakSchedule(schedule.getOrganId(), getOrgName(schedule.getOrganId()), schedule.getId(), schedule.getTeachDate(), schedule.getTeachingclassId(),
            schedule.getTeachingclassName(), schedule.getCourseId(), schedule.getCourseName(), schedule.getTeacherId(), schedule.getTeacherNname(), schedule.getPeriodNo(),
            schedule.getPeriodNum(), schedule.getScheduleStartTime(), schedule.getScheduleEndTime(), flag ? 1 : 0, message, useTime, new Date());
    }

    @Async
    public void addBeforeClass(ScheduleRedisDomain schedule, Long useTime, Boolean flag, String message, String status) {
        monitorService.addBeforeClass(schedule.getOrgId(), getOrgName(schedule.getOrgId()), schedule.getScheduleId(), schedule.getTeachDate(), schedule.getTeachingclassId(),
            schedule.getTeachingclassName(), schedule.getCourseId(), schedule.getCourseName(), schedule.getTeacherId(), schedule.getTeacherName(), schedule.getPeriodNo(),
            schedule.getPeriodNum(), schedule.getScheduleStartTime(), schedule.getScheduleEndTime(), flag ? 1 : 0, message, useTime, new Date(), status);
    }

    @Async
    public void addOutClass(ScheduleRedisDomain schedule, Long useTime, Boolean flag, String message) {
        monitorService.addOutClass(schedule.getOrgId(), getOrgName(schedule.getOrgId()), schedule.getScheduleId(), schedule.getTeachDate(), schedule.getTeachingclassId(),
            schedule.getTeachingclassName(), schedule.getCourseId(), schedule.getCourseName(), schedule.getTeacherId(), schedule.getTeacherName(), schedule.getPeriodNo(),
            schedule.getPeriodNum(), schedule.getScheduleStartTime(), schedule.getScheduleEndTime(), flag ? 1 : 0, message, useTime, new Date());
    }

    @Async
    public void deleteByScheduleId(Long scheduleId) {
        monitorService.deleteByScheduleId(scheduleId);
    }
}
