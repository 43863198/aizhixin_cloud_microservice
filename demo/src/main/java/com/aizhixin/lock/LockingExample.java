package com.aizhixin.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LockingExample {
    private static final int QTY = 1;
    private static final int REPETITIONS = QTY * 2;

    private static final String PATH = "/t1/locks";

    public static void main(String[] args) {
        // all of the useful sample code is in ExampleClientThatLocks.java
        // FakeLimitedResource simulates some external resource that can only be access by one process at a time
        final FakeLimitedResource resource = new FakeLimitedResource();
        ExecutorService service = Executors.newFixedThreadPool(QTY);
        try {
            for (int i = 0; i < QTY; ++i) {
                final int index = i;
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(3000, 3));
                        try {
                            client.start();

                            ClientLockExample example = new ClientLockExample(client, PATH, resource, "Client " + index);
                            for (int j = 0; j < REPETITIONS; ++j) {
                                example.doWork(10, TimeUnit.SECONDS);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            CloseableUtils.closeQuietly(client);
                        }
                        return null;
                    }
                };
                service.submit(task);
            }
            service.shutdown();
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
