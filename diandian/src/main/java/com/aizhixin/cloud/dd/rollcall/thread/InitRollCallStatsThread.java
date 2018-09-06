package com.aizhixin.cloud.dd.rollcall.thread;

import com.aizhixin.cloud.dd.rollcall.service.RollCallStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitRollCallStatsThread extends Thread {
    private final Logger log = LoggerFactory.getLogger(InitRollCallStatsThread.class);
    private RollCallStatsService rollCallStatsService;
    private Long orgId;


    public InitRollCallStatsThread(RollCallStatsService rollCallStatsService, Long orgId) {
        log.info("初始化统计考勤线程");
        this.rollCallStatsService = rollCallStatsService;
        this.orgId = orgId;
    }

    @Override
    public void run() {
        log.info("开始统计考勤:" + orgId);
        rollCallStatsService.initStatsDataByOrg(orgId);
        log.info("完成统计考勤:" + orgId);
    }
}
