package com.aizhixin.cloud.dd.rollcall.serviceV3;

import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.SignInDTO;
import com.aizhixin.cloud.dd.rollcall.serviceV2.RollCallServiceV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-27
 */
@Component
public class ProcessingAttendance {
    private final Logger log = LoggerFactory.getLogger(ProcessingAttendance.class);
    @Autowired
    RollCallServiceV2 rollCallServiceV2;

    public void start(){

        new Thread(new initTimer()).start();
    }
    class initTimer implements Runnable {
        public void run() {
            ExecutorService pool = Executors.newFixedThreadPool(5);
            while (true) {
                if (RollCallServiceV3.concurrentLinkedDeque.isEmpty()) {
                    log.info("签到队列为空！");
                    try {
                        Thread.sleep(2000);
                        log.info("休息2秒！");
                    } catch (InterruptedException e) {
                        log.warn("Exception", e);
                    }
                } else {
                    log.info("开始处理签到队列.");
                    try {
                        Map<String, Object> info = (Map<String, Object>) RollCallServiceV3.concurrentLinkedDeque.poll();
                        pool.execute(new RunnableClass(info));
                    }catch (Exception e){
                        log.warn("Exception", e);
                        log.warn("签到队列中的数据处理异常！");
                    }

                }
            }
        }
    }

    class RunnableClass implements Runnable {

        private Map<String, Object> info;

        public RunnableClass(final Map<String, Object> info) {

            this.info = info;
        }

        @Override
        public void run() {
            rollCallServiceV2.excuteSignIn((AccountDTO) info.get("account"), (SignInDTO) info.get("signInDTO"));
        }
    }
}






