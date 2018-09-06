package com.aizhixin.cloud.token.provider.store.redis;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 序列表方法
 * Created by zhen.pan on 2017/6/6.
 */
public class JdkSerializationStrategy implements RedisTokenStoreSerializationStrategy {
    private static final StringRedisSerializer STRING_SERIALIZER = new StringRedisSerializer();
    private static final JdkSerializationRedisSerializer OBJECT_SERIALIZER = new JdkSerializationRedisSerializer();
    private static final byte[] EMPTY_ARRAY = new byte[0];

    private static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    private String deserializeStringInternal(byte[] bytes) {
        return STRING_SERIALIZER.deserialize(bytes);
    }

    private byte[] serializeInternal(String string) {
        return STRING_SERIALIZER.serialize(string);
    }

    @SuppressWarnings(value = {"unchecked", "unused"})
    private <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
        return (T) OBJECT_SERIALIZER.deserialize(bytes);
    }

    private byte[] serializeInternal(Object object) {
        return OBJECT_SERIALIZER.serialize(object);
    }

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return EMPTY_ARRAY;
        }
        return serializeInternal(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (isEmpty(bytes)) {
            return null;
        }
        return deserializeInternal(bytes, clazz);
    }

    @Override
    public byte[] serialize(String data) {
        if (data == null) {
            return EMPTY_ARRAY;
        }
        return serializeInternal(data);
    }

    @Override
    public String deserializeString(byte[] bytes) {
        if (isEmpty(bytes)) {
            return null;
        }
        return deserializeStringInternal(bytes);
    }
}
