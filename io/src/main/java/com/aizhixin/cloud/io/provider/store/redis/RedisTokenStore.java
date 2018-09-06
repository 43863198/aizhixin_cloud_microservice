package com.aizhixin.cloud.io.provider.store.redis;

import com.aizhixin.cloud.io.domain.LocalFileDomain;
import lombok.Setter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhen.pan on 2017/6/6.
 */
public class RedisTokenStore {
    private static final String ACCESS = ":doc:";
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

    private LocalFileDomain deserializeAccessToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, LocalFileDomain.class);
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

    public void storeAccessToken(String keyStr, LocalFileDomain d) {
        byte[] value = serialize(d);
        byte[] key = serializeKey(ACCESS + keyStr);

        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.set(key, value);
            conn.closePipeline();
        } finally {
            conn.close();
        }
    }

    public LocalFileDomain readToken(String keyStr) {
        byte[] key = serializeKey(ACCESS + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializeAccessToken(value);
        }
        return null;
    }

    public Set<String> readAllKeys() {
        byte[] keys = serializeKey(ACCESS + "*");
        RedisConnection conn = getConnection();
        Set<byte[]> valueSet = null;
        try {
            valueSet =  conn.keys(keys);
        } finally {
            conn.close();
        }
        Set<String> rs = new HashSet<>();
        if (null != valueSet && valueSet.size() > 0) {
            for (byte[] key : valueSet) {
                if (null != key && key.length > 0) {
                    String k = deserializeString(key);
                    if (null != k) {
                        rs.add(k);
                    }
                }
            }
        }
        return rs;
    }

    public List<LocalFileDomain> readKeys(Set<String> keys) {
        RedisConnection conn = getConnection();
        List<byte[]> vbs = new ArrayList<>();
        try {
            for (String key : keys) {
                byte[] k = serialize(key);
                byte[] v = conn.get(k);
                vbs.add(v);
            }
        } finally {
            conn.close();
        }
        List<LocalFileDomain> localFileDomainList = new ArrayList<>();
        for (byte[] v : vbs) {
            if (null != v && v.length > 0) {
                LocalFileDomain d = deserializeAccessToken(v);
                if (null != d) {
                    localFileDomainList.add(d);
                }
            }
        }
        return localFileDomainList;
    }
}
