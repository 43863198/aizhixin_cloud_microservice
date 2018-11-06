package com.aizhixin.cloud.dd.common.services;

import com.aizhixin.cloud.dd.common.utils.DateUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具 部署多个节点的时候，对调度任务限制只能在其中一个节点启动
 */
@Component
public class DistributeLock {
    private final static Logger LOG = LoggerFactory.getLogger(DistributeLock.class);
    private final static int ZOOKEEPER_RETRY_TIMES = 3; // zookeeper连接重试最大次数
    private final static int ZOOKEEPER_RETRY_SLEEP_TIMES = 3000; // zookeeper连接重试的间隔时间
    private final static int TASK_LOCKED_TIME_SECOND = 30; // 任务锁定的最大时间(秒)
    @Value("${zookeeper.connecton}")
    private String zkConnectString;
    @Value("${zookeeper.path}")
    private String zkLockPath;
    @Value("${zookeeper.task}")
    private String zkTaskPath;

    /**
     * 获取分布式锁的核心逻辑
     *
     * @param lockPath 锁路径
     * @param taskPath 任务路径
     * @return 获取锁是否成功
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
                    client.close();
                } catch (Exception e) {
                    LOG.warn("释放锁失败", e);
                    e.printStackTrace();
                }
            }
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
     * 初始化天任务数据锁的获取
     *
     * @return 是否获取到锁
     */
    public boolean getDayInitLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/dayInit");
        taskPath.append("/").append(curDayString).append("/dayInit");

        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delDayInitLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/dayInit");
        taskPath.append("/").append(curDayString).append("/dayInit");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }


    /**
     * 课前课后任务锁的获取
     *
     * @return 是否获取到锁
     */
    public boolean getClassOutAndInLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/inAndOut/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/inAndOut/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delClassOutAndInLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/inAndOut");
        taskPath.append("/").append(curDayString).append("/inAndOut");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * 关闭辅导员点名的获取
     *
     * @return 是否获取到锁
     */
    public boolean getCloseRollCallEverLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/closeRollcall");
        taskPath.append("/").append(curDayString).append("/closeRollcall");
        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delCloseRollCallEverLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/closeRollcall");
        taskPath.append("/").append(curDayString).append("/closeRollcall");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * 获取中值计算任务的分布式锁
     *
     * @return 是否获取到锁
     */
    public boolean getCountMedianLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/countMedian/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/countMedian/").append(HHmm);

        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delCountMedianLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/countMedian");
        taskPath.append("/").append(curDayString).append("/countMedian");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * 初始化当天学生和教学班对应关系到redis
     *
     * @return 是否获取到锁
     */
    public boolean getStartInitTodayTeachClassIdsLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/initTeachClass");
        taskPath.append("/").append(curDayString).append("/initTeachClass");

        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delStartInitTodayTeachClassIdsLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/initTeachClass");
        taskPath.append("/").append(curDayString).append("/initTeachClass");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * 每天刷新组织架构数据
     *
     * @return 是否获取到锁
     */
    public boolean getRefOrgDataLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/orgData/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/orgData/").append(HHmm);
        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delRefOrgDataLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/orgData");
        taskPath.append("/").append(curDayString).append("/orgData");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * 每天考勤统计
     *
     * @return 是否获取到锁
     */
    public boolean getRefRollCallLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/rollCallStats/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/rollCallStats/").append(HHmm);
        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delRefRollCallLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/rollCallStats");
        taskPath.append("/").append(curDayString).append("/rollCallStats");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * 导员点名
     *
     * @return 是否获取到锁
     */
    public boolean getCounsellorRollcallLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/counsellorRollcall/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/counsellorRollcall/").append(HHmm);
        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delCounsellorRollcallLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/counsellorRollcall");
        taskPath.append("/").append(curDayString).append("/counsellorRollcall");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * 导员点名
     *
     * @return 是否获取到锁
     */
    public boolean getCounsellorRollcallRuleLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/counsellorRollcallRule/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/counsellorRollcallRule/").append(HHmm);
        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delCounsellorRollcallRuleLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/counsellorRollcallRule");
        taskPath.append("/").append(curDayString).append("/counsellorRollcallRule");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * 初始化教师平时打分
     *
     * @return 是否获取到锁
     */
    public boolean getInitAllTeacherLimitScoreLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/initAllTeacherLimitScore/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/initAllTeacherLimitScore/").append(HHmm);
        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delInitAllTeacherLimitScoreLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/initAllTeacherLimitScore");
        taskPath.append("/").append(curDayString).append("/initAllTeacherLimitScore");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * 导员点名
     *
     * @return 是否获取到锁
     */
    public boolean getCounsellorRollcallCloseAllLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        String HHmm = DateUtil.format(current, "HHmm");
        lockPath.append("/").append(curDayString).append("/counsellorRollcallCloseAll/").append(HHmm);
        taskPath.append("/").append(curDayString).append("/counsellorRollcallCloseAll/").append(HHmm);
        return getLock(lockPath.toString(), taskPath.toString());
    }

    public void delCounsellorRollcallCloseAllLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        lockPath.append("/").append(curDayString).append("/counsellorRollcallCloseAll");
        taskPath.append("/").append(curDayString).append("/counsellorRollcallCloseAll");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    // @Scheduled(cron = "* 58 23 * * ?")
    // public void cleanZookeeperTaskData() {
    // delete();
    // }

    /**
     * 在线选宿舍
     *
     * @return 是否获取到床位锁
     */
    public boolean getChooseRoomLock(Long bedId) {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        lockPath.append("/").append("chooser").append("/room/").append("lock/" + bedId);
        taskPath.append("/").append("chooser").append("/room/").append("lock/" + bedId);
        return getLock(lockPath.toString(), taskPath.toString());
    }

    /**
     * 在线选宿舍删除锁
     */
    public void deleteChooseRoomLock(Long bedId) {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        lockPath.append("/").append("chooser").append("/room/").append("lock/" + bedId);
        taskPath.append("/").append("chooser").append("/room/").append("lock/" + bedId);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }


    /**
     * 在线选宿舍删除死锁
     */
    public void deleteChooseRoomDieLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        lockPath.append("/").append("chooser").append("/room");
        taskPath.append("/").append("chooser").append("/room");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }

    /**
     * dian一下定时发送
     *
     * @return 是否获取到锁
     */
    public boolean getTaskDianLock(String uuid) {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        lockPath.append("/dian").append("/" + uuid);
        taskPath.append("/dian").append("/" + uuid);
        return getLock(lockPath.toString(), taskPath.toString());
    }


    /**
     * 删除dian一下定时发送
     */
    public void deleteTaskDianLock() {
        StringBuilder lockPath = new StringBuilder(zkLockPath);
        StringBuilder taskPath = new StringBuilder(zkTaskPath);
        lockPath.append("/dian");
        taskPath.append("/dian");
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectString, new RetryNTimes(ZOOKEEPER_RETRY_TIMES, ZOOKEEPER_RETRY_SLEEP_TIMES));
        try {
            client.start();
            client.delete().deletingChildrenIfNeeded().forPath(lockPath.toString());
            client.delete().deletingChildrenIfNeeded().forPath(taskPath.toString());
            client.close();
        } catch (Exception e) {
            LOG.warn("删除锁路径({})和任务路径({})失败:{}", lockPath.toString(), taskPath.toString(), e);
            e.printStackTrace();
        }
    }
}
