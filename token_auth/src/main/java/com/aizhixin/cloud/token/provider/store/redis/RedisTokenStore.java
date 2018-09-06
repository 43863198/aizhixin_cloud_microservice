package com.aizhixin.cloud.token.provider.store.redis;

import com.aizhixin.cloud.token.entity.Access;
import lombok.Setter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Created by zhen.pan on 2017/6/6.
 */
public class RedisTokenStore {
    private static final String ACCESS = ":access:";
    private static final String TOKEN = ":token:";
    private final RedisConnectionFactory connectionFactory;
//    private DefaultKeyGenerator keyGenerator = new DefaultKeyGenerator();
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    @Setter private String prefix = "";

    public RedisTokenStore(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    private String deserializeString(byte[] bytes) {
        return serializationStrategy.deserializeString(bytes);
    }

    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    private Access deserializeAccessToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, Access.class);
    }

    private byte[] serializeKey(String object) {
        return serialize(prefix + object);
    }

    public String readAccessToken(String token) {
        byte[] bytes = null;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(serializeKey(ACCESS + token));
        } finally {
            conn.close();
        }

        return deserializeString(bytes);
    }

    public void storeAccessToken(Access access) {
        byte[] appId = serialize(access.getAppId());
        byte[] token = serializeKey(ACCESS + access.getToken());

        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.set(token, appId);
            int seconds = access.getTtl();
            conn.expire(token, seconds);
            conn.closePipeline();
        } finally {
            conn.close();
        }
    }
}
