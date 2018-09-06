//package com.aizhixin.cloud.dd.kafka;
//
//import io.swagger.annotations.Api;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author: Created by jianwei.wu
// * @E-mail: wujianwei@aizhixin.com
// * @Date: 2017-09-27
// */
//@RestController
//@RequestMapping("/api/web/v1")
//@Api(description = "kafka测试 API")
//public class SourceApplication {
//    @Autowired
//    private KafkaSender kafkaSender;
//
//    @RequestMapping(value = "/send/{msg}", method = RequestMethod.GET)
//    public void send(@PathVariable("msg") String msg){
//        kafkaSender.sendMessage(msg);
//    }
//}