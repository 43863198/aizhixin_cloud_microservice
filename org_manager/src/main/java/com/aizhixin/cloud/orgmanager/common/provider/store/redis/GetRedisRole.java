package com.aizhixin.cloud.orgmanager.common.provider.store.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-11
 */
public class GetRedisRole {
    private static final String USER_ROLE = ":userrole";
    private final RedisConnectionFactory connectionFactory;

    public GetRedisRole(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    public List<String> getRedisRole() {
        List<String> roleList = new ArrayList<>();
        byte[] key = ("ALL" + USER_ROLE).getBytes();
        RedisConnection conn = getConnection();
        List<byte[]> value = null;
        try {
            value = conn.hMGet(key, ("orgadmin").getBytes(), ("orgmanager").getBytes(), ("collegeadmin").getBytes(), ("viewattendance").getBytes(),
                ("viewquestionnaire").getBytes(), ("viewcoursescore").getBytes(), ("financeadmin").getBytes(), ("dormadmin").getBytes(), ("orgdataview").getBytes(),
                ("collegedataview").getBytes(), ("orgeducationalmanager").getBytes(), ("collegeeducationalmanager").getBytes(), ("enroladmin").getBytes());
        } finally {
            conn.close();
        }
        if (null != value && value.size() > 0) {
            for (byte[] d : value) {
                roleList.add(new JdkSerializationStrategy().deserializeStringInternal(d));
            }
            return roleList;
        }
        return null;
    }

}
