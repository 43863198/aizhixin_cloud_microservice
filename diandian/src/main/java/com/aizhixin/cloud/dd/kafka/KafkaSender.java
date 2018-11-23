//package com.aizhixin.cloud.dd.kafka;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.messaging.Source;
//import org.springframework.messaging.support.MessageBuilder;
//import org.slf4j.Logger;
//
//
///**
// * @author: Created by jianwei.wu
// * @E-mail: wujianwei@aizhixin.com
// * @Date: 2017-09-27
// */
//@EnableBinding(Source.class)
//public class KafkaSender {
//
//    private final Logger log = LoggerFactory.getLogger(KafkaSender.class);
//
//    @Autowired
//    private Source source;
//
//    public void sendMessage(String msg) {
//        try {
//            source.output().send(MessageBuilder.withPayload(msg).build());
//        } catch (Exception e) {
//            log.info("消息发送失败，原因："+e);
//            log.warn("Exception", e);
//            log.warn("Exception", e);
//        }
//    }
//
//}
//
