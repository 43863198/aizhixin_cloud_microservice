package com.aizhixin.cloud.rollcall.repository;

import com.aizhixin.cloud.rollcall.entity.RollCall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RollCallRepository extends JpaRepository<RollCall, Long> {
}
