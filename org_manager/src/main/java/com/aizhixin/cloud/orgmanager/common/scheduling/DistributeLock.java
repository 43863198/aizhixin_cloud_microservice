package com.aizhixin.cloud.orgmanager.common.scheduling;

import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具
 * 部署多个节点的时候，对调度任务限制只能在其中一个节点启动
 */
@Component
public class DistributeLock {
    private final static  Logger LOG = LoggerFactory.getLogger(DistributeLock.class);
    private final static  int ZOOKEEPER_RETRY_TIMES = 3;            //zookeeper连接重试最大次数
    private final static  int ZOOKEEPER_RETRY_SLEEP_TIMES = 3000;   //zookeeper连接重试的间隔时间
    private final static  int TASK_LOCKED_TIME_SECOND = 30;         //任务锁定的最大时间(秒)
    @Value("${zookeeper.connecton}")
    private String zkConnectString;
    @Value("${zookeeper.path}")
    private String zkLockPath;
    @Value("${zookeeper.task}")
    private String zkTaskPath;

    /**
     * 获取分布式锁的核心逻辑
     * @param lockPath      锁路径
     * @param taskPath      任务路径
     * @return              获取锁是否成功
     */
    private boolean getLock(String lockPath, String taskPath) {
        boolean locked = false;
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        InterProcessMutex lock = null;
        try {
            client.start();
            lock = new InterProcessMutex(client, lockPath);
            if (lock.acquire(TASK_LOCKED_TIME_SECOND, TimeUnit.SECONDS)) {
                Stat stat = client.checkExists().forPath(taskPath);
                if (null == stat) {
                    client.create().creatingParentContainersIfNeeded().forPath(taskPath);
                    locked = true;
                }
            }
        } catch (Exception e) {
            LOG.warn("获取锁失败", e);
            e.printStackTrace();
        } finally {
            if (null != lock) {
                try {
                    lock.release();
                } catch (Exception e) {
                    LOG.warn("释放锁失败", e);
                    e.printStackTrace();
                }
            }
            CloseableUtils.closeQuietly(client);
        }
        return locked;
    }

    public void delete() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(zkLockPath);
            client.delete().deletingChildrenIfNeeded().forPath(zkTaskPath);
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", zkLockPath, zkTaskPath, e);
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    /**
     * 初始化天任务数据锁的获取
     * @return  是否获取到锁
     */
    public boolean getDayInitLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/init");
        taskPath.append("/").append(curDayString).append("/init");

        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void cleanZookeeperTaskData() {
        delete();
    }
}
