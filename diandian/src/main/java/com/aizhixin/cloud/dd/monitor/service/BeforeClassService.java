package com.aizhixin.cloud.dd.monitor.service;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.monitor.entity.BeforeClass;
import com.aizhixin.cloud.dd.monitor.repository.BeforeClassRepository;
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
public class BeforeClassService {
    @Autowired
    private BeforeClassRepository beforeClassRepository;

    @Autowired
    private ScheduleService scheduleService;

    public void save(BeforeClass beforeClass) {
        beforeClassRepository.save(beforeClass);
    }

    public List<BeforeClass> findAllByOrgIdAndTeachDate(Long orgId, String teachDate, Boolean flag, String status) {
        List<BeforeClass> beforeClassList = null;
        if (null == flag && status == null) {
            beforeClassList = beforeClassRepository.findAllByOrgIdAndTeachDateOrderByDateDesc(orgId, teachDate);
        } else if (null != flag && status == null) {
            beforeClassList = beforeClassRepository.findAllByOrgIdAndTeachDateAndSuccessFlagOrderByDateDesc(orgId, teachDate,
                flag ? StatusEnum.Success.getStatus() : StatusEnum.Fail.getStatus());
        } else if (null == flag && status != null) {
            beforeClassList = beforeClassRepository.findAllByOrgIdAndTeachDateAndStatusOrderByDateDesc(orgId, teachDate, status);
        } else {
            beforeClassList = beforeClassRepository.findAllByOrgIdAndTeachDateAndSuccessFlagAndStatusOrderByDateDesc(orgId, teachDate,
                flag ? StatusEnum.Success.getStatus() : StatusEnum.Fail.getStatus(), status);
        }
        return beforeClassList;
    }

    /**
     * 课前修复
     *
     * @param orgId
     * @param scheduleId
     * @return
     */
    public Map<String, Object> repairBeforeClass(Long orgId, Long scheduleId) {
        Schedule schedule = scheduleService.findOne(scheduleId);
        if (schedule == null) {
            return ApiReturn.message(Boolean.FALSE, "未找到排课信息:scheuleId ", null);
        }
        return scheduleService.beforeClassDoAnyThings(schedule);
    }
}
