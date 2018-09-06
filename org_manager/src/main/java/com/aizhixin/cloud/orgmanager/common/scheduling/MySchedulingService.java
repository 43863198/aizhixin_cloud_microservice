package com.aizhixin.cloud.orgmanager.common.scheduling;

import com.aizhixin.cloud.orgmanager.common.loging.AspectLog;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.service.BaseDataCacheService;

import com.aizhixin.cloud.orgmanager.electrict.service.ElectricFenceBaseService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class MySchedulingService {

    final static private Logger LOG = LoggerFactory.getLogger(AspectLog.class);
    @Value("${zookeeper.connecton}")
    private String zkConnectString;
    @Value("${zookeeper.path}")
    private String zkLockPath;
    @Value("${zookeeper.task}")
    private String zkTaskPath;

    @Autowired
    private DistributeLock distributeLock;
    @Autowired
    private BaseDataCacheService baseDataCacheService;
    @Autowired
    private ElectricFenceBaseService electricFenceBaseService;

    /**
     * 缓存同步
     */
    @Scheduled(cron="0 50 4 * * ?")
    public void synBaseDataTask () {
        if (distributeLock.getDayInitLock()) {
            baseDataCacheService.initAllBaseData(null);
        }
    }

    /**
     * 每天清除当天的zk数据目录
     */
    @Scheduled(cron="0 50 23 * * ?")
    public void delete () {
        distributeLock.delete();
    }

    /**
     * 电子围栏删除7天前的数据
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void electronicFenceDeleteData() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(3, 3000));
        InterProcessMutex lock = null;
        try {
            StringBuilder lockPath = new StringBuilder(zkLockPath);
            StringBuilder taskPath = new StringBuilder(zkTaskPath);
            Date current = new Date();
            String curDayString = DateUtil.format(current);
            lockPath.append("/").append(curDayString).append("/electronicFence").append("/delete");
            taskPath.append("/").append(curDayString).append("/electronicFence").append("/delete");
            client.start();
            lock = new InterProcessMutex(client, lockPath.toString());
            if (lock.acquire(30, TimeUnit.SECONDS)) {
                Stat stat = client.checkExists().forPath(taskPath.toString());
                if (null == stat) {
                    client.create().creatingParentContainersIfNeeded().forPath(taskPath.toString());
                    electricFenceBaseService.deleteData();
                }
            }
        } catch (Exception e) {
            LOG.warn("", e);
            e.printStackTrace();
        } finally {
            if (null != lock) {
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            client.close();
            CloseableUtils.closeQuietly(client);
        }
    }

    /**
     * 电子围栏每个半个小时统计一次
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void electronicFenceTask() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(3, 3000));
        InterProcessMutex lock = null;
        try {
            StringBuilder lockPath = new StringBuilder(zkLockPath);
            StringBuilder taskPath = new StringBuilder(zkTaskPath);
            Date current = new Date();
            String curDayString = DateUtil.format(current);
            String HHmm = DateUtil.format(current, "HHmm");
            lockPath.append("/").append(curDayString).append("/electronicFence").append("/Statistics").append(HHmm);
            taskPath.append("/").append(curDayString).append("/electronicFence").append("/Statistics").append(HHmm);;
            client.start();
            lock = new InterProcessMutex(client, lockPath.toString());
            if (lock.acquire(30, TimeUnit.SECONDS)) {
                Stat stat = client.checkExists().forPath(taskPath.toString());
                if (null == stat) {
                    client.create().creatingParentContainersIfNeeded().forPath(taskPath.toString());
                    electricFenceBaseService.timingStatistics();
                }
            }
        } catch (Exception e) {
            LOG.warn("调用存储过程异常！", e);
            e.printStackTrace();
        } finally {
            if (null != lock) {
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            client.close();
            CloseableUtils.closeQuietly(client);

        }
    }

}
