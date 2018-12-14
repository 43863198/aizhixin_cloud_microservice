package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditCommitLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCommitLogRepository extends JpaRepository<CreditCommitLog, Long> {
    public Page<CreditCommitLog> findByCreditId(Pageable pageable, Long creditId);
}
