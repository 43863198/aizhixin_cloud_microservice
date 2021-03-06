package com.aizhixin.cloud.dd.counsellorollcall.thread;

import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.counsellorollcall.domain.UpdateRollcallReportDomain;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 暂无用
 */
@Component
public class UpdateRollcallCacheThread extends Thread {
    private static LinkedBlockingQueue<UpdateRollcallReportDomain> concurrentLinkedQueue = new LinkedBlockingQueue<>();
    private Logger log = LoggerFactory.getLogger(UpdateRollcallCacheThread.class);
    @Autowired
    private RedisTokenStore redisTokenStore;

    public static void add(UpdateRollcallReportDomain data) {
        try {
            concurrentLinkedQueue.add(data);
        } catch (Exception e) {
//            log.warn("Exception", e);
        }
    }

    @Override
    public void run() {
        for (; ; ) {
            if (concurrentLinkedQueue.size() > 0) {
                try {
                    int size = concurrentLinkedQueue.size();
                    log.info("更新点名缓存队列有数据:" + size);
                    for (int i = 0; i < size; i++) {
                        UpdateRollcallReportDomain data = concurrentLinkedQueue.poll();
                        if (data != null) {
                            redisTokenStore.storeRedisData(data.getStuId(), data.getList());
                        }
                    }
                    log.info("更新点名缓存完成:" + size);
                } catch (Exception e) {
                    log.info("UpdateRollcallCacheThreadException", e);
                }
            } else {
                log.info("更新点名缓存队列无数据");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.warn("Exception", e);
                }
            }
        }
    }
}
