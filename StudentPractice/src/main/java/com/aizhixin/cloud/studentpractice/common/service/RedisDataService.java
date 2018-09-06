package com.aizhixin.cloud.studentpractice.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.studentpractice.common.PageData;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDataService {
    public final static Integer DATA_TTL = 1;
    public final static String PRE = "practice_api:";
    public final static String TASK ="task_";
    public final static String SUMMARY ="summary_";
    public final static String SIGNIN ="signin_";
    public final static String SCORE ="score_";
    public final static String STATISTICAL ="statistical_";
    public final static String EVALUATE ="evaluate_";
    @Autowired
    private RedisTemplate redisTemplate;

    private void expire(String key) {
        redisTemplate.expire(key, DATA_TTL, TimeUnit.DAYS);
    }

    private void expire(String key, long ttl) {
        redisTemplate.expire(key, ttl, TimeUnit.MINUTES);
    }

    /**
     * 清空所有缓存
     */
    public void clearAllCache() {
        Set<String> keys = redisTemplate.keys(PRE + "*");
        if (null != keys && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 缓存进入APP实践日志首页数据
     */
    public void cacheSummaryInfor(Long userId,Long groupId,Map<String, Object> page) {
        String key = PRE+SUMMARY+userId;
        redisTemplate.opsForHash().put(key,groupId.toString(), page);
        expire(key,10);
    }
    
    public void delSummaryInfor(Long userId,Long groupId) {
    	String key = PRE+SUMMARY+userId;
        redisTemplate.opsForHash().delete(key,groupId.toString());
    }
    
    /**
     * 获取进入APP实践日志首页数据
     */
    public Map<String, Object> getSummaryInfor(Long userId,Long groupId) {
        String key = PRE+SUMMARY+userId;
        Object obj = redisTemplate.opsForHash().get(key, groupId.toString());
        if(null != obj){
        	return (Map<String, Object>)obj;
        }
        return null;
    }

    
    /**
     * 缓存进入APP实践评价首页数据
     */
    public void cacheEvaluateInfor(Long userId,Long groupId,PageData page) {
        String key = PRE+EVALUATE+userId;
        redisTemplate.opsForHash().put(key,groupId.toString(), page);
        expire(key,10);
    }
    
    public void delEvaluateInfor(Long userId,Long groupId) {
    	String key = PRE+EVALUATE+userId;
        redisTemplate.opsForHash().delete(key,groupId.toString());
    }
    
    /**
     * 获取进入APP实践评价首页数据
     */
    public PageData getEvaluateInfor(Long userId,Long groupId) {
        String key = PRE+EVALUATE+userId;
        Object obj = redisTemplate.opsForHash().get(key, groupId.toString());
        if(null != obj){
        	return (PageData)obj;
        }
        return null;
    }
}
