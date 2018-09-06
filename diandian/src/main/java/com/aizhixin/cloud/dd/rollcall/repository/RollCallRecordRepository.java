package com.aizhixin.cloud.dd.rollcall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.dd.rollcall.entity.RollCallRecord;

public interface RollCallRecordRepository extends JpaRepository<RollCallRecord, Long> {

}
