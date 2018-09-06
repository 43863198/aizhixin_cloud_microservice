package com.aizhixin.cloud.dd;

import java.util.concurrent.Future;

import javax.ws.rs.core.Application;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@EnableAsync
public class Test {

    // @Autowired
    // AsyncTest asyncTest;
    // @org.junit.Test
    // public void test() throws InterruptedException {
    // System.out.println("begin");
    // long begin = System.currentTimeMillis();
    // Future<String> test1 = asyncTest.test1();
    // Future<String> test2 = asyncTest.test2();
    // Future<String> test3 = asyncTest.test3();
    //
    // while(true) {
    // if(test1.isDone() && test2.isDone() && test3.isDone())
    // break;
    //
    // Thread.sleep(500);
    // }
    //
    // System.out.println("end 耗时: " + (System.currentTimeMillis() - begin));
    // }
    public static void main(String[] args) {
        System.out.println(String.valueOf(null));
    }

}
