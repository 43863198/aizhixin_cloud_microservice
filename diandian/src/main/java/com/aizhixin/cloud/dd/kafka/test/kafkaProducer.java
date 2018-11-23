//package com.aizhixin.cloud.dd.kafka.test;
//
//import java.util.Properties;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import kafka.javaapi.producer.Producer;
//import kafka.producer.KeyedMessage;
//import kafka.producer.ProducerConfig;
//import kafka.serializer.StringEncoder;
///**
// * @author: Created by jianwei.wu
// * @E-mail: wujianwei@aizhixin.com
// * @Date: 2017-09-29
// */
//public class kafkaProducer extends Thread{
//
//    public static void main(String[] args) {
//        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(10000);
//        for (int i = 0; i < 100000; i++) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                log.warn("Exception", e);
//            }
//            cachedThreadPool.execute(new Employee("test"));
//            i++;
//        }
//
//    }
//
//    static class Employee implements Runnable {
//        private String topic;
//
//        public Employee(String topic) {
//            super();
//            this.topic = topic;
//        }
//
//
//        @Override
//        public void run() {
//            Producer producer = createProducer();
//            int i = 0;
//            while (true) {
//                producer.send(new KeyedMessage<Integer, String>(topic, "message: wujianwei" + i++));
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    log.warn("Exception", e);
//                }
//            }
//        }
//
//        private Producer createProducer() {
//            Properties properties = new Properties();
//            properties.put("zookeeper.connect", "localhost:2181");//声明zk
//            properties.put("serializer.class", StringEncoder.class.getName());
//            properties.put("metadata.broker.list", "localhost:9092");// 声明kafka broker
//            return new Producer<Integer, String>(new ProducerConfig(properties));
//        }
//
//    }
//
//
//}
