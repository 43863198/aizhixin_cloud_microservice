package com.aizhixin.test.example;

import com.aizhixin.test.thread.BioThreadPool;

public class TestThread {
    private BioThreadPool pool = new BioThreadPool();

    public void testThreadPool() {
        int taskCount = 0;
        while(true) {
            if (!pool.addAndExecute(new DemoThread())) {
                try {
                    Thread.sleep(800);
                } catch (Exception e) {
                }
            } else {
                taskCount++;
            }
            if (taskCount >= 40) {
                break;
            }
        }
        pool.close();
    }

    public static void main(String[] args) {
        TestThread test = new TestThread();
        test.testThreadPool();
    }
}

class DemoThread extends Thread {
    @Override
    public void run () {
        try {
            Thread.sleep(new java.util.Random().nextInt(2000));
            System.out.println("compelet......");
        } catch (Exception e) {

        }
    }
}