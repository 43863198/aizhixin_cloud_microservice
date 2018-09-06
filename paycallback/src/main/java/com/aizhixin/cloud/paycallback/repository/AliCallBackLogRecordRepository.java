package com.aizhixin.cloud.paycallback.repository;


import com.aizhixin.cloud.paycallback.entity.AliCallBackLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AliCallBackLogRecordRepository extends JpaRepository<AliCallBackLogRecord, String> {

}
