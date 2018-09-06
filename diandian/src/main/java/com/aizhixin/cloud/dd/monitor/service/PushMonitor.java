package com.aizhixin.cloud.dd.monitor.service;

import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

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
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Async
    public void clearDaybreak(Long orgId, String date) {
        LOG.info("清除学校()数据", orgId);
        monitorService.clearDayBreak(orgId, date);
    }

    public String getOrgName(Long id) {
        String orgInfo = orgManagerRemoteService.getOrgInfo(id);
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
    public void addBeforeClass(Schedule schedule, Long useTime, Boolean flag, String message, String status) {
        monitorService.addBeforeClass(schedule.getOrganId(), getOrgName(schedule.getOrganId()), schedule.getId(), schedule.getTeachDate(), schedule.getTeachingclassId(),
            schedule.getTeachingclassName(), schedule.getCourseId(), schedule.getCourseName(), schedule.getTeacherId(), schedule.getTeacherNname(), schedule.getPeriodNo(),
            schedule.getPeriodNum(), schedule.getScheduleStartTime(), schedule.getScheduleEndTime(), flag ? 1 : 0, message, useTime, new Date(), status);
    }

    @Async
    public void addOutClass(Schedule schedule, Long useTime, Boolean flag, String message) {
        monitorService.addOutClass(schedule.getOrganId(), getOrgName(schedule.getOrganId()), schedule.getId(), schedule.getTeachDate(), schedule.getTeachingclassId(),
            schedule.getTeachingclassName(), schedule.getCourseId(), schedule.getCourseName(), schedule.getTeacherId(), schedule.getTeacherNname(), schedule.getPeriodNo(),
            schedule.getPeriodNum(), schedule.getScheduleStartTime(), schedule.getScheduleEndTime(), flag ? 1 : 0, message, useTime, new Date());
    }

    @Async
    public void deleteByScheduleId(Long scheduleId) {
        monitorService.deleteByScheduleId(scheduleId);
    }
}
