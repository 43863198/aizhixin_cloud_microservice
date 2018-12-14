package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditCommitStudentRecordLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCommitStudentRecordLogRepository extends JpaRepository<CreditCommitStudentRecordLog, Long> {
    List<CreditCommitStudentRecordLog> findByCommitStuLogId(Long commitStuLogId);
}
