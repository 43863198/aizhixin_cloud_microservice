package com.zhizixin.cloud.config.actuator;

import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

/**
 *  ./kafka-topics.sh --list --zookeeper 172.16.23.120:2181
 *  ./kafka-topics.sh --describe --zookeeper 172.16.23.120:2181 --topic springCloudBus
 *   ./kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
 *   ./kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group anonymous.52e1fa03-c034-4315-8741-3018b4ea4f8e
 */
@Component
public class KafkaHealthIndicator implements HealthIndicator {
//    @Autowired
//    private KafkaTemplate<String,String> kafkaTemplate;
    @Value("${spring.cloud.stream.kafka.binder.zk-nodes}")
    private String zkNodes;
    @Override
    public Health health() {
        List<String> topics = new ArrayList<>();
        int errorCode = check(topics);
        if (0 != errorCode) {
            return Health.down().withDetail("Error Code", errorCode).build();
        } else {
            StringBuilder tps = new StringBuilder();
            int i = 0;
            for (String topic : topics) {
                if (0 <= i) {
                    tps.append(",");
                }
                tps.append(topic);
                i++;
            }
            return Health.up().withDetail("Topic tables", tps.toString()).build();
        }
    }

    private int check(List<String> topics) {
        int res = 0;
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(zkNodes, 30000, 30000, JaasUtils.isZkSecurityEnabled());
            Seq<String> tps = zkUtils.getAllTopics();
            if (null != tps) {
                List<String> topics2 = scala.collection.JavaConversions.seqAsJavaList(tps);
                for (String topic : topics2) {
                    topics.add(topic);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = -1;
        } finally {
            if (null != zkUtils)
                zkUtils.close();
        }
        return res;
    }
}
