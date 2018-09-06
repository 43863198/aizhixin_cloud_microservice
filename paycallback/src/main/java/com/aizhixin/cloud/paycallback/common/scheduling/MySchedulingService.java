package com.aizhixin.cloud.paycallback.common.scheduling;

import com.aizhixin.cloud.paycallback.service.PaymentSubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MySchedulingService {

    final static private Logger LOG = LoggerFactory.getLogger(MySchedulingService.class);


    @Autowired
    private DistributeLock distributeLock;
    @Autowired
    private PaymentSubjectService paymentSubjectService;

    /**
     * 过期缴费科目处理
     */
    @Scheduled(cron="0 10 0 * * ?")
    public void synBaseDataTask () {
        if (distributeLock.getDayInitLock()) {
            paymentSubjectService.checkLastDateTask();
        }
    }


    /**
     * 清除任务列表
     */
    @Scheduled(cron="0 2 0 * * ?")
    public void deleteZookeeperDir () {
        distributeLock.cleanZookeeperTaskData();
    }
}
