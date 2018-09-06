package com.aizhixin.cloud.dd.monitor.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.rollcall.entity.Claim;
import com.aizhixin.cloud.dd.rollcall.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.monitor.entity.DayBreak;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.DianDianSchoolTimeDomain;
import com.aizhixin.cloud.dd.rollcall.dto.PeriodDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;

/**
 * 
 * 点点凌晨课程比对工具
 * 
 * 1.查询管理平台(org-manager)，将管理平台提供的当天课程数据作为基础数据。 2.在点点定时任务(00:20)执行后一定时间(02:00),从点点库查询当天排课信息。 3.将点点库的排课信息和基础数据进行比较(教学日期,教学班信息,课程节信息)，匹配上即为执行成功，未匹配的即为失败。
 * 4.通过比较判断点点的凌晨任务是否有漏执行，或由于某种原因执行失败的情况。
 * 
 * @author LIMH
 * @date 2018/1/15
 */
@Service
public class ContrastToolService {

    private final Logger log = LoggerFactory.getLogger(ContrastToolService.class);

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private DaybreakService daybreakService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private ClaimService claimService;

    @Scheduled(cron = "* * 2 * * ?")
    public void contrastTask() {
        log.info("开始进行凌晨排课比对任务...");
        Long start = System.currentTimeMillis();
        // 查询系统中所有学校
        List<IdNameDomain> orgAll = orgManagerRemoteService.findAllOrg();
        if (orgAll != null && !orgAll.isEmpty()) {
            for (IdNameDomain idNameDomain : orgAll) {
                try {
                    getSchoolInfo(idNameDomain.getId(), idNameDomain.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn(e.getMessage());
                }

            }
        }
        log.info("当天的凌晨排课比对任务结束,总用时:" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 获取当天需要比较的数据
     * 
     * @param orgId
     * @param name
     */
    public void getSchoolInfo(Long orgId, String name) {
        Long semesterId = semesterService.getSemesterId(orgId);
        if (null == semesterId) {
            log.info("该学校没有设置学期信息，不需要凌晨排课比对任务。学校id为：" + orgId + ",名称为：" + name);
            return;
        }

        String teachDate = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);

        List<DianDianSchoolTimeDomain> ddList = orgManagerRemoteService.findSchoolTimeDay(orgId, semesterId, teachDate);
        List<Schedule> scheduleList = scheduleService.findAllByOrganIdAndTeachDateAndDeleteFlag(orgId, teachDate);

        compareSchedule(orgId, name, ddList, scheduleList);
    }

    /**
     * 进行数据比对
     * 
     * @param ddDomains
     * @param scheduleList
     */
    public void compareSchedule(Long orgId, String name, List<DianDianSchoolTimeDomain> ddDomains, List<Schedule> scheduleList) {
        if (ddDomains == null || ddDomains.isEmpty()) {
            return;
        }
        Map<String, Schedule> scheduleMap = new HashMap<>();
        if (scheduleList != null) {
            for (Schedule schedule : scheduleList) {
                scheduleMap.put(concatkey(String.valueOf(schedule.getTeachingclassId()), schedule.getPeriodNo().toString(), schedule.getPeriodNum().toString()), schedule);
            }
        }

        List<PeriodDTO> periods = periodService.listPeriod(orgId);
        if (periods == null || periods.isEmpty()) {
            return;
        }

        String currentDate = DateFormatUtil.formatShort(new Date());

        for (DianDianSchoolTimeDomain dd : ddDomains) {
            if (!scheduleMap.containsKey(concatkey(dd.getTeachingClassId().toString(), dd.getPeriodNo().toString(), dd.getPeriodNum().toString()))) {

                PeriodDTO period = (PeriodDTO)periods.get(dd.getPeriodNo() - 1);
                if (null == period) {
                    return;
                    // throw new Exception();
                } else if (!dd.getPeriodId().equals(period.getId())) {
                    System.out.println("课程节处理异常!");
                    return;
                    // throw new Exception();
                }

                IdNameDomain teacher = parseTeacher(dd.getTeachingClassId(), currentDate, dd.getPeriodNo(), dd.getPeriodNum(), dd.getTeachers());
                if (teacher == null) {
                    return;
                }
                // 对于点点库未查询到的课程数据向 监控工具推送该条课程信息，并置标志位未初始化成功
                daybreakService.save(new DayBreak(orgId, name, null, currentDate, dd.getTeachingClassId(), dd.getTeachingClassName(), dd.getCourseId(), dd.getCourseName(),
                    teacher.getId(), teacher.getName(), dd.getPeriodNo(), dd.getPeriodNum(), period.getStartTime(), period.getEndTime(), Boolean.FALSE,
                    DataValidity.VALID.getState(), "对比工具检测该课程异常(凌晨调度未初始化)!", 0L, new Date()));
            } else {
                // 置标志位，该课程匹配成功
                List<DayBreak> dayBreakList = daybreakService.findDayBreak(dd.getTeachingClassId(), currentDate, dd.getPeriodNo(), dd.getPeriodNum());
                if (null != dayBreakList && !dayBreakList.isEmpty()) {
                    for (DayBreak dayBreak : dayBreakList) {
                        dayBreak.setCompareflag(Boolean.TRUE);
                    }
                }
                daybreakService.save(dayBreakList);
            }
        }
    }

    public IdNameDomain parseTeacher(Long teachingClassId, String currentDate, Integer periodNo, Integer periodNum, String teacherStr) {
        IdNameDomain teacher = null;
        Claim claim = claimService.findByDb(teachingClassId, currentDate, periodNo, periodNum);
        if (null != claim) {
            teacher = new IdNameDomain(claim.getTeacherId(), claim.getTeacherName());
        } else {
            teacher = InitScheduleService.parseTeacher(teacherStr);
        }
        return teacher;
    }

    /**
     * 拼接字符串
     * 
     * @param args
     * @return
     */
    public String concatkey(String... args) {
        StringBuffer sb = new StringBuffer();
        for (String arg : args) {
            sb.append(arg);
            sb.append("_");
        }
        return sb.toString();
    }
}
