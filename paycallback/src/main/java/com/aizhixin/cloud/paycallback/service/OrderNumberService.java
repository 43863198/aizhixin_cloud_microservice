package com.aizhixin.cloud.paycallback.service;

import com.aizhixin.cloud.paycallback.common.util.DateUtil;
import com.aizhixin.cloud.paycallback.common.util.FormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;


@Component
public class OrderNumberService {
    @Autowired
    private RedisTemplate redisTemplate;
    public String generaterOrderNumber(long orgId){
        RedisAtomicLong counter = new RedisAtomicLong("payOrderNumber", redisTemplate.getConnectionFactory());
        Long redisIncrement=counter.incrementAndGet();
        String orderNumber= orgId+DateUtil.getCurrentYearAndMoth()+ FormatUtil.addZeroForNum(redisIncrement+"",8);
        return orderNumber;
    }
    public void initRedisIncrement(){
        RedisAtomicLong counter = new RedisAtomicLong("payOrderNumber", redisTemplate.getConnectionFactory());
        counter.set(1);
    }

}
