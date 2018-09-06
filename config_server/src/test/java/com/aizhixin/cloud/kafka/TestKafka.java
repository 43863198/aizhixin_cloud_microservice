package com.aizhixin.cloud.kafka;

import kafka.admin.AdminUtils;
import kafka.api.TopicMetadata;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import scala.collection.Seq;

import java.util.List;

public class TestKafka {
    public static void main(String[] args) {
        String zkUrl = "172.16.23.120:2181";
        ZkUtils zkUtils = ZkUtils.apply(zkUrl, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        Seq<String> tps = zkUtils.getAllTopics();
        if (null != tps) {
            List<String> topics = scala.collection.JavaConversions.seqAsJavaList(tps);
            for (String topic : topics) {
                System.out.println("-----------" + topic);
                TopicMetadata metadata = AdminUtils.fetchTopicMetadataFromZk(topic, zkUtils);
                System.out.println(metadata);
            }
        }
        Seq<String> cunsumerGroups = zkUtils.getConsumerGroups();
        if (null != cunsumerGroups) {
            List<String> cunsumerGroupList = scala.collection.JavaConversions.seqAsJavaList(cunsumerGroups);
            for (String cunsumerGroup : cunsumerGroupList) {
                System.out.println("+++++++++++" + cunsumerGroup);
            }
        }
        zkUtils.close();
    }
}
