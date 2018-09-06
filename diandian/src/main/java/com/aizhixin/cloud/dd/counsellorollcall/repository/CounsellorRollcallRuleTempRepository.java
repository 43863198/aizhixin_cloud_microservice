package com.aizhixin.cloud.dd.counsellorollcall.repository;

import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcallRuleTemp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounsellorRollcallRuleTempRepository extends JpaRepository<CounsellorRollcallRuleTemp, Long> {
    CounsellorRollcallRuleTemp findByRuleId(Long ruleId);
}
