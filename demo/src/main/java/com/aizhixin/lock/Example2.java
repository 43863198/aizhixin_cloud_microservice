package com.aizhixin.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.TimeUnit;

public class Example2 {
    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final String ZK_PATH = "/t2/zktest";
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new RetryNTimes(3, 3000)
        );
        try {
            client.start();
//            client.create().creatingParentsIfNeeded().forPath(ZK_PATH);
//            List<String> paths = client.getChildren().forPath("/");
//            for (String p : paths) {
//                System.out.println("---------------------------" + p);
//            }
            Thread t1 = new Thread(() -> {
                doWithLock("Test1", client);
            }, "t1");
            Thread t2 = new Thread(() -> {
                doWithLock("Test2", client);
            }, "t2");

            t1.start();
            t2.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(20000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CloseableUtils.closeQuietly(client);
        }
    }

    private static void doWithLock(String name, CuratorFramework client) {
        InterProcessMutex lock = new InterProcessMutex(client, ZK_PATH);
        try {
            if (lock.acquire(3, TimeUnit.SECONDS)) {
                System.out.println("----------------------------------------" + name + " hold lock");
                Thread.sleep(5000L);
                System.out.println("----------------------------------------" + name + " release lock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
