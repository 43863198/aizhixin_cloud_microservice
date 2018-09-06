package com.aizhixin.cloud.dd.counsellorollcall.repository;

import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroupRuleTemp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempGroupRuleTempRepository extends JpaRepository<TempGroupRuleTemp, Long> {
    public TempGroupRuleTemp findByGroupId(Long groupId);
}
