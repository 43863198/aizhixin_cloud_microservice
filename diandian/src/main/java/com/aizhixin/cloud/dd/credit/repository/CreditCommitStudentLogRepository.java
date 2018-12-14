package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditCommitStudentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCommitStudentLogRepository extends JpaRepository<CreditCommitStudentLog, Long> {
    List<CreditCommitStudentLog> findByCommitLogIdAndCreditId(Long commitLogId, Long creditId);
}
