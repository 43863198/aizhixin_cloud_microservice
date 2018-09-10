package com.aizhixin.cloud.dd.monitor.service;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.monitor.domain.DaybreakDomain;
import com.aizhixin.cloud.dd.monitor.entity.DayBreak;
import com.aizhixin.cloud.dd.monitor.repository.DaybreakRepository;
import com.aizhixin.cloud.dd.monitor.utils.StatusEnum;
import com.aizhixin.cloud.dd.rollcall.service.InitScheduleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Service
public class DaybreakService {

    @Autowired
    private DaybreakRepository daybreakRepository;

    @Lazy
    @Autowired
    private InitScheduleService initScheduleService;

    public void save(DayBreak dayBreak) {
        daybreakRepository.save(dayBreak);
    }

    public void save(List<DayBreak> dayBreaks) {
        daybreakRepository.save(dayBreaks);
    }

    /**
     * 删除重复数据
     * 
     * @param orgId
     * @param date
     */
    public void deleteByOrgId(Long orgId, String date) {
        daybreakRepository.deleteByOrgIdAndTeachDate(orgId, date);
    }

    /**
     * 某学校某天课程列表
     * 
     * @param orgName
     * @param teachDate
     * @return
     */
    public List<DaybreakDomain> listDayBreak(String orgName, String teachDate) {
        List<DayBreak> dayBreakList = null;
        if (StringUtils.isBlank(orgName)) {
            dayBreakList = daybreakRepository.findAllByTeachDate(teachDate);
        } else {
            dayBreakList = daybreakRepository.findAllByTeachDateAndOrgNameLike(teachDate, orgName);
        }
        List<DaybreakDomain> daybreakDomains = new ArrayList<>();
        Map<Long, DaybreakDomain> map = new HashMap(5000);
        if (dayBreakList == null || dayBreakList.isEmpty()) {
            return daybreakDomains;
        }
        DaybreakDomain daybreakDomain = null;
        for (DayBreak dayBreak : dayBreakList) {
            if (!map.containsKey(dayBreak.getOrgId())) {
                daybreakDomain = new DaybreakDomain(dayBreak.getOrgId(), dayBreak.getOrgName(), 1, dayBreak.getSuccessFlag(),
                    ((StatusEnum.Success.getStatus() == dayBreak.getSuccessFlag()) ? 0 : 1), dayBreak.getTeachDate(), dayBreak.getUseTime());
                map.put(dayBreak.getOrgId(), daybreakDomain);
                daybreakDomains.add(daybreakDomain);
                continue;
            }
            daybreakDomain = map.get(dayBreak.getOrgId());
            daybreakDomain.setScheduleSize(daybreakDomain.getSuccessSize() + daybreakDomain.getFailSize() + 1);
            daybreakDomain.setSuccessSize(daybreakDomain.getSuccessSize() + dayBreak.getSuccessFlag());
            daybreakDomain.setFailSize(daybreakDomain.getFailSize() + ((StatusEnum.Success.getStatus() == dayBreak.getSuccessFlag()) ? 0 : 1));
            daybreakDomain.setUseTime(daybreakDomain.getUseTime() + dayBreak.getUseTime());
        }

        if (daybreakDomains != null && !daybreakDomains.isEmpty()) {
            daybreakDomains.sort(new Comparator<DaybreakDomain>() {
                @Override
                public int compare(DaybreakDomain o1, DaybreakDomain o2) {
                    return o1.getOrgId().compareTo(o2.getOrgId());
                }
            });
        }
        return daybreakDomains;
    }

    /**
     * 某学校某天课程列表
     * 
     * @param orgId
     * @param successFlag
     * @param teachDate
     * @return
     */
    public List<DayBreak> list(Long orgId, Boolean successFlag, String teachDate) {
        List<DayBreak> list = null;
        if (successFlag == null) {
            list = daybreakRepository.findAllByOrgIdAndTeachDate(orgId, teachDate);
        } else {
            list = daybreakRepository.findAllByOrgIdAndTeachDateAndSuccessFlag(orgId, teachDate, successFlag ? StatusEnum.Success.getStatus() : StatusEnum.Fail.getStatus());
        }
        return list;
    }

    /**
     * 修复
     *
     * @return
     */
    public Map<String, Object> repairDayBreak(Long orgId, String orgName) {
        try {
            initScheduleService.executeTask(orgId, orgName);
        } catch (Exception e) {
            return ApiReturn.message(Boolean.FALSE, e.getMessage(), null);
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    public void deleteByScheduleId(Long scheduleId) {
        daybreakRepository.deleteByScheduleId(scheduleId);
    }

    List<DayBreak> findDayBreak(Long teachingclassId, String teachDate, Integer periodNo, Integer periodNum) {
        return daybreakRepository.findAllByTeachingclassIdAndTeachDateAndPeriodNoAndPerioidNum(teachingclassId, teachDate, periodNo, periodNum);
    }

}
