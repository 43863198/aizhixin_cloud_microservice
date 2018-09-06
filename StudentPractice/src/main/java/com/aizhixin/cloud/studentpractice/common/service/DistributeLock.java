package com.aizhixin.cloud.studentpractice.common.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.studentpractice.common.util.DateUtil;

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
            client.close();
        }
        return locked;
    }

    public void delete() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(zkLockPath);
            client.delete().deletingChildrenIfNeeded().forPath(zkTaskPath);
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", zkLockPath, zkTaskPath, e);
            e.printStackTrace();
        }
    }


    /**
     * 课后任务锁的获取
     * @return  是否获取到锁
     */
    public boolean getTaskStatisticsLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/tstat/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/tstat/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }

    /**
     * 获取中值计算任务的分布式锁
     * @return  是否获取到锁
     */
    public boolean getCountPeopleNumLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/cpnum/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/cpnum/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean getMentorTaskNumLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/mtc/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/mtc/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    
    public boolean getCountSummaryNumLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/csn/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/csn/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean getEnterpriseCountLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/ec/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/ec/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean getSummaryReplyLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/sp/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/sp/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    
    public boolean getPracticeJoinLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/pj/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/pj/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean getCountJoinLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/cj/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/cj/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean getCountSummaryByClassLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/csc/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/csc/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    
    public boolean getCountReportByClassLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/rc/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/rc/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean getCountReportByStuLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/rt/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/rt/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }

    public boolean getSynSignLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/ss/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/ss/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean getScoreCountLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/sc/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/sc/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean getCounselorCountLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/cc/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/cc/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean openCounselorCallLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/occ/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/occ/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public boolean closeCounselorCallLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/ccc/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/ccc/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }
    
    public void cleanZookeeperTaskData() {
        delete();
    }
}
