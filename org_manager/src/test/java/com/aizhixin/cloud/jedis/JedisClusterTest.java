package com.aizhixin.cloud.jedis;

import com.aizhixin.cloud.orgmanager.common.provider.store.redis.JdkSerializationStrategy;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStoreSerializationStrategy;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JedisClusterTest {
    private final RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    public static List<Long> readUserIdFromFile(String file) {
        List<Long> ids = new ArrayList<>();
        File f = new File(file);
        java.io.BufferedReader in = null;
        try {
            in = new java.io.BufferedReader(new java.io.BufferedReader(new java.io.FileReader(f)));
            String line = in.readLine();
            line = in.readLine();
            while (null != line) {
                ids.add(new Long(line));
                line = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }
    public void testConnect() throws IOException {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 最大连接数
        poolConfig.setMaxTotal(300);
        // 最大空闲数
        poolConfig.setMaxIdle(10);
        // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
        // Could not get a resource from the pool
        poolConfig.setMaxWaitMillis(1000);
        Set<HostAndPort> nodes = new LinkedHashSet<>();
        nodes.add(new HostAndPort("172.16.40.103", 7000));
        nodes.add(new HostAndPort("172.16.40.103", 7001));
        nodes.add(new HostAndPort("172.16.40.103", 7002));
        nodes.add(new HostAndPort("172.16.40.104", 7003));
        nodes.add(new HostAndPort("172.16.40.104", 7004));
        nodes.add(new HostAndPort("172.16.40.104", 7005));
        JedisCluster cluster = new JedisCluster(nodes, poolConfig);
        List<Long> userIdList = readUserIdFromFile("d:/id.txt");
//        for (Long id : userIdList) {
        Long id = new Long(134979);
//        UserDomain u = new UserDomain();
//        u.setId(id);
//        long r = cluster.hset(serializationStrategy.serialize("org_api:first:user" + (id % 10)), serializationStrategy.serialize(id.toString()), serializationStrategy.serialize(u));
//        System.out.println(r);
            byte[] v = cluster.hget(serializationStrategy.serialize("org_api:first:user" + (id % 10)), serializationStrategy.serialize(id.toString()));
            if (null != v && v.length > 0) {
                System.out.println(id);
            }
//        }

        cluster.close();
    }

    public static void main(String[] args) throws IOException {
        JedisClusterTest t = new JedisClusterTest ();
        t.testConnect();
    }
}
