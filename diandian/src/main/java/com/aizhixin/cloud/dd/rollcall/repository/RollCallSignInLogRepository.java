package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.RollCallSignInLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RollCallSignInLogRepository extends MongoRepository<RollCallSignInLog, String> {
}
