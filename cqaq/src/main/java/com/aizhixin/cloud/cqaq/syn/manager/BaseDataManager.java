package com.aizhixin.cloud.cqaq.syn.manager;

import com.aizhixin.cloud.cqaq.syn.dto.BaseDTO;

import java.util.List;

public interface BaseDataManager {
    /**
     * 查找数据
     * @return  所有数据
     */
    List<BaseDTO> findAll();

    /**
     * 输出数据到当天DB目录
     * @param allNowLines   最新数据
     */
    void writeDataToDBFile(List<BaseDTO> allNowLines);

    /**
     * 读取昨天的数据作为基线版本
     * @return  基线版本数据
     */
    List<BaseDTO> readYesterdayData();

    /**
     * 输出新增、修改、删除的记录
     * @param addList       新增数据
     * @param upadateList   修改数据
     * @param delList       删除数据
     */
    void writeDataToOut(List<BaseDTO> addList, List<BaseDTO> upadateList, List<BaseDTO> delList);

}
