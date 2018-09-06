package com.aizhixin.cloud.dd.rollcall.thread;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.aizhixin.cloud.dd.rollcall.domain.HomeAnnouncementDomain;
import com.alibaba.fastjson.JSON;

public class HomeAnnouncementRunnable implements Runnable{
	private Logger log=LoggerFactory.getLogger(HomeAnnouncementRunnable.class);
	private RedisTemplate<String, String> redisTemplate;
	private Map<Long,HomeAnnouncementDomain> mapData;
	
	public HomeAnnouncementRunnable(RedisTemplate<String, String> redisTemplate, Map<Long,HomeAnnouncementDomain> mapData) {
		super();
		this.redisTemplate = redisTemplate;
		this.mapData = mapData;
	}

	@Override
	public void run() {

		log.info("dian一下最新消息开始缓存redis---");
		for (Long key :mapData.keySet()) {
			HomeAnnouncementDomain had=mapData.get(key);
			if(null!=had) {
				String json=JSON.toJSONString(had);
				redisTemplate.opsForValue().set("Ann"+key, json);
			}
		}
		log.info("dian一下最新消息开始缓存redis结束---");
	}

}
