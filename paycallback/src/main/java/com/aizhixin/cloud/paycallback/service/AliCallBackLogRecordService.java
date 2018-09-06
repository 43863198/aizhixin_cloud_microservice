package com.aizhixin.cloud.paycallback.service;


import com.aizhixin.cloud.paycallback.entity.AliCallBackLogRecord;
import com.aizhixin.cloud.paycallback.repository.AliCallBackLogRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class AliCallBackLogRecordService {
    @Autowired
    private AliCallBackLogRecordRepository aliCallBackLogRecordRepository;

    /**
     * 保存实体
     * @param aliCallBackLogRecord
     * @return
     */
    public AliCallBackLogRecord save(AliCallBackLogRecord aliCallBackLogRecord) {
        return aliCallBackLogRecordRepository.save(aliCallBackLogRecord);
    }

    /**
     * 批量保存实体
     * @param aliCallBackLogRecordList
     * @return
     */
    public List<AliCallBackLogRecord> save(List<AliCallBackLogRecord> aliCallBackLogRecordList) {
        return aliCallBackLogRecordRepository.save(aliCallBackLogRecordList);
    }
}
