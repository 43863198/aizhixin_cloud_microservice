package com.aizhixin.cloud.dd.rollcall.utils;

import com.aizhixin.cloud.dd.rollcall.dto.LocaltionDTO;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LIMH on 2017/9/27.
 */
public class RollCallMapUtil {

    //用于切换map
    private static AtomicBoolean status = new AtomicBoolean(Boolean.TRUE);

    private static ConcurrentHashMap <String, Map <Long, LocaltionDTO>> map_1 = new ConcurrentHashMap();
    private static ConcurrentHashMap <String, Map <Long, LocaltionDTO>> map_2 = new ConcurrentHashMap();

    public static void setValue(String scheduleKey, Long studentId, LocaltionDTO dto) {
        Map <String, Map <Long, LocaltionDTO>> map = null;
        if (status.get()) {
            map = map_1;
        } else {
            map = map_2;
        }
        Map <Long, LocaltionDTO> tempMap = map.get(scheduleKey);
        if (tempMap == null) {
            tempMap = new HashedMap();
        }
        tempMap.put(studentId, dto);
        map.put(scheduleKey, tempMap);
    }

    public static ConcurrentHashMap getMap() {
        if (status.get()) {
            status.set(Boolean.FALSE);
            return map_1;
        } else {
            status.set(Boolean.TRUE);
            return map_2;
        }
    }

    public static void clearMap() {
        if (!status.get()) {
            map_1.clear();
        } else {
            map_2.clear();
        }
    }

}
