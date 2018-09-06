package com.aizhixin.cloud.dd.counsellorollcall.repository;

import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcallRule;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CounsellorRollcallRuleRepository extends JpaRepository<CounsellorRollcallRule, Long> {
    public CounsellorRollcallRule findByIdAndUserIdAndDeleteFlag(Long id, Long userId, Integer deleteFlag);
    public List<CounsellorRollcallRule> findByUserIdAndDeleteFlag(Long userId, Integer deleteFlag, Sort sort);
}
