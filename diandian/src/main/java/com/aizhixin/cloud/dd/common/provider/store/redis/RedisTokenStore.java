package com.aizhixin.cloud.dd.common.provider.store.redis;

import com.aizhixin.cloud.dd.counsellorollcall.domain.RollcallReportDomain;
import com.aizhixin.cloud.dd.counsellorollcall.domain.StuTempGroupDomainV2;
import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcallRule;
import com.aizhixin.cloud.dd.homepage.domain.HomeDomain;
import com.aizhixin.cloud.dd.orgStructure.domain.UserInfoDomain;
import com.aizhixin.cloud.dd.rollcall.dto.PeriodDTO;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhen.pan on 2017/6/6.
 */
@Service
public class RedisTokenStore {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String TYPE_STUDENT_COUNSELLOR = "counsellor:student:";
    public static final String TYPE_STUDENT_COUNSELLOR_V2 = "counsellorv2:student:";
    public static final String TYPE_COUNSELLORROLLCALL = "counsellor:rollcall:";
    public static final String TYPE_COUNSELLORROLLCALL_RULE = "counsellor:rollcallrule:";
    public static final String TYPE_COUNSELLORROLLCALL_GROUP = "counsellor:rollcallgroupid:";

    public static final String TYPE_SCHEDULE_STUDENT_TODAY = "schedule:studenttoday:";
    public static final String TYPE_INIT_SCHEDULE_STATUS = "schedule:initstatus:";

    public static final String TYPE_ROLLCALLSTATS_STUDENT_ALL = "rollcallstats:studentall:";
    public static final String TYPE_ROLLCALLSTATS_STUDENT_TEACHINGCLASS = "rollcallstats:studentteachingclass:";
    public static final String TYPE_ROLLCALLSTATS_TEACHINGCLASS = "rollcallstats:teachingclass:";
    public static final String TYPE_ROLLCALLSTATS_TEACHINGCLASS_HISTORY = "rollcallstats:teachingclasshistory:";

    public static final String TYPE_GET_AD = "ddapp:getad:";

    public static final String TYPE_USERINFO_DOMAIN = "userinfo:";

    private void setCacheValue(String type, Long key, Object value) {
        String str = JSON.toJSONString(value);
        redisTemplate.opsForHash().put(type, key.toString(), str);
    }

    private void setCacheValue(String type, String key, Object value) {
        String str = JSON.toJSONString(value);
        redisTemplate.opsForHash().put(type, key, str);
    }

    private <T> List<T> getCacheValueList(String type, Long key, Class<T> clazz) {
        String str = (String) redisTemplate.opsForHash().get(type, key.toString());
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseArray(str, clazz);
        }
        return null;
    }

    private <T> T getCacheValue(String type, Long key, Class<T> clazz) {
        String str = (String) redisTemplate.opsForHash().get(type, key.toString());
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseObject(str, clazz);
        }
        return null;
    }

    private List<RollcallReportDomain> getCacheValueRollcallReportDomain(String type, Long key) {
        String str = (String) redisTemplate.opsForHash().get(type, key.toString());
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseArray(str, RollcallReportDomain.class);
        }
        return null;
    }

    private List<StuTempGroupDomainV2> getCacheValueRollcallReportDomainV2(String type, Long key) {
        String str = (String) redisTemplate.opsForHash().get(type, key.toString());
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseArray(str, StuTempGroupDomainV2.class);
        }
        return null;
    }

    private Long getLongCacheValue(String type, Long key) {
        String str = (String) redisTemplate.opsForHash().get(type, key.toString());
        if (!StringUtils.isEmpty(str)) {
            return Long.parseLong(str);
        }
        return null;
    }

    public void storeRedisData(Long key, List<RollcallReportDomain> data) {
        if (data != null) {
            setCacheValue(TYPE_STUDENT_COUNSELLOR, key, data);
        }
    }

    public void storeRedisDataV2(Long key, List<StuTempGroupDomainV2> data) {
        if (data != null) {
            setCacheValue(TYPE_STUDENT_COUNSELLOR_V2, key, data);
        }
    }


    /**
     * 添加多条 不替换
     *
     * @param datas
     */
    public void pushRedisDatas(Map<Long, List<RollcallReportDomain>> datas) {
        if (datas != null && datas.size() > 0) {
            for (Map.Entry<Long, List<RollcallReportDomain>> item : datas.entrySet()) {
                Map<Long, RollcallReportDomain> map = new HashMap<>();
                List<RollcallReportDomain> newList = new ArrayList<>();
                for (RollcallReportDomain data : item.getValue()) {
                    newList.add(data);
                    map.put(data.getId(), data);
                }
                //缓存中的数据
                List<RollcallReportDomain> list = readRedisData(item.getKey());
                if (list != null && list.size() > 0) {
                    for (RollcallReportDomain d : list) {
                        RollcallReportDomain domain = map.get(d.getId());
                        if (domain == null) {
                            newList.add(d);
                            map.put(d.getId(), d);
                        }
                    }
                }
                storeRedisData(item.getKey(), newList);
            }
        }
    }

    /**
     * 添加多条 不替换
     *
     * @param datas
     */
    public void pushRedisDatasV2(Map<Long, List<StuTempGroupDomainV2>> datas) {
        if (datas != null && datas.size() > 0) {
            for (Map.Entry<Long, List<StuTempGroupDomainV2>> item : datas.entrySet()) {
                Map<Long, StuTempGroupDomainV2> map = new HashMap<>();
                List<StuTempGroupDomainV2> newList = new ArrayList<>();
                for (StuTempGroupDomainV2 data : item.getValue()) {
                    newList.add(data);
                    map.put(data.getGroupId(), data);
                }
                //缓存中的数据
                List<StuTempGroupDomainV2> list = readRedisDataV2(item.getKey());
                if (list != null && list.size() > 0) {
                    for (StuTempGroupDomainV2 d : list) {
                        StuTempGroupDomainV2 domain = map.get(d.getGroupId());
                        if (domain == null) {
                            newList.add(d);
                            map.put(d.getGroupId(), d);
                        }
                    }
                }
                storeRedisDataV2(item.getKey(), newList);
            }
        }
    }

    /**
     * 保存多条 替换
     *
     * @param datas
     */
    public void storeRedisDatas(Map<Long, List<RollcallReportDomain>> datas) {
        if (datas != null && datas.size() > 0) {
            for (Map.Entry<Long, List<RollcallReportDomain>> item : datas.entrySet()) {
                storeRedisData(item.getKey(), item.getValue());
            }
        }
    }

    /**
     * 保存多条 替换
     *
     * @param datas
     */
    public void storeRedisDatasV2(Map<Long, List<StuTempGroupDomainV2>> datas) {
        if (datas != null && datas.size() > 0) {
            for (Map.Entry<Long, List<StuTempGroupDomainV2>> item : datas.entrySet()) {
                storeRedisDataV2(item.getKey(), item.getValue());
            }
        }
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public List<RollcallReportDomain> readRedisData(Long key) {
        return getCacheValueRollcallReportDomain(TYPE_STUDENT_COUNSELLOR, key);
    }

    public List<StuTempGroupDomainV2> readRedisDataV2(Long key) {
        return getCacheValueRollcallReportDomainV2(TYPE_STUDENT_COUNSELLOR_V2, key);
    }

    /**
     * 保存多条counsellor 替换
     *
     * @param datas
     */
    public void storeLongRedisDatas(Map<Long, Long> datas) {
        if (datas != null) {
            for (Map.Entry<Long, Long> item : datas.entrySet()) {
                storeLongRedisData(item.getKey(), item.getValue());
            }
        }
    }

    /**
     * 保存一条counsellor 替换
     *
     * @param key
     * @param data
     */
    public void storeLongRedisData(Long key, Object data) {
        if (data != null) {
            setCacheValue(TYPE_COUNSELLORROLLCALL, key, data);
        }
    }

    /**
     * 读取counsellor
     *
     * @param key
     * @return
     */
    public Long readLongReidsData(Long key) {
        return getLongCacheValue(TYPE_COUNSELLORROLLCALL, key);
    }

    public void storeGroupId(Long key, Long id) {
        if (id != null) {
            setCacheValue(TYPE_COUNSELLORROLLCALL_GROUP, key, id);
        }
    }

    public Long getGroupId(Long key) {
        String str = (String) redisTemplate.opsForHash().get(TYPE_COUNSELLORROLLCALL_GROUP, key.toString());
        if (!StringUtils.isEmpty(str)) {
            return Long.parseLong(str);
        }
        return null;
    }

    public void storeRule(Long key, CounsellorRollcallRule data) {
        if (data != null) {
            setCacheValue(TYPE_COUNSELLORROLLCALL_RULE, key, data);
        }
    }

    public CounsellorRollcallRule getRule(Long key) {
        String str = (String) redisTemplate.opsForHash().get(TYPE_COUNSELLORROLLCALL_RULE, key.toString());
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseObject(str, CounsellorRollcallRule.class);
        }
        return null;
    }

    public Boolean getInitScheduleStatus(String key) {
        Boolean result = (Boolean) redisTemplate.opsForValue().get(TYPE_INIT_SCHEDULE_STATUS + key.toString());
        if (result == null || result == false) {
            return false;
        }
        return true;
    }

    public void setInitScheduleStatus(String key, Boolean data) {
        if (data != null) {
            redisTemplate.opsForValue().set(TYPE_INIT_SCHEDULE_STATUS + key, data, 1, TimeUnit.DAYS);
        }
    }

    public void setScheduleStudentToday(Long userId, List<PeriodDTO> data) {
        if (data != null) {
            String str = JSON.toJSONString(data);
            redisTemplate.opsForValue().set(TYPE_SCHEDULE_STUDENT_TODAY + userId.toString(), str, 1, TimeUnit.DAYS);
        }
    }

    public void delScheduleStudentToday(Long userId) {
        redisTemplate.delete(TYPE_SCHEDULE_STUDENT_TODAY + userId.toString());
    }

    public List<PeriodDTO> getScheduleStudentToday(Long userId) {
        String str = (String) redisTemplate.opsForValue().get(TYPE_SCHEDULE_STUDENT_TODAY + userId.toString());
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseArray(str, PeriodDTO.class);
        }
        return null;
    }

    public void setStudentRollCallStatsAll(Long userId, Map<String, Object> data) {
        if (data != null) {
            setCacheValue(TYPE_ROLLCALLSTATS_STUDENT_ALL, userId, data);
        }
    }

    public Map<String, Object> getStudentRollCallStatsAll(Long userId) {
        String str = (String) redisTemplate.opsForHash().get(TYPE_ROLLCALLSTATS_STUDENT_ALL, userId.toString());
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseObject(str, Map.class);
        }
        return null;
    }

    public void setStudentRollCallTeachingClassStats(String key, Map<String, Object> data) {
        if (data != null) {
            setCacheValue(TYPE_ROLLCALLSTATS_STUDENT_TEACHINGCLASS, key, data);
        }
    }

    public Map<String, Object> getStudentRollCallTeachingClassStats(String key) {
        String str = (String) redisTemplate.opsForHash().get(TYPE_ROLLCALLSTATS_STUDENT_TEACHINGCLASS, key);
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseObject(str, Map.class);
        }
        return null;
    }

    public void setRollCallTeachingClassStats(String key, Map<String, Object> data) {
        if (data != null) {
            setCacheValue(TYPE_ROLLCALLSTATS_TEACHINGCLASS, key, data);
        }
    }

    public Map<String, Object> getRollCallTeachingClassStats(String key) {
        String str = (String) redisTemplate.opsForHash().get(TYPE_ROLLCALLSTATS_TEACHINGCLASS, key);
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseObject(str, Map.class);
        }
        return null;
    }

    public void setRollCallTeachingClassStatsHistory(String key, List<Map<String, Object>> data) {
        if (data != null) {
            setCacheValue(TYPE_ROLLCALLSTATS_TEACHINGCLASS_HISTORY, key, data);
        }
    }

    public List<Map<String, Object>> getRollCallTeachingClassStatsHistory(String key) {
        String str = (String) redisTemplate.opsForHash().get(TYPE_ROLLCALLSTATS_TEACHINGCLASS_HISTORY, key);
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseObject(str, List.class);
        }
        return null;
    }

    public void setAd(String key, List<HomeDomain> data) {
        if (data != null) {
            setCacheValue(TYPE_GET_AD, key, data);
        }
    }

    public List<HomeDomain> getAd(String key) {
        String str = (String) redisTemplate.opsForHash().get(TYPE_GET_AD, key);
        if (!StringUtils.isEmpty(str)) {
            return JSON.parseArray(str, HomeDomain.class);
        }
        return null;
    }

    public UserInfoDomain getUserInfoDomain(Long userId) {
        return getCacheValue(TYPE_USERINFO_DOMAIN, userId, UserInfoDomain.class);
    }

    public void setUserInfoDomain(UserInfoDomain d) {
        setCacheValue(TYPE_USERINFO_DOMAIN, d.getUserId(), d);
    }

    public void setUserInfoDomainList(List<UserInfoDomain> ds) {
        if (ds != null && ds.size() > 0) {
            for (UserInfoDomain d : ds) {
                setCacheValue(TYPE_USERINFO_DOMAIN, d.getUserId(), d);
            }
        }
    }
}