package com.aizhixin.cloud.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池，一般做并发性能测试使用
 * Created by zhen.pan on 2017/4/27.
 */
public class BioThreadPool {
    private ThreadPoolExecutor executor;
    private int queueSize = 0;

    /**
     * 线程池执行时间缺省最多30秒，如果超过此值即为超时
     * @param corePoolSize      线程池核心池的大小(一般不能超过cpu的核心数量)
     * @param maximumPoolSize   最大的池的大小(一般cpu核心数量的3倍范围之内)
     * @param queueSize         线程池任务队列的最大缓存
     */
    public BioThreadPool(int corePoolSize, int maximumPoolSize, int queueSize) {
        this.queueSize = queueSize;
        int keepAliveTime = 30;
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueSize));
//        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
    }

    /**
     * 给线程池添加任务，如果线程池的任务缓存已满，添加失败，返回false。添加成功返回true
     * 进入任务缓冲池的任务会被自动调度启动
     * @param task  线程任务
     * @return  添加任务是否成功
     */
    public boolean addAndExecute(Runnable task) {
        if (executor.getQueue().size() < queueSize) {
            executor.execute(task);
            return true;
        }
        return false;
    }

    /**
     * 已经执行的任务总数量
     * @return  数量
     */
    public long getTaskCount() {
        return executor.getTaskCount();
    }

    /**
     *  已经完成的任务总数量
     * @return      数量
     */
    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }

    /**
     * 当前正在执行的任务的数量
     * @return  数量
     */
    public int getActiveCount() {
        return executor.getActiveCount();
    }

    /**
     * 关闭线程池
     */
    public void close() {
        executor.shutdown();
    }
}
