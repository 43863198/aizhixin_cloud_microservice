package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.entity.Claim;
import com.aizhixin.cloud.dd.rollcall.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by LIMH on 2017/11/7.
 */
@Service
@Transactional
public class ClaimService {


    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public Claim findBy(Long teachingclassId, String teachDate, Integer periodNo, Integer periodNum) {
        String key = teachingclassId + teachDate + periodNo + periodNum;

        Claim claim = (Claim) redisTemplate.opsForValue().get(key);
        if (claim != null) {
            return claim;
        }
//        List <Claim> claims = claimRepository.findAllByTeachingclassIdAndTeachDateAndPeriodNoAndPeriodNumAndDeleteFlag(teachingclassId, teachDate, periodNo, periodNum, DataValidity.VALID.getState());
//        if (claims != null && claims.size() > 0) {
//            return claims.get(0);
//        }
//        List <Claim> claimList = claimRepository.findAllByTeachingclassIdAndDeleteFlagAndTeachDateBeforeOrderByTeachDateDesc(teachingclassId, DataValidity.VALID.getState(), DateFormatUtil.formatShort(new Date()));
//        if (claimList != null && claimList.size() > 0) {
//            return claimList.get(0);
//        }
        claim = (Claim) redisTemplate.opsForValue().get("claim:" + teachingclassId);
        if (claim != null) {
            return claim;
        }

        return null;
    }

    public Claim findByDb(Long teachingclassId, String teachDate, Integer periodNo, Integer periodNum) {

        List <Claim> claims = claimRepository.findAllByTeachingclassIdAndTeachDateAndPeriodNoAndPeriodNumAndDeleteFlag(teachingclassId, teachDate, periodNo, periodNum, DataValidity.VALID.getState());
        if (claims != null && claims.size() > 0) {
            return claims.get(0);
        }
        List <Claim> claimList = claimRepository.findAllByTeachingclassIdAndDeleteFlagAndTeachDateBeforeOrderByTeachDateDesc(teachingclassId, DataValidity.VALID.getState(), DateFormatUtil.formatShort(new Date()));
        if (claimList != null && claimList.size() > 0) {
            return claimList.get(0);
        }
        return null;
    }


    public void save(Claim claim) {
        claimRepository.save(claim);
        String key = claim.getTeachingclassId() + claim.getTeachDate() + claim.getPeriodNo() + claim.getPeriodNum();
        redisTemplate.opsForValue().set(key, claim, 24, TimeUnit.HOURS);
        redisTemplate.opsForValue().set("claim:" + claim.getTeachingclassId(), claim, 120, TimeUnit.DAYS);
    }
}
