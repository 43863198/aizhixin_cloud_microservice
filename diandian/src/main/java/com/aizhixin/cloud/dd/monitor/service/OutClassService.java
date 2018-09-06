package com.aizhixin.cloud.dd.monitor.service;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.monitor.entity.BeforeClass;
import com.aizhixin.cloud.dd.monitor.entity.OutClass;
import com.aizhixin.cloud.dd.monitor.repository.OutClassRepository;
import com.aizhixin.cloud.dd.monitor.utils.StatusEnum;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author LIMH
 * @date 2017/12/11
 */
@Service
public class OutClassService {
    @Autowired
    private OutClassRepository outClassRepository;

    @Autowired
    private ScheduleService scheduleService;

    public void save(OutClass outClass) {
        outClassRepository.save(outClass);
    }

    public List<OutClass> findAllByOrgIdAndTeachDate(Long orgId, String teachDate, Boolean flag) {
        List<OutClass> outClassList = null;
        if (null == flag) {
            outClassList = outClassRepository.findAllByOrgIdAndTeachDateOrderByDateDesc(orgId, teachDate);
        } else {
            outClassList
                = outClassRepository.findAllByOrgIdAndTeachDateAndSuccessFlagOrderByDateDesc(orgId, teachDate, flag ? StatusEnum.Success.getStatus() : StatusEnum.Fail.getStatus());
        }

        return outClassList;
    }

    /**
     * 课后修复
     * 
     * @param orgId
     * @param scheduleId
     * @return
     */
    public Map<String, Object> repairOutClass(Long orgId, Long scheduleId) {
        Schedule schedule = scheduleService.findOne(scheduleId);
        if (schedule == null) {
            return ApiReturn.message(Boolean.FALSE, "未找到排课信息:scheuleId ", null);
        }
        return scheduleService.outClassDoAnything(schedule, Boolean.TRUE);
    }
}
