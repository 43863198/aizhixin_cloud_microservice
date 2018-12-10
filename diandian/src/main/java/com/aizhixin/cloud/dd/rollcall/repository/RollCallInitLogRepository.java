package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.RollCallInitLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RollCallInitLogRepository extends MongoRepository<RollCallInitLog, String> {
}
