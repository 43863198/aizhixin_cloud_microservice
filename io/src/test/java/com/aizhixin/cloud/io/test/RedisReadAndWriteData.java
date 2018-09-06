package com.aizhixin.cloud.io.test;

import com.aizhixin.cloud.io.domain.LocalFileDomain;
import com.aizhixin.cloud.io.provider.store.redis.JdkSerializationStrategy;
import com.aizhixin.cloud.io.provider.store.redis.RedisTokenStoreSerializationStrategy;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisReadAndWriteData {

    private final static int PORT = 6379;

    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    private Jedis getRedis (String host, int port) {
        return new Jedis(host, port);
    }

    public void test () {
        Jedis jedis = getRedis("172.16.23.30", PORT);
        jedis.select(4);

        Jedis newJedis = getRedis("172.16.23.117", PORT);
        newJedis.select(4);

        Set<String> stringSet = jedis.keys("azx:io:doc*");
        for (String key : stringSet) {
            byte[] keyb = serializationStrategy.serialize(key);
            byte[]  value = jedis.get(keyb);
            LocalFileDomain d = null;
            try {
                d = serializationStrategy.deserialize(value, LocalFileDomain.class);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            newJedis.set(keyb, value);
            System.out.println(d.toString());
        }
        jedis.close();
        newJedis.close();
    }

    public void readTestOneData() {
        Jedis newJedis = getRedis("172.16.23.31", PORT);
        newJedis.select(4);
        Set<String> stringSet = newJedis.keys("azx:io:doc*");
        for (String key : stringSet) {
            System.out.println(key);
            byte[] keyb = serializationStrategy.serialize(key);
            byte[]  value = newJedis.get(keyb);
            LocalFileDomain d = null;
            try {
                d = serializationStrategy.deserialize(value, LocalFileDomain.class);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (null != d) {
                System.out.println(d.toString());
            }
        }
//        byte[]  value = newJedis.get(serializationStrategy.serialize("azx:io:doc:6FE81F5F1133454C997D2E4BFAC76E79"));
//        if (null == value) {
//            System.out.println("not read any data...");
//            return;
//        }
//        try {
//            LocalFile d = serializationStrategy.deserialize(value, LocalFile.class);
//            System.out.println(d.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        newJedis.close();
    }


    public void readDiandianData() {
        Jedis newJedis = getRedis("172.16.23.117", PORT);
        newJedis.select(1);
        Set<String> stringSet = newJedis.keys("*");
        for (String key : stringSet) {
            System.out.println(key);
            newJedis.del(key);
        }
        newJedis.close();
    }


    public void readOrgUser() {
        Jedis newJedis = getRedis("172.16.23.117", PORT);
        newJedis.select(13);
        String v = newJedis.hget("org_api:first:user", "161440");
        System.out.println(v);
        newJedis.close();
    }

    public static void main(String[] args) {
        RedisReadAndWriteData test = new RedisReadAndWriteData ();
//        test.test();
//        test.readTestOneData();
//        test.readDiandianData();
        test.readOrgUser();
    }
}
