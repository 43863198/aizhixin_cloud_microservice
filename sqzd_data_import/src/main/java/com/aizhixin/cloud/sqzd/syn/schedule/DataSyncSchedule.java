package com.aizhixin.cloud.sqzd.syn.schedule;

import com.aizhixin.cloud.sqzd.syn.service.SynAllDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataSyncSchedule {
    @Autowired
    private SynAllDataService synAllDataService;


    @Scheduled(cron = "0 0 22 * * ?")
    public void execScheduleTask() {
        log.info("--------定时同步开始--------");
        synAllDataService.syn();
        log.info("--------定时同步结束--------");
    }

}
