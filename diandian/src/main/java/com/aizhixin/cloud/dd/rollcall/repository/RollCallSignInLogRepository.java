package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.RollCallSignInLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RollCallSignInLogRepository extends MongoRepository<RollCallSignInLog, String> {
    List<RollCallSignInLog> findByScheduleRollCallId(Long scheduleRollCallId);
}
