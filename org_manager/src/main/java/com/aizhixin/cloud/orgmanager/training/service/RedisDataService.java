package com.aizhixin.cloud.orgmanager.training.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.training.core.GroupStatusConstants;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDataService {
    public final static Integer DATA_TTL = 1;
    public final static String PRE = "org_api:train:";
    public final static String GROUP ="group_";
    @Autowired
    private RedisTemplate redisTemplate;

    private void expire(String key) {
        redisTemplate.expire(key, DATA_TTL, TimeUnit.DAYS);
    }

    private void expire(String key, long ttl) {
        redisTemplate.expire(key, ttl, TimeUnit.HOURS);
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
     * 缓存辅导员的实践计划信息
     */
    public void cacheCounselorGroupInfor(Long counselorId,PageData<TrainingGroupInfoDTO> groupInfoPage,String flag) {
        String key = PRE+GROUP+counselorId;
        redisTemplate.opsForHash().put(key,flag, groupInfoPage);
        expire(key,8);
    }
    
    public void delCounselorGroupInfor(Long counselorId) {
    	String key = PRE+GROUP+counselorId;
        redisTemplate.opsForHash().delete(key,GroupStatusConstants.GROUP_STATUS_NOT_OVER);
        redisTemplate.opsForHash().delete(key,GroupStatusConstants.GROUP_STATUS_END);
    }
    
    /**
     * 获取某个辅导员的实践计划信息
     */
    public PageData<TrainingGroupInfoDTO> getCounselorGroupInfor(Long counselorId,String flag) {
        String key = PRE+GROUP+counselorId;
        Object obj = redisTemplate.opsForHash().get(key,flag);
        if(null != obj){
        	return (PageData<TrainingGroupInfoDTO>)obj;
        }
        return null;
    }

}
