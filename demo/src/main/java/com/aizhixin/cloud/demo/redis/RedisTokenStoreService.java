package com.aizhixin.cloud.demo.redis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisTokenStoreService {
    public final  static String CURRENT_USER_ALL = "zhixin:user:";
    private final RedisConnectionFactory connectionFactory;

    private JdkSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
    @Autowired
    public RedisTokenStoreService(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }
    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }
    private String deserializString(byte[] bytes) {
        return serializationStrategy.deserializeString(bytes);
    }
    public void storeString(String  keyStr, String  valueStr) {
        byte[] key = serialize(CURRENT_USER_ALL + keyStr);
        byte[] value = serialize(valueStr);

        RedisConnection conn = getConnection();
        try {
            conn.set(key, value);
        } finally {
            conn.close();
        }
    }


    public String readString(String keyStr) {
        byte[] key = serialize(CURRENT_USER_ALL + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializString(value);
        }
        return null;
    }
}
