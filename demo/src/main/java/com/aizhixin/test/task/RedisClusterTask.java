package com.aizhixin.test.task;

import com.aizhixin.cloud.demo.redis.JdkSerializationStrategy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class RedisClusterTask {
    private final JdkSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
    private JedisSentinelPool cluster;
    private Long id;

    public RedisClusterTask(JedisSentinelPool cluster, Long id) {
        this.cluster = cluster;
        this.id = id;
//        this.authorization = authorization;
    }

    public String execute () {
        Jedis jedis = cluster.getResource();
        byte[] v = null;
        try {
            v = jedis.hget(serializationStrategy.serialize("org_api:first:user" + (id % 10)), serializationStrategy.serialize(id.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
        if (null == v || v.length <= 0) {
//            System.out.println(id);
            return "test";
        }
        return "test" + v.length;
    }

//    public static void main(String[] args) {
//        OrgUserInfoTask test = new OrgUserInfoTask(new RestUtil(), "http://localhost:8080/v1/user/get/163837", null);
//        String json = test.execute();
//        if (!StringUtils.isEmpty(json)) {
//            System.out.println(json);
//        }
//    }
}
