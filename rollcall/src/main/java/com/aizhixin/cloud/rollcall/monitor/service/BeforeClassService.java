package com.aizhixin.cloud.rollcall.monitor.service;

import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.rollcall.entity.Schedule;
import com.aizhixin.cloud.rollcall.monitor.entity.BeforeClass;
import com.aizhixin.cloud.rollcall.monitor.repository.BeforeClassRepository;
import com.aizhixin.cloud.rollcall.monitor.utils.ApiReturn;
import com.aizhixin.cloud.rollcall.monitor.utils.StatusEnum;
import com.aizhixin.cloud.rollcall.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // public List<BeforeClass> findAllByOrgIdAndTeachDate(Long orgId, String teachDate, Boolean flag, String status) {
    // List<BeforeClass> beforeClassList = null;
    // if (null == flag && status == null) {
    // beforeClassList = beforeClassRepository.findAllByOrgIdAndTeachDateOrderByDateDesc(orgId, teachDate);
    // } else if (null != flag && status == null) {
    // beforeClassList = beforeClassRepository.findAllByOrgIdAndTeachDateAndSuccessFlagOrderByDateDesc(orgId, teachDate,
    // flag ? StatusEnum.Success.getStatus() : StatusEnum.Fail.getStatus());
    // } else if (null == flag && status != null) b{
    // beforeClassList = beforeClassRepository.findAllByOrgIdAndTeachDateAndStatusOrderByDateDesc(orgId, teachDate, status);
    // } else {
    // beforeClassList = beforeClassRepository.findAllByOrgIdAndTeachDateAndSuccessFlagAndStatusOrderByDateDesc(orgId, teachDate,
    // flag ? StatusEnum.Success.getStatus() : StatusEnum.Fail.getStatus(), status);
    // }
    // return beforeClassList;
    // }

    /**
     * 课前修复
     *
     * @param orgId
     * @param scheduleId
     * @return
     */
    // public Map<String, Object> repairBeforeClass(Long orgId, Long scheduleId) {
    // Schedule schedule = scheduleService.findOne(scheduleId);
    // if (schedule == null) {
    // return ApiReturn.message(Boolean.FALSE, "未找到排课信息:scheuleId ", null);
    // }
    // return scheduleService.beforeClassDoAnyThings(schedule);
    // }
}
