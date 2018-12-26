package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleQuery;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ClassScheduleService {
    @Autowired
    private ScheduleQuery scheduleQuery;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ClassOutService classOutService;

    /**
     * 检查下课
     */
    public void checkClassOut() {
        try {
            long endTime = System.currentTimeMillis();
            String end = DateFormatUtil.format(new Date(endTime), DateFormatUtil.FORMAT_LONG);
            String teachDay = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);
            List<Map<String, Object>> list = scheduleQuery.queryUnOuntSchedule(end, teachDay);
            if (list != null && list.size() > 0) {
                log.info("{} 检查下课: {}: {}", end, end, list);
                for (Map<String, Object> map : list) {
                    if (map.get("schedulerollcallid") != null && map.get("scheduleid") != null) {
                        Long scheduleRollCallId = Long.parseLong(map.get("schedulerollcallid").toString());
                        Long scheduleId = Long.parseLong(map.get("scheduleid").toString());
                        log.info("执行下课: scheduleId {} scheduleRollCallId {}", scheduleId, scheduleRollCallId);
                        Schedule schedule = scheduleRepository.findOne(scheduleId);
                        if (schedule != null) {
                            try {
                                classOutService.outClassDoAnything(schedule.getId());
                            } catch (Exception e) {
                                log.warn("classOutException", e);
                            }
                        }
                    } else {
                        log.info("检查下课: 数据错误 {} ", map);
                    }
                }
            } else {
                log.info("{} 检查下课: 无未下课数据", end);
            }
        } catch (Exception e) {
            log.warn("checkClassOutException", e);
        }
    }
}
