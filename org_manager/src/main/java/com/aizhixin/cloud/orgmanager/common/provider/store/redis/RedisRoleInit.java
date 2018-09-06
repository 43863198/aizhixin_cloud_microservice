package com.aizhixin.cloud.orgmanager.common.provider.store.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-10
 */
@Configuration
public class RedisRoleInit {
    @Value("${sys.role.groupb.orgadmin}")
    private String orgadmin;
    @Value("${sys.role.groupb.orgmanager}")
    private String orgmanager;
    @Value("${sys.role.groupb.collegeadmin}")
    private String collegeadmin;
    @Value("${sys.role.groupb.orgdataview}")
    private String orgdataview;
    @Value("${sys.role.groupb.collegedataview}")
    private String collegedataview;
    @Value("${sys.role.groupb.orgeducationalmanager}")
    private String orgeducationalmanager;
    @Value("${sys.role.groupb.collegeeducationalmanager}")
    private String collegeeducationalmanager;
    @Value("${sys.role.groupb.financeadmin}")
    private String financeadmin;
    @Value("${sys.role.groupb.dormadmin}")
    private String dormadmin;
    @Value("${sys.role.groupb.enroladmin}")
    private String enroladmin;

    private static final String USER_ROLE = ":userrole";

    private RedisConnectionFactory connectionFactory;

    @Autowired
    public RedisRoleInit(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    @PostConstruct
    public void setRedisRole() {
        Map<String, String> redisKey = new HashMap<String, String>();
        redisKey.put("orgadmin", "orgadmin");
        redisKey.put("orgmanager", "orgmanager");
        redisKey.put("collegeadmin", "collegeadmin");
        redisKey.put("orgdataview", "orgdataview");
        redisKey.put("collegedataview", "collegedataview");
        redisKey.put("orgeducationalmanager", "orgeducationalmanager");
        redisKey.put("collegeeducationalmanager", "collegeeducationalmanager");
        redisKey.put("financeadmin", "financeadmin");
        redisKey.put("dormadmin", "dormadmin");
        redisKey.put("enroladmin", "enroladmin");
        Map<byte[], byte[]> bytemap = new HashMap<>();
        bytemap.put(redisKey.get("orgadmin").getBytes(), orgadmin.getBytes());
        bytemap.put(redisKey.get("orgmanager").getBytes(), orgmanager.getBytes());
        bytemap.put(redisKey.get("collegeadmin").getBytes(), collegeadmin.getBytes());
        bytemap.put(redisKey.get("orgdataview").getBytes(), orgdataview.getBytes());
        bytemap.put(redisKey.get("collegedataview").getBytes(), collegedataview.getBytes());
        bytemap.put(redisKey.get("orgeducationalmanager").getBytes(), orgeducationalmanager.getBytes());
        bytemap.put(redisKey.get("collegeeducationalmanager").getBytes(), collegeeducationalmanager.getBytes());
        bytemap.put(redisKey.get("financeadmin").getBytes(), financeadmin.getBytes());
        bytemap.put(redisKey.get("dormadmin").getBytes(), dormadmin.getBytes());
        bytemap.put(redisKey.get("enroladmin").getBytes(), enroladmin.getBytes());
        byte[] key = ("ALL" + USER_ROLE).getBytes();
        RedisConnection conn = getConnection();
        try {
            conn.hMSet(key, bytemap);
        } finally {
            conn.close();
        }

    }
}
