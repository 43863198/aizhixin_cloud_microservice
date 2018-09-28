package com.aizhixin.cloud.dd.messege.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.utils.ConfigCache;
import com.aizhixin.cloud.dd.common.utils.http.HttpResponse;
import com.aizhixin.cloud.dd.common.utils.http.OauthPostJson;

/**
 * Service class for managing users.
 */
@Service
@Transactional
@Scope(value = "singleton")
public class PushService {

    private final Logger log = LoggerFactory.getLogger(PushService.class);

    @Autowired
    private ConfigCache configCache;

    private final static int PUSH_QUEUE_SIZE = 10000;
    private final static int PUSH_QUEUE_OVER_SIZE = 1000;
    private static AtomicInteger ERROR_NUM = new AtomicInteger(0);
    private static BlockingQueue<PushMessage> pushQueue = new LinkedBlockingQueue<PushMessage>(PUSH_QUEUE_SIZE);

    public PushService() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>启动推送消息线程。");
        Thread pushThread = new Thread(run);
        pushThread.start();
    }

    public boolean listPush(String authorization, String text, String ticker,
                            String title, Iterable<Long> userIds) {
        try {
            pushQueue.put(new PushMessage(authorization, text, ticker, title, userIds));
        } catch (InterruptedException e) {
            log.warn("添加消息到推送队列异常。", e);
            e.printStackTrace();
        }
        return true;
    }


    Runnable run = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    log.info("获取需要推送的消息数据...");
                    PushMessage message = pushQueue.take();
                    if (message == null) {
                        sleep(500);
                        continue;
                    }
                    listPush(message);
                    sleep(50);
                    log.info("推送消息成功,现推送队列消息数量为:" + pushQueue.size());
                } catch (Exception e) {
                    ERROR_NUM.incrementAndGet();
                    log.warn("推送消息异常。");
                    e.printStackTrace();
                }
                // 防止推送消息异常缓慢，导致队列增大，从而引起调用服务阻塞。
                if (ERROR_NUM.get() > 20 && pushQueue.size() > PUSH_QUEUE_OVER_SIZE) {
                    log.warn("无法正常推送消息，已删除消息条数:" + pushQueue.size() + ",请检查消息推送服务。");
                    pushQueue.clear();
                    ERROR_NUM.set(0);
                }
            }
        }

        public void sleep(int seconds) {
            try {
                Thread.sleep(seconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void listPush(PushMessage pushMessage) {
            OauthPostJson post = new OauthPostJson();
            HttpResponse response = null;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("text", pushMessage.getText());
            params.put("title", pushMessage.getTitle());
            params.put("ticker", pushMessage.getTicker());
            params.put("userIds", pushMessage.getUserIds());
            String paramsStr = JSONObject.fromMap(params).toString();
            try {
                response = post.post(configCache.getConfigValueByParm("user.service.host") + configCache.getConfigValueByParm("common.service.listpush"), paramsStr, pushMessage.getAuthorization());
            } catch (IOException e) {
                ERROR_NUM.incrementAndGet();
                log.warn("调用推送消息接口异常,消息：" + paramsStr);
                e.printStackTrace();
            }
            if (response.getStatusCode() != HttpStatus.OK.value()) {
                log.warn("推送消息失败,返回值：" + response.getStatusCode() + ",消息："
                        + paramsStr);
            } else {
                log.info("推送消息:" + paramsStr);
            }
        }
    };

    class PushMessage {
        private String authorization;
        private String text;
        private String ticker;
        private String title;
        private Iterable<Long> userIds;

        public PushMessage(String authorization, String text, String ticker,
                           String title, Iterable<Long> userIds) {
            this.authorization = authorization;
            this.text = text;
            this.ticker = ticker;
            this.title = title;
            this.userIds = userIds;
        }

        public String getAuthorization() {

            return authorization;
        }

        public void setAuthorization(String authorization) {

            this.authorization = authorization;
        }

        public String getText() {

            return text;
        }

        public void setText(String text) {

            this.text = text;
        }

        public String getTicker() {

            return ticker;
        }

        public void setTicker(String ticker) {

            this.ticker = ticker;
        }

        public String getTitle() {

            return title;
        }

        public void setTitle(String title) {

            this.title = title;
        }

        public Iterable<Long> getUserIds() {

            return userIds;
        }

        public void setUserIds(Iterable<Long> userIds) {

            this.userIds = userIds;
        }
    }

}
