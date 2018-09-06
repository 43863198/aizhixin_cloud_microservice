package com.aizhixin.cloud.io.provider.store.redis;

/**
 * Created by zhen.pan on 2017/6/6.
 */
public interface RedisTokenStoreSerializationStrategy {
    <T> T deserialize(byte[] bytes, Class<T> clazz);

    String deserializeString(byte[] bytes);

    byte[] serialize(Object object);

    byte[] serialize(String data);
}
