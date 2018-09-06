package com.aizhixin.cloud.data.syn.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.data.syn.dto.BaseDTO;

/**
 * 数据比较的逻辑计算过程
 * 输出新增、修改、删除部分的数据
 *
 */
public class DataCompactBase {
    /**
     * 对数据相对于基线版本进行判定，是否新增、修改、删除，记录新增、修改、删除部分的数据
     * @param baseList      基线版本的数据
     * @param dataList      相对版本的数据
     * @param addList       新增的数据
     * @param updateList    修改的数据
     * @param delList       删除的数据
     */
    public static void compact(List<BaseDTO> baseList, List<BaseDTO> dataList, List<BaseDTO> addList, List<BaseDTO> updateList, List<BaseDTO> delList) {
        Map<String, BaseDTO> baseMap = new HashMap<>();//基线版本缓存
        Map<String, BaseDTO> dataMap = new HashMap<>();//相对版本缓存

        for (BaseDTO d : baseList) {//基线版本
            baseMap.put(d.keyValue(), d);
        }

        for (BaseDTO d : dataList) {////相对版本
            dataMap.put(d.keyValue(), d);
        }

        for (BaseDTO d : baseList) {
            BaseDTO d2 = dataMap.get(d.keyValue());
            if (null == d2) {//基线版本里边有，相对版本里边没有，表示基线版本里边的数据有删除
                delList.add(d);
            } else {//都有，判定是否修改，无变化不用输出
                if (!d.eq(d2)) {
                    updateList.add(d2);
                }
            }
        }

        for (BaseDTO d : dataList) {
            BaseDTO d2 = baseMap.get(d.keyValue());
            if (null == d2) {//基线版本里边没有，判定为新增
                addList.add(d);
            }
        }
    }
}
