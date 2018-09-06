package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.domain.IdNameDomain;
import com.aizhixin.cloud.rollcall.domain.DianDianSchoolTimeDomain;
import com.aizhixin.cloud.rollcall.domain.PeriodDomain;
import com.aizhixin.cloud.rollcall.entity.Schedule;
import com.aizhixin.cloud.rollcall.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> save(List<Schedule> schedules) {
        return scheduleRepository.save(schedules);
    }

    public List<Long> getDayOrgIds(String teachDate) {
        return scheduleRepository.findOrgIdByTeachDate(teachDate);
    }

    public Schedule createSchedule(DianDianSchoolTimeDomain ddDomain, Long orgId, String currentDate, Map<Integer, PeriodDomain> periodDomainMap) {
        Schedule schedule = new Schedule();
        schedule.setOrganId(orgId);
        schedule.setCourseId(ddDomain.getCourseId());
        schedule.setCourseName(ddDomain.getCourseName());

        IdNameDomain teacher = TeachingclassService.parseTeacher(ddDomain.getTeachers());
        schedule.setTeacherId(teacher.getId());

        schedule.setTeacherNname(teacher.getName());
        schedule.setSemesterId(ddDomain.getSemesterId());
        schedule.setSemesterName(ddDomain.getSemesterName());
        schedule.setWeekId(ddDomain.getWeekId());
        schedule.setWeekName(ddDomain.getWeekNo() + "");
        schedule.setDayOfWeek(ddDomain.getDayOfWeek());
        schedule.setPeriodId(ddDomain.getPeriodId());
        schedule.setPeriodNo(ddDomain.getPeriodNo());
        schedule.setPeriodNum(ddDomain.getPeriodNum());

        // 计算课程开始时间
        PeriodDomain periodStart = periodDomainMap.get(ddDomain.getPeriodNo());
        if (null != periodStart) {
            schedule.setScheduleStartTime(periodStart.getStartTime());
        }
        PeriodDomain periodEnd = periodDomainMap.get(ddDomain.getPeriodNo() + ddDomain.getPeriodNum() - 1);
        if (null != periodEnd) {
            schedule.setScheduleEndTime(periodEnd.getEndTime());
        }

        schedule.setClassRoomName(ddDomain.getClassroom());
        schedule.setTeachDate(currentDate);
        schedule.setTeachingclassId(ddDomain.getTeachingClassId());
        schedule.setTeachingclassCode(ddDomain.getTeachingClassCode());
        schedule.setTeachingclassName(ddDomain.getTeachingClassName());
        schedule.setIsInitRollcall(Boolean.FALSE);

        return schedule;
    }
}
