package com.aizhixin.cloud.dd.counsellorollcall.utils;

import com.aizhixin.cloud.dd.rollcall.service.AttendanceStatisticsService;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author LIMH
 * @date 2017/12/25
 */
public class AntiCheantingUtils {

    private final Logger log = LoggerFactory.getLogger(AttendanceStatisticsService.class);

    private StringRedisTemplate stringRedisTemplate;


}
