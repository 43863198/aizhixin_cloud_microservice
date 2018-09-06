package com.aizhixin.cloud.rollcall.monitor.service;

import com.aizhixin.cloud.rollcall.monitor.entity.OutClass;
import com.aizhixin.cloud.rollcall.monitor.repository.OutClassRepository;
import com.aizhixin.cloud.rollcall.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 课后修复
     *
     * @param orgId
     * @param scheduleId
     * @return
     */
//    public Map<String, Object> repairOutClass(Long orgId, Long scheduleId) {
//        Schedule schedule = scheduleService.findOne(scheduleId);
//        if (schedule == null) {
//            return ApiReturn.message(Boolean.FALSE, "未找到排课信息:scheuleId ", null);
//        }
//        return scheduleService.outClassDoAnything(schedule, Boolean.TRUE);
//    }
}
